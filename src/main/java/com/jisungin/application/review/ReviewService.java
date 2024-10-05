package com.jisungin.application.review;

import com.jisungin.application.OffsetLimit;
import com.jisungin.application.PageResponse;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.review.request.ReviewContentGetAllServiceRequest;
import com.jisungin.application.review.request.ReviewCreateServiceRequest;
import com.jisungin.application.review.response.ReviewContentGetAllResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
import com.jisungin.application.review.response.ReviewWithRatingResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jisungin.exception.ErrorCode.*;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public SliceResponse<ReviewWithRatingResponse> findBookReviews(String isbn, OffsetLimit offsetLimit) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BusinessException(BOOK_NOT_FOUND));

        return reviewRepository.findAllByBookId(book.getIsbn(), offsetLimit.getOffset(), offsetLimit.getLimit(),
                offsetLimit.getOrder());
    }

    public Long findBookReviewsCount(String isbn) {
        Book book = bookRepository.findById(isbn)
                .orElseThrow(() -> new BusinessException(BOOK_NOT_FOUND));

        return reviewRepository.countByBookId(book.getIsbn());
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

    public ReviewContentGetAllResponse getReviewContents(Long userId, ReviewContentGetAllServiceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(USER_NOT_FOUND));

        PageResponse<ReviewContentResponse> reviewContents = reviewRepository.findAllReviewContentOrderBy(
                user.getId(), request.getOrderType(), request.getSize(), request.getOffset());

        List<Long> reviewIds = reviewContents.getQueryResponse().stream()
                .map(ReviewContentResponse::getReviewId)
                .toList();

        List<Long> likeReviewIds = reviewLikeRepository.findLikeReviewByReviewId(userId, reviewIds);

        return ReviewContentGetAllResponse.of(reviewContents, likeReviewIds);
    }
}
