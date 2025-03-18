package com.jisungin.docs.rating;

import com.jisungin.api.rating.RatingController;
import com.jisungin.api.rating.request.RatingCreateRequest;
import com.jisungin.api.rating.request.RatingUpdateRequest;
import com.jisungin.application.PageResponse;
import com.jisungin.application.rating.RatingService;
import com.jisungin.application.rating.request.RatingCreateServiceRequest;
import com.jisungin.application.rating.response.RatingCreateResponse;
import com.jisungin.application.rating.response.RatingGetOneResponse;
import com.jisungin.application.rating.response.RatingGetResponse;
import com.jisungin.application.rating.request.UserRatingGetAllServiceRequest;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RatingControllerDocsTest extends RestDocsSupport {

    private final RatingService ratingService = mock(RatingService.class);

    @Override
    protected Object initController() {
        return new RatingController(ratingService);
    }

    @DisplayName("별점 생성 API")
    @Test
    void createRating() throws Exception {
        RatingCreateRequest request = RatingCreateRequest.builder()
                .bookIsbn("책 isbn")
                .rating("0.0")
                .build();

        given(ratingService.creatingRating(anyLong(), any(RatingCreateServiceRequest.class)))
                .willReturn(RatingCreateResponse.builder()
                        .id(0L)
                        .rating(0.0)
                        .isbn("책 isbn")
                        .build());

        mockMvc.perform(
                        post("/v1/ratings")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("rating/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("bookIsbn").type(JsonFieldType.STRING)
                                        .description("책 isbn"),
                                fieldWithPath("rating").type(JsonFieldType.STRING)
                                        .description("별점")
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
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("별점 id"),
                                fieldWithPath("data.rating").type(JsonFieldType.NUMBER)
                                        .description("별점"),
                                fieldWithPath("data.isbn").type(JsonFieldType.STRING)
                                        .description("책 isbn")
                        )
                ));
    }

    @DisplayName("별점 단일 조회 API")
    @Test
    void getRating() throws Exception {
        given(ratingService.getRating(anyLong(), anyString()))
                .willReturn(RatingGetOneResponse.builder()
                        .id(0L)
                        .rating(0.0)
                        .isbn("책 isbn")
                        .build());

        mockMvc.perform(
                        get("/v1/ratings")
                                .param("isbn", "00000000")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("rating/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("isbn")
                                        .description("책 isbn")
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
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("별점 id"),
                                fieldWithPath("data.rating").type(JsonFieldType.NUMBER)
                                        .description("별점"),
                                fieldWithPath("data.isbn").type(JsonFieldType.STRING)
                                        .description("책 isbn")
                        )
                ));
    }

    @DisplayName("별점 수정 API")
    @Test
    void updateRating() throws Exception {
        RatingUpdateRequest request = RatingUpdateRequest.builder()
                .bookIsbn("책 isbn")
                .rating("0.0")
                .build();

        mockMvc.perform(
                        patch("/v1/ratings/{ratingId}", 1)
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("rating/update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("ratingId")
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

    @DisplayName("별점 삭제 API")
    @Test
    void deleteRating() throws Exception {
        mockMvc.perform(
                        delete("/v1/ratings/{ratingId}", 1)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("rating/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("ratingId")
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

    @DisplayName("유저 별점 페이징 조회 API")
    @Test
    void getUserRatings() throws Exception {

        List<RatingGetResponse> ratingGetAllResponse = createRatingFindAllResponse();

        PageResponse<RatingGetResponse> response = PageResponse.<RatingGetResponse>builder()
                .size(10)
                .totalCount(10)
                .queryResponse(ratingGetAllResponse)
                .build();

        given(ratingService.getUserRatings(anyLong(), any(UserRatingGetAllServiceRequest.class)))
                .willReturn(response);

        mockMvc.perform(get("/v1/users/ratings")
                        .param("page", "1")
                        .param("size", "10")
                        .param("order", "rating_asc")
                        .param("rating", "")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print())
                .andDo(document("rating/user-ratings",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지 번호"),
                                parameterWithName("size")
                                        .description("페이지 사이즈"),
                                parameterWithName("order")
                                        .description(
                                                "정렬 기준 : date(날짜순), rating_asc(별점 오름차), rating_desc(별점 내림차), " +
                                                        "rating_avg_asc(별점 평균 오름차), rating_avg_desc(별점 평균 내림차)"),
                                parameterWithName("rating")
                                        .description("해당 별점만 조회 -> 1.0, 2.5, 4.0 (전체 조회는 null로)").optional()
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
                                fieldWithPath("data.queryResponse[].title").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.queryResponse[].image").type(JsonFieldType.STRING)
                                        .description("책 표지"),
                                fieldWithPath("data.queryResponse[].rating").type(JsonFieldType.NUMBER)
                                        .description("책 별점"),
                                fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER)
                                        .description("데이터 총 개수"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                        .description("해당 페이지 데이터 개수")
                        )
                ));
    }

    private List<RatingGetResponse> createRatingFindAllResponse() {
        return IntStream.range(0, 10)
                .mapToObj(i -> RatingGetResponse.builder()
                        .isbn(String.valueOf(i))
                        .title("책 제목" + i)
                        .image("책 이미지" + i)
                        .rating(i % 5.0 + 1)
                        .build())
                .toList();
    }
}
