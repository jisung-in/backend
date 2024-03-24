package com.jisungin.application.service.book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.jisungin.application.book.BookService;
import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.infra.crawler.Crawler;
import com.jisungin.infra.crawler.CrawlingBook;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @MockBean
    private Crawler crawler;

    @AfterEach
    void tearDown() {
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("책을 조회한다.")
    public void getBook() {
        // given
        Book book = bookRepository.save(create());

        // when
        BookResponse response = bookService.getBook(book.getIsbn());

        // then
        assertThat(response.getDateTime()).isEqualTo(book.getDateTime());
        assertThat(response.getAuthors()).hasSize(2)
                .contains("도서 저자1", "도서 저자2");
        assertThat(response)
                .extracting("title", "content", "isbn", "publisher", "imageUrl", "thumbnail")
                .contains("도서 제목", "도서 내용", "123456789X", "도서 출판사", "도서 imageUrl", "도서 썸네일");
    }

    @Test
    @DisplayName("존재하지 않는 책을 조회하면 예외가 발생한다.")
    public void getBookWithInvalidIsbn() {
        // given
        String invalidIsbn = "0000000000";

        // when // then
        assertThatThrownBy(() -> bookService.getBook(invalidIsbn))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("도서 정보에 대한 책을 생성한다.")
    public void createBook() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);

        BookCreateServiceRequest request = BookCreateServiceRequest.builder()
                .title("도서 제목")
                .contents("도서 내용")
                .isbn("123456789X")
                .dateTime(registeredDateTime)
                .authors("도서 저자1, 도서 저자2")
                .publisher("도서 출판사")
                .imageUrl("도서 imageUrl")
                .thumbnail("도서 썸네일")
                .build();

        when(crawler.crawlBook(request.getIsbn()))
                .thenReturn(CrawlingBook.of("도서 imageUrl", "도서 내용"));

        // when
        BookResponse response = bookService.createBook(request);

        // then
        assertThat(response.getDateTime()).isEqualTo(request.getDateTime());
        assertThat(response.getAuthors()).hasSize(2)
                .contains("도서 저자1", "도서 저자2");
        assertThat(response)
                .extracting("title", "content", "isbn", "publisher", "imageUrl", "thumbnail")
                .contains("도서 제목", "도서 내용", "123456789X", "도서 출판사", "도서 imageUrl", "도서 썸네일");
    }

    @Test
    @DisplayName("이미 등록된 ISBN을 사용하여 책을 생성하는 경우 예외가 발생한다.")
    public void createBookWithDuplicateIsbn() {
        // given
        Book book = create();
        bookRepository.save(book);

        BookCreateServiceRequest request = BookCreateServiceRequest.builder()
                .title("도서 제목")
                .contents("도서 내용")
                .isbn(book.getIsbn())
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .authors("도서 저자1, 도서 저자2")
                .publisher("도서 출판사")
                .imageUrl("도서 URL")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 등록된 책 정보 입니다.");
    }

    private static Book create() {
        return Book.builder()
                .title("도서 제목")
                .content("도서 내용")
                .authors("도서 저자1, 도서 저자2")
                .isbn("123456789X")
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .publisher("도서 출판사")
                .imageUrl("도서 imageUrl")
                .thumbnail("도서 썸네일")
                .build();
    }

}
