package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import com.jisungin.application.talkroom.response.TalkRoomPageResponse;

public interface TalkRoomRepositoryCustom {

    TalkRoomPageResponse getTalkRooms(TalkRoomSearchServiceRequest search);

}
