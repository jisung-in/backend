package com.jisungin.domain.userlibrary.repository;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.userlibrary.response.UserReadingStatusResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.Rating;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.userlibrary.UserLibrary;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jisungin.domain.ReadingStatus.*;
import static com.jisungin.domain.userlibrary.ReadingStatusOrderType.*;
import static org.assertj.core.api.Assertions.*;

class UserLibraryRepositoryImplTest extends RepositoryTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @AfterEach
    void tearDown() {
        userLibraryRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        ratingRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("유저가 저장한 모든 독서 상태를 가져온다.")
    @Test
    void findAllReadingStatusOrderBy() {
        //given
        User user1 = userRepository.save(createUser("1"));
        User user2 = userRepository.save(createUser("2"));
        List<Book> books = bookRepository.saveAll(createBooks());
        List<Rating> ratings = ratingRepository.saveAll(createRatings(user1, books));
        List<UserLibrary> userLibraries = userLibraryRepository.saveAll(createUserLibraries(user1, books));

        //when
        PageResponse<UserReadingStatusResponse> result = userLibraryRepository.findAllReadingStatusOrderBy(
                user1.getId(), WANT, DICTIONARY, 4, 0);

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

    @DisplayName("도서와 사용자 아이디와 연관된 서재 정보가 존재하는지 확인한다.")
    @Test
    void exitsByUserIdAndBookId() {
        // given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "0000X"));

        // when
        Boolean result = userLibraryRepository.existsByUserIdAndBookId(user.getId(), book.getIsbn());

        // then
        assertThat(result).isFalse();
    }

    @DisplayName("연관된 서재 정보가 존재하면 TRUE를 반환한다.")
    @Test
    void existsByUserIdAndBookIdAlreadyExists() {
        // given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook("도서 제목", "도서 내용", "0000X"));
        UserLibrary userLibrary = userLibraryRepository.save(createUserLibrary(user, book, WANT));


        // when
        Boolean result = userLibraryRepository.existsByUserIdAndBookId(user.getId(), book.getIsbn());

        // then
        assertThat(result).isTrue();
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