package com.jisungin.application.user;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.user.response.UserInfoResponse;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.domain.review.repository.ReviewRepository;
import com.jisungin.domain.reviewlike.repository.ReviewLikeRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

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
    private UserService userService;

    @AfterEach
    void tearDown() {
        reviewLikeRepository.deleteAllInBatch();
        reviewRepository.deleteAllInBatch();
        ratingRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
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
