package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import java.util.List;

public interface TalkRoomRepositoryCustom {

    List<TalkRoomQueryResponse> findAllTalkRoom(long offset, int size, String order, String query);

    Long countTalkRooms();

    List<TalkRoomQueryResponse> findTalkRoomsRelatedBook(String isbn, long offset, Integer size);

    Long countTalkRoomsRelatedBook(String isbn);

    TalkRoomQueryResponse findOneTalkRoom(Long talkRoomId);

}
