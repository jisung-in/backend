package com.jisungin.application.talkroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.api.Offset;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomPageResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.talkroomimage.repository.TalkRoomImageRepository;
import com.jisungin.domain.talkroomlike.TalkRoomLike;
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.assertj.core.api.Assertions;
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
    BookRepository bookRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    TalkRoomLikeRepository talkRoomLikeRepository;

    @Autowired
    CommentLikeRepository commentLikeRepository;

    @Autowired
    TalkRoomImageRepository talkRoomImageRepository;

    @Autowired
    TalkRoomService talkRoomService;

    @AfterEach
    void tearDown() {
        commentLikeRepository.deleteAllInBatch();
        talkRoomLikeRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        talkRoomImageRepository.deleteAllInBatch();
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
        TalkRoomFindOneResponse response = talkRoomService.createTalkRoom(request, user.getId(), LocalDateTime.now());

        // then
        List<String> readingStatuses = response.getReadingStatuses();
        assertThat(response)
                .extracting("id", "profileImage", "username", "title", "content", "bookName", "bookThumbnail",
                        "likeCount", "likeTalkRoom")
                .contains(response.getId(), "image", "user@gmail.com", "토크방", "내용", "제목", "이미지", 0L, false);
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
        assertThatThrownBy(() -> talkRoomService.createTalkRoom(request, user.getId(), LocalDateTime.now()))
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
        talkRoomService.editTalkRoom(request, user.getId());

        // then
        List<TalkRoom> response = talkRoomRepository.findAll();
        assertThat(response.get(0))
                .extracting("title", "content")
                .contains("토크방 수정", "내용 수정");
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
        talkRoomService.editTalkRoom(request, user.getId());

        // then
        List<TalkRoom> response = talkRoomRepository.findAll();
        assertThat(response.get(0))
                .extracting("title", "content")
                .contains("토크방", "내용");
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
        talkRoomService.editTalkRoom(request, user.getId());

        // then
        List<TalkRoom> response = talkRoomRepository.findAll();
        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.findAll();
        assertThat(response.get(0))
                .extracting("title", "content")
                .contains("토크방", "내용");
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

        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(1, 10), 10, "recent", null,
                null, null, LocalDateTime.now());

        // then
        assertThat(10L).isEqualTo(response.getResponse().getSize());
        assertThat("토론방 19").isEqualTo(response.getResponse().getQueryResponse().get(0).getTitle());
        assertThat(2).isEqualTo(response.getResponse().getQueryResponse().get(0).getReadingStatuses().size());
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

        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(1, 10), 10, "recent", null,
                null, null, LocalDateTime.now());

        // then
        assertThat(103).isEqualTo(response.getResponse().getTotalCount());
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

        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(5, 10), 10, "recent", null,
                null, null, LocalDateTime.now());

        // then
        assertThat(response.getResponse().getQueryResponse().size()).isEqualTo(10L);
        assertThat(response.getResponse().getQueryResponse().get(0).getTitle()).isEqualTo("토론방 62");
        assertThat(response.getResponse().getQueryResponse().get(0).getContent()).isEqualTo("내용 62");
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

        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(11, 10), 10, "recent", null,
                null, null, LocalDateTime.now());

        // then
        assertThat(response.getResponse().getQueryResponse().size()).isEqualTo(3);
        assertThat(response.getResponse().getQueryResponse().get(0).getTitle()).isEqualTo("토론방 2");
        assertThat(response.getResponse().getQueryResponse().get(0).getContent()).isEqualTo("내용 2");
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

        // when
        TalkRoomFindOneResponse findOneTalkRoomResponse = talkRoomService.findOneTalkRoom(talkRoom.getId(),
                user.getId());

        // then
        assertThat("토크방").isEqualTo(findOneTalkRoomResponse.getTitle());
        assertThat(2).isEqualTo(findOneTalkRoomResponse.getReadingStatuses().size());
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
        TalkRoomFindOneResponse findOneTalkRoomResponse = talkRoomService.findOneTalkRoom(talkRoom.getId(),
                user.getId());

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

        // when
        TalkRoomFindOneResponse findOneTalkRoomResponse = talkRoomService.findOneTalkRoom(talkRoom.getId(),
                user.getId());

        // then
        assertThat("토크방").isEqualTo(findOneTalkRoomResponse.getTitle());
        assertThat(2).isEqualTo(findOneTalkRoomResponse.getReadingStatuses().size());
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

        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(2, 10), 10, "recent", null,
                null, null, LocalDateTime.now());

        // then
        assertThat(5L).isEqualTo(response.getResponse().getQueryResponse().get(9).getLikeCount());
    }

    @Test
    @DisplayName("로그인한 유저가 토크룸을 조회했을 때 본인이 좋아요한 토크룸들이 표시 된다.")
    void findAllTalkRoomWithLike() {
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
                        .user(users.get(0))
                        .talkRoom(talkRoom.get(i))
                        .build())
                .toList();

        talkRoomLikeRepository.saveAll(likes);

        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(2, 10), 10, "recent", null,
                null, users.get(0).getId(), LocalDateTime.now());

        // then
        assertThat(talkRoom.get(0).getId()).isEqualTo(response.getUserLikeTalkRoomIds().get(0));
        assertThat(talkRoom.get(1).getId()).isEqualTo(response.getUserLikeTalkRoomIds().get(1));
        assertThat(talkRoom.get(2).getId()).isEqualTo(response.getUserLikeTalkRoomIds().get(2));
        assertThat(talkRoom.get(3).getId()).isEqualTo(response.getUserLikeTalkRoomIds().get(3));
        assertThat(talkRoom.get(4).getId()).isEqualTo(response.getUserLikeTalkRoomIds().get(4));
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
        TalkRoomFindOneResponse response = talkRoomService.findOneTalkRoom(talkRoom.get(0).getId(), null);

        // then
        assertThat(5L).isEqualTo(response.getLikeCount());
    }

    @Test
    @DisplayName("토크방 단건 조회 시 로그인한 유저가 토론방에 좋아요를 눌렀으면 토론방 ture를 리턴해준다.")
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
        TalkRoomFindOneResponse response = talkRoomService.findOneTalkRoom(talkRoom.get(0).getId(),
                users.get(0).getId());

        // then
        assertThat(response.isLikeTalkRoom()).isTrue();
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

        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(1, 10), 10, "recommend", null,
                null, null, LocalDateTime.now());

        // then
        assertThat(10L).isEqualTo(response.getResponse().getQueryResponse().get(0).getLikeCount());
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
        createTalkRoomRole(talkRoom1);
        talkRoomRepository.save(talkRoom2);
        createTalkRoomRole(talkRoom2);
        talkRoomRepository.save(talkRoom3);
        createTalkRoomRole(talkRoom3);

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

        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(1, 10), 10, null, "검색어",
                null, null, LocalDateTime.now());

        // then
        assertThat(talkRoom1.getTitle()).isEqualTo(response.getResponse().getQueryResponse().get(0).getTitle());
        assertThat(talkRoom2.getTitle()).isEqualTo(response.getResponse().getQueryResponse().get(1).getTitle());
        assertThat(talkRoom3.getTitle()).isEqualTo(response.getResponse().getQueryResponse().get(2).getTitle());
    }

    @Test
    @DisplayName("토크룸을 생성 했을 때 이미지 URL을 저장할 수 있다.")
    void createTalkRoomWithImage() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .title("토론방 제목")
                .content("토론방 내용")
                .bookIsbn(book.getIsbn())
                .imageUrls(List.of("image.png"))
                .readingStatus(List.of("읽는 중"))
                .build();

        // when
        TalkRoomFindOneResponse response = talkRoomService.createTalkRoom(request, user.getId(), LocalDateTime.now());

        // then
        assertThat(response.getImages().get(0)).isEqualTo("image.png");
    }

    @Test
    @DisplayName("최근 의견이 달린 토론방을 조회 할때 의견이 없다면 빈 값을 보낸다.")
    void fetchRecentCommentedDiscussionsWithCommentEmpty() throws Exception {
        // when
        TalkRoomPageResponse response = talkRoomService.findAllTalkRoom(Offset.of(1, 3), 3, "recent_comment",
                null,
                null, null, LocalDateTime.now());

        // then
        Assertions.assertThat(response.getResponse().getQueryResponse()).isEmpty();
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
                .thumbnail("이미지")
                .build();
    }

}