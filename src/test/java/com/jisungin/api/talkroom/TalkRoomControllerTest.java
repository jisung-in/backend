package com.jisungin.api.talkroom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
                .bookId(books.get(0).getId())
                .content("토크방")
                .readingStatus(readingStatus)
                .build();

        // when // then
        mockMvc.perform(post("/v1/talk-room/create")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
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
                .bookId(books.get(0).getId())
                .content("토크방")
                .readingStatus(null)
                .build();

        // when // then
        mockMvc.perform(post("/v1/talk-room/create")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("참가 조건은 1개 이상 체크해야합니다."));
    }

    private static User createUser() {
        return User.builder()
                .name("user@gmail.com")
                .profile("image")
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