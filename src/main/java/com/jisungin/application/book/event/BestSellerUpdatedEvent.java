package com.jisungin.application.book.event;

import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.infra.crawler.CrawledBook;
import java.util.List;
import java.util.Map;
import lombok.Getter;

@Getter
public class BestSellerUpdatedEvent {

    private final Map<Long, CrawledBook> crawledBookMap;

    public BestSellerUpdatedEvent(Map<Long, CrawledBook> crawledBookMap) {
        this.crawledBookMap = crawledBookMap;
    }

    public List<BookCreateServiceRequest> getServiceRequests() {
        return crawledBookMap.values().stream()
                .map(CrawledBook::toServiceRequest)
                .toList();
    }

}
