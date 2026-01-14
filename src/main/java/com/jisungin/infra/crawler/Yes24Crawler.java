package com.jisungin.infra.crawler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Yes24Crawler implements Crawler {

    private final Fetcher fetcher;
    private final Parser parser;

    @Override
    public CrawledBook crawlBook(String isbn) {
        String bookId = parser.parseIsbn(fetcher.fetchIsbn(isbn));

        return parser.parseBook(fetcher.fetchBook(bookId));
    }

    @Override
    public Map<Long, CrawledBook> crawlBestSellerBook() {
        Map<Long, String> crawledBookIds = parser.parseBestSellerBookId(fetcher.fetchBestSellerBookId());
        Map<Long, CrawledBook> crawledBookMap = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = crawledBookIds.entrySet().stream()
                .map(entry -> CompletableFuture.supplyAsync(() -> parser.parseBook(fetcher.fetchBook(entry.getValue())))
                        .thenAccept(crawledBook -> {
                            if (!crawledBook.isBlankIsbn()) {
                                crawledBookMap.put(entry.getKey(), crawledBook);
                            }
                        })
                        .exceptionally(throwable -> {
                            log.warn("[WARN] 19세 이상 도서는 조회할 수 없습니다.");

                            return null;
                        }))
                .toList();

        CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

        return crawledBookMap;
    }

}
