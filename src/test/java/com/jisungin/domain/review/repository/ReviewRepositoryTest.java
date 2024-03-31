package com.jisungin.domain.review.repository;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.review.RatingOrderType;
import com.jisungin.domain.review.Review;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.*;

class ReviewRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
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
                user.getId(), RatingOrderType.RATING_ASC, null, 4, 8); // 1점 4개, 2점 4개 이후에 3점 리뷰 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("3", "제목", "image", 3.0),
                        tuple("8", "제목", "image", 3.0),
                        tuple("13", "제목", "image", 3.0),
                        tuple("18", "제목", "image", 3.0)
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
                user.getId(), RatingOrderType.RATING_DESC, null, 4, 0); // 별점이 높은 순이기 때문에 5점 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("5", "제목", "image", 5.0),
                        tuple("10", "제목", "image", 5.0),
                        tuple("15", "제목", "image", 5.0),
                        tuple("20", "제목", "image", 5.0)
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
                user1.getId(), RatingOrderType.RATING_AVG_ASC, null, 4, 0); // 각 유저가 똑같은 점수로 리뷰를 했기 때문에 1점 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("1", "제목", "image", 1.0),
                        tuple("6", "제목", "image", 1.0),
                        tuple("11", "제목", "image", 1.0),
                        tuple("16", "제목", "image", 1.0)
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
                user1.getId(), RatingOrderType.RATING_AVG_DESC, null, 4, 0); // 각 유저가 똑같은 점수로 리뷰를 했기 때문에 5점 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("5", "제목", "image", 5.0),
                        tuple("10", "제목", "image", 5.0),
                        tuple("15", "제목", "image", 5.0),
                        tuple("20", "제목", "image", 5.0)
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
                user.getId(), RatingOrderType.RATING_ASC, 3.0, 4, 0); // 1점 4개, 2점 4개 이후에 3점 리뷰 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(4);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("3", "제목", "image", 3.0),
                        tuple("8", "제목", "image", 3.0),
                        tuple("13", "제목", "image", 3.0),
                        tuple("18", "제목", "image", 3.0)
                );

    }

    private static List<Book> createBooks() {
        return IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBook(String.valueOf(i)))
                .collect(Collectors.toList());
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
                .content("내용")
                .rating(rating)
                .build();
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

    private static Book createBook(String isbn) {
        return Book.builder()
                .title("제목")
                .content("내용")
                .authors("김도형")
                .isbn(isbn)
                .publisher("지성인")
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .imageUrl("image")
                .build();
    }

}