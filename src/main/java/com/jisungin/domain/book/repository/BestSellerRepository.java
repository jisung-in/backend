package com.jisungin.domain.book.repository;

import com.jisungin.application.book.response.BookWithRankingResponse;
import com.jisungin.infra.crawler.CrawledBook;
import java.util.List;
import java.util.Map;

public interface BestSellerRepository {

    Long count();

    List<BookWithRankingResponse> findAll();

    List<BookWithRankingResponse> findBooksWithRank(Integer offset, Integer limit);

    void updateAll(Map<Long, CrawledBook> crawledBookMap);

}
