package com.jisungin.api.review;

import com.jisungin.ControllerTestSupport;
import com.jisungin.api.review.request.ReviewCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReviewControllerTest extends ControllerTestSupport {

    @DisplayName("유저가 리뷰를 등록한다.")
    @Test
    void createReview() throws Exception {
        //given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookIsbn("123456")
                .content("재밌어요.")
                .rating("4.5")
                .build();

        //when //then
        mockMvc.perform(
                        post("/v1/reviews")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @DisplayName("책 isbn과 함께 리뷰를 등록해야 한다.")
    @Test
    void createReviewWithoutBookIsbn() throws Exception {
        //given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .content("재밌어요.")
                .rating("4.5")
                .build();

        //when //then
        mockMvc.perform(
                        post("/v1/reviews")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 작성 시 책 isbn은 필수입니다."))
                .andDo(print());
    }

    @DisplayName("리뷰 내용과 함께 리뷰를 등록해야 한다.")
    @Test
    void createReviewWithoutContent() throws Exception {
        //given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookIsbn("123456")
                .rating("4.5")
                .build();

        //when //then
        mockMvc.perform(
                        post("/v1/reviews")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 작성 시 내용은 필수입니다."))
                .andDo(print());
    }

    @DisplayName("별점과 함께 리뷰를 등록해야 한다.")
    @Test
    void createReviewWithoutRating() throws Exception {
        //given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookIsbn("123456")
                .content("재밌어요.")
                .build();

        //when //then
        mockMvc.perform(
                        post("/v1/reviews")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 작성 시 별점은 필수입니다."))
                .andDo(print());
    }

}