package com.jisungin.api.book.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jisungin.application.book.request.BookCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "책 제목 입력은 필수 입니다.")
    private String title;

    @NotBlank(message = "책 내용 입력은 필수 입니다.")
    private String contents;

    @NotBlank(message = "책 경로 입력은 필수 입니다.")
    private String url;

    @NotBlank(message = "책 isbn 입력은 필수 입니다.")
    private String isbn;

    @JsonProperty("datetime")
    @NotBlank(message = "책 출판일 입력은 필수 입니다.")
    private String dateTime;

    @NotEmpty(message = "책 저자 입력은 필수 입니다.")
    private String[] authors;

    @NotBlank(message = "책 출판사 입력은 필수 입니다.")
    private String publisher;

    @NotBlank(message = "책 썸네일 입력은 필수 입니다.")
    private String thumbnail;

    @Builder
    private BookCreateRequest(String title, String contents, String url, String isbn, String dateTime, String[] authors,
                              String publisher, String thumbnail) {
        this.title = title;
        this.contents = contents;
        this.url = url;
        this.isbn = isbn;
        this.dateTime = dateTime;
        this.authors = authors;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
    }

    public BookCreateServiceRequest toServiceRequest() {
        return BookCreateServiceRequest.builder()
                .title(title)
                .contents(contents)
                .url(url)
                .isbn(isbn)
                .dateTime(convertToLocalDateTime(dateTime))
                .authors(convertToString(authors))
                .publisher(publisher)
                .thumbnail(thumbnail)
                .build();
    }

    private LocalDateTime convertToLocalDateTime(String dateTime) {
        return OffsetDateTime.parse(dateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
    }

    private String convertToString(String[] authors) {
        return Arrays.toString(authors);
    }

}
