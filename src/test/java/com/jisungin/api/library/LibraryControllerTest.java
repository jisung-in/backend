package com.jisungin.api.library;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.ControllerTestSupport;
import com.jisungin.api.library.request.LibraryCreateRequest;
import com.jisungin.api.library.request.LibraryEditRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LibraryControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("서재 정보를 조회한다.")
    public void findLibraries() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/libraries")
                        .accept(APPLICATION_JSON)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 생성한다.")
    public void createLibrary() throws Exception {
        // given
        LibraryCreateRequest request = LibraryCreateRequest.builder()
                .isbn("00001")
                .readingStatus("want")
                .build();

        // when // then
        mockMvc.perform(post("/v1/libraries")
                        .accept(APPLICATION_JSON)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보 등록 시 isbn 입력은 필수이다.")
    public void createLibraryWithoutIsbn() throws Exception {
        // given
        LibraryCreateRequest request = LibraryCreateRequest.builder()
                .readingStatus("want")
                .build();

        // when // then
        mockMvc.perform(post("/v1/libraries")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 isbn 입력은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("사재 정보 등록 시 독서 상태 입력은 필수이다.")
    public void createLibraryWithoutReadingStatus() throws Exception {
        // given
        LibraryCreateRequest request = LibraryCreateRequest.builder()
                .isbn("00001")
                .build();

        // when // then
        mockMvc.perform(post("/v1/libraries")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("독서 상태 정보 입력은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 수정한다.")
    public void editLibrary() throws Exception {
        // given
        Long libraryId = 1L;

        LibraryEditRequest request = LibraryEditRequest.builder()
                .isbn("00001")
                .readingStatus("want")
                .build();

        // when // then
        mockMvc.perform(patch("/v1/libraries/{libraryId}", libraryId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 수정시 isbn 입력은 필수이다.")
    public void editLibraryWithoutIsbn() throws Exception {
        // given
        Long libraryId = 1L;

        LibraryEditRequest request = LibraryEditRequest.builder()
                .readingStatus("want")
                .build();

        // when // then
        mockMvc.perform(patch("/v1/libraries/{libraryId}", libraryId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("책 isbn 입력은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 수정시 독서 상태 정보 입력은 필수이다.")
    public void editLibraryWithoutReadingStatus() throws Exception {
        // given
        Long libraryId = 1L;

        LibraryEditRequest request = LibraryEditRequest.builder()
                .isbn("00001")
                .build();

        // when // then
        mockMvc.perform(patch("/v1/libraries/{libraryId}", libraryId)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("독서 상태 정보 입력은 필수 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("서재 정보를 삭제한다.")
    public void deleteLibrary() throws Exception {
        // given
        Long libraryId = 1L;

        // when // then
        mockMvc.perform(delete("/v1/libraries/{libraryId}", libraryId)
                        .session(mockHttpSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

}
