package com.jisungin.application.service.talkroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TalkRoomServiceTest {

    @Autowired
    TalkRoomRepository talkRoomRepository;

    @Autowired
    TalkRoomRoleRepository talkRoomRoleRepository;

    @Autowired
    TalkRoomService talkRoomService;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        talkRoomRoleRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("유저가 책A에 대한 토크방을 생성한다.")
    void createTalkRoom() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");

        List<Book> books = bookRepository.findAll();

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookId(books.get(0).getId())
                .content("토크방")
                .readingStatus(readingStatus)
                .build();

        // when
        TalkRoomResponse response = talkRoomService.createTalkRoom(request, user.getName());

        // then
        List<ReadingStatus> readingStatuses = response.getReadingStatuses();
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();
        assertThat(response)
                .extracting("id", "content")
                .contains(talkRooms.get(0).getId(), "토크방");
        assertThat(readingStatuses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("토크방을 생성할 때 참가 조건은 1개 이상이어야 한다.")
    void createTalkRoomWithNotReadingStatus() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<Book> books = bookRepository.findAll();

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookId(books.get(0).getId())
                .content("토크방")
                .readingStatus(null)
                .build();

        // when // then
        assertThatThrownBy(() -> talkRoomService.createTalkRoom(request, user.getName()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("참가 조건은 1개 이상이어야 합니다.");
    }

    @Test
    @DisplayName("토크방을 생성했던 사용자가 토크방의 제목을 수정한다.")
    void editTalkRoom() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);
        List<Book> books = bookRepository.findAll();

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();

        createTalkRoomRole(talkRoom);
        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.findAll();

        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRooms.get(0).getId())
                .content("토크방 수정")
                .readingStatus(readingStatus)
                .build();
        // when
        TalkRoomResponse response = talkRoomService.editTalkRoom(request, "user@gmail.com");

        // then
        assertThat(response)
                .extracting("id", "content")
                .contains(talkRooms.get(0).getId(), "토크방 수정");
        assertThat(talkRoomRoles.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("토크방을 생성한 사용자가 토크방의 제목을 NULL 값으로 업데이트하려고 시도했을 때, 원래의 제목이 유지된다.")
    void editTalkRoomWithNullContent() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);
        List<Book> books = bookRepository.findAll();

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();

        createTalkRoomRole(talkRoom);
        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.findAll();

        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRooms.get(0).getId())
                .content(null)
                .readingStatus(readingStatus)
                .build();
        // when
        TalkRoomResponse response = talkRoomService.editTalkRoom(request, "user@gmail.com");

        // then
        assertThat(response)
                .extracting("id", "content")
                .contains(talkRooms.get(0).getId(), "토크방");
        assertThat(talkRoomRoles.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("토크방을 생성했던 사용자가 토크방의 참가 조건을 수정한다.")
    void editTalkRoomReadingStatus() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);
        List<Book> books = bookRepository.findAll();

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();

        createTalkRoomRole(talkRoom);

        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");
        readingStatus.add("잠시 멈춤");

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRooms.get(0).getId())
                .content("토크방")
                .readingStatus(readingStatus)
                .build();

        // when
        TalkRoomResponse response = talkRoomService.editTalkRoom(request, "user@gmail.com");

        // then
        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.findAll();
        assertThat(response)
                .extracting("id", "content")
                .contains(talkRooms.get(0).getId(), "토크방");
        assertThat(talkRoomRoles.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("토크방을 생성한 유저와 토크방을 수정하는 유저가 일치하지 않으면 예외가 발생한다.")
    void editTalkRoomWithUsersMustMatch() {
        // given
        User userA = createUser();
        userRepository.save(userA);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, userA);
        talkRoomRepository.save(talkRoom);
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();

        createTalkRoomRole(talkRoom);

        User userB = User.builder()
                .name("userB@gmail.com")
                .oauthId(
                        OauthId.builder()
                                .oauthId("oauthId2")
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .profileImage("image")
                .build();
        userRepository.save(userB);

        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");
        readingStatus.add("잠시 멈춤");

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRooms.get(0).getId())
                .content("토크방")
                .readingStatus(readingStatus)
                .build();
        // when // then
        assertThatThrownBy(() -> talkRoomService.editTalkRoom(request, "userB@gmail.com"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    private void createTalkRoomRole(TalkRoom talkRoom) {
        List<String> request = new ArrayList<>();
        request.add("읽는 중");
        request.add("읽음");

        List<ReadingStatus> readingStatus = ReadingStatus.createReadingStatus(request);

        readingStatus.stream().map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .forEach(talkRoomRoleRepository::save);
    }

    private static TalkRoom createTalkRoom(Book book, User user) {
        return TalkRoom.builder()
                .book(book)
                .content("토크방")
                .user(user)
                .build();
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
                .url("www")
                .build();
    }

}