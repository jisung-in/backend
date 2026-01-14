package com.jisungin.api.review.request;

import com.jisungin.application.review.request.ReviewCreateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewCreateRequest {

    @NotBlank(message = "리뷰 작성 시 책 isbn은 필수입니다.")
    private String bookIsbn;

    @NotBlank(message = "리뷰 작성 시 내용은 필수입니다.")
    private String content;

    @Builder
    private ReviewCreateRequest(String bookIsbn, String content) {
        this.bookIsbn = bookIsbn;
        this.content = content;
    }

    public ReviewCreateServiceRequest toServiceRequest() {
        return ReviewCreateServiceRequest.builder()
                .bookIsbn(bookIsbn)
                .content(content)
                .build();
    }

}
