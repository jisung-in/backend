package com.jisungin.application.service.book;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.jisungin.application.book.BookService;
import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.exception.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BookServiceTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;

    @AfterEach
    void tearDown() {
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("도서 정보에 대한 책을 생성한다.")
    void createBook() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.now();

        BookCreateServiceRequest request = BookCreateServiceRequest.builder()
                .title("도서 제목")
                .contents("도서 내용")
                .isbn("도서 isbn")
                .dateTime(registeredDateTime)
                .authors("도서 저자1, 도서 저자2")
                .publisher("도서 출판사")
                .url("도서 URL")
                .thumbnail("도서 썸네일")
                .build();

        // when
        BookResponse response = bookService.createBook(request);

        // then
        assertThat(response.getId()).isNotNull();
        assertThat(response.getDateTime()).isEqualTo(registeredDateTime);
        assertThat(response.getAuthors()).hasSize(2)
                .contains("도서 저자1", "도서 저자2");
        assertThat(response)
                .extracting("title", "content", "isbn", "publisher", "url", "thumbnail")
                .contains("도서 제목", "도서 내용", "도서 isbn", "도서 출판사", "도서 URL", "도서 썸네일");
    }

    @Test
    @DisplayName("isbn이 일치하는 책을 생성하는 경우 예외가 발생한다.")
    void createBookDuplicateIsbn() {
        // given
        Book book = create();
        bookRepository.save(book);

        BookCreateServiceRequest request = BookCreateServiceRequest.builder()
                .title("도서 제목")
                .contents("도서 내용")
                .isbn("도서 isbn")
                .dateTime(LocalDateTime.now())
                .authors("도서 저자1, 도서 저자2")
                .publisher("도서 출판사")
                .url("도서 URL")
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
                .isbn("도서 isbn")
                .dateTime(LocalDateTime.now())
                .publisher("도서 출판사")
                .url("도서 URL")
                .thumbnail("도서 썸네일")
                .build();
    }

}
