package com.jisungin.domain.talkroom.repository;

import com.jisungin.domain.ReadingStatus;
import java.util.List;
import java.util.Map;

public interface TalkRoomRoleRepositoryCustom {

    Map<Long, List<ReadingStatus>> findTalkRoomRoleByIds(List<Long> talkRoomIds);

}
