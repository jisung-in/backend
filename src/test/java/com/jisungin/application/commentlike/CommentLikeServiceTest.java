package com.jisungin.application.commentlike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroomlike.TalkRoomLikeService;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentlike.CommentLike;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
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

class CommentLikeServiceTest extends ServiceTestSupport {

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

    @Autowired
    TalkRoomLikeService talkRoomLikeService;

    @Autowired
    CommentLikeService commentLikeService;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @AfterEach
    void tearDown() {
        commentLikeRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        talkRoomRoleRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("유저가 의견에 좋아요를 누른다.")
    void likeComment() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        Comment comment = createComment(user, talkRoom);
        commentRepository.save(comment);

        // when
        commentLikeService.likeComment(comment.getId(), user.getId());

        // then
        List<CommentLike> likes = commentLikeRepository.findAll();
        assertThat(1).isEqualTo(likes.size());
    }

    @Test
    @DisplayName("유저가 의견이 없을 때 좋아요를 누를 수 없다.")
    void likeCommentWithEmpty() {
        // given
        User user = createUser();
        userRepository.save(user);

        // when // then
        assertThatThrownBy(() -> commentLikeService.likeComment(1000L, user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("의견을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("로그인을 하지 않으면 좋아요를 누를 수 없다.")
    void likeCommentWithLogin() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        Comment comment = createComment(user, talkRoom);
        commentRepository.save(comment);

        // when // then
        assertThatThrownBy(() -> commentLikeService.likeComment(comment.getId(), 1000L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("이미 좋아요를 눌렀을 때 다시 좋아요를 누를 수 없다.")
    void likeCommentWithReLike() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        Comment comment = createComment(user, talkRoom);
        commentRepository.save(comment);

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
        commentLikeRepository.save(commentLike);
        // when // then
        assertThatThrownBy(() -> commentLikeService.likeComment(comment.getId(), user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 좋아요를 눌렀습니다.");
    }

    @Test
    @DisplayName("유저가 좋아요를 취소한다.")
    void unLikeComment() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        Comment comment = createComment(user, talkRoom);
        commentRepository.save(comment);

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
        commentLikeRepository.save(commentLike);

        // when
        commentLikeService.unLikeComment(comment.getId(), user.getId());

        // then
        List<CommentLike> likes = commentLikeRepository.findAll();
        assertThat(0).isEqualTo(likes.size());
    }

    private static Comment createComment(User user, TalkRoom talkRoom) {
        return Comment.builder()
                .content("의견")
                .user(user)
                .talkRoom(talkRoom)
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