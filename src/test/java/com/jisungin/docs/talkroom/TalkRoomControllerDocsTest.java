package com.jisungin.docs.talkroom;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jisungin.api.SearchRequest;
import com.jisungin.api.talkroom.TalkRoomController;
import com.jisungin.api.talkroom.request.TalkRoomCreateRequest;
import com.jisungin.api.talkroom.request.TalkRoomEditRequest;
import com.jisungin.application.PageResponse;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomPageResponse;
import com.jisungin.docs.RestDocsSupport;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;

public class TalkRoomControllerDocsTest extends RestDocsSupport {

    private final TalkRoomService talkRoomService = mock(TalkRoomService.class);

    @Override
    protected Object initController() {
        return new TalkRoomController(talkRoomService);
    }

    @Test
    @DisplayName("토론방을 생성하는 API")
    void crateTalkRoom() throws Exception {
        TalkRoomCreateRequest request = TalkRoomCreateRequest.builder()
                .bookIsbn("1111111")
                .title("토론방")
                .content("내용")
                .readingStatus(List.of("읽고 싶은", "읽는 중", "읽음", "잠시 멈춤", "중단", "상관없음"))
                .imageUrls(List.of("이미지 URL"))
                .build();

        given(talkRoomService.createTalkRoom(any(TalkRoomCreateServiceRequest.class), anyLong(),
                any(LocalDateTime.class)))
                .willReturn(TalkRoomFindOneResponse.builder()
                        .id(1L)
                        .profileImage("작성자 이미지 URL")
                        .username("작성자 이름")
                        .title("토론방 제목")
                        .content("토론방 본문")
                        .bookName("책 제목")
                        .bookAuthor("책 저자")
                        .bookThumbnail("책 이미지 URL")
                        .likeCount(0L)
                        .readingStatuses(List.of("읽고 싶은", "읽는 중", "읽음", "잠시 멈춤", "중단"))
                        .registeredDateTime(LocalDateTime.now())
                        .images(List.of("이미지 URL"))
                        .likeTalkRoom(false)
                        .build());

        mockMvc.perform(
                        post("/v1/talk-rooms")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("talkroom/create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("bookIsbn").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용"),
                                fieldWithPath("readingStatus").type(JsonFieldType.ARRAY)
                                        .description("참가 조건 -> { 읽고 싶은, 읽는 중, 읽음, 잠시 멈춤, 중단 }"),
                                fieldWithPath("imageUrls").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("토론방 번호"),
                                fieldWithPath("data.profileImage").type(JsonFieldType.STRING)
                                        .description("생성자 이미지 URL"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING)
                                        .description("생성자 이름"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING)
                                        .description("토론방 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("토론방 본문"),
                                fieldWithPath("data.images").type(JsonFieldType.ARRAY)
                                        .description("토론방 이미지 URL"),
                                fieldWithPath("data.bookName").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.bookAuthor").type(JsonFieldType.STRING)
                                        .description("책 저자"),
                                fieldWithPath("data.bookThumbnail").type(JsonFieldType.STRING)
                                        .description("책 이미지 URL"),
                                fieldWithPath("data.readingStatuses").type(JsonFieldType.ARRAY)
                                        .description("참가 조건"),
                                fieldWithPath("data.registeredDateTime").type(JsonFieldType.ARRAY)
                                        .description("토론방 생성 시간"),
                                fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER)
                                        .description("토론방 좋아요 총 개수"),
                                fieldWithPath("data.likeTalkRoom").type(JsonFieldType.BOOLEAN)
                                        .description("로그인 사용자 좋아요 표시")
                        )
                ));
    }

    @Test
    @DisplayName("토론방 페이징 조회 API")
    void findAllTalkRooms() throws Exception {
        List<TalkRoomFindAllResponse> talkRoomFindAllResponses = createTalkRoomFindAllResponses();

        SearchRequest request = SearchRequest.builder()
                .page(1)
                .size(10)
                .order("sort")
                .query("search")
                .day("sortbydate")
                .build();

        PageResponse<TalkRoomFindAllResponse> pageResponse = PageResponse.<TalkRoomFindAllResponse>builder()
                .queryResponse(talkRoomFindAllResponses)
                .size(10)
                .totalCount(10)
                .build();

        TalkRoomPageResponse response = TalkRoomPageResponse.builder()
                .response(pageResponse)
                .userLikeTalkRoomIds(null)
                .build();

        given(talkRoomService.findAllTalkRoom(anyLong(), any(Integer.class), anyString(), anyString(), anyString(),
                anyLong(), any(LocalDateTime.class)))
                .willReturn(response);

        mockMvc.perform(
                        get("/v1/talk-rooms")
                                .param("page", "1")
                                .param("size", "10")
                                .param("order", "sort")
                                .param("search", "search")
                                .param("day", "sortbydate")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("talkroom/findAll",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("page")
                                        .description("페이지 번호"),
                                parameterWithName("size")
                                        .description("페이지 사이즈"),
                                parameterWithName("order")
                                        .description(
                                                "정렬 기준 : recent(최신순), recommend(좋아요순), recent-comment(최근 등록된 의견순)"),
                                parameterWithName("search")
                                        .description("검색").optional(),
                                parameterWithName("day")
                                        .description("날짜별 정렬 -> 1d(하루 전), 1w(일주일 전), 1m(한달 전)").optional()
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.userLikeTalkRoomIds").type(JsonFieldType.ARRAY)
                                        .description("로그인한 유저가 좋아요 누른 토론방 ID").optional(),
                                fieldWithPath("data.response").type(JsonFieldType.OBJECT)
                                        .description("토론방과 관련된 데이터"),
                                fieldWithPath("data.response.totalCount").type(JsonFieldType.NUMBER)
                                        .description("토론방 총 개수"),
                                fieldWithPath("data.response.size").type(JsonFieldType.NUMBER)
                                        .description("토론방 반환 사이즈"),
                                fieldWithPath("data.response.queryResponse").type(JsonFieldType.ARRAY)
                                        .description("토론방 데이터"),
                                fieldWithPath("data.response.queryResponse[].id").type(JsonFieldType.NUMBER)
                                        .description("토론방 ID"),
                                fieldWithPath("data.response.queryResponse[].profileImage").type(JsonFieldType.STRING)
                                        .description("유저 이미지 URL"),
                                fieldWithPath("data.response.queryResponse[].username").type(JsonFieldType.STRING)
                                        .description("유저 이름"),
                                fieldWithPath("data.response.queryResponse[].title").type(JsonFieldType.STRING)
                                        .description("토론방 제목"),
                                fieldWithPath("data.response.queryResponse[].content").type(JsonFieldType.STRING)
                                        .description("토론방 본문"),
                                fieldWithPath("data.response.queryResponse[].bookName").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.response.queryResponse[].bookAuthor").type(JsonFieldType.STRING)
                                        .description("책 저자"),
                                fieldWithPath("data.response.queryResponse[].bookThumbnail").type(JsonFieldType.STRING)
                                        .description("책 이미지 URL"),
                                fieldWithPath("data.response.queryResponse[].likeCount").type(JsonFieldType.NUMBER)
                                        .description("토론방 좋아요 개수"),
                                fieldWithPath("data.response.queryResponse[].readingStatuses").type(JsonFieldType.ARRAY)
                                        .description("토론방 참가 조건"),
                                fieldWithPath("data.response.queryResponse[].registeredDateTime").type(
                                                JsonFieldType.ARRAY)
                                        .description("토론방 생성 시간")

                        )
                ));

    }

    @Test
    @DisplayName("토론방을 단건 조회하는 API")
    void findOneTalkRoom() throws Exception {
        given(talkRoomService.findOneTalkRoom(anyLong(), anyLong()))
                .willReturn(TalkRoomFindOneResponse.builder()
                        .id(1L)
                        .profileImage("작성자 이미지 URL")
                        .username("작성자 이름")
                        .title("토론방 제목")
                        .content("토론방 본문")
                        .bookName("책 제목")
                        .bookAuthor("책 저자")
                        .bookThumbnail("책 이미지 URL")
                        .likeCount(0L)
                        .readingStatuses(List.of("읽고 싶은", "읽는 중", "읽음", "잠시 멈춤", "중단"))
                        .registeredDateTime(LocalDateTime.now())
                        .images(List.of("이미지 URL"))
                        .likeTalkRoom(false)
                        .build());

        Long request = 1L;

        mockMvc.perform(
                        get("/v1/talk-rooms/{talkRoomId}", request)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("talkroom/findOne",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("talkRoomId")
                                        .description("토론방 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.id").type(JsonFieldType.NUMBER)
                                        .description("토론방 번호"),
                                fieldWithPath("data.profileImage").type(JsonFieldType.STRING)
                                        .description("생성자 이미지 URL"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING)
                                        .description("생성자 이름"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING)
                                        .description("토론방 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("토론방 본문"),
                                fieldWithPath("data.images").type(JsonFieldType.ARRAY)
                                        .description("토론방 이미지 URL"),
                                fieldWithPath("data.bookName").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.bookAuthor").type(JsonFieldType.STRING)
                                        .description("책 저자"),
                                fieldWithPath("data.bookThumbnail").type(JsonFieldType.STRING)
                                        .description("책 이미지 URL"),
                                fieldWithPath("data.readingStatuses").type(JsonFieldType.ARRAY)
                                        .description("참가 조건"),
                                fieldWithPath("data.registeredDateTime").type(JsonFieldType.ARRAY)
                                        .description("토론방 생성 시간"),
                                fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER)
                                        .description("토론방 좋아요 총 개수"),
                                fieldWithPath("data.likeTalkRoom").type(JsonFieldType.BOOLEAN)
                                        .description("로그인 사용자 좋아요 표시")
                        )
                ));
    }

    @Test
    @DisplayName("토론방을 수정하는 API")
    void editTalkRoom() throws Exception {
        TalkRoomEditRequest request = TalkRoomEditRequest.builder()
                .id(1L)
                .title("토론방 제목 수정")
                .content("토론방 본문 수정")
                .readingStatus(List.of("읽음"))
                .newImage(List.of("새로운 이미지"))
                .removeImage(List.of("기존에 있던 이미지"))
                .build();

        mockMvc.perform(
                        patch("/v1/talk-rooms")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("talkroom/edit",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER)
                                        .description("토론방 ID"),
                                fieldWithPath("title").type(JsonFieldType.STRING)
                                        .description("제목").optional(),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("내용").optional(),
                                fieldWithPath("newImage").type(JsonFieldType.ARRAY)
                                        .description("새로운 이미지 URL 저장"),
                                fieldWithPath("removeImage").type(JsonFieldType.ARRAY)
                                        .description("기존의 있던 이미지 URL 삭제"),
                                fieldWithPath("readingStatus").type(JsonFieldType.ARRAY)
                                        .description("참가 조건")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("return data null")
                        )
                ));
    }

    @Test
    @DisplayName("토론방을 삭제하는 API")
    void deleteTalkRoom() throws Exception {
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/v1/talk-rooms/{talkRoomId}", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("talkroom/delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("talkRoomId")
                                        .description("토론방 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").type(JsonFieldType.NUMBER)
                                        .description("코드"),
                                fieldWithPath("status").type(JsonFieldType.STRING)
                                        .description("상태"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("메시지"),
                                fieldWithPath("data").type(JsonFieldType.NULL)
                                        .description("return data null")
                        )
                ));
    }

    private List<TalkRoomFindAllResponse> createTalkRoomFindAllResponses() {
        return IntStream.range(1, 11)
                .mapToObj(i -> TalkRoomFindAllResponse.builder()
                        .id((long) i)
                        .profileImage("프로필 이미지 URL")
                        .username("user " + i)
                        .title("토론방 제목 " + i)
                        .content("토론방 내용 " + i)
                        .bookName("책 제목 +" + i)
                        .bookAuthor("책 저자 " + i)
                        .bookThumbnail("책 이미지 URL")
                        .likeCount(0L)
                        .readingStatuses(List.of("읽고싶은", "읽는 중", "읽음", "잠시 멈춤", "중단"))
                        .registeredDateTime(LocalDateTime.now())
                        .build())
                .toList();
    }

}
