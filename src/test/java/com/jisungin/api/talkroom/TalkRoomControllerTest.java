package com.jisungin.api.talkroom;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TalkRoomControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TalkRoomRepository talkRoomRepository;

    @Autowired
    TalkRoomRoleRepository talkRoomRoleRepository;

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
    void createTalkRoom() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<Book> books = bookRepository.findAll();

        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookIsbn(books.get(0).getIsbn())
                .content("토크방")
                .readingStatus(readingStatus)
                .build();

        // when // then
        mockMvc.perform(post("/v1/talk-rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("토크방을 생성할 때 참가 조건은 1개 이상 체크해야 한다.")
    void createTalkRoomWithEmptyReadingStatus() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<Book> books = bookRepository.findAll();

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookIsbn(books.get(0).getIsbn())
                .content("토크방")
                .readingStatus(null)
                .build();

        // when // then
        mockMvc.perform(post("/v1/talk-rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("참가 조건은 1개 이상 체크해야합니다."));
    }

    @Test
    @DisplayName("토크방을 생성한 유저가 토크방의 제목을 수정한다.")
    void editTalkRoomContent() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();

        createTalkRoomRole(talkRoom);

        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRooms.get(0).getId())
                .content("토크방 수정")
                .readingStatus(readingStatus)
                .build();

        // when // then
        mockMvc.perform(patch("/v1/talk-rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));

    }

    @Test
    @DisplayName("토크방을 생성한 유저가 토크방의 참가 조건을 수정한다.")
    void editTalkRoomReadingStatus() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();

        createTalkRoomRole(talkRoom);

        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");
        readingStatus.add("잠시 멈춤");
        readingStatus.add("중단");

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRooms.get(0).getId())
                .content("토크방")
                .readingStatus(readingStatus)
                .build();
        // when // then
        mockMvc.perform(patch("/v1/talk-rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));

    }

    @Test
    @DisplayName("토크방을 수정할 때 참가 조건은 1개 이상 체크해야 한다.")
    void editTalkRoomWithEmptyReadingStatus() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);
        List<TalkRoom> talkRooms = talkRoomRepository.findAll();

        createTalkRoomRole(talkRoom);

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRooms.get(0).getId())
                .content("토크방")
                .readingStatus(null)
                .build();
        // when // then
        mockMvc.perform(patch("/v1/talk-rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("참가 조건은 1개 이상 체크해야합니다."));
    }

    @Test
    @DisplayName("토크방을 생성한 유저와 토크방을 수정하는 유저가 일치하지 않으면 예외가 발생한다.")
    void editTalkRoomWithUsersMustMatch() throws Exception {
        // given
        User userA = createUser();
        userRepository.save(userA);

        Book book = createBook();
        bookRepository.save(book);

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

        TalkRoom talkRoom = createTalkRoom(book, userB);
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

        // when // then
        mockMvc.perform(patch("/v1/talk-rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("권한이 없는 사용자입니다."));
    }

    @Test
    @DisplayName("유저가 토크방 1페이지를 조회하면 최신순으로 10개의 토크방이 조회된다. 첫 번째 토크방의 이름은 토론방 102이다.")
    void getTalkRooms() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 103)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .content("토론방 " + i)
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

        // when // then
        mockMvc.perform(get("/v1/talk-rooms?page=1&size=10&order=recent")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.talkRoomQueryResponses[0].content").value("토론방 102"));
    }

    @Test
    @DisplayName("사용자가 토크방을 조회 했을 때 페이지를 -1 값을 보내면 첫 번째 페이지가 조회 되어야 한다. 첫 번째 토크방은 토론방 102이다.")
    void getTalkRoomWithMinus() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 103)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .content("토론방 " + i)
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

        // when // then
        mockMvc.perform(get("/v1/talk-rooms?page=-1&size=10&order=recent")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.talkRoomQueryResponses[0].content").value("토론방 102"));
    }

    @Test
    @DisplayName("사용자가 토크방을 조회 했을 때 페이지를 0 값을 보내면 첫 번째 페이지가 조회 되어야 한다. 첫 번째 토크방은 토론방 102이다.")
    void getTalkRoomsWithZero() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 103)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .content("토론방 " + i)
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

        // when // then
        mockMvc.perform(get("/v1/talk-rooms?page=-1&size=10&order=recent")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.talkRoomQueryResponses[0].content").value("토론방 102"));
    }

    @Test
    @DisplayName("토크방이 없을 때 토크방 조회 페이지에 들어갔을 때 에러가 발생하면 안된다.")
    void getTalkRoomsEmpty() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/talk-rooms?page=-1&size=10&order=recent")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
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