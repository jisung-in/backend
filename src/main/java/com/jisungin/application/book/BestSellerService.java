package com.jisungin.application.book;

import com.jisungin.application.PageResponse;
import com.jisungin.application.book.event.BestSellerUpdatedEvent;
import com.jisungin.application.book.request.BookServicePageRequest;
import com.jisungin.application.book.response.BestSellerResponse;
import com.jisungin.domain.book.repository.BestSellerRepository;
import com.jisungin.infra.crawler.Crawler;
import com.jisungin.infra.crawler.CrawlingBook;
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

    public PageResponse<BestSellerResponse> getBestSellers(BookServicePageRequest page) {
        return bestSellerRepository.findBestSellerByPage(page);
    }


    public void updateBestSellers() {
        Map<Long, CrawlingBook> crawledBooks = crawler.crawlBestSellerBook();

        bestSellerRepository.updateAll(crawledBooks);
        eventPublisher.publishEvent(new BestSellerUpdatedEvent(crawledBooks));
    }

}