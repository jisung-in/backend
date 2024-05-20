package com.jisungin.docs.rating;

import com.jisungin.api.rating.RatingController;
import com.jisungin.api.rating.request.RatingCreateRequest;
import com.jisungin.application.rating.RatingService;
import com.jisungin.docs.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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
                .bookIsbn("00000001")
                .rating("3.5")
                .build();

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
