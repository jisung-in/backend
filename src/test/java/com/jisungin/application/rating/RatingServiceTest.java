package com.jisungin.application.rating;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.rating.request.RatingCreateServiceRequest;
import com.jisungin.application.rating.request.RatingUpdateServiceRequest;
import com.jisungin.application.rating.response.RatingGetOneResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.Rating;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
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

class RatingServiceTest extends ServiceTestSupport {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RatingService ratingService;

    @AfterEach
    void tearDown() {
        ratingRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("유저가 책 별점을 추가한다.")
    @Test
    void createRating() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook("제목1", "내용1", "1234"));

        RatingCreateServiceRequest request = RatingCreateServiceRequest.builder()
                .bookIsbn("1234")
                .rating(3.5)
                .build();

        //when
        ratingService.creatingRating(user.getId(), request);

        //then
        List<Rating> result = ratingRepository.findAll();
        assertThat(result).hasSize(1);
        Rating rating = result.get(0);
        assertThat(rating.getRating()).isEqualTo(3.5);
    }

    @DisplayName("중복된 정보로 책 별점을 추가한다.")
    @Test
    void createRatingTwice() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook("제목1", "내용1", "1234"));
        ratingRepository.save(Rating.create(3.5, user, book));

        RatingCreateServiceRequest request = RatingCreateServiceRequest.builder()
                .bookIsbn(book.getIsbn())
                .rating(4.5)
                .build();

        //when //then
        assertThatThrownBy(() -> ratingService.creatingRating(user.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 별점이 존재합니다.");
    }

    @DisplayName("유저가 해당 등록한 별점을 조회한다.")
    @Test
    void getRating() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook("제목1", "내용1", "1234"));
        Rating savadRating = ratingRepository.save(Rating.create(3.5, user, book));

        //when
        RatingGetOneResponse result = ratingService.getRating(user.getId(), book.getIsbn());

        //then
        assertThat(result.getId()).isEqualTo(savadRating.getId());
        assertThat(result.getRating()).isEqualTo(savadRating.getRating());
        assertThat(result.getIsbn()).isEqualTo(savadRating.getBook().getIsbn());
    }

    @DisplayName("유저가 해당 등록하지 않은 책의 별점을 조회한다.")
    @Test
    void getRatingWithEmpty() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook("제목1", "내용1", "1234"));

        //when
        RatingGetOneResponse result = ratingService.getRating(user.getId(), book.getIsbn());

        //then
        assertThat(result.getId()).isNull();
        assertThat(result.getRating()).isNull();
        assertThat(result.getIsbn()).isEqualTo(book.getIsbn());
    }

    @DisplayName("유저가 별점을 수정한다.")
    @Test
    void updateRating() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook("제목1", "내용1", "1234"));
        Rating savadRating = ratingRepository.save(Rating.create(3.5, user, book));

        RatingUpdateServiceRequest request = RatingUpdateServiceRequest.builder()
                .bookIsbn("1234")
                .rating(4.5)
                .build();

        //when
        ratingService.updateRating(user.getId(), savadRating.getId(), request);

        //then
        List<Rating> result = ratingRepository.findAll();
        assertThat(result).hasSize(1);
        Rating rating = result.get(0);
        assertThat(rating.getRating()).isEqualTo(4.5);
    }

    @DisplayName("유저가 별점을 삭제한다.")
    @Test
    void deleteRating() {
        //given
        User user = userRepository.save(createUser("1"));
        Book book = bookRepository.save(createBook("제목1", "내용1", "1234"));
        Rating savadRating = ratingRepository.save(Rating.create(3.5, user, book));

        //when
        ratingService.deleteRating(user.getId(), savadRating.getId());

        //then
        List<Rating> result = ratingRepository.findAll();
        assertThat(result).isEmpty();
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

}