package com.jisungin.docs.commentlike;

import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.commentlike.CommentLikeController;
import com.jisungin.application.commentlike.CommentLikeService;
import com.jisungin.application.commentlike.response.CommentIds;
import com.jisungin.docs.RestDocsSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class CommentLikeControllerDocsTest extends RestDocsSupport {

    private final CommentLikeService commentLikeService = mock(CommentLikeService.class);

    @Override
    protected Object initController() {
        return new CommentLikeController(commentLikeService);
    }

    @Test
    @DisplayName("좋아요한 의견 ID 조회 API")
    void findLikeCommentIds() throws Exception {
        // given
        given(commentLikeService.findCommentIds(anyLong()))
                .willReturn(CommentIds.of(List.of(1L, 2L, 3L, 4L, 5L)));

        // when // then
        mockMvc.perform(get("/v1/comments/likes")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("commentlike/get-like",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.commentIds").type(ARRAY).description("좋아요 누른 의견 ID")
                        )
                ));
    }

    @Test
    @DisplayName("의견 좋아요하는 API")
    void likeComment() throws Exception {
        mockMvc.perform(
                        post("/v1/comments/{commentId}/likes", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("commentlike/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId")
                                        .description("의견 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(NULL)
                                        .description("return data null")
                        )
                ));
    }

    @Test
    @DisplayName("의견 좋아요 취소하는 API")
    void unLikeComment() throws Exception {
        mockMvc.perform(
                        delete("/v1/comments/{commentId}/likes", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("commentlike/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("commentId")
                                        .description("의견 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(NULL)
                                        .description("return data null")
                        )
                ));
    }

}
