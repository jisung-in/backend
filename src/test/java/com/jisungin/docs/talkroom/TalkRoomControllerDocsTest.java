package com.jisungin.docs.talkroom;

import static com.jisungin.domain.ReadingStatus.PAUSE;
import static com.jisungin.domain.ReadingStatus.READ;
import static com.jisungin.domain.ReadingStatus.READING;
import static com.jisungin.domain.ReadingStatus.STOP;
import static com.jisungin.domain.ReadingStatus.WANT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.jisungin.api.oauth.AuthContext;
import com.jisungin.api.talkroom.TalkRoomController;
import com.jisungin.api.talkroom.request.TalkRoomCreateRequest;
import com.jisungin.api.talkroom.request.TalkRoomEditRequest;
import com.jisungin.application.PageResponse;
import com.jisungin.application.SearchServiceRequest;
import com.jisungin.application.talkroom.TalkRoomService;
import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import com.jisungin.application.talkroom.request.TalkRoomEditServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomLikeUserIdResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryReadingStatusResponse;
import com.jisungin.application.talkroom.response.TalkRoomResponse;
import com.jisungin.docs.RestDocsSupport;
import java.util.ArrayList;
import java.util.List;
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
                .readingStatus(List.of("읽고 싶은", "읽는 중", "읽음", "잠시 멈춤", "중단"))
                .imageUrls(List.of("이미지 URL"))
                .build();

        TalkRoomResponse response = TalkRoomResponse.builder()
                .userName("user@mail.com")
                .title("토론방 제목")
                .bookName("책 제목")
                .content("토론방 내용")
                .readingStatuses(List.of(WANT, READING, READ, PAUSE, STOP))
                .bookImage("책 이미지 URL")
                .build();

        response.addTalkRoomImages(List.of("이미지 URL"));

        given(talkRoomService.createTalkRoom(any(TalkRoomCreateServiceRequest.class), any(AuthContext.class)))
                .willReturn(response);

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
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("생성자 이름"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING)
                                        .description("토론방 제목"),
                                fieldWithPath("data.bookName").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("토론방 본문"),
                                fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL 리스트"),
                                fieldWithPath("data.readingStatuses").type(JsonFieldType.ARRAY)
                                        .description("참가 조건"),
                                fieldWithPath("data.bookImage").type(JsonFieldType.STRING)
                                        .description("책 이미지 URL")
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
                .order("RECENT")
                .query(null)
                .build();

        PageResponse<TalkRoomFindAllResponse> response = PageResponse.<TalkRoomFindAllResponse>builder()
                .queryResponse(talkRoomFindAllResponses)
                .totalCount(10L)
                .size(10)
                .build();

        response.addContents(List.of(1L));

        given(talkRoomService.findAllTalkRoom(any(SearchServiceRequest.class), any(AuthContext.class)))
                .willReturn(response);

        mockMvc.perform(
                        get("/v1/talk-rooms")
                                .param("page", String.valueOf(request.getPage()))
                                .param("size", String.valueOf(request.getSize()))
                                .param("order", request.getOrder())
                                .param("search", request.getQuery())
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
                                        .description("정렬 기준(기본값 최신순) -> RECENT(최신순), RECOMMEND(좋아요순)"),
                                parameterWithName("search")
                                        .description("검색").optional()
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
                                fieldWithPath("data.totalCount").type(JsonFieldType.NUMBER)
                                        .description("페이지 총 개수"),
                                fieldWithPath("data.size").type(JsonFieldType.NUMBER)
                                        .description("페이지 사이즈"),
                                fieldWithPath("data.queryResponse").type(JsonFieldType.ARRAY)
                                        .description("조회 데이터"),
                                fieldWithPath("data.queryResponse[].talkRoomId").type(JsonFieldType.NUMBER)
                                        .description("토론방 번호"),
                                fieldWithPath("data.queryResponse[].userName").type(JsonFieldType.STRING)
                                        .description("생성자 이름"),
                                fieldWithPath("data.queryResponse[].title").type(JsonFieldType.STRING)
                                        .description("토론방 제목"),
                                fieldWithPath("data.queryResponse[].content").type(JsonFieldType.STRING)
                                        .description("토론방 본문"),
                                fieldWithPath("data.queryResponse[].bookName").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.queryResponse[].bookImage").type(JsonFieldType.STRING)
                                        .description("책 이미지 URL"),
                                fieldWithPath("data.queryResponse[].readingStatuses").type(JsonFieldType.ARRAY)
                                        .description("참가 조건 리스트와 참가 조건이 해당되는 토론방 ID"),
                                fieldWithPath("data.queryResponse[].readingStatuses[].talkRoomId").type(
                                                JsonFieldType.NUMBER)
                                        .description("참가 조건이 해당되는 토론방 ID"),
                                fieldWithPath("data.queryResponse[].readingStatuses[].readingStatus").type(
                                                JsonFieldType.STRING)
                                        .description("참가 조건"),
                                fieldWithPath("data.queryResponse[].likeCount").type(JsonFieldType.NUMBER)
                                        .description("좋아요 총 개수"),
                                fieldWithPath("data.likeContents").type(JsonFieldType.ARRAY)
                                        .description("로그인한 유저가 좋아요한 토론방 ID들").optional()
                        )
                ));

    }

    @Test
    @DisplayName("토론방을 단건 조회하는 API")
    void findOneTalkRoom() throws Exception {
        TalkRoomFindOneResponse talkRoomFindOneResponse = createFindOneTalkRoom(1L);
        talkRoomFindOneResponse.addTalkRoomStatus(createReadingStatuses(1));
        talkRoomFindOneResponse.addTalkRoomLikeId(1L);

        given(talkRoomService.findOneTalkRoom(anyLong(), any(AuthContext.class)))
                .willReturn(talkRoomFindOneResponse);
        Long request = 1L;

        mockMvc.perform(
                        get("/v1/talk-room/{talkRoomId}", request)
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
                                fieldWithPath("data.talkRoomId").type(JsonFieldType.NUMBER)
                                        .description("토론방 번호"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("생성자 이름"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING)
                                        .description("토론방 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("토론방 본문"),
                                fieldWithPath("data.bookName").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.bookImage").type(JsonFieldType.STRING)
                                        .description("책 이미지 URL"),
                                fieldWithPath("data.readingStatuses").type(JsonFieldType.ARRAY)
                                        .description("참가 조건 리스트와 참가 조건이 해당되는 토론방 ID"),
                                fieldWithPath("data.readingStatuses[].talkRoomId").type(
                                                JsonFieldType.NUMBER)
                                        .description("참가 조건이 해당되는 토론방 ID"),
                                fieldWithPath("data.readingStatuses[].readingStatus").type(
                                                JsonFieldType.STRING)
                                        .description("참가 조건"),
                                fieldWithPath("data.likeCount").type(JsonFieldType.NUMBER)
                                        .description("토론방 좋아요 총 개수"),
                                fieldWithPath("data.likeTalkRoomId").type(JsonFieldType.NUMBER)
                                        .description("사용자가 좋아요한 토론방 ID").optional()
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
                .readingStatus(List.of("READ"))
                .newImage(List.of("새로운 이미지"))
                .removeImage(List.of("기존에 있던 이미지"))
                .build();

        TalkRoomResponse response = TalkRoomResponse.builder()
                .userName("user@mail.com")
                .title("토론방 제목")
                .bookName("책 제목")
                .content("토론방 내용")
                .readingStatuses(List.of(WANT, READING, READ, PAUSE, STOP))
                .bookImage("책 이미지 URL")
                .build();

        response.addTalkRoomImages(List.of("이미지 URL"));

        given(talkRoomService.editTalkRoom(any(TalkRoomEditServiceRequest.class), any(AuthContext.class)))
                .willReturn(response);

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
                                fieldWithPath("data").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING)
                                        .description("생성자 이름"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING)
                                        .description("토론방 제목"),
                                fieldWithPath("data.bookName").type(JsonFieldType.STRING)
                                        .description("책 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING)
                                        .description("토론방 본문"),
                                fieldWithPath("data.imageUrls").type(JsonFieldType.ARRAY)
                                        .description("이미지 URL"),
                                fieldWithPath("data.readingStatuses").type(JsonFieldType.ARRAY)
                                        .description("참가 조건"),
                                fieldWithPath("data.bookImage").type(JsonFieldType.STRING)
                                        .description("책 이미지 URL")
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

    private List<TalkRoomLikeUserIdResponse> createTalkRoomLikeUserIdResponses() {
        List<TalkRoomLikeUserIdResponse> talkRoomLikeUserIdResponses = new ArrayList<>();
        talkRoomLikeUserIdResponses.add(
                TalkRoomLikeUserIdResponse.builder()
                        .talkRoomId(1L)
                        .userId(1L)
                        .build()
        );
        return talkRoomLikeUserIdResponses;
    }

    private static TalkRoomFindOneResponse createFindOneTalkRoom(Long likeCount) {
        return TalkRoomFindOneResponse.builder()
                .talkRoomId(1L)
                .userName("유저 이름")
                .title("토론방 제목")
                .content("토론방 본문")
                .bookName("책 제목")
                .bookImage("책 이미지 URL")
                .likeCount(likeCount)
                .build();
    }

    private List<TalkRoomFindAllResponse> createTalkRoomFindAllResponses() {
        List<TalkRoomFindAllResponse> talkRoomFindAllResponses = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            List<TalkRoomQueryReadingStatusResponse> readingStatuses = createReadingStatuses(i);
            List<TalkRoomLikeUserIdResponse> userIds = createTalkRoomLikeUserIds(i);

            TalkRoomFindAllResponse roomResponse = TalkRoomFindAllResponse.builder()
                    .talkRoomId((long) i)
                    .userName("user" + i)
                    .title("토론방" + i)
                    .content("의견" + i)
                    .bookName("책 제목" + i)
                    .bookImage("책 이미지 URL" + i)
                    .likeCount((long) i)
                    .build();

            roomResponse.addTalkRoomStatus(readingStatuses);

            talkRoomFindAllResponses.add(roomResponse);
        }
        return talkRoomFindAllResponses;
    }

    private List<TalkRoomQueryReadingStatusResponse> createReadingStatuses(int i) {
        List<TalkRoomQueryReadingStatusResponse> readingStatuses = new ArrayList<>();

        readingStatuses.add(TalkRoomQueryReadingStatusResponse.builder()
                .talkRoomId((long) i)
                .readingStatus(WANT)
                .build());
        readingStatuses.add(TalkRoomQueryReadingStatusResponse.builder()
                .talkRoomId((long) i)
                .readingStatus(READING)
                .build());
        readingStatuses.add(TalkRoomQueryReadingStatusResponse.builder()
                .talkRoomId((long) i)
                .readingStatus(READ)
                .build());
        readingStatuses.add(TalkRoomQueryReadingStatusResponse.builder()
                .talkRoomId((long) i)
                .readingStatus(PAUSE)
                .build());
        readingStatuses.add(TalkRoomQueryReadingStatusResponse.builder()
                .talkRoomId((long) i)
                .readingStatus(STOP)
                .build());

        return readingStatuses;
    }

    private List<TalkRoomLikeUserIdResponse> createTalkRoomLikeUserIds(int i) {
        List<TalkRoomLikeUserIdResponse> userIds = new ArrayList<>();

        userIds.add(TalkRoomLikeUserIdResponse.builder()
                .talkRoomId((long) i)
                .userId((long) i)
                .build());

        return userIds;
    }

}
