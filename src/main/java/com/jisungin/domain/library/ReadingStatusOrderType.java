package com.jisungin.domain.library;

import java.util.Locale;

public enum ReadingStatusOrderType {

    DICTIONARY,
    RATING_AVG_DESC;

    public static ReadingStatusOrderType fromName(String name) {
//        if (name == null) {
//            return null;
//        }

        return ReadingStatusOrderType.valueOf(name.toUpperCase(Locale.ENGLISH));
    }

}
