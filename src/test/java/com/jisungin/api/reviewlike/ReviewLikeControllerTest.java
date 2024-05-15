package com.jisungin.api.reviewlike;

import com.jisungin.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReviewLikeControllerTest extends ControllerTestSupport {

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