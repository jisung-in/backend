package com.jisungin.application.talkroom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.OffsetLimit;
import com.jisungin.application.PageResponse;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomSearchCondition;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentlike.repository.CommentLikeRepository;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.talkroomimage.repository.TalkRoomImageRepository;
import com.jisungin.domain.talkroomlike.TalkRoomLike;
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookIsbn(book.getIsbn())
                .title("토크방")
                .content("내용")
                .readingStatus(List.of("읽는 중", "읽음"))
                .build();

        // when
        TalkRoomFindOneResponse result = talkRoomService.createTalkRoom(request, user.getId(), LocalDateTime.now());

        // then
        assertThat(result)
                .extracting("id", "profileImage", "username", "title", "content", "bookName", "bookThumbnail",
                        "likeCount")
                .contains(result.getId(), "image", "user@gmail.com", "토크방", "내용", "제목", "이미지", 0L);
        assertThat(result.getReadingStatuses()).hasSize(2);
    }

    @Test
    @DisplayName("토크방을 생성할 때 참가 조건은 1개 이상이어야 한다.")
    void createTalkRoomWithNotReadingStatus() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookIsbn(book.getIsbn())
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
    @DisplayName("토크방을 생성할 때 참가 조건이 None으로 생성할 수 있다.")
    void createTalkRoomWithNone() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookIsbn(book.getIsbn())
                .title("토크방")
                .content("내용")
                .readingStatus(List.of("상관없음"))
                .build();

        // when
        TalkRoomFindOneResponse result = talkRoomService.createTalkRoom(request, user.getId(), LocalDateTime.now());

        // then
        assertThat(result)
                .extracting("id", "profileImage", "username", "title", "content", "bookName", "bookThumbnail",
                        "likeCount")
                .contains(result.getId(), "image", "user@gmail.com", "토크방", "내용", "제목", "이미지", 0L);
        assertThat(result.getReadingStatuses()).hasSize(1);
    }

    @Test
    @DisplayName("토크방을 생성했던 사용자가 토크방의 제목을 수정한다.")
    void editTalkRoom() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, user));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("상관없음")));

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRoom.getId())
                .title("토크방 수정")
                .content("내용 수정")
                .readingStatus(List.of("읽는 중", "읽음"))
                .build();

        // when
        talkRoomService.editTalkRoom(request, user.getId());

        // then
        List<TalkRoom> findTalkRooms = talkRoomRepository.findAll();
        List<TalkRoomRole> findTalkRoomRoles = talkRoomRoleRepository.findAll();

        assertThat(findTalkRooms.get(0))
                .extracting("title", "content")
                .contains("토크방 수정", "내용 수정");
        assertThat(findTalkRoomRoles).hasSize(2)
                .extracting("readingStatus")
                .contains(ReadingStatus.READ, ReadingStatus.READING);
    }

    @Test
    @DisplayName("토크방을 생성한 사용자가 토크방의 제목을 NULL 값으로 업데이트하려고 시도했을 때, 원래의 제목이 유지된다.")
    void editTalkRoomWithNullContent() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, user));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("상관없음")));

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRoom.getId())
                .title(null)
                .content(null)
                .readingStatus(List.of("읽는 중", "읽음"))
                .build();

        // when
        talkRoomService.editTalkRoom(request, user.getId());

        // then
        List<TalkRoom> findTalkRooms = talkRoomRepository.findAll();
        List<TalkRoomRole> findTalkRoomRoles = talkRoomRoleRepository.findAll();

        assertThat(findTalkRooms.get(0))
                .extracting("title", "content")
                .contains("토크방", "내용");
        assertThat(findTalkRoomRoles).hasSize(2)
                .extracting("readingStatus")
                .contains(ReadingStatus.READ, ReadingStatus.READING);
    }

    @Test
    @DisplayName("토크방을 생성했던 사용자가 토크방의 참가 조건을 수정한다.")
    void editTalkRoomReadingStatus() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, user));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("읽는 중", "읽음")));

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRoom.getId())
                .title("토크방")
                .content("내용")
                .readingStatus(List.of("읽는 중", "읽음", "잠시 멈춤"))
                .build();

        // when
        talkRoomService.editTalkRoom(request, user.getId());

        // then
        List<TalkRoom> findTalkRooms = talkRoomRepository.findAll();
        List<TalkRoomRole> findTalkRoomRoles = talkRoomRoleRepository.findAll();

        assertThat(findTalkRooms.get(0))
                .extracting("title", "content")
                .contains("토크방", "내용");
        assertThat(findTalkRoomRoles).hasSize(3)
                .extracting("readingStatus")
                .contains(ReadingStatus.READ, ReadingStatus.READING, ReadingStatus.PAUSE);
    }

    @Test
    @DisplayName("토크방을 생성한 유저와 토크방을 수정하는 유저가 일치하지 않으면 예외가 발생한다.")
    void editTalkRoomWithUsersMustMatch() {
        // given
        User userA = userRepository.save(createUser());
        User userB = userRepository.save(createUserWithId("1"));
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, userA));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("읽는 중", "읽음")));

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(talkRoom.getId())
                .title("토론방")
                .content("내용")
                .readingStatus(List.of("읽는 중", "읽음", "잠시 멈춤"))
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
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(20, user, book));

        for (TalkRoom t : talkRooms) {
            createTalkRoomRole(t);
        }

        // when
        SliceResponse<TalkRoomFindAllResponse> result = talkRoomService.findAllTalkRoom(OffsetLimit.of(1, 10, "recent"),
                TalkRoomSearchCondition.of(null, null), LocalDateTime.now());

        // then
        assertThat(result.getContent().size()).isEqualTo(10L);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("토론방 19");
        assertThat(result.getContent().get(0).getReadingStatuses().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("토크방 총 11페이지(103개) 중 5페이지를 조회를 조회하면 첫 번째 토크방은 62번 토크방이다.")
    void getTalkRoomsMiddle() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(103, user, book));

        for (TalkRoom t : talkRooms) {
            createTalkRoomRole(t);
        }

        // when
        SliceResponse<TalkRoomFindAllResponse> result = talkRoomService.findAllTalkRoom(OffsetLimit.of(5, 10, "recent"),
                TalkRoomSearchCondition.of(null, null), LocalDateTime.now());

        // then
        assertThat(result.getContent().size()).isEqualTo(10L);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("토론방 62");
        assertThat(result.getContent().get(0).getContent()).isEqualTo("내용 62");
    }

    @Test
    @DisplayName("토크방 총 11 페이지(103개) 중 마지막 페이지를 조회하면 첫 번째 토크방은 2번 토크방이다.")
    void getTalkRoomsLast() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(103, user, book));

        for (TalkRoom t : talkRooms) {
            createTalkRoomRole(t);
        }

        // when
        SliceResponse<TalkRoomFindAllResponse> result = talkRoomService.findAllTalkRoom(
                OffsetLimit.of(11, 10, "recent"),
                TalkRoomSearchCondition.of(null, null), LocalDateTime.now());

        // then
        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("토론방 2");
        assertThat(result.getContent().get(0).getContent()).isEqualTo("내용 2");
    }

    @Test
    @DisplayName("토크방을 단건 조회 한다.")
    void findOneTalkRoom() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, user));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("읽음", "읽는 중")));

        // when
        TalkRoomFindOneResponse result = talkRoomService.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat(result.getTitle()).isEqualTo("토크방");
        assertThat(result.getReadingStatuses()).hasSize(2);
    }

    @Test
    @DisplayName("토크방을 단건 조회 했을 때 의견이 담기지 않았아도 조회는 정상적으로 되어야 한다.")
    void findOneTalkRoomWithCommentNull() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, user));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("읽음", "읽는 중")));

        // when
        TalkRoomFindOneResponse result = talkRoomService.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat(result.getTitle()).isEqualTo("토크방");
        assertThat(result.getReadingStatuses()).hasSize(2);
    }

    @Test
    @DisplayName("토크방을 단건 조회 했을 때 의견이 여러 개 달려있을 때 의견을 전부 보여줘야 한다.")
    void findOneTalkRoomWithFindAllComment() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, user));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("읽음", "읽는 중")));

        // when
        TalkRoomFindOneResponse result = talkRoomService.findOneTalkRoom(talkRoom.getId());

        // then
        assertThat(result.getTitle()).isEqualTo("토크방");
        assertThat(result.getReadingStatuses()).hasSize(2);
    }

    @Test
    @DisplayName("토크방을 생성한 유저가 토크방을 삭제한다.")
    void deleteTalkRoom() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, user));
        Comment comment = commentRepository.save(createComment(talkRoom, user));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("읽음", "읽는 중")));

        // when
        talkRoomService.deleteTalkRoom(talkRoom.getId(), user.getId());

        // then
        assertThat(talkRoomRepository.findAll()).hasSize(0);
        assertThat(commentRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("토크방을 생성하지 않은 유저(UserB)가 다른 유저(UserA)가 생성한 토크방을 삭제할 수 없다.")
    void deleteTalkRoomWithUserB() {
        // given
        User userA = userRepository.save(createUser());
        User userB = userRepository.save(createUserWithId("1"));
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, userA));
        Comment comment = commentRepository.save(createComment(talkRoom, userA));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("읽음", "읽는 중")));

        // when // then
        assertThatThrownBy(() -> talkRoomService.deleteTalkRoom(talkRoom.getId(), userB.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("토크방을 삭제할 때 의견이 없어도 정상적으로 삭제가 된다.")
    void deleteTalkRoomWithComment() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(book, user));

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(
                createTalkRoomRoles(talkRoom, List.of("읽음", "읽는 중")));

        // when
        talkRoomService.deleteTalkRoom(talkRoom.getId(), user.getId());

        // then
        assertThat(talkRoomRepository.findAll()).hasSize(0);
    }

    @Test
    @DisplayName("토크방을 페이징 조회 시 토크방에는 좋아요 총 개수가 표시되어야 한다.")
    void findAllTalkRoomWithLikeCount() {
        // given
        Book book = bookRepository.save(createBook());

        List<User> users = userRepository.saveAll(createUsers());
        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(20, users.get(0), book));

        for (TalkRoom t : talkRooms) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes = IntStream.range(0, 5).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRooms.get(0))
                        .build())
                .toList();

        talkRoomLikeRepository.saveAll(likes);

        // when
        SliceResponse<TalkRoomFindAllResponse> result = talkRoomService.findAllTalkRoom(OffsetLimit.of(2, 10, "recent"),
                TalkRoomSearchCondition.of(null, null), LocalDateTime.now());

        // then
        assertThat(result.getContent().get(9).getLikeCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("토크방 단건 조회 시 좋아요 개수가 표시 된다.")
    void findOneTalkRoomWithLikeCount() {
        // given
        Book book = bookRepository.save(createBook());

        List<User> users = userRepository.saveAll(createUsers());
        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(20, users.get(0), book));

        for (TalkRoom t : talkRooms) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes = IntStream.range(0, 5).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRooms.get(0))
                        .build())
                .toList();

        talkRoomLikeRepository.saveAll(likes);

        // when
        TalkRoomFindOneResponse result = talkRoomService.findOneTalkRoom(talkRooms.get(0).getId());

        // then
        assertThat(result.getLikeCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("토크방을 좋아요 순으로 정렬을 한다.")
    void findAllTalkRoomWithOrderLike() {
        // given
        Book book = bookRepository.save(createBook());

        List<User> users = userRepository.saveAll(createUsers());
        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(20, users.get(0), book));

        for (TalkRoom t : talkRooms) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes1 = IntStream.range(0, 10).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRooms.get(0))
                        .build())
                .toList();

        List<TalkRoomLike> likes2 = IntStream.range(0, 9).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRooms.get(1))
                        .build())
                .toList();

        List<TalkRoomLike> likes = new ArrayList<>();
        likes.addAll(likes1);
        likes.addAll(likes2);

        talkRoomLikeRepository.saveAll(likes);

        // when
        SliceResponse<TalkRoomFindAllResponse> result = talkRoomService.findAllTalkRoom(
                OffsetLimit.of(1, 10, "recommend"), TalkRoomSearchCondition.of(null, null), LocalDateTime.now());

        // then
        assertThat(result.getContent().get(0).getLikeCount()).isEqualTo(10L);
    }

    @Test
    @DisplayName("토크방 제목을 입력해서 조회한다.")
    void findAllTalkRoomWithSearch() {
        // given
        Book book = bookRepository.save(createBook());

        List<User> users = userRepository.saveAll(createUsers());
        List<TalkRoom> talkRoom = talkRoomRepository.saveAll(createTalkRooms(20, users.get(0), book));

        TalkRoom talkRoom1 = TalkRoom.builder()
                .user(users.get(0))
                .book(book)
                .title("검색어")
                .content("내용")
                .registeredDateTime(LocalDateTime.now())
                .build();

        TalkRoom talkRoom2 = TalkRoom.builder()
                .user(users.get(0))
                .book(book)
                .title("아무내용 검색어 아무내용")
                .content("내용")
                .registeredDateTime(LocalDateTime.now())
                .build();

        TalkRoom talkRoom3 = TalkRoom.builder()
                .user(users.get(0))
                .book(book)
                .title("아무내용 아무내용 검색어")
                .content("내용")
                .registeredDateTime(LocalDateTime.now())
                .build();

        talkRoomRepository.save(talkRoom1);
        createTalkRoomRole(talkRoom1);
        talkRoomRepository.save(talkRoom2);
        createTalkRoomRole(talkRoom2);
        talkRoomRepository.save(talkRoom3);
        createTalkRoomRole(talkRoom3);

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
        SliceResponse<TalkRoomFindAllResponse> result = talkRoomService.findAllTalkRoom(OffsetLimit.of(1, 10),
                TalkRoomSearchCondition.of("검색어", null), LocalDateTime.now());

        // then
        assertThat(result.getContent().get(0).getTitle()).isEqualTo(talkRoom1.getTitle());
        assertThat(result.getContent().get(1).getTitle()).isEqualTo(talkRoom2.getTitle());
        assertThat(result.getContent().get(2).getTitle()).isEqualTo(talkRoom3.getTitle());
    }

    @Test
    @DisplayName("토크룸을 생성 했을 때 이미지 URL을 저장할 수 있다.")
    void createTalkRoomWithImage() throws Exception {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

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
    @DisplayName("하루 전에 토론방을 조회하면 데이터 총 개수는 10개이어야 한다.")
    void findAllTalkRoomWithDay() throws Exception {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        LocalDateTime yesterdayWithSec = LocalDateTime.of(2024, 4, 28, 23, 59, 59);
        LocalDateTime yesterday = LocalDateTime.of(2024, 4, 29, 0, 0);
        LocalDateTime now = LocalDateTime.of(2024, 4, 30, 0, 0);

        List<TalkRoom> talkRoom1 = IntStream.range(0, 5)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .registeredDateTime(yesterdayWithSec)
                        .build())
                .toList();

        List<TalkRoom> talkRoom2 = IntStream.range(5, 10)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .registeredDateTime(yesterday)
                        .build())
                .toList();

        List<TalkRoom> talkRoom3 = IntStream.range(10, 15)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .registeredDateTime(now)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoom1);
        talkRoomRepository.saveAll(talkRoom2);
        talkRoomRepository.saveAll(talkRoom3);

        for (TalkRoom t : talkRoom1) {
            createTalkRoomRole(t);
        }
        for (TalkRoom t : talkRoom2) {
            createTalkRoomRole(t);
        }
        for (TalkRoom t : talkRoom3) {
            createTalkRoomRole(t);
        }

        // when
        SliceResponse<TalkRoomFindAllResponse> result = talkRoomService.findAllTalkRoom(
                OffsetLimit.of(1, 20, "recent"), TalkRoomSearchCondition.of(null, "1d"), now);

        // then
        assertThat(result.getSize()).isEqualTo(10L);
    }

    @Test
    @DisplayName("유저 본인이 생성한 토론방을 조회 한다.")
    void getTalkRoomsOwner() {
        // given
        User userA = userRepository.save(createUser());
        User userB = userRepository.save(createUserWithId("1"));
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRoomA = talkRoomRepository.saveAll(createTalkRooms(10, userA, book));
        List<TalkRoom> talkRoomB = talkRoomRepository.saveAll(createTalkRooms(10, userB, book));

        for (TalkRoom t : talkRoomA) {
            createTalkRoomRole(t);
        }

        for (TalkRoom t : talkRoomB) {
            createTalkRoomRole(t);
        }

        // when
        PageResponse<TalkRoomFindAllResponse> result = talkRoomService.findUserTalkRoom(OffsetLimit.of(1, 10),
                true, false, false, userA.getId());

        // then
        assertThat(result.getTotalCount()).isEqualTo(10);
        assertThat(talkRoomRepository.findAll()).hasSize(20);
    }

    @Test
    @DisplayName("유저 본인이 생성한 토크방 중 좋아요 누른 토크방을 조회한다.")
    void getTalkRoomsOwnerWithLike() {
        // given
        User userA = userRepository.save(createUser());
        User userB = userRepository.save(createUserWithId("1"));
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRoomA = talkRoomRepository.saveAll(createTalkRooms(10, userA, book));
        List<TalkRoom> talkRoomB = talkRoomRepository.saveAll(createTalkRooms(10, userB, book));

        for (TalkRoom t : talkRoomA) {
            createTalkRoomRole(t);
        }

        for (TalkRoom t : talkRoomB) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes = IntStream.range(0, 5).mapToObj(i -> TalkRoomLike.builder()
                        .user(userA)
                        .talkRoom(talkRoomA.get(i))
                        .build())
                .toList();

        talkRoomLikeRepository.saveAll(likes);

        // when
        PageResponse<TalkRoomFindAllResponse> result = talkRoomService.findUserTalkRoom(OffsetLimit.of(1, 10),
                true, false, true, userA.getId());

        // then
        assertThat(result.getTotalCount()).isEqualTo(5);
        assertThat(talkRoomRepository.findAll()).hasSize(20);
    }

    @Test
    @DisplayName("유저가 좋아요 누른 토크방을 조회한다.")
    void getTalkRoomsWithLike() {
        // given
        User userA = userRepository.save(createUser());
        User userB = userRepository.save(createUserWithId("1"));
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRoomB = talkRoomRepository.saveAll(createTalkRooms(10, userB, book));

        for (TalkRoom t : talkRoomB) {
            createTalkRoomRole(t);
        }

        List<TalkRoomLike> likes = IntStream.range(0, 8).mapToObj(i -> TalkRoomLike.builder()
                        .user(userA)
                        .talkRoom(talkRoomB.get(i))
                        .build())
                .toList();

        talkRoomLikeRepository.saveAll(likes);

        // when
        PageResponse<TalkRoomFindAllResponse> result = talkRoomService.findUserTalkRoom(OffsetLimit.of(1, 10),
                false, false, true, userA.getId());

        // then
        assertThat(result.getTotalCount()).isEqualTo(8);
        assertThat(talkRoomRepository.findAll()).hasSize(10);
    }

    @Test
    @DisplayName("유저가 좋아요 누른 토크방을 조회한다.")
    void getTalkRoomsWithComment() {
        // given
        User userA = userRepository.save(createUser());
        User userB = userRepository.save(createUserWithId("1"));
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(10, userB, book));

        for (TalkRoom t : talkRooms) {
            createTalkRoomRole(t);
        }

        List<Comment> comments = IntStream.range(0, 8).mapToObj(i -> Comment.builder()
                        .user(userA)
                        .talkRoom(talkRooms.get(i))
                        .content("의견")
                        .build())
                .toList();

        commentRepository.saveAll(comments);

        // when
        PageResponse<TalkRoomFindAllResponse> result = talkRoomService.findUserTalkRoom(OffsetLimit.of(1, 10),
                false, true, false, userA.getId());

        // then
        assertThat(result.getTotalCount()).isEqualTo(8);
        assertThat(talkRoomRepository.findAll()).hasSize(10);
    }

    private static List<User> createUsers() {
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

    private static Comment createComment(TalkRoom talkRoom, User user) {
        return Comment.builder()
                .talkRoom(talkRoom)
                .user(user)
                .content("의견 남기기")
                .build();
    }

    private void createTalkRoomRole(TalkRoom talkRoom) {
        List<ReadingStatus> readingStatus = ReadingStatus.createReadingStatus(List.of("읽는 중", "읽음"));

        readingStatus.stream().map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .forEach(talkRoomRoleRepository::save);
    }

    private List<TalkRoomRole> createTalkRoomRoles(TalkRoom talkRoom, List<String> readingStatuses) {
        return ReadingStatus.createReadingStatus(readingStatuses).stream()
                .map(status -> TalkRoomRole.roleCreate(talkRoom, status))
                .toList();
    }

    private static TalkRoom createTalkRoom(Book book, User user) {
        return TalkRoom.builder()
                .book(book)
                .title("토크방")
                .content("내용")
                .user(user)
                .registeredDateTime(LocalDateTime.now())
                .build();
    }

    private static List<TalkRoom> createTalkRooms(int endExclusive, User users, Book book) {
        return IntStream.range(0, endExclusive)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users)
                        .book(book)
                        .title("토론방 " + i)
                        .content("내용 " + i)
                        .registeredDateTime(LocalDateTime.now())
                        .build())
                .toList();
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

    private static User createUserWithId(String id) {
        return User.builder()
                .name("user@gmail.com" + id)
                .profileImage("image" + id)
                .oauthId(
                        OauthId.builder()
                                .oauthId("oauthId" + id)
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