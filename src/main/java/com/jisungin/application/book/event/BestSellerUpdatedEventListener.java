package com.jisungin.application.book.event;

import com.jisungin.application.book.BookService;
import com.jisungin.application.book.request.BookCreateServiceRequests;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BestSellerUpdatedEventListener {

    private final BookService bookService;

    @EventListener
    public void handleBestSellerUpdatedEvent(BestSellerUpdatedEvent event) {
        bookService.addNewBooks(BookCreateServiceRequests.of(event.getServiceRequests()));
    }

}
