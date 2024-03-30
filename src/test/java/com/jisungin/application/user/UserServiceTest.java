package com.jisungin.application.user;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.review.response.RatingFindAllResponse;
import com.jisungin.application.user.request.UserRatingGetAllServiceRequest;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.review.Review;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jisungin.domain.review.RatingOrderType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class UserServiceTest extends ServiceTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;

    @DisplayName("사용자의 모든 리뷰 별점을 오름차 순으로 가져온다.")
    @Test
    void getUserRatings() {
        //given
        User user = createUser("1");
        userRepository.save(user);

        List<Book> books = createBooks();
        List<Review> reviews = createReviews(user, books);
        bookRepository.saveAll(books);
        reviewRepository.saveAll(reviews);

        // 5번째부터 8번째 데이터를 요청, 별점 2.0이 4개가 나와야 함
        UserRatingGetAllServiceRequest request = UserRatingGetAllServiceRequest.builder()
                .page(2)
                .size(4)
                .orderType(RATING_ASC)
                .build();

        //when
        PageResponse<RatingFindAllResponse> result = userService.getUserRatings(user.getId(), request);

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("2", "제목", "image", 2.0),
                        tuple("7", "제목", "image", 2.0),
                        tuple("12", "제목", "image", 2.0),
                        tuple("17", "제목", "image", 2.0)
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