package com.jisungin.application.userlibrary;

import com.jisungin.application.userlibrary.request.UserLibraryCreateServiceRequest;
import com.jisungin.application.userlibrary.response.UserLibraryResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.mylibrary.UserLibrary;
import com.jisungin.domain.mylibrary.repository.UserLibraryRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLibraryService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserLibraryRepository userLibraryRepository;

    @Transactional
    public UserLibraryResponse createUserLibrary(UserLibraryCreateServiceRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        UserLibrary savedUserLibrary = userLibraryRepository.save(request.toEntity(user, book));

        return UserLibraryResponse.of(savedUserLibrary);
    }

}
