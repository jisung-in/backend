package com.jisungin.domain.book.repository;

import com.jisungin.application.book.response.BookFindAllResponse;
import java.util.List;

public interface BookRepositoryCustom {

    List<BookFindAllResponse> getBooks(Integer offset, Integer size, String order);

    Long getTotalCount(String order);

}
