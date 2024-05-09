package com.jisungin.domain.book.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.SearchServiceRequest;
import com.jisungin.application.book.response.SimpleBookResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class BookRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private BookRepository bookrepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TalkRoomRepository talkRoomRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    @DisplayName("최근 등록된 책 페이지 조회 쿼리")
    public void getBooksByRecent() {
        List<User> users = userRepository.saveAll(createUsers());
        List<Book> books = bookrepository.saveAll(createBooks());
        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(users, books));
        List<Comment> comments = commentRepository.saveAll(createComments(users.get(0), talkRooms.get(0)));

        SearchServiceRequest params = SearchServiceRequest.builder()
                .page(1)
                .size(5)
                .order("recent")
                .build();

        PageResponse<SimpleBookResponse> response = bookrepository.getBooks(params.getOffset(), params.getSize(),
                params.getOrder());

        assertThat(response.getSize()).isEqualTo(5);
        assertThat(response.getTotalCount()).isEqualTo(5);
        assertThat(response.getQueryResponse()).hasSize(5)
                .extracting("isbn")
                .containsExactly(
                        "00004", "00003", "00002", "00001", "00000"
                );
    }

    @Test
    @DisplayName("토크 많은 책 페이지 조회 쿼리")
    public void getBooksByComment() {
        List<User> users = userRepository.saveAll(createUsers());
        List<Book> books = bookrepository.saveAll(createBooks());
        List<TalkRoom> talkRooms = talkRoomRepository.saveAll(createTalkRooms(users, books));
        List<Comment> comments = commentRepository.saveAll(createComments(users.get(0), talkRooms.get(0)));

        SearchServiceRequest params = SearchServiceRequest.builder()
                .page(1)
                .size(5)
                .order("comment")
                .build();

        PageResponse<SimpleBookResponse> response = bookrepository.getBooks(params.getOffset(), params.getSize(),
                params.getOrder());

        assertThat(response.getSize()).isEqualTo(5);
        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getQueryResponse()).hasSize(1)
                .extracting("isbn")
                .containsExactlyInAnyOrder("00000");
    }


    @NotNull
    private static List<Comment> createComments(User user, TalkRoom talkRoom) {
        return IntStream.range(0, 10)
                .mapToObj(i -> Comment.builder()
                        .user(user)
                        .talkRoom(talkRoom)
                        .content("comment" + i)
                        .build())
                .toList();
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
                        .build())
                .toList();
    }

    private static List<Book> createBooks() {
        return IntStream.range(0, 5)
                .mapToObj(i -> Book.builder()
                        .isbn("0000" + i)
                        .title("책 제목" + i)
                        .content("책 내용" + i)
                        .authors("저자1,저자2")
                        .publisher("출판사" + i)
                        .imageUrl("www.image.com/" + i)
                        .thumbnail("www.thumbnail.com/" + i)
                        .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build())
                .toList();
    }

    public static List<TalkRoom> createTalkRooms(List<User> users, List<Book> books) {
        return IntStream.range(0, 5)
                .mapToObj(i -> TalkRoom.builder()
                        .user(users.get(0))
                        .book(books.get(i))
                        .title("title" + i)
                        .content("content" + i)
                        .build())
                .toList();
    }

}
