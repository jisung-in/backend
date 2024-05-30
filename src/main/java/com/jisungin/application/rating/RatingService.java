package com.jisungin.application.rating;

import com.jisungin.application.rating.request.RatingCreateServiceRequest;
import com.jisungin.application.rating.request.RatingUpdateServiceRequest;
import com.jisungin.application.rating.response.RatingCreateResponse;
import com.jisungin.application.rating.response.RatingGetOneResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.Rating;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jisungin.exception.ErrorCode.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    private final BookRepository bookRepository;

    private final UserRepository userRepository;

    @Transactional
    public RatingCreateResponse creatingRating(Long userId, RatingCreateServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        if (ratingRepository.existsByUserAndBook(user, book)) {
            throw new BusinessException(RATING_ALREADY_EXIST);
        }

        Rating rating = ratingRepository.save(Rating.create(request.getRating(), user, book));

        return RatingCreateResponse.of(rating.getId(), rating.getRating(), book.getIsbn());
    }

    public RatingGetOneResponse getRating(Long userId, String isbn) {
        User user = userRepository.findById(userId)
                .orElse(null);

        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        Rating rating = ratingRepository.findRatingByUserAndBook(user, book)
                .orElse(null);

        if (rating == null || user == null) {
            return RatingGetOneResponse.of(null, null, book.getIsbn());
        }

        return RatingGetOneResponse.of(rating.getId(), rating.getRating(), book.getIsbn());
    }

    @Transactional
    public void updateRating(Long userId, Long ratingId, RatingUpdateServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RATING_NOT_FOUND));

        rating.updateRating(request.getRating());
    }

    @Transactional
    public void deleteRating(Long userId, Long ratingId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RATING_NOT_FOUND));

        ratingRepository.deleteById(ratingId);
    }

}
