package com.jisungin.api.book;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.SearchRequest;
import com.jisungin.api.book.request.BookCreateRequest;
import com.jisungin.api.book.request.BookPageRequest;
import com.jisungin.application.PageResponse;
import com.jisungin.application.book.BestSellerService;
import com.jisungin.application.book.BookService;
import com.jisungin.application.book.response.BestSellerResponse;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.application.book.response.SimpleBookResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class BookController {

    private final BookService bookService;
    private final BestSellerService bestSellerService;

    @GetMapping("/books/{isbn}")
    public ApiResponse<BookResponse> getBook(@PathVariable("isbn") String isbn) {
        return ApiResponse.ok(bookService.getBook(isbn));
    }

    @GetMapping("/books")
    public ApiResponse<PageResponse<SimpleBookResponse>> getBooks(@ModelAttribute SearchRequest params) {
        return ApiResponse.ok(bookService.getBooks(params.toService()));
    }

    @GetMapping("/books/best-seller")
    public ApiResponse<PageResponse<BestSellerResponse>> getBestSellers(@ModelAttribute BookPageRequest page) {
        return ApiResponse.ok(bestSellerService.getBestSellers(page.toService()));
    }

    @PostMapping("/books")
    public ApiResponse<BookResponse> createBook(@RequestBody @Valid BookCreateRequest request) {
        return ApiResponse.ok(bookService.createBook(request.toServiceRequest()));
    }

}
