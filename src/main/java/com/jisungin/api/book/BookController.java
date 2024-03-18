package com.jisungin.api.book;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.book.request.BookCreateRequest;
import com.jisungin.application.book.BookService;
import com.jisungin.application.book.response.BookResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class BookController {

    private final BookService bookService;

    @PostMapping("/books")
    public ApiResponse<BookResponse> createBook(@RequestBody @Valid BookCreateRequest request) {
        return ApiResponse.ok(bookService.createBook(request.toServiceRequest()));
    }

}
