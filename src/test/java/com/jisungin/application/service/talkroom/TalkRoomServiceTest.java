package com.jisungin.application.service.talkroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.OrderType;
import com.jisungin.application.PageResponse;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomResponse;
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
import com.jisungin.domain.talkroomlike.TalkRoomLike;
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
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

        List<TalkRoom> talkRoom = listTalkRooms(20, user, book);

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(1)
                .size(10)
                .orderType(OrderType.RECENT)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> talkRooms = talkRoomRepository.findAllTalkRoom(search);

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

        List<TalkRoom> talkRoom = listTalkRooms(103, user, book);

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(1)
                .size(10)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> talkRooms = talkRoomRepository.findAllTalkRoom(search);

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

        List<TalkRoom> talkRoom = listTalkRooms(103, user, book);

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(5)
                .size(10)
                .orderType(OrderType.RECENT)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> talkRooms = talkRoomRepository.findAllTalkRoom(search);

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

        List<TalkRoom> talkRoom = listTalkRooms(103, user, book);

        talkRoomRepository.saveAll(talkRoom);

        for (TalkRoom t : talkRoom) {
            createTalkRoomRole(t);
        }

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(11)
                .size(10)
                .orderType(OrderType.RECENT)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> talkRooms = talkRoomRepository.findAllTalkRoom(search);

        // then
        assertThat(talkRooms.getQueryResponse().size()).isEqualTo(3);
        assertThat(talkRooms.getQueryResponse().get(0).getTitle()).isEqualTo("토론방 2");
        assertThat(talkRooms.getQueryResponse().get(0).getContent()).isEqualTo("내용 2");
    }

    @Test
    @DisplayName("토크방을 단건 조회 한다.")
    void findOneTalkRoom() {
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
        TalkRoomFindOneResponse findOneTalkRoomResponse = talkRoomService.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat("토크방").isEqualTo(findOneTalkRoomResponse.getTitle());
        assertThat(2).isEqualTo(findOneTalkRoomResponse.getReadingStatuses().size());
        assertThat("의견 남기기").isEqualTo(findOneTalkRoomResponse.getComments().get(0).getContent());
        assertThat("user@gmail.com").isEqualTo(findOneTalkRoomResponse.getComments().get(0).getUserName());
    }

    @Test
    @DisplayName("토크방을 단건 조회 했을 때 의견이 담기지 않았아도 조회는 정상적으로 되어야 한다.")
    void findOneTalkRoomWithCommentNull() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        // when
        TalkRoomFindOneResponse findOneTalkRoomResponse = talkRoomService.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat("토크방").isEqualTo(findOneTalkRoomResponse.getTitle());
        assertThat(2).isEqualTo(findOneTalkRoomResponse.getReadingStatuses().size());
    }

    @Test
    @DisplayName("토크방을 단건 조회 했을 때 의견이 여러 개 달려있을 때 의견을 전부 보여줘야 한다.")
    void findOneTalkRoomWithFindAllComment() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        List<Comment> comments = IntStream.range(0, 20)
                .mapToObj(i -> Comment.builder()
                        .talkRoom(talkRoom)
                        .user(user)
                        .content("의견 " + i)
                        .build())
                .toList();

        commentRepository.saveAll(comments);
        // when
        TalkRoomFindOneResponse findOneTalkRoomResponse = talkRoomService.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat("토크방").isEqualTo(findOneTalkRoomResponse.getTitle());
        assertThat(2).isEqualTo(findOneTalkRoomResponse.getReadingStatuses().size());
        assertThat(20).isEqualTo(findOneTalkRoomResponse.getComments().size());
        assertThat("의견 0").isEqualTo(findOneTalkRoomResponse.getComments().get(0).getContent());
        assertThat("의견 19").isEqualTo(findOneTalkRoomResponse.getComments().get(19).getContent());
    }

    @Test
    @DisplayName("토크방을 생성한 유저가 토크방을 삭제한다.")
    void deleteTalkRoom() {
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
        talkRoomService.deleteTalkRoom(talkRoom.getId(), user.getId());

        // then
        assertThat(0).isEqualTo(talkRoomRepository.findAll().size());
        assertThat(0).isEqualTo(commentRepository.findAll().size());
    }

    @Test
    @DisplayName("토크방을 생성하지 않은 유저(UserB)가 다른 유저(UserA)가 생성한 토크방을 삭제할 수 없다.")
    void deleteTalkRoomWithUserB() {
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

        Comment comment = createComment(talkRoom, userA);

        commentRepository.save(comment);

        // when // then
        assertThatThrownBy(() -> talkRoomService.deleteTalkRoom(talkRoom.getId(), userB.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("토크방을 삭제할 때 의견이 없어도 정상적으로 삭제가 된다.")
    void deleteTalkRoomWithComment() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        // when
        talkRoomService.deleteTalkRoom(talkRoom.getId(), user.getId());

        // then
        assertThat(0).isEqualTo(talkRoomRepository.findAll().size());
    }

    @Test
    @DisplayName("토크방을 페이징 조회 시 토크방에는 좋아요 총 개수가 표시되어야 한다.")
    void findAllTalkRoomWithLikeCount() {
        // given
        List<User> users = listUsers();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = listTalkRooms(20, users.get(0), book);

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

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(2)
                .size(10)
                .orderType(OrderType.RECENT)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> response = talkRoomService.findAllTalkRoom(search);

        // then
        assertThat(5L).isEqualTo(response.getQueryResponse().get(9).getLikeCount());
    }

    @Test
    @DisplayName("토크방 페이지 조회 시 토크방에 좋아요 누른 사용자 ID들이 프론트에 전송되어야 한다.")
    void findAllTalkRoomWithLikeUserId() {
        // given
        List<User> users = listUsers();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = listTalkRooms(20, users.get(0), book);

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

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(2)
                .size(10)
                .orderType(OrderType.RECENT)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> response = talkRoomService.findAllTalkRoom(search);

        // then
        assertThat(users.get(0).getId()).isEqualTo(response.getQueryResponse().get(9).getUserIds().get(0).getUserId());
        assertThat(users.get(1).getId()).isEqualTo(response.getQueryResponse().get(9).getUserIds().get(1).getUserId());
        assertThat(users.get(2).getId()).isEqualTo(response.getQueryResponse().get(9).getUserIds().get(2).getUserId());
        assertThat(users.get(3).getId()).isEqualTo(response.getQueryResponse().get(9).getUserIds().get(3).getUserId());
        assertThat(users.get(4).getId()).isEqualTo(response.getQueryResponse().get(9).getUserIds().get(4).getUserId());
    }

    @Test
    @DisplayName("토크방 단건 조회 시 좋아요 개수가 표시 된다.")
    void findOneTalkRoomWithLikeCount() {
        // given
        List<User> users = listUsers();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = listTalkRooms(20, users.get(0), book);

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
        TalkRoomFindOneResponse response = talkRoomService.findOneTalkRoom(talkRoom.get(0).getId());

        // then
        assertThat(5L).isEqualTo(response.getLikeCount());
    }

    @Test
    @DisplayName("토크방 단건 조회 시 좋아요한 유저의 ID가 보내진다.")
    void findOneTalkRoomWithLikeUserId() {
        // given
        List<User> users = listUsers();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = listTalkRooms(20, users.get(0), book);

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
        TalkRoomFindOneResponse response = talkRoomService.findOneTalkRoom(talkRoom.get(0).getId());

        // then
        assertThat(users.get(0).getId()).isEqualTo(response.getUserIds().get(0).getUserId());
        assertThat(users.get(1).getId()).isEqualTo(response.getUserIds().get(1).getUserId());
        assertThat(users.get(2).getId()).isEqualTo(response.getUserIds().get(2).getUserId());
        assertThat(users.get(3).getId()).isEqualTo(response.getUserIds().get(3).getUserId());
        assertThat(users.get(4).getId()).isEqualTo(response.getUserIds().get(4).getUserId());
    }

    @Test
    @DisplayName("토크방 단건 조회 시 의견 개수가 표시 된다.")
    void findOneTalkRoomWithCommentCount() {
        // given
        List<User> users = listUsers();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = listTalkRooms(20, users.get(0), book);

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
        TalkRoomFindOneResponse response = talkRoomService.findOneTalkRoom(talkRoom.get(0).getId());

        // then
        assertThat(5L).isEqualTo(response.getCommentCount());
    }

    @Test
    @DisplayName("토크방을 좋아요 순으로 정렬을 한다.")
    void findAllTalkRoomWithOrderLike() {
        // given
        List<User> users = listUsers();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = listTalkRooms(20, users.get(0), book);

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

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(1)
                .size(10)
                .orderType(OrderType.RECOMMEND)
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> response = talkRoomService.findAllTalkRoom(search);

        // then
        assertThat(10L).isEqualTo(response.getQueryResponse().get(0).getLikeCount());
    }

    @Test
    @DisplayName("토크방 제목을 입력해서 조회한다.")
    void findAllTalkRoomWithSearch() {
        // given
        List<User> users = listUsers();

        userRepository.saveAll(users);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRoom = listTalkRooms(20, users.get(0), book);

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

        TalkRoomSearchServiceRequest search = TalkRoomSearchServiceRequest.builder()
                .page(1)
                .size(10)
                .search("검색어")
                .build();

        // when
        PageResponse<TalkRoomFindAllResponse> response = talkRoomService.findAllTalkRoom(search);

        // then
        assertThat(talkRoom1.getTitle()).isEqualTo(response.getQueryResponse().get(0).getTitle());
        assertThat(talkRoom2.getTitle()).isEqualTo(response.getQueryResponse().get(1).getTitle());
        assertThat(talkRoom3.getTitle()).isEqualTo(response.getQueryResponse().get(2).getTitle());
    }

    @Test
    @DisplayName("토크방 단건 조회 시 의견의 좋아요 개수가 표시 되어야한다.")
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
        TalkRoomFindOneResponse findOneTalkRoom = talkRoomService.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat("토크방").isEqualTo(findOneTalkRoom.getTitle());
        assertThat(2).isEqualTo(findOneTalkRoom.getReadingStatuses().size());
        assertThat("의견 남기기").isEqualTo(findOneTalkRoom.getComments().get(0).getContent());
        assertThat("user@gmail.com").isEqualTo(findOneTalkRoom.getComments().get(0).getUserName());
        assertThat(1L).isEqualTo(findOneTalkRoom.getComments().get(0).getCommentLikeCount());
        assertThat(user.getId()).isEqualTo(findOneTalkRoom.getComments().get(0).getUserIds().get(0).getUserId());
    }

    private static List<User> listUsers() {
        return IntStream.range(0, 10)
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
    }

    private static List<TalkRoom> listTalkRooms(int endExclusive, User users, Book book) {
        return IntStream.range(0, endExclusive)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .build())
                .toList();
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