package com.jisungin.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import com.jisungin.infra.crawler.CrawlingBook;
import com.jisungin.infra.crawler.Yes24Crawler;
import com.jisungin.infra.crawler.Yes24Fetcher;
import com.jisungin.infra.crawler.Yes24Parser;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class Yes24CrawlerTest {

    @InjectMocks
    private Yes24Crawler crawler;

    @Mock
    private Yes24Parser parser;

    @Mock
    private Yes24Fetcher fetcher;

    @Test
    @DisplayName("isbn을 통해 크롤링 된 책을 생성한다.")
    public void crawlingBook() {
        // given
        String isbn = "0000000000";
        String bookId = "1111111111";

        Document isbnDocument = mock(Document.class);
        Document bookDocument = mock(Document.class);

        LocalDateTime registeredTime = LocalDateTime.of(2024, 1, 1, 0, 0);

        CrawlingBook crawlingBook = CrawlingBook.of("도서 제목", "도서 내용", "도서 ISBN",
                "도서 출판사", "도서 이미지 링크", "도서 썸네일", "도서 작가1", registeredTime);

        when(fetcher.fetchIsbn(isbn)).thenReturn(isbnDocument);
        when(fetcher.fetchBook(bookId)).thenReturn(bookDocument);
        when(parser.parseIsbn(isbnDocument)).thenReturn(bookId);
        when(parser.parseBook(bookDocument)).thenReturn(crawlingBook);

        // when
        CrawlingBook expectedCrawlingBook = crawler.crawlBook(isbn);

        // then
        assertThat(expectedCrawlingBook).isEqualTo(crawlingBook);
    }

    @Test
    @DisplayName("올바르지 않은 isbn을 입력하면 예외가 발생한다.")
    public void crawlingBookWithInvalidIsbn() {
        // given
        String isbn = "XXXXXXXXXX";

        when(fetcher.fetchIsbn(isbn)).thenThrow(new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        // when  then
        assertThatThrownBy(() -> crawler.crawlBook(isbn))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("베스트 셀러 책을 크롤링 한다.")
    public void crawlingBestSeller() {
        // given
        Document bestSellerBookIdsDoc = mock(Document.class);
        Document fetchBookDoc1 = mock(Document.class);
        Document fetchBookDoc2 = mock(Document.class);

        Map<Long, String> bestSellerBookIds = new HashMap<>();
        bestSellerBookIds.put(1L, "00001");
        bestSellerBookIds.put(2L, "00002");

        when(fetcher.fetchBestSellerBookId()).thenReturn(bestSellerBookIdsDoc);
        when(parser.parseBestSellerBookId(any(Document.class))).thenReturn(bestSellerBookIds);

        when(fetcher.fetchBook("00001")).thenReturn(fetchBookDoc1);
        when(fetcher.fetchBook("00002")).thenReturn(fetchBookDoc2);

        CrawlingBook book1 = CrawlingBook.of("책 제목1", "책 내용1", "책 ISBN1", "책 출판사1",
                "책 이미지 URL1", "책 썸네일1", "책 저자1, 책 저자2",
                LocalDateTime.of(2024, 1, 1, 0, 0));
        CrawlingBook book2 = CrawlingBook.of("책 제목2", "책 내용2", "책 ISBN2", "책 출판사2",
                "책 이미지 URL2", "책 썸네일2", "책 저자3, 책 저자4",
                LocalDateTime.of(2024, 1, 1, 0, 0));

        when(parser.parseBook(fetchBookDoc1)).thenReturn(book1);
        when(parser.parseBook(fetchBookDoc2)).thenReturn(book2);

        // when
        Map<Long, CrawlingBook> bestSellerBooks = crawler.crawlBestSellerBook();

        // then
        assertThat(bestSellerBooks.size()).isEqualTo(2);
        assertThat(bestSellerBooks.get(1L)).isEqualTo(book1);
        assertThat(bestSellerBooks.get(2L)).isEqualTo(book2);
    }

}
