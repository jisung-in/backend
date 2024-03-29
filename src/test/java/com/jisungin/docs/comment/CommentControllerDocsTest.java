package com.jisungin.docs.comment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.comment.CommentController;
import com.jisungin.api.comment.request.CommentCreateRequest;
import com.jisungin.api.comment.request.CommentEditRequest;
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.application.comment.CommentService;
import com.jisungin.application.comment.request.CommentCreateServiceRequest;
import com.jisungin.application.comment.request.CommentEditServiceRequest;
import com.jisungin.application.comment.response.CommentResponse;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class CommentControllerDocsTest extends RestDocsSupport {

    private final CommentService commentService = mock(CommentService.class);

    @Override
    protected Object initController() {
        return new CommentController(commentService);
    }

    @Test
    @DisplayName("의견을 작성하는 API")
    void createComment() throws Exception {
        Long talkRoomId = 1L;
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("의견 내용")
                .build();

        given(commentService.writeComment(any(CommentCreateServiceRequest.class), any(Long.class),
                any(AuthContext.class)))
                .willReturn(CommentResponse.builder()
                        .content("의견 내용")
                        .userName("user")
                        .build());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/v1/talk-rooms/{talkRoomId}/comments", talkRoomId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("talkRoomId")
                                        .description("토론방 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("의견 내용")
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
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("의견 내용"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("작성자 이름")
                        )
                ));
    }

    @Test
    @DisplayName("의견을 수정하는 API")
    void editComment() throws Exception {
        Long commentId = 1L;
        CommentEditRequest request = CommentEditRequest.builder()
                .content("수정된 의견 내용")
                .build();

        given(commentService.editComment(any(Long.class), any(CommentEditServiceRequest.class),
                any(AuthContext.class)))
                .willReturn(CommentResponse.builder()
                        .content("수정된 의견 내용")
                        .userName("user")
                        .build());

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/v1/talk-rooms/comments/{commentId}", commentId)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment/edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId")
                                        .description("의견 ID")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("수정된 의견 내용")
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
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("수정된 의견 내용"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("작성자 이름")
                        )
                ));
    }

    @Test
    @DisplayName("의견을 삭제하는 API")
    void deleteComment() throws Exception {
        Long commentId = 1L;

        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/v1/talk-rooms/comments/{commentId}", commentId)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("comment/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId")
                                        .description("의견 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("return data null")
                        )
                ));
    }
}
