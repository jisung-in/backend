package com.jisungin.api.talkroom;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.ControllerTestSupport;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TalkRoomControllerTest extends ControllerTestSupport {

    @Test
    @DisplayName("유저가 책A에 대한 토크방을 생성한다.")
    void createTalkRoom() throws Exception {
        // given
        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");

        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookIsbn("111111")
                .title("토크방")
                .content("내용")
                .readingStatus(readingStatus)
                .build();

        // when // then
        mockMvc.perform(post("/v1/talk-rooms")
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
    @DisplayName("토크방을 생성할 때 참가 조건은 1개 이상 체크해야 한다.")
    void createTalkRoomWithEmptyReadingStatus() throws Exception {
        // given
        TalkRoomCreateServiceRequest request = TalkRoomCreateServiceRequest.builder()
                .bookIsbn("111111")
                .title("토크방")
                .content("내용")
                .readingStatus(null)
                .build();

        // when // then
        mockMvc.perform(post("/v1/talk-rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("참가 조건은 1개 이상 체크해야합니다."));
    }

    @Test
    @DisplayName("토크방을 생성한 유저가 토크방의 제목을 수정한다.")
    void editTalkRoomContent() throws Exception {
        // given
        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(1L)
                .title("토크방 수정")
                .content("내용 수정")
                .readingStatus(readingStatus)
                .build();

        // when // then
        mockMvc.perform(patch("/v1/talk-rooms")
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
    @DisplayName("토크방을 생성한 유저가 토크방의 참가 조건을 수정한다.")
    void editTalkRoomReadingStatus() throws Exception {
        // given
        List<String> readingStatus = new ArrayList<>();
        readingStatus.add("읽는 중");
        readingStatus.add("읽음");
        readingStatus.add("잠시 멈춤");
        readingStatus.add("중단");

        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(1L)
                .title("토크방")
                .content("내용")
                .readingStatus(readingStatus)
                .build();
        // when // then
        mockMvc.perform(patch("/v1/talk-rooms")
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
    @DisplayName("토크방을 수정할 때 참가 조건은 1개 이상 체크해야 한다.")
    void editTalkRoomWithEmptyReadingStatus() throws Exception {
        // given
        TalkRoomEditServiceRequest request = TalkRoomEditServiceRequest.builder()
                .id(1L)
                .title("토크방")
                .content("내용")
                .readingStatus(null)
                .build();
        // when // then
        mockMvc.perform(patch("/v1/talk-rooms")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("참가 조건은 1개 이상 체크해야합니다."));
    }

    @Test
    @DisplayName("사용자가 토크방을 조회 했을 때 페이지를 -1 값을 보내면 첫 번째 페이지가 조회 되어야 한다.")
    void getTalkRoomWithMinus() throws Exception {

        // when // then
        mockMvc.perform(get("/v1/talk-rooms?page=-1&size=10&order=recent")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("토크방이 없을 때 토크방 조회 페이지에 들어갔을 때 에러가 발생하면 안된다.")
    void getTalkRoomsEmpty() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/talk-rooms?page=-1&size=10&order=recent")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("토크방 단건 조회를 한다.")
    void findOneTalkRoom() throws Exception {
        // when // then
        mockMvc.perform(get("/v1/talk-room/1")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("토크방을 삭제한다.")
    void deleteTalkRoom() throws Exception {
        // when // then
        mockMvc.perform(delete("/v1/talk-rooms/1")
                        .contentType(APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.message").value("OK"));
    }

}