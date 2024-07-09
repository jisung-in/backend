package com.jisungin.application.talkroom.request;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTalkRoomSearchCondition {

    private boolean userTalkRoomFilter;
    private boolean commentedFilter;
    private boolean likedFilter;

    @Builder
    private UserTalkRoomSearchCondition(boolean userTalkRoomFilter, boolean commentedFilter, boolean likedFilter) {
        this.userTalkRoomFilter = userTalkRoomFilter;
        this.commentedFilter = commentedFilter;
        this.likedFilter = likedFilter;
    }

    public static UserTalkRoomSearchCondition of(boolean userTalkRoomFilter, boolean commentedFilter, boolean likedFilter) {
        return UserTalkRoomSearchCondition.builder()
                .userTalkRoomFilter(userTalkRoomFilter)
                .commentedFilter(commentedFilter)
                .likedFilter(likedFilter)
                .build();
    }

}
