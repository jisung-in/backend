package com.jisungin.application.user;

import static com.jisungin.domain.ReadingStatus.PAUSE;
import static com.jisungin.domain.ReadingStatus.READ;
import static com.jisungin.domain.ReadingStatus.READING;
import static com.jisungin.domain.ReadingStatus.STOP;
import static com.jisungin.domain.ReadingStatus.WANT;
import static com.jisungin.domain.review.RatingOrderType.RATING_ASC;
import static com.jisungin.domain.userlibrary.ReadingStatusOrderType.DICTIONARY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.rating.response.RatingGetResponse;
import com.jisungin.application.review.response.ReviewContentGetAllResponse;
import com.jisungin.application.user.request.ReviewContentGetAllServiceRequest;
import com.jisungin.application.user.request.UserRatingGetAllServiceRequest;
import com.jisungin.application.user.request.UserReadingStatusGetAllServiceRequest;
import com.jisungin.application.user.response.UserInfoResponse;
import com.jisungin.application.userlibrary.response.UserReadingStatusResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.Rating;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.reviewlike.ReviewLike;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.domain.userlibrary.UserLibrary;
import com.jisungin.domain.userlibrary.repository.UserLibraryRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserServiceTest extends ServiceTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private ReviewLikeRepository reviewLikeRepository;

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown() {
        userLibraryRepository.deleteAllInBatch();
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        ratingRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자의 모든 리뷰 별점을 오름차 순으로 가져온다.")
    @Test
    void getUserRatings() {
        //given
        User user = userRepository.save(createUser("1"));
        List<Book> books = bookRepository.saveAll(createBooks());
        List<Rating> ratings = ratingRepository.saveAll(createRatings(user, books));

        // 5번째부터 8번째 데이터를 요청, 별점 2.0이 4개가 나와야 함
        UserRatingGetAllServiceRequest request = UserRatingGetAllServiceRequest.builder()
                .page(2)
                .size(4)
                .orderType(RATING_ASC)
                .build();

        //when
        PageResponse<RatingGetResponse> result = userService.getUserRatings(user.getId(), request);

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse()).hasSize(4)
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("12", "제목12", "bookImage", 2.0),
                        tuple("17", "제목17", "bookImage", 2.0),
                        tuple("2", "제목2", "bookImage", 2.0),
                        tuple("7", "제목7", "bookImage", 2.0)
                );
    }

    @DisplayName("사용자의 모든 리뷰 내용을 별점 오름차 순으로 가져온다.")
    @Test
    void getReviewContents() {
        //given
        User user1 = userRepository.save(createUser("1"));
        User user2 = userRepository.save(createUser("2"));
        List<Book> books = bookRepository.saveAll(createBooks());
        List<Review> reviews = reviewRepository.saveAll(createReviews(user1, books));
        List<Rating> ratings = ratingRepository.saveAll(createRatings(user1, books));
        List<ReviewLike> reviewLikesWithUser1 = reviewLikeRepository.saveAll(createReviewLikes(user1, reviews));
        List<ReviewLike> reviewLikesWithUser2 = reviewLikeRepository.saveAll(createReviewLikes(user2, reviews));

        // 1번째부터 4번째 데이터를 요청, 별점 1.0인 리뷰 내용이 4개 나와야 함
        ReviewContentGetAllServiceRequest request = ReviewContentGetAllServiceRequest.builder()
                .page(1)
                .size(4)
                .orderType(RATING_ASC)
                .build();

        //when
        ReviewContentGetAllResponse result = userService.getReviewContents(user1.getId(), request);

        //then
        assertThat(result.getReviewContents().getTotalCount()).isEqualTo(20);
        assertThat(result.getReviewContents().getQueryResponse()).hasSize(4)
                .extracting(
                        "userImage", "userName", "rating", "content", "isbn", "title", "bookImage")
                .containsExactly(
                        tuple("userImage", "김도형", 1.0, "리뷰 내용1", "1", "제목1", "bookImage"),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용11", "11", "제목11", "bookImage"),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용16", "16", "제목16", "bookImage"),
                        tuple("userImage", "김도형", 1.0, "리뷰 내용6", "6", "제목6", "bookImage")
                );
        assertThat(result.getUserLikes()).hasSize(4);
    }

    @DisplayName("사용자의 독서 상태가 읽고 싶은 책을 가져온다.")
    @Test
    void getReadingStatuses() {
        //given
        User user1 = userRepository.save(createUser("1"));
        User user2 = userRepository.save(createUser("2"));
        List<Book> books = bookRepository.saveAll(createBooks());
        List<Rating> ratings1 = ratingRepository.saveAll(createRatings(user1, books));
        List<UserLibrary> userLibraries = userLibraryRepository.saveAll(createUserLibraries(user1, books));

        // 읽고 싶은 상태인 책을 사전 순으로 정렬하고 1페이지를 가져온다.
        UserReadingStatusGetAllServiceRequest request = UserReadingStatusGetAllServiceRequest.builder()
                .page(1)
                .size(4)
                .orderType(DICTIONARY)
                .readingStatus(WANT)
                .build();

        //when
        PageResponse<UserReadingStatusResponse> result = userService.getUserReadingStatuses(user1.getId(), request);

        //then
        assertThat(result.getTotalCount()).isEqualTo(4);
        assertThat(result.getQueryResponse()).hasSize(4)
                .extracting("bookImage", "bookTitle", "ratingAvg")
                .containsExactly(
                        tuple("bookImage", "제목1", 1.0),
                        tuple("bookImage", "제목11", 1.0),
                        tuple("bookImage", "제목16", 1.0),
                        tuple("bookImage", "제목6", 1.0)
                );
    }


    @DisplayName("해당 사용자 정보를 조회한다.")
    @Test
    void getUserInfo() {
        //given
        User user = userRepository.save(createUser("1"));

        //when
        UserInfoResponse result = userService.getUserInfo(user.getId());

        //then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getUserName()).isEqualTo(user.getName());
        assertThat(result.getUserImage()).isEqualTo(user.getProfileImage());
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

    private static List<UserLibrary> createUserLibraries(User user, List<Book> books) {
        List<UserLibrary> userLibraries = new ArrayList<>();
        List<ReadingStatus> statuses = List.of(WANT, READING, READ, PAUSE, STOP);

        IntStream.rangeClosed(1, 20)
                .forEach(i -> {
                    ReadingStatus readingStatus = statuses.get((i - 1) % statuses.size());
                    UserLibrary userLibrary = createUserLibrary(user, books.get(i - 1), readingStatus);
                    userLibraries.add(userLibrary);
                });

        return userLibraries;
    }

    private static UserLibrary createUserLibrary(User user, Book book, ReadingStatus readingStatus) {
        return UserLibrary.builder()
                .user(user)
                .book(book)
                .status(readingStatus)
                .build();
    }

    private static List<Rating> createRatings(User user, List<Book> books) {
        return IntStream.range(0, 20)
                .mapToObj(i -> {
                    double rating = i % 5 + 1.0;
                    return createRating(user, books.get(i), rating);
                })
                .collect(Collectors.toList());
    }

    private static Rating createRating(User user, Book book, Double rating) {
        return Rating.builder()
                .user(user)
                .book(book)
                .rating(rating)
                .build();
    }

}