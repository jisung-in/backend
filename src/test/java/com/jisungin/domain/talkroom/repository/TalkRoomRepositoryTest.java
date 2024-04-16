package com.jisungin.domain.talkroom.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.SearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
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
                .order("recent")
                .build();

        // when
        List<TalkRoomQueryResponse> response = talkRoomRepository.findAllTalkRoom(search.getOffset(),
                search.getSize(), search.getOrder(), search.getQuery());

        // then
        assertThat(10L).isEqualTo(response.size());
        assertThat("토론방 19").isEqualTo(response.get(0).getTitle());
        assertThat("내용 19").isEqualTo(response.get(0).getContent());
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
                .order("recent")
                .build();

        // when
        List<TalkRoomQueryResponse> response = talkRoomRepository.findAllTalkRoom(search.getOffset(),
                search.getSize(), search.getOrder(), search.getQuery());

        // then
        assertThat(5L).isEqualTo(response.get(9).getLikeCount());
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
        TalkRoomQueryResponse response = talkRoomRepository.findOneTalkRoom(talkRoom.get(0).getId());

        // then
        assertThat(5L).isEqualTo(response.getLikeCount());
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
                .order("recommend")
                .build();

        // when
        List<TalkRoomQueryResponse> response = talkRoomRepository.findAllTalkRoom(search.getOffset(),
                search.getSize(), search.getOrder(), search.getQuery());

        // then
        assertThat(10L).isEqualTo(response.get(0).getLikeCount());
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
                .query("검색어")
                .build();

        // when
        List<TalkRoomQueryResponse> response = talkRoomRepository.findAllTalkRoom(search.getOffset(),
                search.getSize(), search.getOrder(), search.getQuery());

        // then
        assertThat(talkRoom1.getTitle()).isEqualTo(response.get(0).getTitle());
        assertThat(talkRoom2.getTitle()).isEqualTo(response.get(1).getTitle());
        assertThat(talkRoom3.getTitle()).isEqualTo(response.get(2).getTitle());
    }

    @Test
    @DisplayName("querydsl 책과 연관된 토크방 조회")
    void getTalkRoomRelatedBook() {
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

        Book book = bookRepository.save(createBookWithIsbn("00001"));
        Book anotherBook = bookRepository.save(createBookWithIsbn("00002"));

        List<TalkRoom> talkRoomsWithBook = IntStream.range(0, 10)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(book)
                        .title("토론방" + i)
                        .content("내용" + i)
                        .build())
                .toList();

        List<TalkRoom> talkRoomsWithAnotherBook = IntStream.range(10, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(anotherBook)
                        .title("토론방" + i)
                        .content("내용" + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoomsWithBook);
        talkRoomRepository.saveAll(talkRoomsWithAnotherBook);

        talkRoomsWithBook.forEach(this::createTalkRoomRole);
        talkRoomsWithAnotherBook.forEach(this::createTalkRoomRole);

        List<TalkRoomLike> likes1 = IntStream.range(0, 10).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoomsWithBook.get(0))
                        .build())
                .toList();

        List<TalkRoomLike> likes2 = IntStream.range(0, 9).mapToObj(i -> TalkRoomLike.builder()
                        .user(users.get(i))
                        .talkRoom(talkRoomsWithAnotherBook.get(1))
                        .build())
                .toList();

        talkRoomLikeRepository.saveAll(likes1);
        talkRoomLikeRepository.saveAll(likes2);

        // when
        List<TalkRoomQueryResponse> talkRoomsRelatedBook = talkRoomRepository.findTalkRoomsRelatedBook(book.getIsbn(),
                0, 20);

        // then
        assertThat(talkRoomsRelatedBook.size()).isEqualTo(10);
        assertThat(talkRoomsRelatedBook.get(0).getLikeCount()).isEqualTo(10);
        assertThat(talkRoomsRelatedBook.get(1).getLikeCount()).isEqualTo(0);
        assertThat(talkRoomsRelatedBook).extracting("bookName", "bookThumbnail")
                .containsOnly(tuple("제목00001", "www.thumbnail.com/00001"));
    }

    @Test
    @DisplayName("querydsl 책과 관련된 총 페이지 조회")
    public void getTalkRoomsRelatedBookTotalSize() {
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

        Book book = bookRepository.save(createBookWithIsbn("00001"));
        Book anotherBook = bookRepository.save(createBookWithIsbn("00002"));

        List<TalkRoom> talkRoomsWithBook = IntStream.range(0, 10)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(book)
                        .title("토론방" + i)
                        .content("내용" + i)
                        .build())
                .toList();

        List<TalkRoom> talkRoomsWithAnotherBook = IntStream.range(10, 20)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(anotherBook)
                        .title("토론방" + i)
                        .content("내용" + i)
                        .build())
                .toList();

        talkRoomRepository.saveAll(talkRoomsWithBook);
        talkRoomRepository.saveAll(talkRoomsWithAnotherBook);

        talkRoomsWithBook.forEach(this::createTalkRoomRole);
        talkRoomsWithAnotherBook.forEach(this::createTalkRoomRole);

        // when
        Long totalCount = talkRoomRepository.countTalkRoomsRelatedBook(book.getIsbn());
        Long anotherTotalCount = talkRoomRepository.countTalkRoomsRelatedBook(anotherBook.getIsbn());

        // then
        assertThat(totalCount).isEqualTo(10L);
        assertThat(anotherTotalCount).isEqualTo(10L);
    }

    @Test
    @DisplayName("최근 의견이 달린 토론방을 가져온다.")
    void fetchRecentCommentedDiscussions() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRooms = IntStream.range(1, 11)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방" + i)
                        .content("내용" + i)
                        .build())
                .toList();
        talkRoomRepository.saveAll(talkRooms);

        List<Comment> comments = IntStream.range(3, 6)
                .mapToObj(i -> Comment.builder()
                        .user(user)
                        .talkRoom(talkRooms.get(i))
                        .content("의견")
                        .build())
                .toList();
        commentRepository.saveAll(comments);

        SearchServiceRequest request = SearchServiceRequest.builder()
                .size(3)
                .page(1)
                .order("recent-comment")
                .build();

        // when
        List<TalkRoomQueryResponse> response = talkRoomRepository.findAllTalkRoom(request.getOffset(),
                request.getSize(), request.getOrder(), request.getQuery());

        // then
        assertThat("토론방6").isEqualTo(response.get(0).getTitle());
        assertThat("토론방5").isEqualTo(response.get(1).getTitle());
        assertThat("토론방4").isEqualTo(response.get(2).getTitle());
    }

    @Test
    @DisplayName("최근 의견이 달린 토론방을 가져온다.")
    void fetchRecentCommentedDiscussions2() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        List<TalkRoom> talkRooms = IntStream.range(1, 11)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("토론방" + i)
                        .content("내용" + i)
                        .build())
                .toList();
        talkRoomRepository.saveAll(talkRooms);

        Comment comment1 = Comment.builder()
                .content("의견")
                .user(user)
                .talkRoom(talkRooms.get(3))
                .build();
        Comment comment2 = Comment.builder()
                .content("의견")
                .user(user)
                .talkRoom(talkRooms.get(4))
                .build();
        Comment comment3 = Comment.builder()
                .content("의견")
                .user(user)
                .talkRoom(talkRooms.get(5))
                .build();
        Comment comment4 = Comment.builder()
                .content("의견 추가")
                .user(user)
                .talkRoom(talkRooms.get(5))
                .build();

        commentRepository.saveAll(List.of(comment1, comment2, comment3, comment4));

        SearchServiceRequest request = SearchServiceRequest.builder()
                .size(3)
                .page(1)
                .order("recent-comment")
                .build();

        // when
        List<TalkRoomQueryResponse> response = talkRoomRepository.findAllTalkRoom(request.getOffset(),
                request.getSize(), request.getOrder(), request.getQuery());

        // then
        assertThat("토론방6").isEqualTo(response.get(0).getTitle());
        assertThat("토론방5").isEqualTo(response.get(1).getTitle());
        assertThat("토론방4").isEqualTo(response.get(2).getTitle());
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
                .thumbnail("www.thumbnail.com")
                .build();
    }

    private static Book createBookWithIsbn(String isbn) {
        return Book.builder()
                .title("제목" + isbn)
                .content("내용" + isbn)
                .authors("작가")
                .isbn(isbn)
                .publisher("publisher")
                .dateTime(LocalDateTime.now())
                .imageUrl("www.image.com/" + isbn)
                .thumbnail("www.thumbnail.com/" + isbn)
                .build();
    }

}