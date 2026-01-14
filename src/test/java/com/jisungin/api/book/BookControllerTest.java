package com.jisungin.api.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.ControllerTestSupport;
import com.jisungin.api.book.request.BookCreateRequest;
import com.jisungin.application.book.response.BookResponse;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BookControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("책을 조회한다.")
    public void getBook() throws Exception {
        // given
        when(bookService.getBook(any(String.class))).thenReturn(createResponse());

        // when // then
        mockMvc.perform(get("/v1/books/{isbn}", "isbn")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("책을 페이징 조회한다.")
    public void getBooks() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/books?page=1&size=10")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("베스트 셀러 도서를 조회한다.")
    public void getBestSellers() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/books/best-seller?page=1&size=5")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("신규 도서를 등록한다.")
    public void createBook() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .isbn("도서 ISBN")
                .dateTime("2024-01-01T00:00:00.000+09:00")
                .authors(new String[]{"도서 저자1", "도서 저자2"})
                .publisher("도서 출판사")
                .thumbnail("도서 썸네일")
                .build();

        when(bookService.createBook(request.toServiceRequest())).thenReturn(createResponse());

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
    @DisplayName("신규 책을 등록 시 책 제목 입력은 필수 값이다.")
    public void createBookWithTitle() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .contents("도서 내용")
                .isbn("도서 ISBN")
                .dateTime("2024-01-01T00:00:00.000+09:00")
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
    @DisplayName("신규 책을 등록 시 책 내용 입력은 필수 값이다.")
    public void createBookWithContent() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .isbn("도서 ISBN")
                .dateTime("2024-01-01T00:00:00.000+09:00")
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
    @DisplayName("신규 책 등록 시 책 ISBN 입력은 필수 값이다.")
    public void createBookWithIsbn() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .dateTime("2024-01-01T00:00:00.000+09:00")
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
    @DisplayName("신규 책 등록 시 책 출판일 입력은 필수 값이다.")
    public void createBookWithDateTime() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .isbn("도서 ISBN")
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
    @DisplayName("신규 책 등록 시 책 저자 입력은 필수 값이다.")
    public void createBookWithAuthors() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .isbn("도서 ISBN")
                .dateTime("2024-01-01T00:00:00.000+09:00")
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
    @DisplayName("신규 책 등록 시 출판사 입력은 필수 값이다.")
    public void createBookWithPublisher() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .isbn("도서 ISBN")
                .dateTime("2024-01-01T00:00:00.000+09:00")
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
    @DisplayName("신규 책 등록 시 썸네일 입력은 필수 값이다.")
    public void createBookWithThumbnail() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .title("도서 정보")
                .contents("도서 내용")
                .isbn("도서 ISBN")
                .dateTime("2024-01-01T00:00:00.000+09:00")
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

    private static BookResponse createResponse() {
        return BookResponse.builder()
                .title("책 이름")
                .content("책 설명")
                .isbn("1111111111")
                .authors("책 저자1, 책 저자2")
                .publisher("책 출판사")
                .imageUrl("책 이미지 URL")
                .thumbnail("책 썸네일 URL")
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .ratingAverage(0.0)
                .build();
    }

}
