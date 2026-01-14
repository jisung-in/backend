package com.jisungin.domain.rating.repository;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.rating.response.RatingGetResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.Rating;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
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

import static com.jisungin.domain.review.RatingOrderType.RATING_ASC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class RatingRepositoryImplTest extends RepositoryTestSupport {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @AfterEach
    void tearDown() {
        ratingRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("리뷰 별점이 낮은 순으로 책을 정렬한다.")
    @Test
    void getRatingsOrderByRatingAsc() {
        User user = userRepository.save(createUser("1"));
        List<Book> books = createBooks();
        List<Rating> ratings = createRatings(user, books);
        bookRepository.saveAll(books);
        ratingRepository.saveAll(ratings);

        //when
        PageResponse<RatingGetResponse> result = ratingRepository.getAllRatingOrderBy(
                user.getId(), RATING_ASC, null, 4, 8);// 1점 4개, 2점 4개 이후에 3점 리뷰 4개가 나와야 함.

        //then
        assertThat(result.getTotalCount()).isEqualTo(20);
        assertThat(result.getQueryResponse().size()).isEqualTo(4);
        assertThat(result.getQueryResponse())
                .extracting("isbn", "title", "image", "rating")
                .containsExactly(
                        tuple("13", "제목13", "bookImage", 3.0),
                        tuple("18", "제목18", "bookImage", 3.0),
                        tuple("3", "제목3", "bookImage", 3.0),
                        tuple("8", "제목8", "bookImage", 3.0)
                );
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