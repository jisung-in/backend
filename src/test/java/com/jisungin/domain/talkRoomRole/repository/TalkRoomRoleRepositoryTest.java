package com.jisungin.domain.talkRoomRole.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.TalkRoomRole;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TalkRoomRoleRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TalkRoomRepository talkRoomRepository;

    @Autowired
    private TalkRoomRoleRepository talkRoomRoleRepository;

    @Test
    @DisplayName("토크방 아이디와 관련된 토크방 조건을 가져온다.")
    public void findTalkRoomRoleByTalkRoomIds() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(user, book));

        List<Long> talkRoomIds = extractTalkRoomIds(talkRooms);

        List<TalkRoomRole> talkRoomRoles = talkRoomRoleRepository.saveAll(createTalkRoomRole(talkRooms));

        // when
        Map<Long, List<ReadingStatus>> response = talkRoomRoleRepository.findTalkRoomRoleByIds(talkRoomIds);

        // then
        assertThat(response).hasSize(5)
                .contains(
                        entry(talkRooms.get(0).getId(), List.of(ReadingStatus.READ)),
                        entry(talkRooms.get(1).getId(), List.of(ReadingStatus.READ)),
                        entry(talkRooms.get(2).getId(), List.of(ReadingStatus.READ)),
                        entry(talkRooms.get(3).getId(), List.of(ReadingStatus.READ)),
                        entry(talkRooms.get(4).getId(), List.of(ReadingStatus.READ))
                );
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

    private List<TalkRoomRole> createTalkRoomRole(List<TalkRoom> talkRooms) {
        return talkRooms.stream()
                .map(talkRoom -> TalkRoomRole.roleCreate(talkRoom, ReadingStatus.READ))
                .toList();
    }

}
