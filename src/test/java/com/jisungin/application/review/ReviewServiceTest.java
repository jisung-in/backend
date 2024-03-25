package com.jisungin.application.review;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.review.request.ReviewCreateServiceRequest;
import com.jisungin.application.review.response.ReviewResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ReviewServiceTest extends ServiceTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewService reviewService;

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("유저가 리뷰를 등록한다.")
    @Test
    void createReview() {
        //given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        ReviewCreateServiceRequest request = ReviewCreateServiceRequest.builder()
                .bookIsbn(book.getIsbn())
                .content("내용이 좋아요.")
                .rating(4.5)
                .build();

        //when
        ReviewResponse reviewResponse = reviewService.createReview(request, user.getId());

        //then
        List<Review> reviews = reviewRepository.findAll();

        assertThat(reviewResponse.getBook())
                .extracting("isbn", "title", "content")
                .contains("123456", "제목", "내용");

        assertThat(reviews).hasSize(1)
                .extracting("content", "rating")
                .contains(
                        tuple("내용이 좋아요.", 4.5)
                );

    }

    @DisplayName("유저가 리뷰를 등록하는 책이 존재해야 한다")
    @Test
    void createReviewWithoutBook() {
        //given
        User user = createUser();
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

    private static User createUser() {
        return User.builder()
                .name("김도형")
                .profileImage("image")
                .oauthId(
                        OauthId.builder()
                                .oauthId("oauthId")
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

}