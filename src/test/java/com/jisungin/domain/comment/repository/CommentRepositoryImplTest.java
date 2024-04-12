package com.jisungin.domain.comment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.comment.response.CommentQueryResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentRepositoryImplTest extends RepositoryTestSupport {

    @Autowired
    TalkRoomRepository talkRoomRepository;

    @Autowired
    TalkRoomRoleRepository talkRoomRoleRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TalkRoomLikeRepository talkRoomLikeRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @AfterEach
    void tearDown() {
        commentLikeRepository.deleteAllInBatch();
        talkRoomLikeRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        talkRoomRoleRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("querydsl 의견 조회 테스트")
    void findAllComment() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        Comment comment = createComment(talkRoom, user);
        commentRepository.save(comment);

        // when
        List<CommentQueryResponse> response = commentRepository.findAllComments(talkRoom.getId());

        // then
        assertThat("의견 남기기").isEqualTo(response.get(0).getContent());
    }

    @Test
    @DisplayName("querydsl 의견 여러개 조회 테스트")
    void findAllCommentsList() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        List<Comment> comments = IntStream.range(1, 6)
                .mapToObj(i -> Comment.builder()
                        .talkRoom(talkRoom)
                        .user(user)
                        .content("의견 " + i)
                        .build())
                .collect(Collectors.toList());

        for (int i = 0; i < 5; i++) {
            commentRepository.save(comments.get(i));
        }

        // when
        List<CommentQueryResponse> response = commentRepository.findAllComments(talkRoom.getId());

        // then
        assertThat("의견 5").isEqualTo(response.get(0).getContent());
        assertThat("의견 4").isEqualTo(response.get(1).getContent());
        assertThat("의견 3").isEqualTo(response.get(2).getContent());
        assertThat("의견 2").isEqualTo(response.get(3).getContent());
        assertThat("의견 1").isEqualTo(response.get(4).getContent());
    }

    private static Comment createComment(TalkRoom talkRoom, User user) {
        return Comment.builder()
                .talkRoom(talkRoom)
                .user(user)
                .content("의견 남기기")
                .build();
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
                .title("토론방")
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