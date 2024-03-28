package com.jisungin.api.commentlike;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CommentLikeControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("유저가 의견에 좋아요를 누를 수 있다.")
    void likeTalkRoom() throws Exception {
        // when // then
        mockMvc.perform(post("/v1/comments/1/likes")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("좋아요 성공"));
    }

    @Test
    @DisplayName("유저가 의견에 좋아요를 취소할 수 있다")
    void nuLikeTalkRoom() throws Exception {
        // when // then
        mockMvc.perform(delete("/v1/comments/1/likes")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("좋아요 취소"));
    }

}