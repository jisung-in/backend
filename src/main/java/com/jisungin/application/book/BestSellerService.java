package com.jisungin.application.book;

import com.jisungin.application.OffsetLimit;
import com.jisungin.application.PageResponse;
import com.jisungin.application.book.event.BestSellerUpdatedEvent;
import com.jisungin.application.book.response.BookWithRankingResponse;
import com.jisungin.domain.book.repository.BestSellerRepository;
import com.jisungin.infra.crawler.CrawledBook;
import com.jisungin.infra.crawler.Crawler;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BestSellerService {

    private final Crawler crawler;
    private final BestSellerRepository bestSellerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PageResponse<BookWithRankingResponse> getBestSellers(OffsetLimit offsetLimit) {
        List<BookWithRankingResponse> response = bestSellerRepository.findBooksWithRank(offsetLimit.getOffset(),
                offsetLimit.getLimit());

        Long count = bestSellerRepository.count();

        return PageResponse.of(response.size(), count, response);
    }

    public void updateBestSellers() {
        Map<Long, CrawledBook> crawledBookMap = crawler.crawlBestSellerBook();

        bestSellerRepository.updateAll(crawledBookMap);
        eventPublisher.publishEvent(new BestSellerUpdatedEvent(crawledBookMap));
    }

}