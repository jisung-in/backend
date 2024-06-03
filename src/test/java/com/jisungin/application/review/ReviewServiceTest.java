package com.jisungin.application.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.OffsetLimit;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.review.request.ReviewCreateServiceRequest;
import com.jisungin.application.review.response.ReviewWithRatingResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.reviewlike.ReviewLike;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReviewServiceTest extends ServiceTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewService reviewService;

    @AfterEach
    void tearDown() {
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("도서와 연관된 리뷰를 조회한다.")
    @Test
    void findBookReviews() {
        // given
        OffsetLimit offsetLimit = OffsetLimit.of(1, 5, "like");

        Book book = bookRepository.save(createBook());
        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviews(users, book));
        List<ReviewLike> reviewLikes = reviewLikeRepository.saveAll(
                createReviewLikesForReviewWithUsers(users, reviews.get(0)));

        // when
        SliceResponse<ReviewWithRatingResponse> result = reviewService.findBookReviews(book.getIsbn(),
                offsetLimit);

        // then
        assertThat(result.isHasContent()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
        assertThat(result.getNumber()).isEqualTo(1L);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(5)
                .extracting("likeCount")
                .containsExactly(20L, 0L, 0L, 0L, 0L);
    }

    @DisplayName("도서와 연관된 리뷰 조회 시 도서가 존재해야 한다.")
    @Test
    void findBookReviewsWithoutBook() {
        // given
        String invalidBookIsbn = "0000X";
        OffsetLimit offsetLimit = OffsetLimit.of(1, 10);

        // when // then
        assertThatThrownBy(() -> reviewService.findBookReviews(invalidBookIsbn, offsetLimit))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @DisplayName("도서와 연관된 리뷰 개수를 조회한다.")
    @Test
    void findBookReviewsCount() {
        // given
        Book book = bookRepository.save(createBook());

        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviews(users, book));

        // when
        Long result = reviewService.findBookReviewsCount(book.getIsbn());

        // then
        assertThat(result).isEqualTo(20L);
    }

    @DisplayName("도서와 연관된 리뷰 개수 조회시 도서가 존재해야 한다.")
    @Test
    void findBookReviewsCountWithoutBook() {
        // given
        String invalidIsbn = "0000X";

        // when // then
        assertThatThrownBy(() -> reviewService.findBookReviewsCount(invalidIsbn))
                .hasMessage("책을 찾을 수 없습니다.")
                .isInstanceOf(BusinessException.class);
    }

    @DisplayName("유저가 리뷰를 등록한다.")
    @Test
    void createReview() {
        //given
        User user = createUser("1");
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        ReviewCreateServiceRequest request = ReviewCreateServiceRequest.builder()
                .bookIsbn(book.getIsbn())
                .content("내용이 좋아요.")
                .build();

        //when
        reviewService.createReview(request, user.getId());

        //then
        List<Review> reviews = reviewRepository.findAll();

        assertThat(reviews).hasSize(1);
        assertThat(reviews.get(0).getContent()).isEqualTo("내용이 좋아요.");
    }

    @DisplayName("유저가 리뷰를 등록하는 책이 존재해야 한다")
    @Test
    void createReviewWithoutBook() {
        //given
        User user = createUser("1");
        userRepository.save(user);

        ReviewCreateServiceRequest request = ReviewCreateServiceRequest.builder()
                .bookIsbn("123457")
                .build();

        //when //then
        assertThatThrownBy(() -> reviewService.createReview(request, user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @DisplayName("리뷰를 등록하는 유저가 존재해야 한다.")
    @Test
    void createReviewWithoutUser() {
        //given
        ReviewCreateServiceRequest request = ReviewCreateServiceRequest.builder()
                .build();

        //when //then
        assertThatThrownBy(() -> reviewService.createReview(request, 1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("리뷰를 삭제한다.")
    @Test
    void deleteReview() {
        //given
        User user = createUser("1");
        Book book = createBook();
        Review review = createReview(user, book);
        userRepository.save(user);
        bookRepository.save(book);
        reviewRepository.save(review);

        //when
        reviewService.deleteReview(review.getId(), user.getId());

        //then
        assertThat(reviewRepository.findAll()).isEmpty();
    }

    @DisplayName("다른 유저가 리뷰를 삭제한다.")
    @Test
    void deleteReviewWithAnotherUser() {
        //given
        User user1 = createUser("1");
        User user2 = createUser("2");
        Book book = createBook();
        Review review = createReview(user1, book);
        userRepository.saveAll(List.of(user1, user2));
        bookRepository.save(book);
        reviewRepository.save(review);

        //when //then
        assertThatThrownBy(() -> reviewService.deleteReview(review.getId(), user2.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    private static ReviewLike createReviewLike(User user, Review review) {
        return ReviewLike.builder()
                .user(user)
                .review(review)
                .build();
    }

    private static List<ReviewLike> createReviewLikesForReviewWithUsers(List<User> users, Review review) {
        return IntStream.range(0, 20)
                .mapToObj(i -> createReviewLike(users.get(i), review))
                .toList();
    }


    private static Review createReview(User user, Book book) {
        return Review.builder()
                .user(user)
                .book(book)
                .content("내용")
                .build();
    }

    private static List<Review> createReviews(List<User> users, Book book) {
        return IntStream.range(0, 20)
                .mapToObj(i -> createReview(users.get(i), book))
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

    private static List<User> createUsers() {
        return IntStream.range(0, 20)
                .mapToObj(i -> createUser(String.valueOf(i)))
                .toList();
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

}