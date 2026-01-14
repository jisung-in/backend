package com.jisungin.api.search.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SearchKeywordRequest {

    @NotBlank(message = "키워드 값은 필수 입니다.")
    private String keyword;

}
