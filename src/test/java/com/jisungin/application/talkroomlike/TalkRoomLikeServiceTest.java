package com.jisungin.application.talkroomlike;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TalkRoomLikeServiceTest extends ServiceTestSupport {

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
    TalkRoomLikeRepository talkRoomLikeRepository;

    @AfterEach
    void tearDown() {
        talkRoomLikeRepository.deleteAllInBatch();
        talkRoomRoleRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("유저가 토크방을 좋아요 한다.")
    void likeTalkRoom() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        // when
        talkRoomLikeService.likeTalkRoom(talkRoom.getId(), user.getId());

        // then
        List<TalkRoomLike> talkRoomLikes = talkRoomLikeRepository.findAll();
        assertThat(1).isEqualTo(talkRoomLikes.size());
    }

    @Test
    @DisplayName("토크방이 없는 상태에선 좋아요 할 수 없다.")
    void likeTalkRoomWithTalkRoomEmpty() {
        // given
        User user = createUser();
        userRepository.save(user);

        // when // then
        assertThatThrownBy(() -> talkRoomLikeService.likeTalkRoom(1L, user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("토크방을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("로그인을 하지 않는 상태에선 좋아요를 누를 수 없다.")
    void likeTalkRoomWithUserEmpty() {
        // given
        Long invalidUserId = 1000L;

        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        // when // then
        assertThatThrownBy(() -> talkRoomLikeService.likeTalkRoom(talkRoom.getId(), invalidUserId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("유저가 좋아요를 취소한다.")
    void unLikeTalkRoom() {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        TalkRoomLike talkRoomLike = createTalkRoomLike(talkRoom, user);
        talkRoomLikeRepository.save(talkRoomLike);

        // when
        talkRoomLikeService.unLikeTalkRoom(talkRoom.getId(), user.getId());

        // then
        List<TalkRoomLike> talkRoomLikes = talkRoomLikeRepository.findAll();
        assertThat(0).isEqualTo(talkRoomLikes.size());
    }

    @Test
    @DisplayName("유저A의 좋아요를 유저B가 취소할 순 없다.")
    void unLikeTalkRoomWithUserB() {
        // given
        User user = createUser();
        userRepository.save(user);

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

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        createTalkRoomRole(talkRoom);

        TalkRoomLike talkRoomLike = createTalkRoomLike(talkRoom, user);
        talkRoomLikeRepository.save(talkRoomLike);

        // when // then
        assertThatThrownBy(() -> talkRoomLikeService.unLikeTalkRoom(talkRoom.getId(), userB.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("토크방 좋아요를 찾을 수 없습니다.");
    }

    private static TalkRoomLike createTalkRoomLike(TalkRoom talkRoom, User user) {
        return TalkRoomLike.builder()
                .talkRoom(talkRoom)
                .user(user)
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