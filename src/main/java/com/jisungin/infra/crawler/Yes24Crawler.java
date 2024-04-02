package com.jisungin.infra.crawler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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

    @Override
    public Map<Long, CrawlingBook> crawlBestSellerBook() {
        Map<Long, String> bestSellerBookIds = parser.parseBestSellerBookId(fetcher.fetchBestSellerBookId());
        Map<Long, CrawlingBook> bestSellerBooks = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = bestSellerBookIds.entrySet().stream()
                .map(entry -> CompletableFuture.supplyAsync(() -> parser.parseBook(fetcher.fetchBook(entry.getValue())))
                        .thenAccept(crawlingBook -> bestSellerBooks.put(entry.getKey(), crawlingBook)))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        return bestSellerBooks;
    }

}
