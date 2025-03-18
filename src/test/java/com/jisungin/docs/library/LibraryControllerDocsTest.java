package com.jisungin.docs.library;

import com.jisungin.api.library.LibraryController;
import com.jisungin.api.library.request.LibraryCreateRequest;
import com.jisungin.api.library.request.LibraryEditRequest;
import com.jisungin.application.PageResponse;
import com.jisungin.application.library.LibraryService;
import com.jisungin.application.library.request.LibraryCreateServiceRequest;
import com.jisungin.application.library.response.LibraryResponse;
import com.jisungin.application.library.response.UserReadingStatusResponse;
import com.jisungin.application.library.request.UserReadingStatusGetAllServiceRequest;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LibraryControllerDocsTest extends RestDocsSupport {

    private final LibraryService libraryService = mock(LibraryService.class);

    @Override
    protected Object initController() {
        return new LibraryController(libraryService);
    }

    @Test
    @DisplayName("서재 조회 API")
    public void findLibraries() throws Exception {
        // given
        given(libraryService.findLibraries(anyLong()))
                .willReturn(createLibraryResponses());

        // when // then
        mockMvc.perform(get("/v1/libraries")
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("library/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data[]").type(ARRAY).description("응답 데이터"),
                                fieldWithPath("data[].id").type(NUMBER).description("서재 ID"),
                                fieldWithPath("data[].bookIsbn").type(STRING).description("도서 ISBN"),
                                fieldWithPath("data[].status").type(STRING).description("서재 도서 상태")
                        )
                ));
    }

    @Test
    @DisplayName("서재 생성 API")
    public void createLibrary() throws Exception {
        String isbn = "000000000001";

        LibraryCreateRequest request = LibraryCreateRequest.builder()
                .isbn(isbn)
                .readingStatus("read")
                .build();

        given(libraryService.createLibrary(any(LibraryCreateServiceRequest.class), anyLong()))
                .willReturn(createLibraryResponse(1L, isbn));

        mockMvc.perform(post("/v1/libraries")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("library/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("isbn").type(STRING).description("도서 ISBN"),
                                fieldWithPath("readingStatus").type(STRING).description(
                                        "도서 상태 : want(읽고 싶은), reading(읽는 중), read(읽음), pause(잠시 멈춤), stop(중단)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("서재 ID"),
                                fieldWithPath("data.bookIsbn").type(STRING).description("도서 ISBN"),
                                fieldWithPath("data.status").type(STRING).description("서재 도서 상태")
                        )
                ));
    }

    @Test
    @DisplayName("서재 수정 API")
    public void editUserLibrary() throws Exception {
        String isbn = "000000000001";

        LibraryEditRequest request = LibraryEditRequest.builder()
                .isbn(isbn)
                .readingStatus("pause")
                .build();

        mockMvc.perform(patch("/v1/libraries/{libraryId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("library/edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("libraryId").description("서재 ID")
                        ),
                        requestFields(
                                fieldWithPath("isbn").description("도서 ISBN"),
                                fieldWithPath("readingStatus")
                                        .description(
                                                "변경할 도서 상태: want(읽고 싶은), reading(읽는 중), read(읽음), pause(잠시 멈춤), stop(중단)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터").optional()
                        )
                ));
    }

    @Test
    @DisplayName("서재 삭제 API")
    public void deleteLibrary() throws Exception {
        mockMvc.perform(delete("/v1/libraries/{libraryId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("library/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("libraryId").description("서재 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터").optional()
                        )
                ));

    }

    @DisplayName("유저 독서 상태 페이징 조회 API")
    @Test
    void getReadingStatuses() throws Exception {
        List<UserReadingStatusResponse> readingStatusesResponse = createReadingStatusResponse();

        PageResponse<UserReadingStatusResponse> response = PageResponse.<UserReadingStatusResponse>builder()
                .size(10)
                .totalCount(10)
                .queryResponse(readingStatusesResponse)
                .build();

        given(libraryService.getUserReadingStatuses(anyLong(), any(UserReadingStatusGetAllServiceRequest.class)))
                .willReturn(response);

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/users/libraries/statuses")
                        .param("page", "1")
                        .param("size", "10")
                        .param("order", "dictionary")
                        .param("status", "want")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print())
                .andDo(document("library/user-statuses",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지 번호"),
                                parameterWithName("size")
                                        .description("페이지 사이즈"),
                                parameterWithName("order")
                                        .description(
                                                "정렬 기준 : dictionary(가나다순), rating_avg_desc(평균 별점 높은 순)"),
                                parameterWithName("status")
                                        .description(
                                                "선택 기준 : want(읽고 싶은), reading(읽는 중), pause(잠시 멈춤)," +
                                                        "stop(중단), none(상관없음)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.queryResponse").type(JsonFieldType.ARRAY)
                                        .description("책 목록"),
                                fieldWithPath("data.queryResponse[].isbn").type(JsonFieldType.STRING)
                                        .description("책 isbn"),
                                fieldWithPath("data.queryResponse[].bookTitle").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.queryResponse[].bookImage").type(JsonFieldType.STRING)
                                        .description("책 표지"),
                                fieldWithPath("data.queryResponse[].ratingAvg").type(JsonFieldType.NUMBER)
                                        .description("책 평균 별점"),
                                fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER)
                                        .description("데이터 총 개수"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                        .description("해당 페이지 데이터 개수")
                        )
                ));
    }

    private List<UserReadingStatusResponse> createReadingStatusResponse() {
        return IntStream.range(0, 10)
                .mapToObj(i -> UserReadingStatusResponse.builder()
                        .isbn(String.valueOf(i))
                        .bookTitle("책 제목" + i)
                        .bookImage("책 표지" + i)
                        .ratingAvg(i % 5.0 + 1)
                        .build())
                .toList();
    }

    private LibraryResponse createLibraryResponse(Long id, String bookIsbn) {
        return LibraryResponse.builder()
                .id(id)
                .bookIsbn(bookIsbn)
                .status("읽음")
                .build();
    }

    private List<LibraryResponse> createLibraryResponses() {
        return LongStream.rangeClosed(1, 5)
                .mapToObj(i -> createLibraryResponse(i, String.valueOf(i)))
                .toList();
    }
}
