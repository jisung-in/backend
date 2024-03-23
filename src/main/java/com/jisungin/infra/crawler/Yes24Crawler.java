package com.jisungin.infra.crawler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Yes24Crawler implements Crawler {

    private final Fetcher fetcher;
    private final Parser parser;

    @Override
    public CrawlingBook crawlBook(String isbn) {
        String bookId = parser.parseIsbn(fetcher.fetchIsbn(isbn));

        return parser.parseBook(fetcher.fetchBook(bookId));
    }

}
