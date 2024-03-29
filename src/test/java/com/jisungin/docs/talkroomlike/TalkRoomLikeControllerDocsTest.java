package com.jisungin.docs.talkroomlike;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.talkroomlike.TalkRoomLikeController;
import com.jisungin.application.talkroomlike.TalkRoomLikeService;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class TalkRoomLikeControllerDocsTest extends RestDocsSupport {

    private final TalkRoomLikeService talkRoomLikeService = mock(TalkRoomLikeService.class);

    @Override
    protected Object initController() {
        return new TalkRoomLikeController(talkRoomLikeService);
    }

    @Test
    @DisplayName("토론방 좋아요하는 API")
    void likeTalkRoom() throws Exception {
        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/v1/talk-rooms/{talkRoomId}/likes", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("talkroomlike/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("talkRoomId")
                                        .description("토론방 ID")
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

    @Test
    @DisplayName("토론방 좋아요 취소하는 API")
    void unLikeTalkRoom() throws Exception {
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/v1/talk-rooms/{talkRoomId}/likes", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("talkroomlike/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("talkRoomId")
                                        .description("토론방 ID")
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
