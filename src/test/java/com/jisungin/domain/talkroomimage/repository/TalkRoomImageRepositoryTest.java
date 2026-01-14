package com.jisungin.domain.talkroomimage.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroomimage.TalkRoomImage;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class TalkRoomImageRepositoryTest extends RepositoryTestSupport {

    @Autowired
    UserRepository userRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    TalkRoomRepository talkRoomRepository;

    @Autowired
    TalkRoomImageRepository talkRoomImageRepository;

    @AfterEach
    void tearDown() {
        talkRoomImageRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("querydsl 토론방 단건 조회 시 이미지가 있으면 같이 조회된다.")
    void findTalkRoomImage() throws Exception {
        // given
        User user = createUser();
        userRepository.save(user);

        Book book = createBook();
        bookRepository.save(book);

        TalkRoom talkRoom = createTalkRoom(book, user);
        talkRoomRepository.save(talkRoom);

        TalkRoomImage image = TalkRoomImage.builder()
                .talkRoom(talkRoom)
                .imageUrl("imageUrl")
                .build();
        talkRoomImageRepository.save(image);

        // when
        List<String> response = talkRoomImageRepository.findTalkRoomImages(talkRoom.getId());

        // then
        assertThat("imageUrl").isEqualTo(response.get(0));
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