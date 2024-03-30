package com.jisungin.domain.review;

import java.util.Locale;

public enum RatingOrderType {

    DATE,
    RATING_ASC,
    RATING_DESC,
    RATING_AVG_ASC,
    RATING_AVG_DESC;

    public static RatingOrderType fromName(String name) {
        return RatingOrderType.valueOf(name.toUpperCase(Locale.ENGLISH));
    }

}
