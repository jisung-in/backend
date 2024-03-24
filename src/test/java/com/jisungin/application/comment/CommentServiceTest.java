package com.jisungin.application.comment;

import static org.assertj.core.api.Assertions.assertThat;

import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import com.jisungin.application.comment.response.CommentResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.repository.CommentRepository;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommentServiceTest {

    @Autowired
    TalkRoomRepository talkRoomRepository;

    @Autowired
    TalkRoomRoleRepository talkRoomRoleRepository;

    @Autowired
    CommentService commentService;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        talkRoomRoleRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("유저가 1번 토크방에 자신의 의견 작성한다.")
    void writeComment() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .content("의견 남기기")
                .build();
        // when
        CommentResponse response = commentService.writeComment(request, talkRoom.getId(), user.getId());

        // then
        assertThat(response)
                .extracting("content", "userName")
                .contains("의견 남기기", "user@gmail.com");
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