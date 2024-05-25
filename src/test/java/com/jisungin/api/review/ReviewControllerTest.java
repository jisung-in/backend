package com.jisungin.api.review;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.ControllerTestSupport;
import com.jisungin.api.review.request.ReviewCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ReviewControllerTest extends ControllerTestSupport {

    @DisplayName("도서와 연관된 리뷰를 조회한다.")
    void findBookReviews() throws Exception {
        // given
        String isbn = "000000000000";

        // when // then
        mockMvc.perform(get("/v1/books/{isbn}/reviews", isbn)
                        .param("page", "1")
                        .param("size", "5")
                        .param("order", "like")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("유저가 리뷰를 등록한다.")
    @Test
    void createReview() throws Exception {
        //given
        ReviewCreateRequest request = ReviewCreateRequest.builder()
                .bookIsbn("123456")
                .content("재밌어요.")
                .build();

        //when //then
        mockMvc.perform(
                        post("/v1/reviews")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(mockHttpSession))
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
                .build();

        //when //then
        mockMvc.perform(
                        post("/v1/reviews")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(mockHttpSession))
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
                .build();

        //when //then
        mockMvc.perform(
                        post("/v1/reviews")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(mockHttpSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("리뷰 작성 시 내용은 필수입니다."))
                .andDo(print());
    }

    @DisplayName("리뷰를 삭제한다.")
    @Test
    void deleteReview() throws Exception {
        //given
        Long deleteReviewId = 1L;

        //when //then
        mockMvc.perform(
                        delete("/v1/reviews/{reviewId}", deleteReviewId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

}