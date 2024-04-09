package com.jisungin.domain.talkroomimage.repository;

import java.util.List;

public interface TalkRoomImageRepositoryCustom {

    List<String> findTalkRoomImages(Long talkRoomId);
}
