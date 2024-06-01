package com.jisungin.application.book.request;

import com.jisungin.domain.book.Book;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BookCreateServiceRequests {

    private final List<BookCreateServiceRequest> requests;

    @Builder
    private BookCreateServiceRequests(List<BookCreateServiceRequest> requests) {
        this.requests = requests;
    }

    public static BookCreateServiceRequests of(List<BookCreateServiceRequest> requests) {
        return BookCreateServiceRequests.builder()
                .requests(requests)
                .build();
    }

    public List<String> getIsbns() {
        return requests.stream()
                .map(BookCreateServiceRequest::getIsbn)
                .toList();
    }

    public List<Book> toEntitiesNotInclude(Set<String> existIsbns) {
        return requests.stream()
                .filter(request -> !existIsbns.contains(request.getIsbn()))
                .map(BookCreateServiceRequest::toEntity)
                .toList();
    }

}
