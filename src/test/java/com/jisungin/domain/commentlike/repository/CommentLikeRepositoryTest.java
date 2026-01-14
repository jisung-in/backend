package com.jisungin.domain.commentlike.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.jisungin.RepositoryTestSupport;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.commentlike.CommentLike;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CommentLikeRepositoryTest extends RepositoryTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TalkRoomRepository talkRoomRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @AfterEach
    void tearDown() {
        commentLikeRepository.deleteAllInBatch();
        commentRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("사용자가 좋아요한 의견 아이디를 조회한다.")
    @Test
    public void findLikeCommentIds() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(user, book));

        List<Comment> comments = commentRepository.saveAll(createComments(user, talkRoom));
        List<CommentLike> commentLikes = commentLikeRepository.saveAll(createCommentLikes(user, comments));

        // when
        List<Long> result = commentLikeRepository.findCommentIdsByUserId(user.getId());

        // then
        assertThat(result).hasSize(5)
                .containsExactly(
                        comments.get(0).getId(),
                        comments.get(1).getId(),
                        comments.get(2).getId(),
                        comments.get(3).getId(),
                        comments.get(4).getId()
                );
    }

    private static User createUser() {
        return User.builder()
                .name("사용자")
                .profileImage("userImage")
                .oauthId(
                        OauthId.builder()
                                .oauthId("000001")
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .build();
    }

    private static Book createBook() {
        return Book.builder()
                .title("도서 제목")
                .content("도서 내용")
                .authors("저자")
                .isbn("000001")
                .publisher("지성인")
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .imageUrl("bookImage")
                .build();
    }

    private static TalkRoom createTalkRoom(User user, Book book) {
        return TalkRoom.builder()
                .title("토크방 이름")
                .content("토크방 내용")
                .registeredDateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .user(user)
                .book(book)
                .build();
    }

    private static Comment createCommentWithContent(User user, TalkRoom talkRoom, String content) {
        return Comment.builder()
                .user(user)
                .talkRoom(talkRoom)
                .content(content)
                .build();
    }

    private static List<Comment> createComments(User user, TalkRoom talkRoom) {
        return IntStream.range(0, 5)
                .mapToObj(i -> createCommentWithContent(user, talkRoom, "content" + i))
                .toList();
    }

    private static CommentLike createCommentLike(User user, Comment comment) {
        return CommentLike.builder()
                .user(user)
                .comment(comment)
                .build();
    }

    private static List<CommentLike> createCommentLikes(User user, List<Comment> comments) {
        return IntStream.range(0, 5)
                .mapToObj(i -> createCommentLike(user, comments.get(i)))
                .toList();
    }

}
