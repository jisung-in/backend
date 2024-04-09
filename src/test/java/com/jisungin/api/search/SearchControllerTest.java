package com.jisungin.api.search;

import com.jisungin.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SearchControllerTest extends ControllerTestSupport {

    @DisplayName("검색한 키워드의 점수를 증가시킨다.")
    @Test
    void addScoreSearchKeyword() throws Exception {
        //given
        //when //then
        mockMvc.perform(
                        post("/v1/search/rank?keyword=정의")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @DisplayName("검색한 키워드가 null이면 예외가 발생한다.")
    @Test
    void addScoreSearchKeywordWithNull() throws Exception {
        //given
        //when //then
        mockMvc.perform(
                        post("/v1/search/rank")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("키워드 값은 필수 입니다."))
                .andDo(print());
    }

    @DisplayName("검색한 키워드가 공백이면 예외가 발생한다.")
    @Test
    void addScoreSearchKeywordWithEmpty() throws Exception {
        //given
        //when //then
        mockMvc.perform(
                        post("/v1/search/rank?keyword=")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("키워드 값은 필수 입니다."))
                .andDo(print());
    }

    @DisplayName("인기 검색어 랭킹을 조회한다.")
    @Test
    void getSearchRanking() throws Exception {
        //given
        //when //then
        mockMvc.perform(get("/v1/search/rank")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

}