package com.jisungin.api.book;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.book.request.BookCreateRequest;
import com.jisungin.domain.book.Book;
import com.jisungin.domain.book.repository.BookRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @AfterEach
    void tearDown() {
        bookRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("신규 도서를 등록한다.")
    public void createBook() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .url("도서 URL")
                .isbn("도서 isbn")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("신규 도서 등록 시 isbn이 다른 책이어야 한다.")
    void createBookWithDuplicateIsbn() throws Exception {
        // given
        Book book = create();
        bookRepository.save(book);

        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .url("도서 URL")
                .isbn("도서 isbn")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("이미 등록된 책 정보 입니다."));
    }

    @Test
    @DisplayName("신규 도서 등록 시 책 제목은 필수이어야 한다.")
    void createBookWithNonTitle() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .contents("도서 내용")
                .url("도서 URL")
                .isbn("도서 isbn")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 제목 입력은 필수 입니다."));
    }

    @Test
    @DisplayName("신규 도서 등록 시 책 내용은 필수이어야 한다.")
    void createBookWithNonContents() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .url("도서 URL")
                .isbn("도서 isbn")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 내용 입력은 필수 입니다."));
    }

    @Test
    @DisplayName("신규 도서 등록 시 책 경로는 필수이어야 한다.")
    void createBookWithNonUrl() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .isbn("도서 isbn")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 경로 입력은 필수 입니다."));
    }

    @Test
    @DisplayName("신규 도서 등록 시 책 isbn 입력은 필수이어야 한다.")
    void createBookWithNonIsbn() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .url("도서 URL")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 isbn 입력은 필수 입니다."));
    }

    @Test
    @DisplayName("신규 도서 등록 시 책 출판일 입력은 필수이어야 한다.")
    void createBookWithNonDateTime() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .url("도서 URL")
                .isbn("도서 isbn")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 출판일 입력은 필수 입니다."));
    }

    @Test
    @DisplayName("신규 도서 등록 시 책 저자 입력은 필수이어야 한다.")
    void createBookWithAuthors() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .url("도서 URL")
                .isbn("도서 isbn")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 저자 입력은 필수 입니다."));
    }

    @Test
    @DisplayName("신규 도서 등록 시 책 출판사 입력은 필수이어야 한다.")
    void createBookWithPublisher() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .url("도서 URL")
                .isbn("도서 isbn")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .thumbnail("도서 썸네일")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 출판사 입력은 필수 입니다."));
    }

    @Test
    @DisplayName("신규 도서 등록 시 책 썸네일 입력은 필수이어야 한다.")
    void createBookWithThumbnail() throws Exception {
        // given
        Book book = create();
        bookRepository.save(book);

        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .url("도서 URL")
                .isbn("도서 isbn")
                .dateTime("2024-03-15T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .build();

        // when // then
        mockMvc.perform(post("/v1/books")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 썸네일 입력은 필수 입니다."));
    }

    private static Book create() {
        return Book.builder()
                .title("도서 정보")
                .content("도서 내용")
                .url("도서 URL")
                .isbn("도서 isbn")
                .dateTime(LocalDateTime.now())
                .authors("도서 저자1, 도서 저자2")
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();
    }

}
