package com.jisungin.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SliceResponse<T> {

    private List<T> content;

    private boolean hasContent;

    @JsonProperty("isFirst")
    private boolean first;

    @JsonProperty("isLast")
    private boolean last;

    private Integer number;

    private Integer size;

    @Builder
    private SliceResponse(List<T> content, boolean hasContent, boolean first, boolean last, Integer number,
                          Integer size) {
        this.content = content;
        this.hasContent = hasContent;
        this.first = first;
        this.last = last;
        this.number = number;
        this.size = size;
    }

    public static <T> SliceResponse<T> of(List<T> content, Integer offset, Integer limit, boolean hasNext) {
        boolean hasContent = !content.isEmpty();
        boolean first = (offset == 0);
        boolean last = !hasNext;

        Integer number = (offset / limit) + 1;
        Integer size = content.size();

        return SliceResponse.<T>builder()
                .content(content)
                .hasContent(hasContent)
                .first(first)
                .last(last)
                .number(number)
                .size(size)
                .build();
    }

}
