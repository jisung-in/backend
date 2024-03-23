package com.jisungin.infra.crawler;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CrawlingBook {

    private String imageUrl;
    private String content;

    @Builder
    private CrawlingBook(String imageUrl, String content) {
        this.imageUrl = imageUrl;
        this.content = content;
    }

    public static CrawlingBook of(String imageUrl, String content) {
        return CrawlingBook.builder()
                .imageUrl(imageUrl)
                .content(content)
                .build();
    }

    public boolean isBlankContent() {
        return this.content.isBlank();
    }

}
