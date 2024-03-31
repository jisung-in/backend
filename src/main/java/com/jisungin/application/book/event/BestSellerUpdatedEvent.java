package com.jisungin.application.book.event;

import com.jisungin.infra.crawler.CrawlingBook;
import java.util.Map;
import lombok.Getter;

@Getter
public class BestSellerUpdatedEvent {

    private final Map<Long, CrawlingBook> crawledBooks;

    public BestSellerUpdatedEvent(Map<Long, CrawlingBook> crawledBooks) {
        this.crawledBooks = crawledBooks;
    }

}
