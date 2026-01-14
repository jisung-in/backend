package com.jisungin.application.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jisungin.RedisTestContainer;
import com.jisungin.application.OffsetLimit;
import com.jisungin.application.PageResponse;
import com.jisungin.application.book.event.BestSellerUpdatedEvent;
import com.jisungin.application.book.event.BestSellerUpdatedEventListener;
import com.jisungin.application.book.response.BookWithRankingResponse;
import com.jisungin.domain.book.repository.BestSellerRedisRepository;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.infra.crawler.CrawledBook;
import com.jisungin.infra.crawler.Crawler;
import com.jisungin.infra.s3.S3FileManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@SpringBootTest
public class BestSellerServiceTest extends RedisTestContainer {

    @Autowired
    private BestSellerService bestSellerService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BestSellerRedisRepository bestSellerRedisRepository;

    @MockBean
    private BestSellerUpdatedEventListener eventEventListener;

    @MockBean
    private Crawler crawler;

    @MockBean
    private S3FileManager s3FileManager;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @AfterEach
    public void tearDown() {
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("베스트 셀러 페이지를 조회한다.")
    public void getBestSellers() {
        // given
        bestSellerRedisRepository.updateAll(createCrawledBookMap());

        // when
        PageResponse<BookWithRankingResponse> result = bestSellerService.getBestSellers(OffsetLimit
                .ofRange(1, 5));

        // then
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getTotalCount()).isEqualTo(6);
        assertThat(result.getQueryResponse().size()).isEqualTo(5);
        assertThat(result.getQueryResponse()).extracting("title", "isbn", "publisher", "authors")
                .containsExactly(
                        tuple("title1", "isbn1", "publisher1", new String[]{"author1"}),
                        tuple("title2", "isbn2", "publisher2", new String[]{"author2"}),
                        tuple("title3", "isbn3", "publisher3", new String[]{("author3")}),
                        tuple("title4", "isbn4", "publisher4", new String[]{"author4"}),
                        tuple("title5", "isbn5", "publisher5", new String[]{"author5"})
                );
    }

    @Test
    @DisplayName("베스트 셀러를 갱신 한다.")
    public void updateBestSellers() {
        // given
        Map<Long, CrawledBook> crawledBookMap = createCrawledBookMap();

        when(crawler.crawlBestSellerBook()).thenReturn(crawledBookMap);

        // when
        bestSellerService.updateBestSellers();

        // then
        List<BookWithRankingResponse> bookResponses = bestSellerRedisRepository.findAll();

        assertThat(bookResponses.size()).isEqualTo(6);
        assertThat(bookResponses).extracting("title", "isbn", "publisher", "authors")
                .containsExactly(
                        tuple("title1", "isbn1", "publisher1", new String[]{"author1"}),
                        tuple("title2", "isbn2", "publisher2", new String[]{"author2"}),
                        tuple("title3", "isbn3", "publisher3", new String[]{"author3"}),
                        tuple("title4", "isbn4", "publisher4", new String[]{"author4"}),
                        tuple("title5", "isbn5", "publisher5", new String[]{"author5"}),
                        tuple("title6", "isbn6", "publisher6", new String[]{"author6"})
                );
    }

    @Test
    @DisplayName("베스트 셀러를 갱신하면 DB에 새로 등록된 책을 저장하는 이벤트가 발생한다.")
    public void updateBestSellerEventRaised() {
        // given
        Map<Long, CrawledBook> crawledBookMap = createCrawledBookMap();
        when(crawler.crawlBestSellerBook()).thenReturn(crawledBookMap);

        // when
        bestSellerService.updateBestSellers();

        // then
        verify(eventEventListener).handleBestSellerUpdatedEvent(any(BestSellerUpdatedEvent.class));
    }

    private static Map<Long, CrawledBook> createCrawledBookMap() {
        return IntStream.rangeClosed(1, 6)
                .boxed()
                .collect(Collectors.toMap(Long::valueOf,
                        i -> CrawledBook.of("title" + i, "content" + i, "isbn" + i,
                                "publisher" + i, "imageUrl" + i, "thumbnail" + i, "author" + i,
                                LocalDateTime.of(2024, 1, 1, 0, 0))));
    }

}
