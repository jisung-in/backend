package com.jisungin.api.library.request;

import com.jisungin.application.library.request.UserReadingStatusGetAllServiceRequest;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.library.ReadingStatusOrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserReadingStatusGetAllRequest {

    private Integer page;

    private Integer size;

    private String order;

    private String status;

    @Builder
    public UserReadingStatusGetAllRequest(Integer page, Integer size, String order, String status) {
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 10;
        this.order = order != null ? order : "dictionary";
        this.status = status;
    }

    public UserReadingStatusGetAllServiceRequest toService() {
        return UserReadingStatusGetAllServiceRequest.builder()
                .page(page)
                .size(size)
                .orderType(ReadingStatusOrderType.fromName(order))
                .readingStatus(ReadingStatus.fromName(status))
                .build();
    }
}
