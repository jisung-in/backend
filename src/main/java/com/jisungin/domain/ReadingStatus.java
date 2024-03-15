package com.jisungin.domain;

import java.util.List;
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

    public static List<ReadingStatus> createReadingStatus(List<String> statusList) {
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
