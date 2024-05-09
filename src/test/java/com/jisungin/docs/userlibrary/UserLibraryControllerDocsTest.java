package com.jisungin.docs.userlibrary;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
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
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
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

import com.jisungin.api.userlibrary.UserLibraryController;
import com.jisungin.api.userlibrary.request.UserLibraryCreateRequest;
import com.jisungin.api.userlibrary.request.UserLibraryEditRequest;
import com.jisungin.application.userlibrary.UserLibraryService;
import com.jisungin.application.userlibrary.request.UserLibraryCreateServiceRequest;
import com.jisungin.application.userlibrary.response.UserLibraryResponse;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserLibraryControllerDocsTest extends RestDocsSupport {

    private final UserLibraryService userLibraryService = mock(UserLibraryService.class);

    @Override
    protected Object initController() {
        return new UserLibraryController(userLibraryService);
    }

    @Test
    @DisplayName("서재 단건 조회 API")
    public void getUserLibrary() throws Exception {
        String isbn = "000000000001";

        given(userLibraryService.getUserLibrary(anyLong(), anyString()))
                .willReturn(createUserLibraryResponse());

        mockMvc.perform(get("/v1/user-libraries")
                        .param("isbn", isbn)
                        .accept(APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-library/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("isbn").description("도서 ISBN")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.id").type(NUMBER).description("서재 ID"),
                                fieldWithPath("data.status").type(STRING).description("서재 도서 상태"),
                                fieldWithPath("data.hasReadingStatus").type(BOOLEAN).description("서재 도서 상태 존재 여부")
                        )
                ));
    }

    @Test
    @DisplayName("서재 생성 API")
    public void createUserLibrary() throws Exception {
        String isbn = "000000000001";

        UserLibraryCreateRequest request = UserLibraryCreateRequest.builder()
                .isbn(isbn)
                .readingStatus("read")
                .build();

        given(userLibraryService.createUserLibrary(any(UserLibraryCreateServiceRequest.class), anyLong()))
                .willReturn(createUserLibraryResponse());

        mockMvc.perform(post("/v1/user-libraries")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-library/create",
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
                                fieldWithPath("data.status").type(STRING).description("서재 도서 상태"),
                                fieldWithPath("data.hasReadingStatus").type(BOOLEAN).description("서재 도서 상태 존재 여부")
                        )
                ));
    }

    @Test
    @DisplayName("서재 수정 API")
    public void editUserLibrary() throws Exception {
        String isbn = "000000000001";

        UserLibraryEditRequest request = UserLibraryEditRequest.builder()
                .isbn(isbn)
                .readingStatus("pause")
                .build();

        mockMvc.perform(patch("/v1/user-libraries/{userLibraryId}", 1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-library/edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("userLibraryId").description("서재 ID")
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
    public void deleteUserLibrary() throws Exception {
        mockMvc.perform(delete("/v1/user-libraries/{userLibraryId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-library/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("userLibraryId").description("서재 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터").optional()
                        )
                ));

    }

    private UserLibraryResponse createUserLibraryResponse() {
        return UserLibraryResponse.builder()
                .id(1L)
                .status("읽음")
                .hasReadingStatus(true)
                .build();
    }

}
