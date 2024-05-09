package com.jisungin.application.userlibrary;

import com.jisungin.application.userlibrary.request.UserLibraryCreateServiceRequest;
import com.jisungin.application.userlibrary.request.UserLibraryEditServiceRequest;
import com.jisungin.application.userlibrary.response.UserLibraryResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.userlibrary.UserLibrary;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.domain.userlibrary.repository.UserLibraryRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserLibraryService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final UserLibraryRepository userLibraryRepository;

    public UserLibraryResponse getUserLibrary(Long userId, String isbn) {
        return userLibraryRepository.findByUserIdAndBookId(userId, isbn)
                .map(UserLibraryResponse::of)
                .orElseGet(UserLibraryResponse::empty);
    }

    @Transactional
    public UserLibraryResponse createUserLibrary(UserLibraryCreateServiceRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        if (userLibraryRepository.existsByUserIdAndBookId(user.getId(), book.getIsbn())) {
            throw new BusinessException(ErrorCode.USER_LIBRARY_ALREADY_EXIST);
        }

        return UserLibraryResponse.of(userLibraryRepository.save(request.toEntity(user, book)));
    }

    @Transactional
    public void editUserLibrary(Long userLibraryId, Long userId, UserLibraryEditServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        UserLibrary userLibrary = userLibraryRepository.findByIdWithBookAndUser(userLibraryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LIBRARY_NOT_FOUND));

        if (!userLibrary.isUserLibraryOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        if (!userLibrary.isSameBook(book.getIsbn())) {
            throw new BusinessException(ErrorCode.BOOK_INVALID_INFO);
        }

        userLibrary.editReadingStatus(ReadingStatus.createReadingStatus(request.getReadingStatus()));
    }

    @Transactional
    public void deleteUserLibrary(Long userLibraryId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        UserLibrary userLibrary = userLibraryRepository.findByIdWithBookAndUser(userLibraryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LIBRARY_NOT_FOUND));

        if (!userLibrary.isUserLibraryOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        userLibraryRepository.deleteById(userLibrary.getId());
    }

}
