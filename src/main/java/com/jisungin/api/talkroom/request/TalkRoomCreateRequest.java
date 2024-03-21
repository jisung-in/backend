package com.jisungin.api.talkroom.request;

import com.jisungin.application.talkroom.request.TalkRoomCreateServiceRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomCreateRequest {

    @NotNull(message = "책 isbn은 필수입니다.")
    private String bookIsbn;

    @NotEmpty(message = "주제는 필수 입니다.")
    @Size(min = 1, max = 2000, message = "2000자 이하로 작성해야 합니다.")
    private String content;

    @NotEmpty(message = "참가 조건은 1개 이상 체크해야합니다.")
    private List<String> readingStatus = new ArrayList<>();

    @Builder
    private TalkRoomCreateRequest(String bookIsbn, String content, List<String> readingStatus) {
        this.bookIsbn = bookIsbn;
        this.content = content;
        this.readingStatus = readingStatus;
    }

    public TalkRoomCreateServiceRequest toServiceRequest() {
        return TalkRoomCreateServiceRequest.builder()
                .bookIsbn(bookIsbn)
                .content(content)
                .readingStatus(readingStatus)
                .build();
    }

}
