package com.jisungin.application.service.talkroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.response.PageResponse;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
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
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TalkRoomServiceTest extends ServiceTestSupport {

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
                .bookIsbn(books.get(0).getIsbn())
                .title("토크방")
                .content("내용")
                .readingStatus(readingStatus)
                .build();

        // when
        TalkRoomResponse response = talkRoomService.createTalkRoom(request, user.getId());

        // then
        List<ReadingStatus> readingStatuses = response.getReadingStatuses();
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();
        assertThat(response)
                .extracting("userName", "title", "content")
                .contains("user@gmail.com", "토크방", "내용");
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
                .bookIsbn(books.get(0).getIsbn())
                .title("토크방")
                .content("내용")
                .readingStatus(null)
                .build();

        // when // then
        assertThatThrownBy(() -> talkRoomService.createTalkRoom(request, user.getId()))
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
                .title("토크방 수정")
                .content("내용 수정")
                .readingStatus(readingStatus)
                .build();
        // when
        TalkRoomResponse response = talkRoomService.editTalkRoom(request, user.getId());

        // then
        assertThat(response)
                .extracting("userName", "title", "content")
                .contains("user@gmail.com", "토크방 수정", "내용 수정");
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
                .title(null)
                .content(null)
                .readingStatus(readingStatus)
                .build();
        // when
        TalkRoomResponse response = talkRoomService.editTalkRoom(request, user.getId());

        // then
        assertThat(response)
                .extracting("userName", "title", "content")
                .contains("user@gmail.com", "토크방", "내용");
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
                .title("토크방")
                .content("내용")
                .readingStatus(readingStatus)
                .build();

        // when
        TalkRoomResponse response = talkRoomService.editTalkRoom(request, user.getId());

        // then
        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.findAll();
        assertThat(response)
                .extracting("userName", "title", "content")
                .contains("user@gmail.com", "토크방", "내용");
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
                .title("토론방")
                .content("내용")
                .readingStatus(readingStatus)
                .build();
        // when // then
        assertThatThrownBy(() -> talkRoomService.editTalkRoom(request, userB.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("토크방을 최신순으로 10개의 토크방만 조회 했을 때, 첫 번째 토크방은 19번 토크방이다.")
    void getTalkRooms() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(1)
                .size(10)
                .build();

        // when
        PageResponse<TalkRoomQueryResponse> talkRooms = talkRoomRepository.getTalkRooms(search);

        // then
        assertThat(10L).isEqualTo(talkRooms.getQueryResponse().size());
        assertThat("토론방 19").isEqualTo(talkRooms.getQueryResponse().get(0).getTitle());
        assertThat(2).isEqualTo(talkRooms.getQueryResponse().get(0).getReadingStatuses().size());
    }

    @Test
    @DisplayName("토크방이 총 103개가 생성 됐을 경우 토크방 개수는 총 103개여야 한다.")
    void getTalkRoomsPageTotalCount() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 103)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(1)
                .size(10)
                .build();

        // when
        PageResponse<TalkRoomQueryResponse> talkRooms = talkRoomRepository.getTalkRooms(search);

        // then
        assertThat(103).isEqualTo(talkRooms.getTotalCount());
    }

    @Test
    @DisplayName("토크방 총 11페이지(103개) 중 5페이지를 조회를 조회하면 첫 번째 토크방은 62번 토크방이다.")
    void getTalkRoomsMiddle() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 103)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(5)
                .size(10)
                .build();

        // when
        PageResponse<TalkRoomQueryResponse> talkRooms = talkRoomRepository.getTalkRooms(search);

        // then
        assertThat(talkRooms.getQueryResponse().size()).isEqualTo(10L);
        assertThat(talkRooms.getQueryResponse().get(0).getTitle()).isEqualTo("토론방 62");
        assertThat(talkRooms.getQueryResponse().get(0).getContent()).isEqualTo("내용 62");
    }

    @Test
    @DisplayName("토크방 총 11 페이지(103개) 중 마지막 페이지를 조회하면 첫 번째 토크방은 2번 토크방이다.")
    void getTalkRoomsLast() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 103)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(11)
                .size(10)
                .build();

        // when
        PageResponse<TalkRoomQueryResponse> talkRooms = talkRoomRepository.getTalkRooms(search);

        // then
        assertThat(talkRooms.getQueryResponse().size()).isEqualTo(3);
        assertThat(talkRooms.getQueryResponse().get(0).getTitle()).isEqualTo("토론방 2");
        assertThat(talkRooms.getQueryResponse().get(0).getContent()).isEqualTo("내용 2");
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
                .title("토크방")
                .content("내용")
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
                .imageUrl("www")
                .build();
    }

}