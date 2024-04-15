package com.jisungin.domain;

import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import java.util.List;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReadingStatus {

    WANT("읽고 싶은"),
    READING("읽는 중"),
    READ("읽음"),
    PAUSE("잠시 멈춤"),
    STOP("중단");

    private final String text;

    public static ReadingStatus createReadingStatus(String status) {
        return ReadingStatus.valueOf(status.toUpperCase(Locale.ENGLISH));
    }

    public static List<ReadingStatus> createReadingStatus(List<String> statusList) {
        if (statusList == null) {
            throw new BusinessException(ErrorCode.PARTICIPATION_CONDITION_ERROR);
        }
        return statusList.stream()
                .map(value -> {
                    for (ReadingStatus rs : values()) {
                        if (rs.getText().equals(value)) {
                            return rs;
                        }
                    }
                    throw new IllegalArgumentException();
                })
                .toList();
    }

}
