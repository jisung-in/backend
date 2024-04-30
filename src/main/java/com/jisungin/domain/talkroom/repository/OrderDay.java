package com.jisungin.domain.talkroom.repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OrderDay {

    DAY("1d") {
        @Override
        public LocalDateTime getDataTime(LocalDateTime now) {
            return now.minusDays(1);
        }
    },
    WEEK("1w") {
        @Override
        public LocalDateTime getDataTime(LocalDateTime now) {
            return now.minusWeeks(1);
        }
    },
    MONTH("1m") {
        @Override
        public LocalDateTime getDataTime(LocalDateTime now) {
            return now.minusMonths(1);
        }
    };

    public abstract LocalDateTime getDataTime(LocalDateTime now);

    private final String value;

    public static OrderDay of(String day) {
        return Arrays.stream(OrderDay.values()).filter(orderDay -> orderDay.value.equals(day)).findFirst().orElse(null);
    }

}
