package com.jisungin.infra.crawler;

import java.util.Map;

public interface Crawler {

    CrawlingBook crawlBook(String isbn);
    Map<Long, CrawlingBook> crawlBestSellerBook();

}
