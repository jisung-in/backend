package com.jisungin.api.talkroom.request;

import com.jisungin.application.OrderType;
import com.jisungin.application.talkroom.request.TalkRoomSearchServiceRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TalkRoomSearchRequest {

    private Integer page = 1;

    private Integer size = 10;

    private String order;

    private String search;

    @Builder
    private TalkRoomSearchRequest(Integer page, Integer size, String order, String search) {
        this.page = page != null ? page : 1;
        this.size = size != null ? size : 1;
        this.order = order != null ? order : "recent";
        this.search = search;
    }

    public TalkRoomSearchServiceRequest toService() {
        return TalkRoomSearchServiceRequest.builder()
                .page(page)
                .size(size)
                .search(search)
                .orderType(OrderType.conversionOrderType(order))
                .build();
    }

}
