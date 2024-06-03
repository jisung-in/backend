package com.jisungin.api.user;

import com.jisungin.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends ControllerTestSupport {

    @DisplayName("사용자의 모든 리뷰 별점을 조회한다.")
    @Test
    void getUserRatings() throws Exception {
        //given
        //when //then
        mockMvc.perform(get("/v1/users/ratings?page=1&size=4&order=rating_asc&rating=")
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @DisplayName("사용자의 모든 리뷰 내용을 조회한다.")
    @Test
    void getReviewContents() throws Exception {
        //given
        //when //then
        mockMvc.perform(get("/v1/users/reviews?page=1&size=4&order=rating_avg_desc")
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @DisplayName("사용자의 독서 상태를 조회한다.")
    @Test
    void getReadingStatuses() throws Exception {
        //given
        //when //then
        mockMvc.perform(get("/v1/users/statuses?page=1&size=4&order=dictionary&status=want")
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @DisplayName("사용자 정보를 조회한다.")
    @Test
    void getUserInfo() throws Exception {
        //given
        //when //then
        mockMvc.perform(get("/v1/users/me")
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

}