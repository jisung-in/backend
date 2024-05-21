package com.jisungin.application.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import com.jisungin.application.comment.request.CommentEditServiceRequest;
import com.jisungin.application.comment.response.CommentPageResponse;
import com.jisungin.application.comment.response.CommentResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentimage.CommentImage;
import com.jisungin.domain.commentimage.repository.CommentImageRepository;
import com.jisungin.domain.commentlike.CommentLike;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.talkroomimage.repository.TalkRoomImageRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.domain.userlibrary.UserLibrary;
import com.jisungin.domain.userlibrary.repository.UserLibraryRepository;
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
    UserLibraryRepository userLibraryRepository;

    @Autowired
    CommentImageRepository commentImageRepository;

    @AfterEach
    void tearDown() {
        commentImageRepository.deleteAllInBatch();
        commentLikeRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        talkRoomImageRepository.deleteAllInBatch();
        talkRoomRoleRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        userLibraryRepository.deleteAllInBatch();
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

        UserLibrary userLibrary = UserLibrary.builder()
                .user(user)
                .book(book)
                .status(ReadingStatus.READING)
                .build();

        userLibraryRepository.save(userLibrary);

        createTalkRoomRole(talkRoom);

        CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .content("의견 남기기")
                .build();

        // when
        CommentResponse response = commentService.writeComment(request, talkRoom.getId(), user.getId(),
                LocalDateTime.now());

        // then
        assertThat(response)
                .extracting("content", "userName", "imageUrls")
                .contains("의견 남기기", "user@gmail.com", List.of(""));
    }

    @Test
    @DisplayName("의견을 작성할땐 토론방 참가 조건하고 유저의 책 상태가 일치해야한다.")
    void writeCommentWithInvalidStatus() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        UserLibrary userLibrary = UserLibrary.builder()
                .user(user)
                .book(book)
                .status(ReadingStatus.PAUSE)
                .build();

        userLibraryRepository.save(userLibrary);

        createTalkRoomRole(talkRoom);

        CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .content("의견 남기기")
                .build();

        // when
        assertThatThrownBy(
                () -> commentService.writeComment(request, talkRoom.getId(), user.getId(), LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("의견을 쓸 권한이 없습니다.");
    }

    @Test
    @DisplayName("의견을 작성할땐 토론방 참가 조건하고 유저의 책 상태가 일치해야한다. -> Reading Status NULL")
    void writeCommentWithStatusEmpty() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        UserLibrary userLibrary = UserLibrary.builder()
                .user(user)
                .book(book)
                .build();

        userLibraryRepository.save(userLibrary);

        createTalkRoomRole(talkRoom);

        CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .content("의견 남기기")
                .build();

        // when
        assertThatThrownBy(
                () -> commentService.writeComment(request, talkRoom.getId(), user.getId(), LocalDateTime.now()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("의견을 쓸 권한이 없습니다.");
    }

    @Test
    @DisplayName("토론방의 참가조건이 상관없음이면 유저는 책에 대한 상태가 없어도 의견을 작성할 수 있다.")
    void writeCommentWithEmpty() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        TalkRoomRole role = TalkRoomRole.builder()
                .talkRoom(talkRoom)
                .readingStatus(ReadingStatus.NONE)
                .build();

        talkRoomRoleRepository.save(role);

        CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .content("의견 남기기")
                .build();

        // when
        CommentResponse response = commentService.writeComment(request, talkRoom.getId(), user.getId(),
                LocalDateTime.now());

        // then
        assertThat(response.getContent()).isEqualTo("의견 남기기");
    }

    @Test
    @DisplayName("유저가 1번 토크방에 자신의 의견을 작성하고 이미지를 추가한다.")
    void writeCommentWithImages() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        UserLibrary userLibrary = UserLibrary.builder()
                .user(user)
                .book(book)
                .status(ReadingStatus.READING)
                .build();

        userLibraryRepository.save(userLibrary);

        createTalkRoomRole(talkRoom);

        CommentCreateServiceRequest request = CommentCreateServiceRequest.builder()
                .content("의견 남기기")
                .imageUrls(List.of("이미지 URL"))
                .build();

        // when
        CommentResponse response = commentService.writeComment(request, talkRoom.getId(), user.getId(),
                LocalDateTime.now());

        // then
        assertThat(response)
                .extracting("content", "userName", "imageUrls")
                .contains("의견 남기기", "user@gmail.com", List.of("이미지 URL"));
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

        // when
        CommentResponse response = commentService.editComment(comment.getId(), request, user.getId());

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

        // when
        CommentResponse response = commentService.editComment(comment.getId(), request, user.getId());

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

        // when // then
        assertThatThrownBy(() -> commentService.editComment(comment.getId(), request, userB.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("의견을 작성한 유저가 의견과 이미지를 수정한다")
    void editCommentWithImage() {
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

        CommentImage imageUrl = CommentImage.builder()
                .imageUrl("basic Image")
                .comment(comment)
                .build();
        commentImageRepository.save(imageUrl);

        CommentEditServiceRequest request = CommentEditServiceRequest.builder()
                .content("의견 수정")
                .newImage(List.of("new Image"))
                .removeImage(List.of("basic Image"))
                .build();

        // when
        CommentResponse response = commentService.editComment(comment.getId(), request, user.getId());

        // then
        assertThat(response)
                .extracting("content", "userName", "imageUrls")
                .contains("의견 수정", "user@gmail.com", List.of("new Image"));
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

        // when
        commentService.deleteComment(comment.getId(), user.getId());

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

        // when // then
        assertThatThrownBy(() -> commentService.deleteComment(comment.getId(), userB.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("의견을 작성한 유저가 의견과 이미지를 삭제한다.")
    void deleteCommentWithImages() {
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

        CommentImage imageUrl = CommentImage.builder()
                .imageUrl("basic Image")
                .comment(comment)
                .build();
        commentImageRepository.save(imageUrl);

        // when
        commentService.deleteComment(comment.getId(), user.getId());

        // then
        List<Comment> comments = commentRepository.findAll();
        List<CommentImage> images = commentImageRepository.findAll();
        assertThat(0).isEqualTo(comments.size());
        assertThat(0).isEqualTo(images.size());
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

        // when
        CommentPageResponse response = commentService.findAllComments(talkRoom.getId(), user.getId());

        // then
        assertThat(5L).isEqualTo(response.getResponse().getTotalCount());
        assertThat("의견 5").isEqualTo(response.getResponse().getQueryResponse().get(0).getContent());
        assertThat("의견 4").isEqualTo(response.getResponse().getQueryResponse().get(1).getContent());
        assertThat("의견 3").isEqualTo(response.getResponse().getQueryResponse().get(2).getContent());
        assertThat("의견 2").isEqualTo(response.getResponse().getQueryResponse().get(3).getContent());
        assertThat("의견 1").isEqualTo(response.getResponse().getQueryResponse().get(4).getContent());
    }

    @Test
    @DisplayName("의견을 조회할 때 이미지도 같이 조회 된다.")
    void findAllCommentsWithImage() throws Exception {
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
                .toList();

        for (int i = 0; i < 5; i++) {
            commentRepository.save(comments.get(i));
        }

        List<CommentImage> images = IntStream.range(1, 6)
                .mapToObj(i -> CommentImage.builder()
                        .comment(comments.get(i - 1))
                        .imageUrl("이미지 " + i)
                        .build())
                .toList();

        commentImageRepository.saveAll(images);

        // when
        CommentPageResponse response = commentService.findAllComments(talkRoom.getId(), user.getId());

        // then
        assertThat(5L).isEqualTo(response.getResponse().getTotalCount());
        assertThat("이미지 5").isEqualTo(response.getResponse().getQueryResponse().get(0).getCommentImages().get(0));
        assertThat("이미지 4").isEqualTo(response.getResponse().getQueryResponse().get(1).getCommentImages().get(0));
        assertThat("이미지 3").isEqualTo(response.getResponse().getQueryResponse().get(2).getCommentImages().get(0));
        assertThat("이미지 2").isEqualTo(response.getResponse().getQueryResponse().get(3).getCommentImages().get(0));
        assertThat("이미지 1").isEqualTo(response.getResponse().getQueryResponse().get(4).getCommentImages().get(0));
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

        // when
        CommentPageResponse response = commentService.findAllComments(talkRoom.getId(), user.getId());

        // then
        assertThat(comments.get(0).getId()).isEqualTo(response.getUserLikeCommentIds().get(0));
        assertThat(comments.get(1).getId()).isEqualTo(response.getUserLikeCommentIds().get(1));
        assertThat(comments.get(2).getId()).isEqualTo(response.getUserLikeCommentIds().get(2));
        assertThat(comments.get(3).getId()).isEqualTo(response.getUserLikeCommentIds().get(3));
        assertThat(comments.get(4).getId()).isEqualTo(response.getUserLikeCommentIds().get(4));
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