package com.jisungin.application.userlibrary;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.userlibrary.request.UserLibraryCreateServiceRequest;
import com.jisungin.application.userlibrary.request.UserLibraryEditServiceRequest;
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
import java.util.List;
import java.util.Optional;
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
    @DisplayName("사용자가 서재 정보를 조회한다.")
    public void getUserLibrary() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        UserLibrary userLibrary = userLibraryRepository.save(create(user, book));

        // when
        UserLibraryResponse response = userLibraryService.getUserLibrary(user.getId(), book.getIsbn());

        // then
        assertThat(response.getId()).isEqualTo(userLibrary.getId());
        assertThat(response.getStatus()).isEqualTo(userLibrary.getStatus().getText());
        assertThat(response.getHasReadingStatus()).isTrue();
    }

    @Test
    @DisplayName("비로그인으로 서재 정보 조회시 빈 응답을 받는다.")
    public void getUserLibraryForUnAuthenticatedUser() {
        // given
        String bookIsbn = "0000X";

        // when
        UserLibraryResponse response = userLibraryService.getUserLibrary(null, bookIsbn);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getStatus()).isNull();
        assertThat(response.getHasReadingStatus()).isFalse();
    }

    @Test
    @DisplayName("서재 정보 조회 시 isbn이 없는 경우 빈 응답을 받는다.")
    public void getUserLibraryWithNonIsbn() {
        // given
        User user = userRepository.save(createUser());

        // when
        UserLibraryResponse response = userLibraryService.getUserLibrary(user.getId(), null);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getHasReadingStatus()).isFalse();
    }

    @Test
    @DisplayName("서재 정보 조회 시 사용자 정보가 존재해야 한다.")
    public void getUserLibraryWithoutUser() {
        // given
        Long invalidUserId = -1L;
        Book book = bookRepository.save(createBook());

        // when // then
        assertThatThrownBy(() -> userLibraryService.getUserLibrary(invalidUserId, book.getIsbn()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 조회 시 책 정보가 존재해야 한다.")
    public void getUserLibraryWithoutBook() {
        // given
        String invalidIsbn = "0000X";
        User user = userRepository.save(createUser());

        // when // then
        assertThatThrownBy(() -> userLibraryService.getUserLibrary(user.getId(), invalidIsbn))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 조회 시 서재 정보가 없을 시 빈 응답을 받는다.")
    public void getUserLibraryWhenLibraryEmpty() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        // when
        UserLibraryResponse response = userLibraryService.getUserLibrary(user.getId(), book.getIsbn());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getStatus()).isNull();
        assertThat(response.getHasReadingStatus()).isFalse();
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

    @Test
    @DisplayName("서재 정보를 수정한다.")
    public void editUserLibrary() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        UserLibrary userLibrary = userLibraryRepository.save(create(user, book));

        UserLibraryEditServiceRequest request = UserLibraryEditServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("read")
                .build();

        // when
        userLibraryService.editUserLibrary(userLibrary.getId(), user.getId(), request);

        // then
        Optional<UserLibrary> savedLibrary = userLibraryRepository.findById(userLibrary.getId());

        assertThat(savedLibrary).isNotEmpty();
        assertThat(savedLibrary.get().getStatus()).isEqualTo(ReadingStatus.READ);
    }

    @Test
    @DisplayName("서재 정보 수정 시 사용자 정보가 존재해야 한다.")
    public void editUserLibraryWithoutUser() {
        // given
        Long userLibraryId = 1L;
        Long userId = 1L;
        Book book = bookRepository.save(createBook());

        UserLibraryEditServiceRequest request = UserLibraryEditServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> userLibraryService.editUserLibrary(userLibraryId, userId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 수정 시 책 정보가 존재해야 한다.")
    public void editUserLibraryWithoutBook() {
        // given
        Long userLibraryId = 1L;
        String bookIsbn = "0000X";
        User user = userRepository.save(createUser());

        UserLibraryEditServiceRequest request = UserLibraryEditServiceRequest.builder()
                .isbn(bookIsbn)
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> userLibraryService.editUserLibrary(userLibraryId, user.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 수정 시 서재 정보가 존재해야 한다.")
    public void editUserLibraryWithoutUserLibrary() {
        // given
        Long userLibraryId = 1L;
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        UserLibraryEditServiceRequest request = UserLibraryEditServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> userLibraryService.editUserLibrary(userLibraryId, user.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("서재 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 수정 시 서재 정보와 사용자 정보는 일치해야 한다.")
    public void editUserLibraryInvalidUser() {
        // given
        User user = userRepository.save(createUser());
        User anotherUser = userRepository.save(createAnotherUser());

        Book book = bookRepository.save(createBook());

        UserLibrary userLibrary = userLibraryRepository.save(create(user, book));

        UserLibraryEditServiceRequest request = UserLibraryEditServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> userLibraryService.editUserLibrary(userLibrary.getId(), anotherUser.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("서재 정보 수정 시 서재 정보와 도서 정보는 일치해야 한다.")
    public void editUserLibraryInvalidBook() {
        // given
        User user = userRepository.save(createUser());

        Book book = bookRepository.save(createBookWithIsbn("00001"));
        Book anotherBook = bookRepository.save(createBookWithIsbn("00002"));

        UserLibrary userLibrary = userLibraryRepository.save(create(user, book));

        UserLibraryEditServiceRequest request = UserLibraryEditServiceRequest.builder()
                .isbn(anotherBook.getIsbn())
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> userLibraryService.editUserLibrary(userLibrary.getId(), user.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("올바르지 않은 책 정보 입니다.");
    }

    @Test
    @DisplayName("서재 정보를 삭제한다.")
    public void deleteUserLibrary() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        UserLibrary userLibrary = userLibraryRepository.save(create(user, book));

        // when
        userLibraryService.deleteUserLibrary(userLibrary.getId(), user.getId(), book.getIsbn());

        // then
        List<UserLibrary> response = userLibraryRepository.findAll();

        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("서재 정보 삭제 시 사용자 정보가 존재해야 한다.")
    public void deleteUserLibraryWithoutUser() {
        // given
        Long userLibraryId = 1L;
        Long userId = 1L;
        Book book = bookRepository.save(createBook());

        // when // then
        assertThatThrownBy(() -> userLibraryService.deleteUserLibrary(userLibraryId, userId, book.getIsbn()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 삭제 시 책 정보가 존재해야 한다.")
    public void deleteUserLibraryWithoutBook() {
        // given
        Long userLibraryId = 1L;
        String bookIsbn = "0000X";
        User user = userRepository.save(createUser());

        // when // then
        assertThatThrownBy(() -> userLibraryService.deleteUserLibrary(userLibraryId, user.getId(), bookIsbn))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 삭제 시 서재 정보가 존재해야 한다.")
    public void deleteUserLibraryWithoutUserLibrary() {
        // given
        Long userLibraryId = 1L;
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        // when // then
        assertThatThrownBy(() -> userLibraryService.deleteUserLibrary(userLibraryId, user.getId(), book.getIsbn()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("서재 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 삭제 시 서재 정보와 사용자 정보는 일치해야 한다.")
    public void deleteUserLibraryInvalidUser() {
        // given
        User user = userRepository.save(createUser());
        User anotherUser = userRepository.save(createAnotherUser());

        Book book = bookRepository.save(createBook());

        UserLibrary userLibrary = userLibraryRepository.save(create(user, book));

        // when // then
        assertThatThrownBy(
                () -> userLibraryService.deleteUserLibrary(userLibrary.getId(), anotherUser.getId(), book.getIsbn()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("서재 정보 삭제 시 서재 정보와 도서 정보는 일치해야 한다.")
    public void deleteUserLibraryInvalidBook() {
        // given
        User user = userRepository.save(createUser());

        Book book = bookRepository.save(createBookWithIsbn("00001"));
        Book anotherBook = bookRepository.save(createBookWithIsbn("00002"));

        UserLibrary userLibrary = userLibraryRepository.save(create(user, book));

        // when // then
        assertThatThrownBy(
                () -> userLibraryService.deleteUserLibrary(userLibrary.getId(), user.getId(), anotherBook.getIsbn()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("올바르지 않은 책 정보 입니다.");
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

    private static User createAnotherUser() {
        return User.builder()
                .name("another@gmail.com")
                .profileImage("image")
                .oauthId(
                        OauthId.builder()
                                .oauthId("anotherOauthId")
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

    private static Book createBookWithIsbn(String isbn) {
        return Book.builder()
                .title("제목")
                .content("내용")
                .authors("작가")
                .isbn(isbn)
                .publisher("publisher")
                .dateTime(LocalDateTime.now())
                .imageUrl("www")
                .thumbnail("이미지")
                .build();
    }

    public static UserLibrary create(User user, Book book) {
        return UserLibrary.builder()
                .user(user)
                .book(book)
                .status(ReadingStatus.WANT)
                .build();
    }

}
