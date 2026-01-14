package com.jisungin.application.library;

import com.jisungin.application.PageResponse;
import com.jisungin.application.library.request.LibraryCreateServiceRequest;
import com.jisungin.application.library.request.LibraryEditServiceRequest;
import com.jisungin.application.library.response.LibraryResponse;
import com.jisungin.application.library.response.UserReadingStatusResponse;
import com.jisungin.application.library.request.UserReadingStatusGetAllServiceRequest;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.library.Library;
import com.jisungin.domain.library.repository.LibraryRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jisungin.exception.ErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LibraryService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;

    public List<LibraryResponse> findLibraries(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return LibraryResponse.fromList(libraryRepository.findAllByUserId(user.getId()));
    }

    @Transactional
    public LibraryResponse createLibrary(LibraryCreateServiceRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        if (libraryRepository.existsByUserIdAndBookId(user.getId(), book.getIsbn())) {
            throw new BusinessException(ErrorCode.USER_LIBRARY_ALREADY_EXIST);
        }

        Library savedLibrary = libraryRepository.save(request.toEntity(user, book));

        return LibraryResponse.of(savedLibrary.getId(), book.getIsbn(), savedLibrary.getStatus().getText());
    }

    @Transactional
    public void editLibrary(Long userLibraryId, Long userId, LibraryEditServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        Library library = libraryRepository.findByIdWithBookAndUser(userLibraryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LIBRARY_NOT_FOUND));

        if (!library.isLibraryOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        if (!library.isSameBook(book.getIsbn())) {
            throw new BusinessException(ErrorCode.BOOK_INVALID_INFO);
        }

        library.editReadingStatus(ReadingStatus.createReadingStatus(request.getReadingStatus()));
    }

    @Transactional
    public void deleteLibrary(Long userLibraryId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Library library = libraryRepository.findByIdWithBookAndUser(userLibraryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LIBRARY_NOT_FOUND));

        if (!library.isLibraryOwner(user.getId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        libraryRepository.deleteById(library.getId());
    }

    public PageResponse<UserReadingStatusResponse> getUserReadingStatuses(
            Long userId, UserReadingStatusGetAllServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        return libraryRepository.findAllReadingStatusOrderBy(
                user.getId(), request.getReadingStatus(), request.getOrderType(), request.getSize(), request.getOffset());
    }
}
