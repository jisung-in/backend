package com.jisungin.domain.review.repository;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.reviewlike.ReviewLike;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jisungin.domain.review.RatingOrderType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.*;

class ReviewRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @AfterEach
    void tearDown() {
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("리뷰 별점이 낮은 순으로 책을 정렬한다.")
    @Test
    void getRatingsOrderByRatingAsc() {
        User user = createUser("1");
        userRepository.save(user);

        List<Book> books = createBooks();
        List<Review> reviews = createReviews(user, books);
        bookRepository.saveAll(books);
        reviewRepository.saveAll(reviews);

        //when
        PageResponse<RatingFindAllResponse> result = reviewRepository.findAllRatingOrderBy(
                user.getId(), RATING_ASC, null, 4, 8); // 1점 4개, 2점 4개 이후에 3점 리뷰 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("3", "제목3", "bookImage", 3.0),
                        tuple("8", "제목8", "bookImage", 3.0),
                        tuple("13", "제목13", "bookImage", 3.0),
                        tuple("18", "제목18", "bookImage", 3.0)
                );
    }

    @DisplayName("리뷰 별점이 높은 순으로 책을 정렬한다.")
    @Test
    void getRatingsOrderByRatingDesc() {
        //given
        User user = createUser("1");
        userRepository.save(user);

        List<Book> books = createBooks();
        List<Review> reviews = createReviews(user, books);
        bookRepository.saveAll(books);
        reviewRepository.saveAll(reviews);

        //when
        PageResponse<RatingFindAllResponse> result = reviewRepository.findAllRatingOrderBy(
                user.getId(), RATING_DESC, null, 4, 0); // 별점이 높은 순이기 때문에 5점 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("5", "제목5", "bookImage", 5.0),
                        tuple("10", "제목10", "bookImage", 5.0),
                        tuple("15", "제목15", "bookImage", 5.0),
                        tuple("20", "제목20", "bookImage", 5.0)
                );
    }

    @DisplayName("리뷰 평균 별점이 낮은 순으로 책을 정렬한다.")
    @Test
    void getRatingsOrderByRatingAvgAsc() {
        //given
        User user1 = createUser("1");
        User user2 = createUser("2");
        userRepository.saveAll(List.of(user1, user2));

        List<Book> books = createBooks();
        bookRepository.saveAll(books);

        List<Review> reviews1 = createReviews(user1, books);
        List<Review> reviews2 = createReviews(user2, books);
        reviewRepository.saveAll(reviews1);
        reviewRepository.saveAll(reviews2);

        //when
        PageResponse<RatingFindAllResponse> result = reviewRepository.findAllRatingOrderBy(
                user1.getId(), RATING_AVG_ASC, null, 4, 0); // 각 유저가 똑같은 점수로 리뷰를 했기 때문에 1점 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("1", "제목1", "bookImage", 1.0),
                        tuple("6", "제목6", "bookImage", 1.0),
                        tuple("11", "제목11", "bookImage", 1.0),
                        tuple("16", "제목16", "bookImage", 1.0)
                );
    }

    @DisplayName("리뷰 평균 별점이 높은 순으로 책을 정렬한다.")
    @Test
    void getRatingsOrderByRatingAvgDesc() {
        //given
        User user1 = createUser("1");
        User user2 = createUser("2");
        userRepository.saveAll(List.of(user1, user2));

        List<Book> books = createBooks();
        bookRepository.saveAll(books);

        List<Review> reviews1 = createReviews(user1, books);
        List<Review> reviews2 = createReviews(user2, books);
        reviewRepository.saveAll(reviews1);
        reviewRepository.saveAll(reviews2);

        //when
        PageResponse<RatingFindAllResponse> result = reviewRepository.findAllRatingOrderBy(
                user1.getId(), RATING_AVG_DESC, null, 4, 0); // 각 유저가 똑같은 점수로 리뷰를 했기 때문에 5점 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("5", "제목5", "bookImage", 5.0),
                        tuple("10", "제목10", "bookImage", 5.0),
                        tuple("15", "제목15", "bookImage", 5.0),
                        tuple("20", "제목20", "bookImage", 5.0)
                );
    }

    @DisplayName("별점이 3점인 리뷰만 조회한다.")
    @Test
    void getRatingsOrderByRatingAscOnlyThree() {
        User user = createUser("1");
        userRepository.save(user);

        List<Book> books = createBooks();
        List<Review> reviews = createReviews(user, books);
        bookRepository.saveAll(books);
        reviewRepository.saveAll(reviews);

        //when
        PageResponse<RatingFindAllResponse> result = reviewRepository.findAllRatingOrderBy(
                user.getId(), RATING_ASC, 3.0, 4, 0); // 1점 4개, 2점 4개 이후에 3점 리뷰 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(4);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("3", "제목3", "bookImage", 3.0),
                        tuple("8", "제목8", "bookImage", 3.0),
                        tuple("13", "제목13", "bookImage", 3.0),
                        tuple("18", "제목18", "bookImage", 3.0)
                );

    }

    @DisplayName("내 한줄평을 별점이 높은 순으로 조회한다.")
    @Test
    void getReviewContentOrderByRatingAsc() {
        //given
        User user1 = userRepository.save(createUser("1"));
        User user2 = userRepository.save(createUser("2"));
        List<Book> books = bookRepository.saveAll(createBooks());
        List<Review> reviews = reviewRepository.saveAll(createReviews(user1, books));
        List<ReviewLike> reviewLikesWithUser1 = reviewLikeRepository.saveAll(createReviewLikes(user1, reviews));
        List<ReviewLike> reviewLikesWithUser2 = reviewLikeRepository.saveAll(createReviewLikes(user2, reviews));

        //when
        PageResponse<ReviewContentResponse> result =
                reviewRepository.findAllReviewContentOrderBy(user1.getId(), RATING_ASC, 4, 0);

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse()).hasSize(4)
                .extracting(
                        "userImage", "userName", "rating", "content", "isbn", "title", "bookImage")
                .containsExactly(
                        tuple("userImage", "김도형", 1.0, "리뷰 내용1", "1", "제목1", "bookImage"),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용6", "6", "제목6", "bookImage"),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용11", "11", "제목11", "bookImage"),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용16", "16", "제목16", "bookImage")
                );
    }

    private static List<Book> createBooks() {
        return IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBook(
                        "제목" + String.valueOf(i), "내용" + String.valueOf(i), String.valueOf(i)))
                .collect(Collectors.toList());
    }

    private static Book createBook(String title, String content, String isbn) {
        return Book.builder()
                .title(title)
                .content(content)
                .authors("김도형")
                .isbn(isbn)
                .publisher("지성인")
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .imageUrl("bookImage")
                .build();
    }

    private static List<Review> createReviews(User user, List<Book> books) {
        return IntStream.range(0, 20)
                .mapToObj(i -> {
                    double rating = i % 5 + 1.0; // 1.0, 2.0, 3.0, 4.0, 5.0이 순환되도록 설정
                    return createReview(user, books.get(i), rating); // Review 객체 생성
                })
                .collect(Collectors.toList());
    }

    private static Review createReview(User user, Book book, Double rating) {
        return Review.builder()
                .user(user)
                .book(book)
                .content("리뷰 내용" + book.getIsbn())
                .rating(rating)
                .build();
    }

    private static List<ReviewLike> createReviewLikes(User user, List<Review> reviews) {
        return reviews.stream()
                .map(review -> createReviewLike(user, review))
                .toList();
    }

    private static ReviewLike createReviewLike(User user, Review review) {
        return ReviewLike.builder()
                .user(user)
                .review(review)
                .build();
    }

    private static User createUser(String oauthId) {
        return User.builder()
                .name("김도형")
                .profileImage("userImage")
                .oauthId(
                        OauthId.builder()
                                .oauthId(oauthId)
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .build();
    }

}