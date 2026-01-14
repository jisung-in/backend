package com.jisungin.application.library.request;

import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.library.ReadingStatusOrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Getter
@NoArgsConstructor
public class UserReadingStatusGetAllServiceRequest {

    private static final int MAX_SIZE = 2_000;

    private ReadingStatus readingStatus;

    private ReadingStatusOrderType orderType;

    private int size;

    private int page;

    @Builder
    public UserReadingStatusGetAllServiceRequest(
            ReadingStatus readingStatus, ReadingStatusOrderType orderType, int size, int page) {
        this.readingStatus = readingStatus;
        this.orderType = orderType;
        this.size = size;
        this.page = page;
    }

    public int getOffset() {
        return (max(1, page) - 1) * min(size, MAX_SIZE);
    }
}
