package com.jisungin.domain.library;

import com.jisungin.domain.ReadingStatus;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
public class LibraryQueryEntity {

    private Long id;
    private String bookIsbn;
    private String readingStatus;

    @QueryProjection
    public LibraryQueryEntity(Long id, String bookIsbn, ReadingStatus readingStatus) {
        this.id = id;
        this.bookIsbn = bookIsbn;
        this.readingStatus = readingStatus.getText();
    }

}
