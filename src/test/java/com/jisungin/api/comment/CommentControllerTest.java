package com.jisungin.api.comment;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.api.comment.request.CommentCreateRequest;
import com.jisungin.application.comment.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {

    @MockBean
    CommentService commentService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("유저가 토크방에 자신의 의견을 작성한다.")
    void writeComment() throws Exception {
        // given
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("의견을 작성하다")
                .build();

        // when // then
        mockMvc.perform(post("/v1/talk-rooms/1/comments")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("유저가 토크방에 자신의 의견을 남길 때 내용은 필수여야 한다.")
    void writeCommentWithEmptyContent() throws Exception {
        // given
        CommentCreateRequest request = CommentCreateRequest.builder()
                .build();

        // when // then
        mockMvc.perform(post("/v1/talk-rooms/1/comments")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("내용은 필수 입니다."));
    }

}