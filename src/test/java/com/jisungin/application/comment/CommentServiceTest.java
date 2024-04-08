package com.jisungin.application.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.PageResponse;
import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import com.jisungin.application.comment.request.CommentEditServiceRequest;
import com.jisungin.application.comment.response.CommentQueryResponse;
import com.jisungin.application.comment.response.CommentResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentlike.CommentLike;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.talkroomimage.repository.TalkRoomImageRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class CommentServiceTest extends ServiceTestSupport {

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

    @Autowired
    TalkRoomImageRepository talkRoomImageRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @Autowired
    AuthContext authContext;

    @AfterEach
    void tearDown() {
        commentLikeRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        talkRoomImageRepository.deleteAllInBatch();
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

        authContext.setUserId(user.getId());

        // when
        CommentResponse response = commentService.writeComment(request, talkRoom.getId(), authContext);

        // then
        assertThat(response)
                .extracting("content", "userName")
                .contains("의견 남기기", "user@gmail.com");
    }

    @Test
    @DisplayName("의견을 작성한 유저가 의견을 수정한다.")
    void editComment() {
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

        CommentEditServiceRequest request = CommentEditServiceRequest.builder()
                .content("의견 수정")
                .build();

        authContext.setUserId(user.getId());

        // when
        CommentResponse response = commentService.editComment(comment.getId(), request, authContext);

        // then
        assertThat(response)
                .extracting("content", "userName")
                .contains("의견 수정", "user@gmail.com");
    }

    @Test
    @DisplayName("의견을 작성한 유저가 의견을 NULL로 보내면 원래 작성 했던 의견이 반영된다.")
    void editCommentWithNullContent() {
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

        CommentEditServiceRequest request = CommentEditServiceRequest.builder()
                .build();

        authContext.setUserId(user.getId());

        // when
        CommentResponse response = commentService.editComment(comment.getId(), request, authContext);

        // then
        assertThat(response)
                .extracting("content", "userName")
                .contains("의견", "user@gmail.com");
    }

    @Test
    @DisplayName("의견을 작성한 유저(userA)가 아닌 다른 유저(userB)가 의견에 대해 수정을 할 수 없다.")
    void editCommentWithUserB() {
        // given
        User userA = createUser();
        userRepository.save(userA);

        User userB = User.builder()
                .name("userB@gmail.com")
                .profileImage("image")
                .oauthId(
                        OauthId.builder()
                                .oauthId("oauthId2")
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .build();
        userRepository.save(userB);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, userA);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        Comment comment = createComment(userA, talkRoom);
        commentRepository.save(comment);

        CommentEditServiceRequest request = CommentEditServiceRequest.builder()
                .content("의견 수정")
                .build();

        authContext.setUserId(userB.getId());

        // when // then
        assertThatThrownBy(() -> commentService.editComment(comment.getId(), request, authContext))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("의견을 작성한 유저가 의견을 삭제한다.")
    void deleteComment() {
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

        authContext.setUserId(user.getId());

        // when
        commentService.deleteComment(comment.getId(), authContext);

        // then
        List<Comment> comments = commentRepository.findAll();
        assertThat(0).isEqualTo(comments.size());
    }

    @Test
    @DisplayName("의견을 작성한 유저(userA)가 아닌 다른 유저(userB)가 의견을 삭제할 수 없다.")
    void deleteCommentWithUserB() {
        // given
        User userA = createUser();
        userRepository.save(userA);

        User userB = User.builder()
                .name("userB@gmail.com")
                .profileImage("image")
                .oauthId(
                        OauthId.builder()
                                .oauthId("oauthId2")
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .build();
        userRepository.save(userB);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, userA);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        Comment comment = createComment(userA, talkRoom);
        commentRepository.save(comment);

        authContext.setUserId(userB.getId());

        // when // then
        assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), authContext))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("의견을 조회한다.")
    void findAllComments() throws Exception {
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

        AuthContext authContext = new AuthContext();
        // when
        PageResponse<CommentQueryResponse> response = commentService.findAllComments(talkRoom.getId(), authContext);

        // then
        assertThat(5L).isEqualTo(response.getTotalCount());
        assertThat("의견 5").isEqualTo(response.getQueryResponse().get(0).getContent());
        assertThat("의견 4").isEqualTo(response.getQueryResponse().get(1).getContent());
        assertThat("의견 3").isEqualTo(response.getQueryResponse().get(2).getContent());
        assertThat("의견 2").isEqualTo(response.getQueryResponse().get(3).getContent());
        assertThat("의견 1").isEqualTo(response.getQueryResponse().get(4).getContent());
    }

    @Test
    @DisplayName("유저가 로그인을 한 상태에서 의견을 조회하면 본인이 좋아요한 의견을 확인할 수 있다.")
    void findAllCommentsWithLike() throws Exception {
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

        CommentLike.builder().build();
        List<CommentLike> likes = IntStream.range(0, 5).mapToObj(i -> CommentLike.builder()
                .user(user)
                .comment(comments.get(i))
                .build()).collect(Collectors.toList());

        commentLikeRepository.saveAll(likes);

        authContext.setUserId(user.getId());

        // when
        PageResponse<CommentQueryResponse> response = commentService.findAllComments(talkRoom.getId(), authContext);

        // then
//        assertThat(comments.get(0).getId()).isEqualTo(response.getLikeContents().get(0));
//        assertThat(comments.get(1).getId()).isEqualTo(response.getLikeContents().get(1));
//        assertThat(comments.get(2).getId()).isEqualTo(response.getLikeContents().get(2));
//        assertThat(comments.get(3).getId()).isEqualTo(response.getLikeContents().get(3));
//        assertThat(comments.get(4).getId()).isEqualTo(response.getLikeContents().get(4));
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