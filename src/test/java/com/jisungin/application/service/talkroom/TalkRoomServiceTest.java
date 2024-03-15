package com.jisungin.application.service.talkroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomResponse;
import com.jisungin.domain.ReadingStatus;
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
        assertThat(response)
                .extracting("id", "content")
                .contains(1L, "토크방");
        assertThat(readingStatuses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("토크방을 생성할 때 참가 조건은 1개 이상이어야 한다.")
    void createTalkRoomWithReadingStatus() {
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
                .isInstanceOf(NullPointerException.class)
                .hasMessage("참가 조건은 1개 이상이어야 합니다.");
    }

    private static User createUser() {
        return User.builder()
                .name("user@gmail.com")
                .profile("image")
                .build();
    }

    private static Book createBook() {
        String[] authors = {"작가"};
        return Book.builder()
                .title("제목")
                .content("내용")
                .authors(authors)
                .isbn("11111")
                .publisher("publisher")
                .dateTime(LocalDateTime.now())
                .url("www")
                .build();
    }
}