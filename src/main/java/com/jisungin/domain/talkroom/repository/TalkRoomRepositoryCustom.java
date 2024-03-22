package com.jisungin.domain.talkroom.repository;

import com.jisungin.application.response.PageResponse;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;

public interface TalkRoomRepositoryCustom {

    PageResponse getTalkRooms(TalkRoomSearchServiceRequest search);

}
