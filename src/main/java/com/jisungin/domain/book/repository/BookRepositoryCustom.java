package com.jisungin.domain.book.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.book.response.SimpleBookResponse;

public interface BookRepositoryCustom {

    PageResponse<SimpleBookResponse> getBooks(Long offset, Integer size, String order);

}
