package com.jisungin.domain.review.repository;

import static com.jisungin.domain.review.RatingOrderType.RATING_ASC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
import com.jisungin.application.review.response.ReviewWithRatingResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.Rating;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.reviewlike.ReviewLike;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ReviewRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RatingRepository ratingRepository;

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
        ratingRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("내 한줄평을 별점이 높은 순으로 조회한다.")
    @Test
    void getReviewContentOrderByRatingAsc() {
        //given
        User user1 = userRepository.save(createUser("1"));
        User user2 = userRepository.save(createUser("2"));
        List<Book> books = bookRepository.saveAll(createBooks());
        List<Review> reviews = reviewRepository.saveAll(createReviewsForUser(user1, books));
        List<Rating> ratings = ratingRepository.saveAll(createRatingsForUserWithBooks(user1, books));
        List<ReviewLike> reviewLikesWithUser1 = reviewLikeRepository.saveAll(createReviewLikes(user1, reviews));
        List<ReviewLike> reviewLikesWithUser2 = reviewLikeRepository.saveAll(createReviewLikes(user2, reviews));

        //when
        PageResponse<ReviewContentResponse> result =
                reviewRepository.findAllReviewContentOrderBy(user1.getId(), RATING_ASC, 4, 0);

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse()).hasSize(4)
                .extracting("userImage", "userName", "rating", "content", "isbn", "title", "bookImage",
                        "authors", "publisher", "likeCount")
                .containsExactly(
                        tuple("userImage", "김도형", 1.0, "리뷰 내용1", "1", "제목1", "bookImage", "저자1", "출판사1", 2L),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용11", "11", "제목11", "bookImage", "저자11", "출판사11", 2L),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용16", "16", "제목16", "bookImage", "저자16", "출판사16", 2L),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용6", "6", "제목6", "bookImage", "저자6", "출판사6", 2L)
                );
    }

    @DisplayName("도서와 연관된 리뷰를 조회한다.")
    @Test
    void findAllByBookById() {
        // given
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "00001", "저자명", "출판사"));
        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviewsForBook(users, book));
        List<Rating> ratings = ratingRepository.saveAll(createRatingsForBookWithUsers(users, book));
        List<ReviewLike> reviewLikes = reviewLikeRepository.saveAll(
                createReviewLikesForReviewWithUsers(users, reviews.get(0)));

        // when
        SliceResponse<ReviewWithRatingResponse> result = reviewRepository.findAllByBookId(book.getIsbn(), 0, 5,
                "like");

        // then
        assertThat(result.isHasContent()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(5)
                .extracting("likeCount")
                .containsExactly(20L, 0L, 0L, 0L, 0L);
    }

    @DisplayName("도서와 연관된 리뷰를 최근 생성된 순으로 조회한다.")
    @Test
    void findAllByBookIdOrderByRecent() {
        // given
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "00001", "저자명", "출판사"));
        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviewsForBook(users, book));
        List<Rating> ratings = ratingRepository.saveAll(createRatingsForBookWithUsers(users, book));
        List<ReviewLike> reviewLikes = reviewLikeRepository.saveAll(
                createReviewLikesForReviewWithUsers(users, reviews.get(0)));

        // when
        SliceResponse<ReviewWithRatingResponse> result = reviewRepository.findAllByBookId(book.getIsbn(), 0, 5,
                "recent");

        // then
        assertThat(result.isHasContent()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(5)
                .extracting("reviewId", "ratingId")
                .contains(
                        tuple(reviews.get(reviews.size() - 1).getId(), ratings.get(ratings.size() - 1).getId()),
                        tuple(reviews.get(reviews.size() - 2).getId(), ratings.get(ratings.size() - 2).getId()),
                        tuple(reviews.get(reviews.size() - 3).getId(), ratings.get(ratings.size() - 3).getId()),
                        tuple(reviews.get(reviews.size() - 4).getId(), ratings.get(ratings.size() - 4).getId()),
                        tuple(reviews.get(reviews.size() - 5).getId(), ratings.get(ratings.size() - 5).getId())
                );
    }

    @DisplayName("도서와 연관된 리뷰를 별점 많은 순으로 조회한다.")
    @Test
    public void findAllBookIdOrderByRatingDesc() {
        // given
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "00001", "저자명", "출판사"));
        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviewsForBook(users, book));
        List<Rating> ratings = ratingRepository.saveAll(createRatingsForBookWithUsers(users, book));
        List<ReviewLike> reviewLikes = reviewLikeRepository.saveAll(
                createReviewLikesForReviewWithUsers(users, reviews.get(0)));

        // when
        SliceResponse<ReviewWithRatingResponse> result = reviewRepository.findAllByBookId(book.getIsbn(), 0, 5,
                "rating_desc");

        // then
        assertThat(result.isHasContent()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(5)
                .extracting("starRating")
                .containsExactly(5.0, 5.0, 5.0, 5.0, 4.0);
    }

    @DisplayName("도서와 연관된 리뷰를 별점 높은 순으로 조회 시 별점이 없는 리뷰는 조회되지 않는다.")
    @Test
    public void findAllBookIdOrderByRatingDescWithoutRating() {
        // given
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "00001", "저자명", "출판사"));
        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviewsForBook(users, book));
        List<ReviewLike> reviewLikes = reviewLikeRepository.saveAll(
                createReviewLikesForReviewWithUsers(users, reviews.get(0)));

        // when
        SliceResponse<ReviewWithRatingResponse> result = reviewRepository.findAllByBookId(book.getIsbn(), 0, 5,
                "rating_desc");

        // then
        assertThat(result.isHasContent()).isFalse();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(0);
        assertThat(result.getContent()).hasSize(0);
    }

    @DisplayName("도서와 연관된 리뷰를 별점 낮은 순으로 조회한다.")
    @Test
    public void findAllBookIdOrderByRatingAsc() {
        // given
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "00001", "저자명", "출판사"));
        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviewsForBook(users, book));
        List<Rating> ratings = ratingRepository.saveAll(createRatingsForBookWithUsers(users, book));
        List<ReviewLike> reviewLikes = reviewLikeRepository.saveAll(
                createReviewLikesForReviewWithUsers(users, reviews.get(0)));

        // when
        SliceResponse<ReviewWithRatingResponse> result = reviewRepository.findAllByBookId(book.getIsbn(), 0, 5,
                "rating_asc");

        // then
        assertThat(result.isHasContent()).isTrue();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isFalse();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getContent()).hasSize(5)
                .extracting("starRating")
                .containsExactly(1.0, 1.0, 1.0, 1.0, 2.0);
    }

    @DisplayName("도서와 연관된 리뷰를 별점 낮은 순으로 조회 시 별점이 없는 경우 조회되지 않는다.")
    @Test
    public void findAllBookIdOrderByRatingAscWithoutRating() {
        // given
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "00001", "저자명", "출판사"));
        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviewsForBook(users, book));
        List<ReviewLike> reviewLikes = reviewLikeRepository.saveAll(
                createReviewLikesForReviewWithUsers(users, reviews.get(0)));

        // when
        SliceResponse<ReviewWithRatingResponse> result = reviewRepository.findAllByBookId(book.getIsbn(), 0, 5,
                "rating_asc");

        // then
        assertThat(result.isHasContent()).isFalse();
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(0);
        assertThat(result.getContent()).hasSize(0);
    }

    @DisplayName("도서와 연관된 리뷰의 개수를 조회한다.")
    @Test
    public void findBookReviewsCount() {
        // given
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "00001", "저자명", "출판사"));

        List<User> users = userRepository.saveAll(createUsers());
        List<Review> reviews = reviewRepository.saveAll(createReviewsForBook(users, book));

        // when
        Long result = reviewRepository.countByBookId(book.getIsbn());

        // then
        assertThat(result).isEqualTo(20L);
    }

    @DisplayName("도서와 연관된 리뷰가 없는 경우 0을 리턴한다.")
    @Test
    public void findBookReviewsCountWithoutReview() {
        // given
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "00001", "저자명", "출판사"));

        // when
        Long result = reviewRepository.countByBookId(book.getIsbn());

        // then
        assertThat(result).isEqualTo(0L);
    }

    private static List<Book> createBooks() {
        return IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBook(
                        "제목" + i, "내용" + i, String.valueOf(i), "저자" + i, "출판사" + i))
                .collect(Collectors.toList());
    }

    private static Book createBook(String title, String content, String isbn, String authors, String publisher) {
        return Book.builder()
                .title(title)
                .content(content)
                .authors(authors)
                .isbn(isbn)
                .publisher(publisher)
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .imageUrl("bookImage")
                .build();
    }

    private static List<Review> createReviewsForUser(User user, List<Book> books) {
        return IntStream.range(0, 20)
                .mapToObj(i -> {
                    double rating = i % 5 + 1.0; // 1.0, 2.0, 3.0, 4.0, 5.0이 순환되도록 설정
                    return createReview(user, books.get(i)); // Review 객체 생성
                })
                .collect(Collectors.toList());
    }

    private static List<Review> createReviewsForBook(List<User> users, Book book) {
        return IntStream.range(0, 20)
                .mapToObj(i -> createReview(users.get(i), book))
                .toList();
    }

    private static Review createReview(User user, Book book) {
        return Review.builder()
                .user(user)
                .book(book)
                .content("리뷰 내용" + book.getIsbn())
                .build();
    }

    private static List<ReviewLike> createReviewLikes(User user, List<Review> reviews) {
        return reviews.stream()
                .map(review -> createReviewLike(user, review))
                .toList();
    }

    private List<ReviewLike> createReviewLikesForReviewWithUsers(List<User> users, Review review) {
        return users.stream()
                .map(user -> createReviewLike(user, review))
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

    private static User createUserWithId(int id) {
        return User.builder()
                .name("사용자" + id)
                .profileImage("www.profileImage.com/" + id)
                .oauthId(
                        OauthId.builder()
                                .oauthId(String.valueOf(id))
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .build();
    }

    private static List<User> createUsers() {
        return IntStream.range(0, 20)
                .mapToObj(i -> createUserWithId(i))
                .toList();
    }

    private static List<Rating> createRatingsForUserWithBooks(User user, List<Book> books) {
        return IntStream.range(0, 20)
                .mapToObj(i -> {
                    double rating = i % 5 + 1.0;
                    return createRating(user, books.get(i), rating);
                })
                .collect(Collectors.toList());
    }

    private static List<Rating> createRatingsForBookWithUsers(List<User> users, Book book) {
        return IntStream.range(0, 20)
                .mapToObj(i -> {
                    double rating = i % 5 + 1.0;
                    return createRating(users.get(i), book, rating);
                })
                .toList();
    }

    private static Rating createRating(User user, Book book, Double rating) {
        return Rating.builder()
                .user(user)
                .book(book)
                .rating(rating)
                .build();
    }

}