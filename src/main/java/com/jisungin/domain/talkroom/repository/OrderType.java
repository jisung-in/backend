package com.jisungin.domain.talkroom.repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderType {

    RECENT("최신순"),
    RECOMMEND("좋아요순"),
    COMMENT("토크 많은순"),
    RECENT_COMMENT("최근 등록된 의견순");

    private final String text;

    public static OrderType convertToOrderType(String order) {
        if (order == null) {
            return null;
        }

        return switch (order) {
            case "recent" -> RECENT;
            case "recommend" -> RECOMMEND;
            case "comment" -> COMMENT;
            case "recent-comment" -> RECENT_COMMENT;
            default -> RECENT;
        };
    }

}
