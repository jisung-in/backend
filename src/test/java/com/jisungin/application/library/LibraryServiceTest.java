package com.jisungin.application.library;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.jisungin.ServiceTestSupport;
import com.jisungin.application.library.request.LibraryCreateServiceRequest;
import com.jisungin.application.library.request.LibraryEditServiceRequest;
import com.jisungin.application.library.response.LibraryResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import com.jisungin.domain.library.Library;
import com.jisungin.domain.library.repository.LibraryRepository;
import com.jisungin.domain.user.OauthId;
import com.jisungin.domain.user.OauthType;
import com.jisungin.domain.user.User;
import com.jisungin.domain.user.repository.UserRepository;
import com.jisungin.exception.BusinessException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LibraryServiceTest extends ServiceTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private LibraryService libraryService;

    @AfterEach
    public void tearDown() {
        libraryRepository.deleteAllInBatch();
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

    private static List<Book> createBooks() {
        return IntStream.rangeClosed(1, 5)
                .mapToObj(i -> createBookWithIsbn(String.valueOf(i)))
                .toList();
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

}
