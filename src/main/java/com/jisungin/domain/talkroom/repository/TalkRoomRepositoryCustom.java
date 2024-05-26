package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface TalkRoomRepositoryCustom {

    List<TalkRoomQueryResponse> findAllTalkRoom(Integer offset, Integer size, String order, String search, String day,
                                                LocalDateTime now);

    Long countTalkRooms(String search, String day, LocalDateTime now);

    List<TalkRoomQueryResponse> findTalkRoomsRelatedBook(String isbn, long offset, Integer size);

    Long countTalkRoomsRelatedBook(String isbn);

    TalkRoomQueryResponse findOneTalkRoom(Long talkRoomId);

    List<TalkRoomQueryResponse> findByTalkRoomOwner(Integer offset, Integer size, boolean userTalkRoomsFilter,
                                                    boolean commentFilter,
                                                    boolean likeFilter, Long id);

    Long countTalkRoomsByUserId(Long userId, boolean userTalkRoomsFilter, boolean commentFilter, boolean likeFilter);

}
