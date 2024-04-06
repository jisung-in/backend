package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import java.util.List;

public interface TalkRoomRepositoryCustom {

    PageResponse<TalkRoomFindAllResponse> findAllTalkRoom(long offset, int size, String order, String query);

    List<TalkRoomQueryResponse> findTalkRoomsRelatedBook(String isbn, long offset, Integer size);

    Long countTalkRoomsRelatedBook(String isbn);

    TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId);

}
