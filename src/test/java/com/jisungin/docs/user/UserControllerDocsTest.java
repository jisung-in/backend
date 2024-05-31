package com.jisungin.docs.user;

import com.jisungin.api.user.UserController;
import com.jisungin.application.PageResponse;
import com.jisungin.application.rating.response.RatingGetResponse;
import com.jisungin.application.review.response.ReviewContentGetAllResponse;
import com.jisungin.application.review.response.ReviewContentResponse;
import com.jisungin.application.user.UserService;
import com.jisungin.application.user.request.ReviewContentGetAllServiceRequest;
import com.jisungin.application.user.request.UserRatingGetAllServiceRequest;
import com.jisungin.application.user.request.UserReadingStatusGetAllServiceRequest;
import com.jisungin.application.user.response.UserInfoResponse;
import com.jisungin.application.userlibrary.response.UserReadingStatusResponse;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
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

    @DisplayName("유저 한줄평 페이징 조회 API")
    @Test
    void getUserReviews() throws Exception {
        List<ReviewContentResponse> reviewGetAllResponse = createReviewFindAllResponse();
        List<Long> likeReviewIds = createLikeReviewIds();

        PageResponse<ReviewContentResponse> reviewContents = PageResponse.<ReviewContentResponse>builder()
                .size(10)
                .totalCount(10)
                .queryResponse(reviewGetAllResponse)
                .build();

        given(userService.getReviewContents(anyLong(), any(ReviewContentGetAllServiceRequest.class)))
                .willReturn(ReviewContentGetAllResponse.of(reviewContents, likeReviewIds));

        mockMvc.perform(get("/v1/users/reviews")
                        .param("page", "1")
                        .param("size", "10")
                        .param("order", "rating_asc")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print())
                .andDo(document("review/findAll",
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
                                                        "rating_avg_asc(별점 평균 오름차), rating_avg_desc(별점 평균 내림차)")
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
                                fieldWithPath("data.reviewContents").type(JsonFieldType.OBJECT)
                                        .description("리뷰 컨텐츠"),
                                fieldWithPath("data.reviewContents.queryResponse").type(JsonFieldType.ARRAY)
                                        .description("리뷰 목록"),
                                fieldWithPath("data.reviewContents.queryResponse[].reviewId").type(JsonFieldType.NUMBER)
                                        .description("리뷰 ID"),
                                fieldWithPath("data.reviewContents.queryResponse[].userImage").type(
                                                JsonFieldType.STRING)
                                        .description("유저 프로필 이미지"),
                                fieldWithPath("data.reviewContents.queryResponse[].userName").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.reviewContents.queryResponse[].rating").type(JsonFieldType.NUMBER)
                                        .description("별점"),
                                fieldWithPath("data.reviewContents.queryResponse[].content").type(JsonFieldType.STRING)
                                        .description("리뷰 내용"),
                                fieldWithPath("data.reviewContents.queryResponse[].isbn").type(JsonFieldType.STRING)
                                        .description("책 ISBN"),
                                fieldWithPath("data.reviewContents.queryResponse[].title").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.reviewContents.queryResponse[].bookImage").type(JsonFieldType.STRING)
                                        .description("책 표지"),
                                fieldWithPath("data.reviewContents.queryResponse[].authors").type(JsonFieldType.STRING)
                                        .description("책 저자"),
                                fieldWithPath("data.reviewContents.queryResponse[].publisher").type(JsonFieldType.STRING)
                                        .description("책 출판사"),
                                fieldWithPath("data.reviewContents.totalCount").type(JsonFieldType.NUMBER)
                                        .description("총 리뷰 개수"),
                                fieldWithPath("data.reviewContents.size").type(JsonFieldType.NUMBER)
                                        .description("해당 페이지 리뷰 개수"),
                                fieldWithPath("data.userLikes").type(JsonFieldType.ARRAY)
                                        .description("유저가 좋아요한 리뷰 ID 목록")
                        )
                ));
    }

    @DisplayName("유저 독서 상태 페이징 조회 API")
    @Test
    void getReadingStatuses() throws Exception {
        List<UserReadingStatusResponse> readingStatusesResponse = createReadingStatusResponse();

        PageResponse<UserReadingStatusResponse> response = PageResponse.<UserReadingStatusResponse>builder()
                .size(10)
                .totalCount(10)
                .queryResponse(readingStatusesResponse)
                .build();

        given(userService.getUserReadingStatuses(anyLong(), any(UserReadingStatusGetAllServiceRequest.class)))
                .willReturn(response);

        mockMvc.perform(get("/v1/users/statuses")
                        .param("page", "1")
                        .param("size", "10")
                        .param("order", "dictionary")
                        .param("status", "want")
                        .contentType(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print())
                .andDo(document("user-library/get-status",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지 번호"),
                                parameterWithName("size")
                                        .description("페이지 사이즈"),
                                parameterWithName("order")
                                        .description(
                                                "정렬 기준 : dictionary(가나다순), rating_avg_desc(평균 별점 높은 순)"),
                                parameterWithName("status")
                                        .description(
                                                "선택 기준 : want(읽고 싶은), reading(읽는 중), pause(잠시 멈춤)," +
                                                        "stop(중단), none(상관없음)")
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
                                fieldWithPath("data.queryResponse[].bookTitle").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.queryResponse[].bookImage").type(JsonFieldType.STRING)
                                        .description("책 표지"),
                                fieldWithPath("data.queryResponse[].ratingAvg").type(JsonFieldType.NUMBER)
                                        .description("책 평균 별점"),
                                fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER)
                                        .description("데이터 총 개수"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                        .description("해당 페이지 데이터 개수")
                        )
                ));
    }

    @DisplayName("유저 상세 조회 API")
    @Test
    void getUserInfo() throws Exception {
        given(userService.getUserInfo(anyLong()))
                .willReturn(UserInfoResponse.builder()
                        .userId(1L)
                        .userName("유저 이름")
                        .userImage("유저 프로필 이미지")
                        .build());

        mockMvc.perform(
                        get("/v1/users/me")
                                .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user/get",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("유저 정보"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER)
                                        .description("유저 아이디"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.userImage").type(JsonFieldType.STRING)
                                        .description("유저 프로필 이미지")
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

    private List<ReviewContentResponse> createReviewFindAllResponse() {
        return IntStream.range(0, 10)
                .mapToObj(i -> ReviewContentResponse.builder()
                        .reviewId(i + 1L)
                        .userImage("userImage" + i)
                        .userName("name" + i)
                        .rating(i % 5.0 + 1)
                        .content("content" + i)
                        .isbn(String.valueOf(i))
                        .title("title" + i)
                        .bookImage("bookImage" + i)
                        .authors("저자" + i)
                        .publisher("출판사" + i)
                        .build())
                .toList();
    }

    private List<Long> createLikeReviewIds() {
        return IntStream.rangeClosed(1, 5)
                .mapToLong(i -> i)
                .boxed()
                .collect(Collectors.toList());
    }

    private List<UserReadingStatusResponse> createReadingStatusResponse() {
        return IntStream.range(0, 10)
                .mapToObj(i -> UserReadingStatusResponse.builder()
                        .isbn(String.valueOf(i))
                        .bookTitle("책 제목" + i)
                        .bookImage("책 표지" + i)
                        .ratingAvg(i % 5.0 + 1)
                        .build())
                .toList();
    }

}
