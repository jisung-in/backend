package com.jisungin.domain.book.repository;

import com.jisungin.application.PageResponse;
import com.jisungin.application.book.request.BookServicePageRequest;
import com.jisungin.application.book.response.BestSellerResponse;
import com.jisungin.infra.crawler.CrawlingBook;
import java.util.List;
import java.util.Map;

public interface BestSellerRepository {

    List<BestSellerResponse> findAll();

    PageResponse<BestSellerResponse> findBestSellerByPage(BookServicePageRequest request);

    void updateAll(Map<Long, CrawlingBook> bestSellers);

}
