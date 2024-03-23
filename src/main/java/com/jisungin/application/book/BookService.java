package com.jisungin.application.book;

import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import com.jisungin.infra.crawler.Crawler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final Crawler crawler;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public BookResponse getBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        Double averageRating = reviewRepository.findAverageRatingByBookId(book.getIsbn());

        return BookResponse.of(book, averageRating);
    }

    @Transactional
    public BookResponse createBook(BookCreateServiceRequest request) {
        if (bookRepository.existsBookByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_EXIST);
        }

        request.addCrawlingData(crawler.crawlBook(request.getIsbn()));

        return BookResponse.of(bookRepository.save(request.toEntity()));
    }

}
