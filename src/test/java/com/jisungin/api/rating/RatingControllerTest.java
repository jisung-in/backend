package com.jisungin.api.rating;

import com.jisungin.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RatingControllerTest extends ControllerTestSupport {

    @DisplayName("사용자의 모든 별점을 조회한다.")
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
}
