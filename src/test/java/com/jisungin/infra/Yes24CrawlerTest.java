package com.jisungin.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import com.jisungin.infra.crawler.CrawlingBook;
import com.jisungin.infra.crawler.Yes24Crawler;
import com.jisungin.infra.crawler.Yes24Fetcher;
import com.jisungin.infra.crawler.Yes24Parser;
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

        CrawlingBook crawlingBook = CrawlingBook.of("image url link", "crawling content");

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

}
