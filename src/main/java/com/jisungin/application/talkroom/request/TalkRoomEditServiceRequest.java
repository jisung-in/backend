package com.jisungin.application.talkroom.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TalkRoomEditServiceRequest {

    private Long id;

    @Size(min = 1, max = 2000, message = "2000자 이하로 작성해야 합니다.")
    private String content;

    @NotEmpty(message = "참가 조건은 1개 이상 체크해야합니다.")
    private List<String> readingStatus;

    @Builder
    private TalkRoomEditServiceRequest(Long id, String content, List<String> readingStatus) {
        this.id = id;
        this.content = content;
        this.readingStatus = readingStatus;
    }

}
