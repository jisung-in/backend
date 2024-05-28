package com.jisungin.application.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.OffsetLimit;
import com.jisungin.application.PageResponse;
import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.application.book.response.BookFindAllResponse;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.comment.Comment;
import com.jisungin.domain.comment.repository.CommentRepository;
import com.jisungin.domain.talkroom.TalkRoom;
import com.jisungin.domain.talkroom.repository.TalkRoomRepository;
import com.jisungin.domain.talkroom.repository.TalkRoomRoleRepository;
import com.jisungin.domain.talkroomlike.repository.TalkRoomLikeRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import com.jisungin.infra.crawler.Crawler;
import com.jisungin.infra.crawler.CrawlingBook;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class BookServiceTest extends ServiceTestSupport {

    @MockBean
    private Crawler crawler;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private TalkRoomRepository talkRoomRepository;

    @Autowired
    private TalkRoomRoleRepository talkRoomRoleRepository;

    @Autowired
    private TalkRoomLikeRepository talkRoomLikeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookService bookService;

    @AfterEach
    void tearDown() {
        commentRepository.deleteAllInBatch();
        talkRoomLikeRepository.deleteAllInBatch();
        talkRoomRoleRepository.deleteAllInBatch();
        talkRoomRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("책을 조회한다.")
    public void getBook() {
        // given
        Book book = bookRepository.save(create());

        // when
        BookResponse response = bookService.getBook(book.getIsbn());

        // then
        assertThat(response.getDateTime()).isEqualTo(book.getDateTime());
        assertThat(response.getAuthors()).hasSize(2)
                .contains("도서 저자1", "도서 저자2");
        assertThat(response)
                .extracting("title", "content", "isbn", "publisher", "imageUrl", "thumbnail")
                .contains("도서 제목", "도서 내용", "123456789X", "도서 출판사", "도서 imageUrl", "도서 썸네일");
    }

    @Test
    @DisplayName("존재하지 않는 책을 조회하면 예외가 발생한다.")
    public void getBookWithInvalidIsbn() {
        // given
        String invalidIsbn = "0000000000";

        // when // then
        assertThatThrownBy(() -> bookService.getBook(invalidIsbn))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("최근 등록된 책을 페이징 조회한다.")
    public void getBooksByPage() {
        // given
        User user = userRepository.save(createUser(1));
        List<Book> books = bookRepository.saveAll(createBooks());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(1, user, books.get(0)));

        OffsetLimit offsetLimit = OffsetLimit.of(1, 5, "recent");

        // when
        PageResponse<BookFindAllResponse> response = bookService.getBooks(offsetLimit);

        // then
        assertThat(response.getSize()).isEqualTo(5);
        assertThat(response.getTotalCount()).isEqualTo(5);
        assertThat(response.getQueryResponse()).hasSize(5)
                .extracting("isbn")
                .containsExactly("00004", "00003", "00002", "00001", "00000");
    }

    @Test
    @DisplayName("토크가 많은 책을 페이징 조회한다.")
    public void getBooksByComment() {
        // given
        User user = userRepository.save(createUser(1));
        List<Book> books = bookRepository.saveAll(createBooks());
        TalkRoom talkRoom = talkRoomRepository.save(createTalkRoom(1, user, books.get(0)));
        List<Comment> comments = commentRepository.saveAll(createComments(user, talkRoom));

        OffsetLimit offsetLimit = OffsetLimit.of(1, 5, "comment");

        // when
        PageResponse<BookFindAllResponse> response = bookService.getBooks(offsetLimit);

        // then
        assertThat(response.getSize()).isEqualTo(1);
        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getQueryResponse()).hasSize(1)
                .extracting("isbn")
                .containsExactly("00000");
    }

    @Test
    @DisplayName("도서 정보에 대한 책을 생성한다.")
    public void createBook() {
        // given
        LocalDateTime registeredDateTime = LocalDateTime.of(2024, 1, 1, 0, 0);

        BookCreateServiceRequest request = BookCreateServiceRequest.builder()
                .title("도서 제목")
                .contents("도서 내용")
                .isbn("123456789X")
                .dateTime(registeredDateTime)
                .authors("도서 작가1, 도서 작가2")
                .publisher("도서 출판사")
                .imageUrl("도서 imageUrl")
                .thumbnail("도서 썸네일")
                .build();

        when(crawler.crawlBook(request.getIsbn()))
                .thenReturn(CrawlingBook.of("도서 제목", "도서 내용", "123456789X", "도서 출판사",
                        "도서 imageUrl", "도서 썸네일", "도서 작가1,도서 작가2", registeredDateTime));

        // when
        BookResponse response = bookService.createBook(request);

        // then
        assertThat(response.getDateTime()).isEqualTo(request.getDateTime());
        assertThat(response.getAuthors()).hasSize(2)
                .contains("도서 작가1", "도서 작가2");
        assertThat(response)
                .extracting("title", "content", "isbn", "publisher", "imageUrl", "thumbnail")
                .contains("도서 제목", "도서 내용", "123456789X", "도서 출판사", "도서 imageUrl", "도서 썸네일");
    }

    @Test
    @DisplayName("이미 등록된 ISBN을 사용하여 책을 생성하는 경우 예외가 발생한다.")
    public void createBookWithDuplicateIsbn() {
        // given
        Book book = create();
        bookRepository.save(book);

        BookCreateServiceRequest request = BookCreateServiceRequest.builder()
                .title("도서 제목")
                .contents("도서 내용")
                .isbn(book.getIsbn())
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .authors("도서 저자1, 도서 저자2")
                .publisher("도서 출판사")
                .imageUrl("도서 URL")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 등록된 책 정보 입니다.");
    }

    private static Book create() {
        return Book.builder()
                .title("도서 제목")
                .content("도서 내용")
                .authors("도서 저자1,도서 저자2")
                .isbn("123456789X")
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .publisher("도서 출판사")
                .imageUrl("도서 imageUrl")
                .thumbnail("도서 썸네일")
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

    private static List<Book> createBooks() {
        return IntStream.range(0, 5)
                .mapToObj(i -> createBookWithIsbn("0000" + i))
                .toList();
    }

    private static User createUser(int id) {
        return User.builder()
                .name("user@gmail.com " + id)
                .profileImage("image")
                .oauthId(
                        OauthId.builder()
                                .oauthId("oauthId " + id)
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .build();
    }

    private static TalkRoom createTalkRoom(int id, User user, Book book) {
        return TalkRoom.builder()
                .title("토론방 제목" + id)
                .content("토론방 내용" + id)
                .user(user)
                .book(book)
                .build();
    }

    private static Comment createComment(User user, TalkRoom talkRoom) {
        return Comment.builder()
                .content("토크 내용")
                .user(user)
                .talkRoom(talkRoom)
                .build();
    }

    private static List<Comment> createComments(User user, TalkRoom talkRoom) {
        return IntStream.range(0, 10)
                .mapToObj(i -> createComment(user, talkRoom))
                .toList();
    }

}
