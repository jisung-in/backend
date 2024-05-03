package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface TalkRoomRepositoryCustom {

    List<TalkRoomQueryResponse> findAllTalkRoom(long offset, int size, String order, String search, String day,
                                                LocalDateTime now);

    Long countTalkRooms();

    List<TalkRoomQueryResponse> findTalkRoomsRelatedBook(String isbn, long offset, Integer size);

    Long countTalkRoomsRelatedBook(String isbn);

    TalkRoomQueryResponse findOneTalkRoom(Long talkRoomId);

}
