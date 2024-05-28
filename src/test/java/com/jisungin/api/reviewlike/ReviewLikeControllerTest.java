package com.jisungin.api.reviewlike;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ReviewLikeControllerTest extends ControllerTestSupport {

    @DisplayName("유저가 좋아요 누른 리뷰를 조회한다.")
    @Test
    void findLikeReviewIds() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/reviews/likes")
                        .accept(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @DisplayName("유저가 리뷰 좋아요를 누른다.")
    @Test
    void likeReview() throws Exception {
        //given
        Long reviewId = 1L;

        //when //then
        mockMvc.perform(post("/v1/reviews/{reviewId}/likes", reviewId)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @DisplayName("유저가 리뷰 좋아요를 취소한다.")
    @Test
    void unlikeReview() throws Exception {
        //given
        Long reviewId = 1L;

        //when //then
        mockMvc.perform(delete("/v1/reviews/{reviewId}/likes", reviewId)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

}