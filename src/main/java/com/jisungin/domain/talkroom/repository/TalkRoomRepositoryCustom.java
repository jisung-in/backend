package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;

public interface TalkRoomRepositoryCustom {

    PageResponse<TalkRoomFindAllResponse> findAllTalkRoom(long offset, int size, String order, String query);

    TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId);

}
