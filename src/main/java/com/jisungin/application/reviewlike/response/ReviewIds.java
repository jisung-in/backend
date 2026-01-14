package com.jisungin.application.reviewlike.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewIds {

    private List<Long> reviewIds;

    @Builder
    private ReviewIds(List<Long> reviewIds) {
        this.reviewIds = reviewIds;
    }

    public static ReviewIds of(List<Long> reviewIds) {
        return ReviewIds.builder()
                .reviewIds(reviewIds)
                .build();
    }

}
