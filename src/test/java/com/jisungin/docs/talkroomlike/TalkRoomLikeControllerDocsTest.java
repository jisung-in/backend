package com.jisungin.docs.talkroomlike;

import static org.mockito.ArgumentMatchers.anyLong;
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

import com.jisungin.api.talkroomlike.TalkRoomLikeController;
import com.jisungin.application.talkroomlike.TalkRoomLikeService;
import com.jisungin.application.talkroomlike.response.TalkRoomIds;
import com.jisungin.docs.RestDocsSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class TalkRoomLikeControllerDocsTest extends RestDocsSupport {

    private final TalkRoomLikeService talkRoomLikeService = mock(TalkRoomLikeService.class);

    @Override
    protected Object initController() {
        return new TalkRoomLikeController(talkRoomLikeService);
    }

    @Test
    @DisplayName("좋아요한 토론방 조회 API")
    void findLikeTalkRoomIds() throws Exception {
        // given
        given(talkRoomLikeService.findTalkRoomIds(anyLong()))
                .willReturn(TalkRoomIds.of(List.of(1L, 2L, 3L, 4L, 5L)));

        // when // then
        mockMvc.perform(get("/v1/talk-rooms/likes")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("talkroomlike/get-ids",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.talkRoomIds").type(ARRAY).description("좋아요 누른 토크방 ID")
                        )
                ));
    }

    @Test
    @DisplayName("토론방 좋아요하는 API")
    void likeTalkRoom() throws Exception {
        mockMvc.perform(
                        post("/v1/talk-rooms/{talkRoomId}/likes", "1")
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
    @DisplayName("토론방 좋아요 취소하는 API")
    void unLikeTalkRoom() throws Exception {
        mockMvc.perform(
                        delete("/v1/talk-rooms/{talkRoomId}/likes", "1")
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
