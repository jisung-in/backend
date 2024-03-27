package com.jisungin.application;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderType {

    RECENT("최신순"),
    RECOMMEND("좋아요순");

    private final String text;

    public static OrderType conversionOrderType(String order) {
        return switch (order) {
            case "recent" -> RECENT;
            case "recommend" -> RECOMMEND;
            default -> RECENT;
        };
    }

}
