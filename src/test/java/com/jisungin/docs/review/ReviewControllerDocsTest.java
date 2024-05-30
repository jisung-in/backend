package com.jisungin.docs.review;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.review.ReviewController;
import com.jisungin.api.review.request.ReviewCreateRequest;
import com.jisungin.application.OffsetLimit;
import com.jisungin.application.SliceResponse;
import com.jisungin.application.review.ReviewService;
import com.jisungin.application.review.response.ReviewWithRatingResponse;
import com.jisungin.docs.RestDocsSupport;
import java.util.List;
import java.util.stream.LongStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

public class ReviewControllerDocsTest extends RestDocsSupport {

    private final ReviewService reviewService = mock(ReviewService.class);

    @Override
    protected Object initController() {
        return new ReviewController(reviewService);
    }

    @DisplayName("도서와 연관된 리뷰 조회 API")
    @Test
    void findBookReviews() throws Exception {
        // given
        String isbn = "000000000001";

        List<ReviewWithRatingResponse> response = LongStream.rangeClosed(1, 10)
                .mapToObj(i -> ReviewWithRatingResponse.builder()
                        .reviewId(i)
                        .ratingId(i)
                        .creatorId(i)
                        .username("작성자 " + i)
                        .profileImage("http://www.profile-image.com/" + i)
                        .reviewContent("리뷰 내용 " + i)
                        .starRating(3.5)
                        .likeCount(20L)
                        .build())
                .toList();

        given(reviewService.findBookReviews(anyString(), any(OffsetLimit.class)))
                .willReturn(SliceResponse.of(response, 0, 10, true));

        // when // then
        mockMvc.perform(
                        get("/v1/books/{isbn}/reviews", isbn)
                                .param("page", "1")
                                .param("size", "10")
                                .param("order", "like")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("review/get-related-book",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("isbn")
                                        .description("도서 ISBN")
                        ),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지 번호"),
                                parameterWithName("size")
                                        .description("페이지 사이즈"),
                                parameterWithName("order").description(
                                        "정렬 기준: like(좋아요 순), recent(최신순), rating_desc(별점 높은 순), rating_asc(별점 낮은 순)")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메세지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.hasContent").type(JsonFieldType.BOOLEAN)
                                        .description("데이터 존재 여부"),
                                fieldWithPath("data.number").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 사이즈"),
                                fieldWithPath("data.isFirst").type(JsonFieldType.BOOLEAN)
                                        .description("첫 번째 페이지 여부"),
                                fieldWithPath("data.isLast").type(JsonFieldType.BOOLEAN)
                                        .description("마지막 페에지 여부"),
                                fieldWithPath("data.content[]").type(JsonFieldType.ARRAY)
                                        .description("슬라이싱 데이터"),
                                fieldWithPath("data.content[].reviewId").type(JsonFieldType.NUMBER)
                                        .description("리뷰 ID"),
                                fieldWithPath("data.content[].ratingId").type(JsonFieldType.NUMBER)
                                        .description("별점 ID"),
                                fieldWithPath("data.content[].creatorId").type(JsonFieldType.NUMBER)
                                        .description("작성자 유저 ID"),
                                fieldWithPath("data.content[].username").type(JsonFieldType.STRING)
                                        .description("작성자 이름"),
                                fieldWithPath("data.content[].profileImage").type(JsonFieldType.STRING)
                                        .description("작성자 프로필 이미지 URL"),
                                fieldWithPath("data.content[].reviewContent").type(JsonFieldType.STRING)
                                        .description("리뷰 작성 내용"),
                                fieldWithPath("data.content[].starRating").type(JsonFieldType.NUMBER)
                                        .description("별점 점수"),
                                fieldWithPath("data.content[].likeCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 개수")
                        ))
                );
    }

    @DisplayName("한줄평을 생성하는 API")
    @Test
    void createReview() throws Exception {
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookIsbn("123456")
                .content("재밌어요.")
                .build();

        mockMvc.perform(
                        post("/v1/reviews")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("review/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("bookIsbn").type(JsonFieldType.STRING)
                                        .description("책 isbn"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("한줄평")
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

    @DisplayName("한줄평을 삭제하는 API")
    @Test
    void deleteRating() throws Exception {
        mockMvc.perform(
                        delete("/v1/reviews/{reviewId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("review/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("reviewId")
                                        .description("별점 ID")
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
