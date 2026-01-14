package com.jisungin.api.rating.request;

import com.jisungin.application.rating.request.RatingUpdateServiceRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RatingUpdateRequest {

    @NotBlank(message = "별점 수정 시 책 isbn은 필수입니다.")
    private String bookIsbn;

    @NotBlank(message = "별점 수정 시 별점은 필수입니다.")
    private String rating;

    @Builder
    private RatingUpdateRequest(String bookIsbn, String rating) {
        this.bookIsbn = bookIsbn;
        this.rating = rating;
    }

    public RatingUpdateServiceRequest toServiceRequest() {
        return RatingUpdateServiceRequest.builder()
                .bookIsbn(bookIsbn)
                .rating(Double.parseDouble(rating))
                .build();
    }

}
