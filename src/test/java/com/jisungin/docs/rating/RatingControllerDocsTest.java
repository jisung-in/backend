package com.jisungin.docs.rating;

import com.jisungin.api.rating.RatingController;
import com.jisungin.api.rating.request.RatingCreateRequest;
import com.jisungin.api.rating.request.RatingUpdateRequest;
import com.jisungin.application.rating.RatingService;
import com.jisungin.application.rating.request.RatingCreateServiceRequest;
import com.jisungin.application.rating.response.RatingCreateResponse;
import com.jisungin.application.rating.response.RatingGetOneResponse;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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

}
