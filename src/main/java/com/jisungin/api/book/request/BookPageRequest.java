package com.jisungin.api.book.request;

import com.jisungin.application.book.request.BookServicePageRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookPageRequest {

    Integer page;
    Integer size;

    @Builder
    private BookPageRequest(Integer page, Integer size) {
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 5;
    }

    public BookServicePageRequest toServiceRequest() {
        return BookServicePageRequest.builder()
                .page(page)
                .size(size)
                .build();
    }

}
