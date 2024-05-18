package com.jisungin.application.review;

import static com.jisungin.exception.ErrorCode.BOOK_NOT_FOUND;
import static com.jisungin.exception.ErrorCode.REVIEW_NOT_FOUND;
import static com.jisungin.exception.ErrorCode.UNAUTHORIZED_REQUEST;
import static com.jisungin.exception.ErrorCode.USER_NOT_FOUND;

import com.jisungin.application.OffsetLimit;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.review.request.ReviewCreateServiceRequest;
import com.jisungin.application.review.response.ReviewWithRatingResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public SliceResponse<ReviewWithRatingResponse> findBookReviews(String isbn, OffsetLimit offsetLimit) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BusinessException(BOOK_NOT_FOUND));

        return reviewRepository.findAllByBookId(book.getIsbn(), offsetLimit.getOffset(), offsetLimit.getLimit(),
                offsetLimit.getOrder());
    }

    @Transactional
    public void createReview(ReviewCreateServiceRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        Book book = bookRepository.findById(request.getBookIsbn())
                .orElseThrow(() -> new BusinessException(BOOK_NOT_FOUND));

        Review savedReview = reviewRepository.save(Review.create(user, book, request.getContent()));
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review deleteReview = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(REVIEW_NOT_FOUND));

        User reviewUser = deleteReview.getUser();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        if (!user.isMe(reviewUser.getId())) {
            throw new BusinessException(UNAUTHORIZED_REQUEST);
        }
        reviewRepository.delete(deleteReview);
    }

}
