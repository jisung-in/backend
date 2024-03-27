package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;

public interface TalkRoomRepositoryCustom {

    PageResponse<TalkRoomFindAllResponse> findAllTalkRoom(TalkRoomSearchServiceRequest search);

    TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId);
}
