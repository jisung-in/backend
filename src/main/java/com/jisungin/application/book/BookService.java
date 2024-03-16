package com.jisungin.application.book;

import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public BookResponse createBook(BookCreateServiceRequest request) {
        if (bookRepository.existsBookByIsbn(request.getIsbn())) {
            throw new BusinessException(ErrorCode.BOOK_ALREADY_EXIST);
        }

        return BookResponse.of(bookRepository.save(request.toEntity()));
    }

}
