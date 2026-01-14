package com.jisungin.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import com.jisungin.infra.crawler.CrawledBook;
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

        CrawledBook crawledBook = createCrawledBookWithIsbn("0000000000");

        when(fetcher.fetchIsbn(isbn)).thenReturn(isbnDocument);
        when(fetcher.fetchBook(bookId)).thenReturn(bookDocument);

        when(parser.parseIsbn(isbnDocument)).thenReturn(bookId);
        when(parser.parseBook(bookDocument)).thenReturn(crawledBook);

        // when
        CrawledBook result = crawler.crawlBook(isbn);

        // then
        assertThat(result).isEqualTo(crawledBook);
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

        CrawledBook book1 = createCrawledBookWithIsbn("1");
        CrawledBook book2 = createCrawledBookWithIsbn("2");

        when(parser.parseBook(fetchBookDoc1)).thenReturn(book1);
        when(parser.parseBook(fetchBookDoc2)).thenReturn(book2);

        // when
        Map<Long, CrawledBook> result = crawler.crawlBestSellerBook();

        // then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(1L)).isEqualTo(book1);
        assertThat(result.get(2L)).isEqualTo(book2);
    }

    @Test
    @DisplayName("ISBN이 없는 도서를 조회한다.")
    public void crawledBookWithoutISBN() {
        Document bestSellerBookIdsDoc = mock(Document.class);
        Document fetchBookDoc1 = mock(Document.class);
        Document fetchBookDoc2 = mock(Document.class);

        Map<Long, String> crawledBookIds = new HashMap<>();

        crawledBookIds.put(1L, "000001");
        crawledBookIds.put(2L, "000002");

        when(fetcher.fetchBestSellerBookId()).thenReturn(bestSellerBookIdsDoc);
        when(parser.parseBestSellerBookId(any(Document.class))).thenReturn(crawledBookIds);

        when(fetcher.fetchBook("000001")).thenReturn(fetchBookDoc1);
        when(fetcher.fetchBook("000002")).thenReturn(fetchBookDoc2);

        CrawledBook book1 = createCrawledBookWithIsbn("");
        CrawledBook book2 = createCrawledBookWithIsbn("");

        when(parser.parseBook(fetchBookDoc1)).thenReturn(book1);
        when(parser.parseBook(fetchBookDoc2)).thenReturn(book2);

        // when
        Map<Long, CrawledBook> result = crawler.crawlBestSellerBook();

        // then
        assertThat(result).hasSize(0);
    }

    @Test
    @DisplayName("19세 이상 도서는 조회할 수 없다.")
    public void crawlingBookWithAdultBook() {
        // given
        Document bestSellerBookIdsDoc = mock(Document.class);
        Document fetchBookDoc1 = mock(Document.class);
        Document fetchBookDoc2 = mock(Document.class);

        Map<Long, String> crawledBookIds = new HashMap<>();
        crawledBookIds.put(1L, "000001");
        crawledBookIds.put(2L, "000002");

        when(fetcher.fetchBestSellerBookId()).thenReturn(bestSellerBookIdsDoc);
        when(parser.parseBestSellerBookId(any(Document.class))).thenReturn(crawledBookIds);

        when(fetcher.fetchBook("000001")).thenReturn(fetchBookDoc1);
        when(fetcher.fetchBook("000002")).thenReturn(fetchBookDoc2);

        when(parser.parseBook(fetchBookDoc1)).thenReturn(null);
        when(parser.parseBook(fetchBookDoc2)).thenReturn(null);

        // when
        Map<Long, CrawledBook> result = crawler.crawlBestSellerBook();

        // then
        assertThat(result).hasSize(0);
    }

    private static CrawledBook createCrawledBookWithIsbn(String isbn) {
        return CrawledBook.builder()
                .title("도서 제목" + isbn)
                .content("도서 내용" + isbn)
                .isbn(isbn)
                .publisher("도서 출판사" + isbn)
                .imageUrl("www.image-url.com/" + isbn)
                .thumbnail("www.image-thumbnail.com/" + isbn)
                .authors("도서 저자" + isbn)
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
    }

}
