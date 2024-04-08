package com.jisungin.domain.talkRoomLike.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.oauth.OauthId;
import com.jisungin.domain.oauth.OauthType;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroomlike.TalkRoomLike;
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TalkRoomLikeRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TalkRoomRepository talkRoomRepository;

    @Autowired
    private TalkRoomLikeRepository talkRoomLikeRepository;

    @Test
    @DisplayName("사용자가 좋아요한 토크방 아이디 조회")
    public void findLikeTalkRoomIdsByUserId() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(user, book));

        List<Long> talkRoomIds = extractTalkRoomIds(talkRooms);

        List<TalkRoomLike> talkRoomLikes = talkRoomLikeRepository.saveAll(createTalkRoomLikes(user, talkRooms));

        // when
        List<Long> response = talkRoomLikeRepository.findLikeTalkRoomIdsByUserId(user.getId(),
                talkRoomIds);

        // then
        assertThat(response).contains(
                talkRooms.get(0).getId(),
                talkRooms.get(1).getId(),
                talkRooms.get(2).getId(),
                talkRooms.get(3).getId(),
                talkRooms.get(4).getId()
        );
    }

    @NotNull
    private static List<TalkRoomLike> createTalkRoomLikes(User user, List<TalkRoom> talkRooms) {
        return IntStream.range(0, 5)
                .mapToObj(i -> TalkRoomLike.builder()
                        .user(user)
                        .talkRoom(talkRooms.get(i))
                        .build())
                .toList();
    }

    @NotNull
    private static List<Long> extractTalkRoomIds(List<TalkRoom> talkRooms) {
        return talkRooms.stream()
                .map(TalkRoom::getId)
                .toList();
    }

    @NotNull
    private static List<TalkRoom> createTalkRooms(User user, Book book) {
        return LongStream.range(0, 5)
                .mapToObj(i -> TalkRoom.builder()
                        .user(user)
                        .book(book)
                        .title("title" + i)
                        .content("content" + i)
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

}
