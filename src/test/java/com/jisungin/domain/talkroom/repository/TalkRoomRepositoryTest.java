package com.jisungin.domain.talkroom.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.OrderType;
import com.jisungin.application.PageResponse;
import com.jisungin.application.SearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
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
import com.jisungin.domain.talkroomlike.TalkRoomLike;
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

class TalkRoomRepositoryTest extends RepositoryTestSupport {

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
    @DisplayName("querydsl 페이징 조회 테스트")
    void pageTest() {
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

        SearchServiceRequest search = SearchServiceRequest.builder()
                .page(1)
                .size(10)
                .orderType(OrderType.RECENT)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> talkRooms = talkRoomRepository.findAllTalkRoom(search);

        // then
        assertThat(10L).isEqualTo(talkRooms.getQueryResponse().size());
        assertThat("토론방 19").isEqualTo(talkRooms.getQueryResponse().get(0).getTitle());
        assertThat("내용 19").isEqualTo(talkRooms.getQueryResponse().get(0).getContent());
        assertThat(2).isEqualTo(talkRooms.getQueryResponse().get(0).getReadingStatuses().size());
        assertThat(20).isEqualTo(talkRooms.getTotalCount());
    }

    @Test
    @DisplayName("querydsl 단건 조회 토크방 의견 조회 테스트")
    void talkRoomFindOneComment() {
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
        TalkRoomFindOneResponse findOneTalkRoom = talkRoomRepository.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat("토론방").isEqualTo(findOneTalkRoom.getTitle());
        assertThat(2).isEqualTo(findOneTalkRoom.getReadingStatuses().size());
        assertThat("의견 남기기").isEqualTo(findOneTalkRoom.getComments().get(0).getContent());
        assertThat("user@gmail.com").isEqualTo(findOneTalkRoom.getComments().get(0).getUserName());
    }

    @Test
    @DisplayName("querydsl 좋아요 총 개수 조회")
    void likeTalkRoomFindCount() {
        // given
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> User.builder()
                        .name("user@gmail.com " + i)
                        .profileImage("image")
                        .oauthId(
                                OauthId.builder()
                                        .oauthId("oauthId " + i)
                                        .oauthType(OauthType.KAKAO)
                                        .build()
                        )
                        .build()).toList();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes1 = IntStream.range(0, 5).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(0))
                        .build())
                .toList();

        List<TalkRoomLike> likes2 = IntStream.range(5, 10).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(i))
                        .build())
                .toList();

        List<TalkRoomLike> likes = new ArrayList<>();
        likes.addAll(likes1);
        likes.addAll(likes2);

        talkRoomLikeRepository.saveAll(likes);

        SearchServiceRequest search = SearchServiceRequest.builder()
                .page(2)
                .size(10)
                .orderType(OrderType.RECENT)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> response = talkRoomRepository.findAllTalkRoom(search);

        // then
        assertThat(5L).isEqualTo(response.getQueryResponse().get(9).getLikeCount());
    }

    @Test
    @DisplayName("querydsl 토크방 단건 조회 시 좋아요 개수 표시 테스트")
    void findOneTalkRoomWithLikeCount() {
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> User.builder()
                        .name("user@gmail.com " + i)
                        .profileImage("image")
                        .oauthId(
                                OauthId.builder()
                                        .oauthId("oauthId " + i)
                                        .oauthType(OauthType.KAKAO)
                                        .build()
                        )
                        .build()).toList();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes1 = IntStream.range(0, 5).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(0))
                        .build())
                .toList();

        List<TalkRoomLike> likes2 = IntStream.range(5, 10).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(i))
                        .build())
                .toList();

        List<TalkRoomLike> likes = new ArrayList<>();
        likes.addAll(likes1);
        likes.addAll(likes2);

        talkRoomLikeRepository.saveAll(likes);

        // when
        TalkRoomFindOneResponse response = talkRoomRepository.findOneTalkRoom(talkRoom.get(0).getId());

        // then
        assertThat(5L).isEqualTo(response.getLikeCount());
    }

    @Test
    @DisplayName("querydsl 토크방 단건 조회 시 의견 개수 표시 테스트")
    void findOneTalkRoomWithCommentCount() {
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> User.builder()
                        .name("user@gmail.com " + i)
                        .profileImage("image")
                        .oauthId(
                                OauthId.builder()
                                        .oauthId("oauthId " + i)
                                        .oauthType(OauthType.KAKAO)
                                        .build()
                        )
                        .build()).toList();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        List<Comment> comments = IntStream.range(0, 5)
                .mapToObj(i -> Comment.builder()
                        .talkRoom(talkRoom.get(0))
                        .user(users.get(0))
                        .content("의견 " + i)
                        .build())
                .collect(Collectors.toList());

        commentRepository.saveAll(comments);

        // when
        TalkRoomFindOneResponse response = talkRoomRepository.findOneTalkRoom(talkRoom.get(0).getId());

        // then
        assertThat(5L).isEqualTo(response.getCommentCount());
    }

    @Test
    @DisplayName("querydsl 토크방 단건 조회 시 좋아요 누른 userId 반환")
    void findOneTalkRoomWithUserId() {
        // given
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> User.builder()
                        .name("user@gmail.com " + i)
                        .profileImage("image")
                        .oauthId(
                                OauthId.builder()
                                        .oauthId("oauthId " + i)
                                        .oauthType(OauthType.KAKAO)
                                        .build()
                        )
                        .build()).toList();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes = IntStream.range(0, 5).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(0))
                        .build())
                .toList();

        talkRoomLikeRepository.saveAll(likes);

        // when
        TalkRoomFindOneResponse response = talkRoomRepository.findOneTalkRoom(talkRoom.get(0).getId());

        // then
        assertThat(users.get(0).getId()).isEqualTo(response.getUserIds().get(0).getUserId());
        assertThat(users.get(1).getId()).isEqualTo(response.getUserIds().get(1).getUserId());
        assertThat(users.get(2).getId()).isEqualTo(response.getUserIds().get(2).getUserId());
        assertThat(users.get(3).getId()).isEqualTo(response.getUserIds().get(3).getUserId());
        assertThat(users.get(4).getId()).isEqualTo(response.getUserIds().get(4).getUserId());
    }

    @Test
    @DisplayName("querydsl 좋아요순 정렬 조회")
    void findAllTalkRoomWithOrderLike() {
        // given
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> User.builder()
                        .name("user@gmail.com " + i)
                        .profileImage("image")
                        .oauthId(
                                OauthId.builder()
                                        .oauthId("oauthId " + i)
                                        .oauthType(OauthType.KAKAO)
                                        .build()
                        )
                        .build()).toList();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes1 = IntStream.range(0, 10).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(0))
                        .build())
                .toList();

        List<TalkRoomLike> likes2 = IntStream.range(0, 9).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(1))
                        .build())
                .toList();

        List<TalkRoomLike> likes = new ArrayList<>();
        likes.addAll(likes1);
        likes.addAll(likes2);

        talkRoomLikeRepository.saveAll(likes);

        SearchServiceRequest search = SearchServiceRequest.builder()
                .page(1)
                .size(10)
                .orderType(OrderType.RECOMMEND)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> response = talkRoomRepository.findAllTalkRoom(search);

        // then
        assertThat(10L).isEqualTo(response.getQueryResponse().get(0).getLikeCount());
    }

    @Test
    @DisplayName("querydsl 토크방 제목 검색")
    void findAllTalkRoomWithSearch() {
        // given
        List<User> users = IntStream.range(0, 10)
                .mapToObj(i -> User.builder()
                        .name("user@gmail.com " + i)
                        .profileImage("image")
                        .oauthId(
                                OauthId.builder()
                                        .oauthId("oauthId " + i)
                                        .oauthType(OauthType.KAKAO)
                                        .build()
                        )
                        .build()).toList();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = IntStream.range(0, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();

        TalkRoom talkRoom1 = TalkRoom.builder()
                .user(users.get(0))
                .book(book)
                .title("검색어")
                .content("내용")
                .build();

        TalkRoom talkRoom2 = TalkRoom.builder()
                .user(users.get(0))
                .book(book)
                .title("아무내용 검색어 아무내용")
                .content("내용")
                .build();

        TalkRoom talkRoom3 = TalkRoom.builder()
                .user(users.get(0))
                .book(book)
                .title("아무내용 아무내용 검색어")
                .content("내용")
                .build();

        talkRoomRepository.save(talkRoom1);
        talkRoomRepository.save(talkRoom2);
        talkRoomRepository.save(talkRoom3);

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes1 = IntStream.range(0, 10).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(0))
                        .build())
                .toList();

        List<TalkRoomLike> likes2 = IntStream.range(0, 9).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoom.get(1))
                        .build())
                .toList();

        List<TalkRoomLike> likes = new ArrayList<>();
        likes.addAll(likes1);
        likes.addAll(likes2);

        talkRoomLikeRepository.saveAll(likes);

        SearchServiceRequest search = SearchServiceRequest.builder()
                .page(1)
                .size(10)
                .search("검색어")
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> response = talkRoomRepository.findAllTalkRoom(search);

        // then
        assertThat(talkRoom1.getTitle()).isEqualTo(response.getQueryResponse().get(0).getTitle());
        assertThat(talkRoom2.getTitle()).isEqualTo(response.getQueryResponse().get(1).getTitle());
        assertThat(talkRoom3.getTitle()).isEqualTo(response.getQueryResponse().get(2).getTitle());
    }

    @Test
    @DisplayName("querydsl 단건 조회 토크방 의견 좋아요 표시 테스트")
    void commentLikeCountTest() {
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

        CommentLike commentLike = CommentLike.builder()
                .comment(comment)
                .user(user)
                .build();
        commentLikeRepository.save(commentLike);

        // when
        TalkRoomFindOneResponse findOneTalkRoom = talkRoomRepository.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat("토론방").isEqualTo(findOneTalkRoom.getTitle());
        assertThat(2).isEqualTo(findOneTalkRoom.getReadingStatuses().size());
        assertThat("의견 남기기").isEqualTo(findOneTalkRoom.getComments().get(0).getContent());
        assertThat("user@gmail.com").isEqualTo(findOneTalkRoom.getComments().get(0).getUserName());
        assertThat(1L).isEqualTo(findOneTalkRoom.getComments().get(0).getCommentLikeCount());
        assertThat(user.getId()).isEqualTo(findOneTalkRoom.getComments().get(0).getUserIds().get(0).getUserId());
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