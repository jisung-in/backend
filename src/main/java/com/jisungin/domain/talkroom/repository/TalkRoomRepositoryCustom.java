package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.response.PageResponse;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomQueryResponse;

public interface TalkRoomRepositoryCustom {

    PageResponse<TalkRoomQueryResponse> getTalkRooms(TalkRoomSearchServiceRequest search);

}
