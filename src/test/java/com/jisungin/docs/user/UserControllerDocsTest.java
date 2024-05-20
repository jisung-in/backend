package com.jisungin.docs.user;

import com.jisungin.api.user.UserController;
import com.jisungin.application.PageResponse;
import com.jisungin.application.rating.response.RatingGetResponse;
import com.jisungin.application.user.UserService;
import com.jisungin.application.user.request.UserRatingGetAllServiceRequest;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);

    @Override
    protected Object initController() {
        return new UserController(userService);
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

        given(userService.getUserRatings(anyLong(), any(UserRatingGetAllServiceRequest.class)))
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
                .andDo(document("rating/findAll",
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
