package com.jisungin.domain.talkroom.repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderType {

    RECENT("최신순"),
    RECOMMEND("좋아요순");

    private final String text;

    public static OrderType convertToOrderType(String order) {
        if (order == null) {
            return null;
        }

        return switch (order) {
            case "recent" -> RECENT;
            case "recommend" -> RECOMMEND;
            default -> RECENT;
        };
    }

}
