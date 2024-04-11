package com.jisungin.application.userlibrary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.userlibrary.request.UserLibraryCreateServiceRequest;
import com.jisungin.application.userlibrary.response.UserLibraryResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.mylibrary.UserLibrary;
import com.jisungin.domain.mylibrary.repository.UserLibraryRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class UserLibraryServiceTest extends ServiceTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserLibraryRepository userLibraryRepository;

    @Autowired
    private UserLibraryService userLibraryService;

    @BeforeEach
    public void tearDown() {
        userLibraryRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자가 서재 정보를 생성한다.")
    public void createUserLibrary() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        UserLibraryCreateServiceRequest request = UserLibraryCreateServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("want")
                .build();

        // when
        UserLibraryResponse response = userLibraryService.createUserLibrary(request, user.getId());

        // then
        UserLibrary savedUserLibrary = userLibraryRepository.findAll().get(0);

        assertThat(response.getId()).isEqualTo(savedUserLibrary.getId());
        assertThat(response.getStatus()).isEqualTo(ReadingStatus.WANT.getText());
    }

    @Test
    @DisplayName("서재 등록시 사용자 정보가 존재해야 한다.")
    public void createUserLibraryWithoutUser() {
        // given
        Long invalidUserId = -1L;
        Book book = bookRepository.save(createBook());

        UserLibraryCreateServiceRequest request = UserLibraryCreateServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("want")
                .build();

        // when // then
        assertThatThrownBy(() -> userLibraryService.createUserLibrary(request, invalidUserId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 등록 시 책 정보가 존재해야 한다.")
    public void createUserLibraryWithoutBook() {
        // given
        String invalidIsbn = "XXXXXXXXXXX";
        User user = userRepository.save(createUser());

        UserLibraryCreateServiceRequest request = UserLibraryCreateServiceRequest.builder()
                .isbn(invalidIsbn)
                .readingStatus("want")
                .build();

        // when // then
        assertThatThrownBy(() -> userLibraryService.createUserLibrary(request, user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    private static User createUser() {
        return User.builder()
                .name("user@gmail.com")
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
                .authors("작가")
                .isbn("11111")
                .publisher("publisher")
                .dateTime(LocalDateTime.now())
                .imageUrl("www")
                .thumbnail("이미지")
                .build();
    }

}
