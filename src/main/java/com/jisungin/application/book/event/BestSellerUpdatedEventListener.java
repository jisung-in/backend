package com.jisungin.application.book.event;

import com.jisungin.application.book.BookService;
import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.infra.crawler.CrawlingBook;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BestSellerUpdatedEventListener {

    private final BookService bookService;

    @EventListener
    public void handleBestSellerUpdatedEvent(BestSellerUpdatedEvent event) {
        Map<Long, CrawlingBook> crawledBook = event.getCrawledBooks();

        List<BookCreateServiceRequest> bookCreateServiceRequests = crawledBook.values().stream()
                .map(CrawlingBook::toServiceRequest)
                .toList();

        bookService.addNewBooks(bookCreateServiceRequests);
    }

}
