package com.jisungin.api.userlibrary;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.ControllerTestSupport;
import com.jisungin.api.userlibrary.request.UserLibraryCreateRequest;
import com.jisungin.api.userlibrary.request.UserLibraryEditRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserLibraryControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("서재 정보를 조회한다.")
    public void getUserLibrary() throws Exception {
        // given
        String isbn = "00001";

        // when // then
        mockMvc.perform(get("/v1/user-libraries")
                        .param("isbn", isbn)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보 조회 시 책 isbn 입력은 필수이다.")
    public void getUserLibraryWithoutIsbn() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/user-libraries")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 파라미터 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 생성한다.")
    public void createUseLibrary() throws Exception {
        // given
        UserLibraryCreateRequest request = UserLibraryCreateRequest.builder()
                .isbn("00001")
                .readingStatus("want")
                .build();

        // when // then
        mockMvc.perform(post("/v1/user-libraries")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보 등록 시 isbn 입력은 필수이다.")
    public void createUserLibraryWithoutIsbn() throws Exception {
        // given
        UserLibraryCreateRequest request = UserLibraryCreateRequest.builder()
                .readingStatus("want")
                .build();

        // when // then
        mockMvc.perform(post("/v1/user-libraries")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 isbn 입력은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("사재 정보 등록 시 독서 상태 입력은 필수이다.")
    public void createUserLibraryWithoutReadingStatus() throws Exception {
        // given
        UserLibraryCreateRequest request = UserLibraryCreateRequest.builder()
                .isbn("00001")
                .build();

        // when // then
        mockMvc.perform(post("/v1/user-libraries")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("독서 상태 정보 입력은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 수정한다.")
    public void editUserLibrary() throws Exception {
        // given
        Long userLibraryId = 1L;

        UserLibraryEditRequest request = UserLibraryEditRequest.builder()
                .isbn("00001")
                .readingStatus("want")
                .build();

        // when // then
        mockMvc.perform(patch("/v1/user-libraries/{userLibraryId}", userLibraryId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 수정시 isbn 입력은 필수이다.")
    public void editUserLibraryWithoutIsbn() throws Exception {
        // given
        Long userLibraryId = 1L;

        UserLibraryEditRequest request = UserLibraryEditRequest.builder()
                .readingStatus("want")
                .build();

        // when // then
        mockMvc.perform(patch("/v1/user-libraries/{userLibraryId}", userLibraryId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 isbn 입력은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 수정시 독서 상태 정보 입력은 필수이다.")
    public void editUserLibraryWithoutReadingStatus() throws Exception {
        // given
        Long userLibraryId = 1L;

        UserLibraryEditRequest request = UserLibraryEditRequest.builder()
                .isbn("00001")
                .build();

        // when // then
        mockMvc.perform(patch("/v1/user-libraries/{userLibraryId}", userLibraryId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("독서 상태 정보 입력은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 삭제한다.")
    public void deleteUserLibrary() throws Exception {
        // given
        Long userLibraryId = 1L;

        // when // then
        mockMvc.perform(delete("/v1/user-libraries/{userLibraryId}", userLibraryId)
                        .param("isbn", "0000X"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보 삭제 시 책 isbn 입력은 필수이다.")
    public void deleteUserLibraryWithoutIsbn() throws Exception {
        // given
        Long userLibraryId = 1L;

        // when // then
        mockMvc.perform(delete("/v1/user-libraries/{userLibraryId}", userLibraryId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("유효하지 않은 파라미터 입니다."))
                .andDo(print());
    }

}
