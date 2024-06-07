package com.jisungin.docs.library;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.library.LibraryController;
import com.jisungin.api.library.request.LibraryCreateRequest;
import com.jisungin.api.library.request.LibraryEditRequest;
import com.jisungin.application.library.LibraryService;
import com.jisungin.application.library.request.LibraryCreateServiceRequest;
import com.jisungin.application.library.response.LibraryResponse;
import com.jisungin.docs.RestDocsSupport;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
