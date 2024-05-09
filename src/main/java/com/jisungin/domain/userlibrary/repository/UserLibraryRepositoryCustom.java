package com.jisungin.domain.userlibrary.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.userlibrary.response.UserReadingStatusResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.userlibrary.ReadingStatusOrderType;

public interface UserLibraryRepositoryCustom {

    PageResponse<UserReadingStatusResponse> findAllReadingStatusOrderBy(
            Long userId, ReadingStatus readingStatus, ReadingStatusOrderType orderType, int size, int offset);

    Boolean existsByUserIdAndBookId(Long userId, String bookIsbn);

}
