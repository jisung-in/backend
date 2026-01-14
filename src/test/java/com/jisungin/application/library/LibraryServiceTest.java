package com.jisungin.application.library;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.PageResponse;
import com.jisungin.application.library.request.LibraryCreateServiceRequest;
import com.jisungin.application.library.request.LibraryEditServiceRequest;
import com.jisungin.application.library.response.LibraryResponse;
import com.jisungin.application.library.response.UserReadingStatusResponse;
import com.jisungin.application.library.request.UserReadingStatusGetAllServiceRequest;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.library.Library;
import com.jisungin.domain.library.ReadingStatusOrderType;
import com.jisungin.domain.library.repository.LibraryRepository;
import com.jisungin.domain.rating.Rating;
import com.jisungin.domain.rating.repository.RatingRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jisungin.domain.ReadingStatus.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

public class LibraryServiceTest extends ServiceTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private LibraryService libraryService;

    @AfterEach
    public void tearDown() {
        libraryRepository.deleteAllInBatch();
        ratingRepository.deleteAllInBatch();
        bookRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자가 서재 정보를 조회한다.")
    public void findLibraries() {
        // given
        User user = userRepository.save(createUser());
        List<Book> books = bookRepository.saveAll(createBooks());
        List<Library> libraries = libraryRepository.saveAll(createLibraries(user, books));

        // when
        List<LibraryResponse> result = libraryService.findLibraries(user.getId());

        // then
        assertThat(result).hasSize(5)
                .extracting("bookIsbn")
                .containsExactly("1", "2", "3", "4", "5");
    }

    @Test
    @DisplayName("서재 정보 조회할 때 서재 정보가 없으면 빈 리스트를 반환한다.")
    public void findLibrariesWithoutLibraries() {
        // given
        User user = userRepository.save(createUser());

        // when
        List<LibraryResponse> result = libraryService.findLibraries(user.getId());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("서재 정보 조회 시 사용자 정보가 존재해야 한다.")
    public void findLibrariesWithoutUser() {
        // given
        Long invalidUserId = 1L;

        // when // then
        assertThatThrownBy(() -> libraryService.findLibraries(invalidUserId))
                .hasMessage("사용자를 찾을 수 없습니다.")
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("사용자가 서재 정보를 생성한다.")
    public void createLibrary() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        LibraryCreateServiceRequest request = LibraryCreateServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("want")
                .build();

        // when
        LibraryResponse result = libraryService.createLibrary(request, user.getId());

        // then
        Library savedLibrary = libraryRepository.findAll().get(0);

        assertThat(result.getId()).isEqualTo(savedLibrary.getId());
        assertThat(result.getBookIsbn()).isEqualTo(book.getIsbn());
        assertThat(result.getStatus()).isEqualTo(ReadingStatus.WANT.getText());
    }

    @Test
    @DisplayName("서재 등록시 사용자 정보가 존재해야 한다.")
    public void createLibraryWithoutUser() {
        // given
        Long invalidUserId = -1L;
        Book book = bookRepository.save(createBook());

        LibraryCreateServiceRequest request = LibraryCreateServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("want")
                .build();

        // when // then
        assertThatThrownBy(() -> libraryService.createLibrary(request, invalidUserId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 등록 시 책 정보가 존재해야 한다.")
    public void createLibraryWithoutBook() {
        // given
        String invalidIsbn = "XXXXXXXXXXX";
        User user = userRepository.save(createUser());

        LibraryCreateServiceRequest request = LibraryCreateServiceRequest.builder()
                .isbn(invalidIsbn)
                .readingStatus("want")
                .build();

        // when // then
        assertThatThrownBy(() -> libraryService.createLibrary(request, user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 등록 시 동일한 정보의 서재가 존재하지 않아야 한다.")
    public void createLibraryAlreadyExists() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        Library library = libraryRepository.save(create(user, book));

        LibraryCreateServiceRequest request = LibraryCreateServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("reading")
                .build();

        // when // then
        assertThatThrownBy(() -> libraryService.createLibrary(request, user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 등록된 서재 정보 입니다.");
    }

    @Test
    @DisplayName("서재 정보를 수정한다.")
    public void editLibrary() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        Library library = libraryRepository.save(create(user, book));

        LibraryEditServiceRequest request = LibraryEditServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("read")
                .build();

        // when
        libraryService.editLibrary(library.getId(), user.getId(), request);

        // then
        Optional<Library> savedLibrary = libraryRepository.findById(library.getId());

        assertThat(savedLibrary).isNotEmpty();
        assertThat(savedLibrary.get().getStatus()).isEqualTo(ReadingStatus.READ);
    }

    @Test
    @DisplayName("서재 정보 수정 시 사용자 정보가 존재해야 한다.")
    public void editLibraryWithoutUser() {
        // given
        Long userLibraryId = 1L;
        Long userId = 1L;
        Book book = bookRepository.save(createBook());

        LibraryEditServiceRequest request = LibraryEditServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> libraryService.editLibrary(userLibraryId, userId, request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 수정 시 책 정보가 존재해야 한다.")
    public void editLibraryWithoutBook() {
        // given
        Long userLibraryId = 1L;
        String bookIsbn = "0000X";
        User user = userRepository.save(createUser());

        LibraryEditServiceRequest request = LibraryEditServiceRequest.builder()
                .isbn(bookIsbn)
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> libraryService.editLibrary(userLibraryId, user.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("책을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 수정 시 서재 정보가 존재해야 한다.")
    public void editLibraryWithoutLibrary() {
        // given
        Long userLibraryId = 1L;
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());

        LibraryEditServiceRequest request = LibraryEditServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> libraryService.editLibrary(userLibraryId, user.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("서재 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 수정 시 서재 정보와 사용자 정보는 일치해야 한다.")
    public void editLibraryInvalidUser() {
        // given
        User user = userRepository.save(createUser());
        User anotherUser = userRepository.save(createAnotherUser());

        Book book = bookRepository.save(createBook());

        Library library = libraryRepository.save(create(user, book));

        LibraryEditServiceRequest request = LibraryEditServiceRequest.builder()
                .isbn(book.getIsbn())
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> libraryService.editLibrary(library.getId(), anotherUser.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @Test
    @DisplayName("서재 정보 수정 시 서재 정보와 도서 정보는 일치해야 한다.")
    public void editLibraryInvalidBook() {
        // given
        User user = userRepository.save(createUser());

        Book book = bookRepository.save(createBookWithIsbn("00001"));
        Book anotherBook = bookRepository.save(createBookWithIsbn("00002"));

        Library library = libraryRepository.save(create(user, book));

        LibraryEditServiceRequest request = LibraryEditServiceRequest.builder()
                .isbn(anotherBook.getIsbn())
                .readingStatus("read")
                .build();

        // when // then
        assertThatThrownBy(() -> libraryService.editLibrary(library.getId(), user.getId(), request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("올바르지 않은 책 정보 입니다.");
    }

    @Test
    @DisplayName("서재 정보를 삭제한다.")
    public void deleteLibrary() {
        // given
        User user = userRepository.save(createUser());
        Book book = bookRepository.save(createBook());
        Library library = libraryRepository.save(create(user, book));

        // when
        libraryService.deleteLibrary(library.getId(), user.getId());

        // then
        List<Library> response = libraryRepository.findAll();

        assertThat(response).isEmpty();
    }

    @Test
    @DisplayName("서재 정보 삭제 시 사용자 정보가 존재해야 한다.")
    public void deleteLibraryWithoutUser() {
        // given
        Long userLibraryId = 1L;
        Long userId = 1L;
        Book book = bookRepository.save(createBook());

        // when // then
        assertThatThrownBy(() -> libraryService.deleteLibrary(userLibraryId, userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 삭제 시 서재 정보가 존재해야 한다.")
    public void deleteLibraryWithoutLibrary() {
        // given
        Long userLibraryId = 1L;
        User user = userRepository.save(createUser());

        // when // then
        assertThatThrownBy(() -> libraryService.deleteLibrary(userLibraryId, user.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("서재 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("서재 정보 삭제 시 서재 정보와 사용자 정보는 일치해야 한다.")
    public void deleteLibraryInvalidUser() {
        // given
        User user = userRepository.save(createUser());
        User anotherUser = userRepository.save(createAnotherUser());

        Book book = bookRepository.save(createBook());

        Library library = libraryRepository.save(create(user, book));

        // when // then
        assertThatThrownBy(
                () -> libraryService.deleteLibrary(library.getId(), anotherUser.getId()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("권한이 없는 사용자입니다.");
    }

    @DisplayName("사용자의 독서 상태가 읽고 싶은 책을 가져온다.")
    @Test
    void getReadingStatuses() {
        //given
        User user = userRepository.save(createUser());
        List<Book> books = bookRepository.saveAll(createBooks());
        List<Rating> ratings = ratingRepository.saveAll(createRatings(user, books));
        List<Library> userLibraries = libraryRepository.saveAll(createUserLibraries(user, books));

        // 읽고 싶은 상태인 책을 사전 순으로 정렬하고 1페이지를 가져온다.
        UserReadingStatusGetAllServiceRequest request = UserReadingStatusGetAllServiceRequest.builder()
                .page(1)
                .size(4)
                .orderType(ReadingStatusOrderType.DICTIONARY)
                .readingStatus(WANT)
                .build();

        //when
        PageResponse<UserReadingStatusResponse> result = libraryService.getUserReadingStatuses(user.getId(), request);

        //then
        assertThat(result.getTotalCount()).isEqualTo(4);
        assertThat(result.getQueryResponse()).hasSize(4)
                .extracting("bookImage", "bookTitle", "ratingAvg")
                .containsExactly(
                        tuple("bookImage", "제목1", 1.0),
                        tuple("bookImage", "제목11", 1.0),
                        tuple("bookImage", "제목16", 1.0),
                        tuple("bookImage", "제목6", 1.0)
                );
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

    private static User createAnotherUser() {
        return User.builder()
                .name("another@gmail.com")
                .profileImage("image")
                .oauthId(
                        OauthId.builder()
                                .oauthId("anotherOauthId")
                                .oauthType(OauthType.KAKAO)
                                .build()
                )
                .build();
    }

    private static List<Book> createBooks() {
        return IntStream.rangeClosed(1, 20)
                .mapToObj(i -> createBook(
                        "제목" + String.valueOf(i), "내용" + String.valueOf(i), String.valueOf(i)))
                .collect(Collectors.toList());
    }

    private static Book createBook(String title, String content, String isbn) {
        return Book.builder()
                .title(title)
                .content(content)
                .authors("김도형")
                .isbn(isbn)
                .publisher("지성인")
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .imageUrl("bookImage")
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

    private static Book createBookWithIsbn(String isbn) {
        return Book.builder()
                .title("제목")
                .content("내용")
                .authors("작가")
                .isbn(isbn)
                .publisher("publisher")
                .dateTime(LocalDateTime.now())
                .imageUrl("www")
                .thumbnail("이미지")
                .build();
    }

    public static Library create(User user, Book book) {
        return Library.builder()
                .user(user)
                .book(book)
                .status(ReadingStatus.WANT)
                .build();
    }

    public static List<Library> createLibraries(User user, List<Book> books) {
        return IntStream.range(0, 5)
                .mapToObj(i -> create(user, books.get(i)))
                .toList();
    }

    private static List<Library> createUserLibraries(User user, List<Book> books) {
        List<Library> userLibraries = new ArrayList<>();
        List<ReadingStatus> statuses = List.of(WANT, READING, READ, PAUSE, STOP);

        IntStream.rangeClosed(1, 20)
                .forEach(i -> {
                    ReadingStatus readingStatus = statuses.get((i - 1) % statuses.size());
                    Library library = createUserLibrary(user, books.get(i - 1), readingStatus);
                    userLibraries.add(library);
                });

        return userLibraries;
    }

    private static Library createUserLibrary(User user, Book book, ReadingStatus readingStatus) {
        return Library.builder()
                .user(user)
                .book(book)
                .status(readingStatus)
                .build();
    }

    private static List<Rating> createRatings(User user, List<Book> books) {
        return IntStream.range(0, 20)
                .mapToObj(i -> {
                    double rating = i % 5 + 1.0;
                    return createRating(user, books.get(i), rating);
                })
                .collect(Collectors.toList());
    }

    private static Rating createRating(User user, Book book, Double rating) {
        return Rating.builder()
                .user(user)
                .book(book)
                .rating(rating)
                .build();
    }
}
