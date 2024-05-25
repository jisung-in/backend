package com.jisungin.application.reviewlike;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.reviewlike.response.ReviewIds;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.reviewlike.ReviewLike;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ReviewLikeServiceTest extends ServiceTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private ReviewLikeService reviewLikeService;

    @AfterEach
    void tearDown() {
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 좋아요 누른 리뷰 아이디를 조회한다.")
    @Test
    void findLikeReviewIds() {
        // given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook());

        List<Review> reviews = reviewRepository.saveAll(createReviews(user, book));
        List<ReviewLike> reviewLikes = reviewLikeRepository.saveAll(createReviewLikes(user, reviews));

        // when
        ReviewIds result = reviewLikeService.findLikeReviewIds(user.getId());

        // then
        assertThat(result.getReviewIds()).hasSize(20);
    }

    @DisplayName("사용자가 리뷰 좋아요를 누른다.")
    @Test
    void likeReview() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook());
        Review review = reviewRepository.save(createReview(user, book));

        //when
        reviewLikeService.likeReview(user.getId(), review.getId());

        //then
        List<ReviewLike> reviewLike = reviewLikeRepository.findAll();

        assertThat(reviewLike).hasSize(1);
        assertThat(reviewLike.get(0).getUser().getId()).isEqualTo(user.getId());
        assertThat(reviewLike.get(0).getReview().getId()).isEqualTo(review.getId());
    }

    @DisplayName("리뷰 좋아요가 중복되면 예외가 발생한다.")
    @Test
    void likeReviewWithReLike() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook());
        Review review = reviewRepository.save(createReview(user, book));
        ReviewLike reviewLike = reviewLikeRepository.save(createReviewLike(user, review));

        //when //then
        assertThatThrownBy(() -> reviewLikeService.likeReview(user.getId(), review.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 좋아요를 눌렀습니다.");

    }

    @DisplayName("사용자가 리뷰 좋아요를 취소한다.")
    @Test
    void unlikeReview() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook());
        Review review = reviewRepository.save(createReview(user, book));
        ReviewLike reviewLike = reviewLikeRepository.save(createReviewLike(user, review));

        //when
        reviewLikeService.unlikeReview(user.getId(), review.getId());

        //then
        List<ReviewLike> reviewLikes = reviewLikeRepository.findAll();

        assertThat(reviewLikes).isEmpty();
    }

    @DisplayName("존재하지 않는 리뷰 좋아요를 취소하면 예외가 발생한다.")
    @Test
    void unlikeReviewWithNotExist() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook());
        Review review = reviewRepository.save(createReview(user, book));

        //when //then
        assertThatThrownBy(() -> reviewLikeService.unlikeReview(user.getId(), review.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("리뷰 좋아요를 찾을 수 없습니다.");

    }

    private static ReviewLike createReviewLike(User user, Review review) {
        return ReviewLike.likeReview(user, review);
    }

    private static List<ReviewLike> createReviewLikes(User user, List<Review> reviews) {
        return IntStream.range(0, 20)
                .mapToObj(i -> createReviewLike(user, reviews.get(i)))
                .toList();
    }

    private static User createUser(String oauthId) {
        return User.builder()
                .name("김도형")
                .profileImage("image")
                .oauthId(
                        OauthId.builder()
                                .oauthId(oauthId)
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .build();
    }

    private static Book createBook() {
        return Book.builder()
                .title("제목")
                .content("내용")
                .authors("김도형")
                .isbn("123456")
                .publisher("지성인")
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .imageUrl("image")
                .build();
    }

    private static Review createReview(User user, Book book) {
        return Review.builder()
                .user(user)
                .book(book)
                .content("내용")
                .build();
    }

    private static List<Review> createReviews(User user, Book book) {
        return IntStream.range(0, 20)
                .mapToObj(i -> createReview(user, book))
                .toList();
    }

}