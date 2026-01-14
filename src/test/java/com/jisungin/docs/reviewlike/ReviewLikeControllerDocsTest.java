package com.jisungin.docs.reviewlike;

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
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.OBJECT;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.reviewlike.ReviewLikeController;
import com.jisungin.application.reviewlike.ReviewLikeService;
import com.jisungin.application.reviewlike.response.ReviewIds;
import com.jisungin.docs.RestDocsSupport;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ReviewLikeControllerDocsTest extends RestDocsSupport {

    private final ReviewLikeService reviewLikeService = mock(ReviewLikeService.class);

    @Override
    protected Object initController() {
        return new ReviewLikeController(reviewLikeService);
    }

    @DisplayName("좋아요한 리뷰 ID 조회 API")
    @Test
    public void findLikeReviewIds() throws Exception {
        // given
        given(reviewLikeService.findLikeReviewIds(anyLong()))
                .willReturn(ReviewIds.of(List.of(1L, 2L, 3L, 4L, 5L)));

        // when // then
        mockMvc.perform(get("/v1/reviews/likes")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reviewlike/get-ids",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(OBJECT).description("응답 데이터"),
                                fieldWithPath("data.reviewIds[]").type(ARRAY).description("좋아요 한 리뷰 ID")
                        )
                ));
    }

    @DisplayName("리뷰 좋아요 생성 API")
    @Test
    public void likeReview() throws Exception {
        // given
        Long reviewId = 1L;

        // when // then
        mockMvc.perform(post("/v1/reviews/{reviewId}/likes", reviewId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reviewlike/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(NULL).description("응답 데이터")
                        )
                ));
    }

    @DisplayName("리뷰 좋아요 삭제 API")
    @Test
    public void unlikeReview() throws Exception {
        // given
        Long reviewId = 1L;

        // when // then
        mockMvc.perform(delete("/v1/reviews/{reviewId}/likes", reviewId))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("reviewlike/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId").description("리뷰 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(NUMBER).description("코드"),
                                fieldWithPath("status").type(STRING).description("상태"),
                                fieldWithPath("message").type(STRING).description("메세지"),
                                fieldWithPath("data").type(NULL).description("응답 데이터")
                        )
                ));
    }

}
