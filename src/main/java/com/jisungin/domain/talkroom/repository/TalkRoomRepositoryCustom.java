package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.SearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomFindAllResponse;
import com.jisungin.application.talkroom.response.TalkRoomFindOneResponse;

public interface TalkRoomRepositoryCustom {

    PageResponse<TalkRoomFindAllResponse> findAllTalkRoom(SearchServiceRequest search);

    TalkRoomFindOneResponse findOneTalkRoom(Long talkRoomId);
}
