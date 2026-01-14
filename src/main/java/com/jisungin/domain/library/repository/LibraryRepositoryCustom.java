package com.jisungin.domain.library.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.domain.library.LibraryQueryEntity;
import com.jisungin.application.library.response.UserReadingStatusResponse;
import com.jisungin.domain.ReadingStatus;
import com.jisungin.domain.library.ReadingStatusOrderType;
import java.util.List;

public interface LibraryRepositoryCustom {

    List<LibraryQueryEntity> findAllByUserId(Long userId);

    PageResponse<UserReadingStatusResponse> findAllReadingStatusOrderBy(
            Long userId, ReadingStatus readingStatus, ReadingStatusOrderType orderType, int size, int offset);

    Boolean existsByUserIdAndBookId(Long userId, String bookIsbn);

}
