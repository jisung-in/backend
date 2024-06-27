package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.talkroom.response.TalkRoomQueryEntity;
import java.time.LocalDateTime;
import java.util.List;

public interface TalkRoomRepositoryCustom {

    List<TalkRoomQueryEntity> findAllTalkRoom(Integer offset, Integer size, String order, String search, String day,
                                              LocalDateTime now);

    List<TalkRoomQueryEntity> findTalkRoomsRelatedBook(String isbn, long offset, Integer size);

    Long countTalkRoomsRelatedBook(String isbn);

    TalkRoomQueryEntity findOneTalkRoom(Long talkRoomId);

    List<TalkRoomQueryEntity> findByTalkRoomOwner(Integer offset, Integer size, boolean userTalkRoomsFilter,
                                                  boolean commentFilter,
                                                  boolean likeFilter, Long id);

    Long countTalkRoomsByUserId(Long userId, boolean userTalkRoomsFilter, boolean commentFilter, boolean likeFilter);

}
