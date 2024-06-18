package com.jisungin.application.book;

import com.jisungin.application.OffsetLimit;
import com.jisungin.application.PageResponse;
import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.application.book.request.BookCreateServiceRequests;
import com.jisungin.application.book.response.BookFindAllResponse;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import com.jisungin.infra.crawler.Crawler;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final Crawler crawler;
    private final BookRepository bookRepository;
    private final RatingRepository ratingRepository;

    public BookResponse getBook(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        Double averageRating = ratingRepository.findAverageRatingByBookId(book.getIsbn());

        return BookResponse.of(book, averageRating);
    }

    public PageResponse<BookFindAllResponse> getBooks(OffsetLimit offsetLimit) {
        List<BookFindAllResponse> response = bookRepository.getBooks(offsetLimit.getOffset(), offsetLimit.getLimit(),
                offsetLimit.getOrder());

        Long totalCount = bookRepository.getTotalCount(offsetLimit.getOrder());

        return PageResponse.of(response.size(), totalCount, response);
    }

    @Transactional
    public BookResponse createBook(BookCreateServiceRequest request) {
        if (bookRepository.existsBookByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_EXIST);
        }

        BookCreateServiceRequest newServiceRequest = crawler.crawlBook(request.getIsbn()).toServiceRequest();

        return BookResponse.of(bookRepository.save(newServiceRequest.toEntity()));
    }

    @Transactional
    public void addNewBooks(BookCreateServiceRequests requests) {
        Set<String> existIsbns = bookRepository.findExistIsbns(requests.getIsbns());
        List<Book> newBooks = requests.toEntitiesNotInclude(existIsbns);

        bookRepository.saveAll(newBooks);
    }

}
