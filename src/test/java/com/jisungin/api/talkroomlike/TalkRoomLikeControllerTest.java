package com.jisungin.api.talkroomlike;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TalkRoomLikeControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("유저가 토크방에 좋아요를 누를 수 있다.")
    void likeTalkRoom() throws Exception {
        // when // then
        mockMvc.perform(post("/v1/talk-rooms/1/likes")
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("좋아요 성공"));
    }

    @Test
    @DisplayName("유저가 토크방 좋아요를 취소 할 수 있다.")
    void unLikeTalkRoom() throws Exception {
        // when // then
        mockMvc.perform(delete("/v1/talk-rooms/1/likes")
                        .contentType(APPLICATION_JSON)
                        .session(mockHttpSession)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("좋아요 취소"));
    }

}