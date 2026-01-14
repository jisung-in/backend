package com.jisungin.docs.book;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.book.BookController;
import com.jisungin.api.book.request.BookCreateRequest;
import com.jisungin.application.OffsetLimit;
import com.jisungin.application.PageResponse;
import com.jisungin.application.book.BestSellerService;
import com.jisungin.application.book.BookService;
import com.jisungin.application.book.request.BookCreateServiceRequest;
import com.jisungin.application.book.response.BookWithRankingResponse;
import com.jisungin.application.book.response.BookFindAllResponse;
import com.jisungin.application.book.response.BookResponse;
import com.jisungin.docs.RestDocsSupport;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BookControllerDocsTest extends RestDocsSupport {

    private final BookService bookService = mock(BookService.class);
    private final BestSellerService bestSellerService = mock(BestSellerService.class);

    @Override
    protected Object initController() {
        return new BookController(bookService, bestSellerService);
    }

    @Test
    @DisplayName("도서 단건 조회 API")
    public void getBook() throws Exception {
        // given
        String isbn = "0000000000001";

        given(bookService.getBook(anyString()))
                .willReturn(createBookResponseWithIsbn(isbn));

        // when // then
        mockMvc.perform(get("/v1/books/{isbn}", isbn)
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("book/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("isbn").description("도서 ISBN")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.title").type(STRING).description("도서 제목"),
                                fieldWithPath("data.content").type(STRING).description("도서 내용"),
                                fieldWithPath("data.isbn").type(STRING).description("도서 ISBN"),
                                fieldWithPath("data.publisher").type(STRING).description("도서 출판사"),
                                fieldWithPath("data.imageUrl").type(STRING).description("도서 이미지 URL"),
                                fieldWithPath("data.thumbnail").type(STRING).description("도서 썸네일 URL"),
                                fieldWithPath("data.authors[]").type(ARRAY).description("도서 저자"),
                                fieldWithPath("data.ratingAverage").type(NUMBER).description("도서 별점"),
                                fieldWithPath("data.dateTime").type(ARRAY).description("도서 출판일")
                        )
                ));
    }

    @Test
    @DisplayName("도서 페이징 조회 API")
    public void getBooks() throws Exception {
        // given
        List<BookFindAllResponse> response = createBookFindAllResponse();
        PageResponse<BookFindAllResponse> pageResponse = PageResponse.of(response.size(), response.size(),
                response);

        given(bookService.getBooks(any(OffsetLimit.class))).willReturn(pageResponse);

        // when // then
        mockMvc.perform(get("/v1/books")
                        .param("page", "1")
                        .param("size", "10")
                        .param("order", "recent")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("book/get-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈"),
                                parameterWithName("order")
                                        .description("정렬 기준 -> recent(최근 등록된 도서), comment(토크 많은 순)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.totalCount").type(NUMBER).description("도서 총 개수"),
                                fieldWithPath("data.size").type(NUMBER).description("도서 반환 사이즈"),
                                fieldWithPath("data.queryResponse").type(ARRAY).description("도서 정보"),
                                fieldWithPath("data.queryResponse[].isbn").type(STRING).description("도서 ISBN"),
                                fieldWithPath("data.queryResponse[].title").type(STRING).description("도서 제목"),
                                fieldWithPath("data.queryResponse[].publisher").type(STRING).description("도서 춢판사"),
                                fieldWithPath("data.queryResponse[].thumbnail").type(STRING).description("도서 썸네일 URL"),
                                fieldWithPath("data.queryResponse[].authors").type(ARRAY).description("도서 저자"),
                                fieldWithPath("data.queryResponse[].dateTime").type(ARRAY).description("도서 출판일")
                        )
                ));
    }

    @Test
    @DisplayName("베스트 셀러 조회 API")
    public void getBestSellers() throws Exception {
        // given
        List<BookWithRankingResponse> response = createBookWithRankingResponse();

        PageResponse<BookWithRankingResponse> pageResponse = PageResponse.of(response.size(),
                response.size(), response);

        given(bestSellerService.getBestSellers(any(OffsetLimit.class)))
                .willReturn(pageResponse);

        // when // then
        mockMvc.perform(get("/v1/books/best-seller")
                        .param("page", "1")
                        .param("size", "5")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("book/get-best-seller",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호"),
                                parameterWithName("size").description("페이지 사이즈")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.totalCount").type(NUMBER).description("베스트 셀러 총 개수"),
                                fieldWithPath("data.size").type(NUMBER).description("베스트 셀러 반환 사이즈"),
                                fieldWithPath("data.queryResponse").type(ARRAY).description("베스트 셀러 데이터"),
                                fieldWithPath("data.queryResponse[].ranking").type(NUMBER).description("배스트 셀러 순위"),
                                fieldWithPath("data.queryResponse[].isbn").type(STRING).description("도서 ISBN"),
                                fieldWithPath("data.queryResponse[].title").type(STRING).description("도서 제목"),
                                fieldWithPath("data.queryResponse[].publisher").type(STRING).description("도서 출판사"),
                                fieldWithPath("data.queryResponse[].thumbnail").type(STRING).description("도서 썸네일 URL"),
                                fieldWithPath("data.queryResponse[].authors[]").type(ARRAY).description("도서 저자"),
                                fieldWithPath("data.queryResponse[].dateTime").type(ARRAY).description("도서 출판일")
                        )
                ));
    }

    @Test
    @DisplayName("도서 생성 API")
    public void createBook() throws Exception {
        // given
        BookCreateRequest request = BookCreateRequest.builder()
                .isbn("0000000000001")
                .title("book title")
                .contents("book content")
                .dateTime("2024-01-01T00:00:00.000+09:00")
                .authors(new String[]{"book author1", "book author2"})
                .publisher("book publisher")
                .thumbnail("www.book-thumbnail.com")
                .build();

        given(bookService.createBook(any(BookCreateServiceRequest.class)))
                .willReturn(createBookResponseWithIsbn(request.getIsbn()));

        // when // then
        mockMvc.perform(post("/v1/books")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("book/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("title").type(STRING).description("도서 제목"),
                                fieldWithPath("contents").type(STRING).description("도서 내용"),
                                fieldWithPath("isbn").type(STRING).description("도서 ISBN"),
                                fieldWithPath("datetime").type(STRING).description("도서 출판일"),
                                fieldWithPath("authors[]").type(ARRAY).description("도서 저자"),
                                fieldWithPath("publisher").type(STRING).description("도서 출판사"),
                                fieldWithPath("thumbnail").type(STRING).description("도서 썸네일 URL")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.title").type(STRING).description("도서 제목"),
                                fieldWithPath("data.content").type(STRING).description("도서 내용"),
                                fieldWithPath("data.isbn").type(STRING).description("도서 ISBN"),
                                fieldWithPath("data.publisher").type(STRING).description("도서 출판사"),
                                fieldWithPath("data.imageUrl").type(STRING).description("도서 이미지 URL"),
                                fieldWithPath("data.thumbnail").type(STRING).description("도서 썸네일 URL"),
                                fieldWithPath("data.authors[]").type(ARRAY).description("도서 저자"),
                                fieldWithPath("data.ratingAverage").type(NUMBER).description("도서 별점"),
                                fieldWithPath("data.dateTime").type(ARRAY).description("도서 출판일")
                        )
                ));
    }

    private BookResponse createBookResponseWithIsbn(String isbn) {
        return BookResponse.builder()
                .title("book title")
                .content("book content")
                .isbn(isbn)
                .publisher("book publisher")
                .imageUrl("www.book-image.com")
                .thumbnail("www.book-thumbnail.com")
                .authors("book author1,book author2")
                .ratingAverage(5.0)
                .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
    }

    private List<BookFindAllResponse> createBookFindAllResponse() {
        return IntStream.rangeClosed(1, 5)
                .mapToObj(i -> BookFindAllResponse.builder()
                        .isbn("000000000000" + i)
                        .title("book title" + i)
                        .publisher("book publisher" + i)
                        .thumbnail("www.book-thumbnail.com/" + i)
                        .authors("book author1,book author2")
                        .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build())
                .toList();
    }

    private List<BookWithRankingResponse> createBookWithRankingResponse() {
        return LongStream.rangeClosed(1, 5)
                .mapToObj(i -> BookWithRankingResponse.builder()
                        .ranking(i)
                        .isbn("00000000000" + i)
                        .title("book title" + i)
                        .publisher("book publisher" + i)
                        .thumbnail("www.book-thumbnail.com/" + i)
                        .authors(new String[]{"book author1", "book author2"})
                        .dateTime(LocalDateTime.of(2024, 1, 1, 0, 0))
                        .build())
                .toList();
    }

}
