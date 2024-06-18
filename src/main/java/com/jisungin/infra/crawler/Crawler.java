package com.jisungin.infra.crawler;

import java.util.Map;

public interface Crawler {

    CrawledBook crawlBook(String isbn);
    Map<Long, CrawledBook> crawlBestSellerBook();

}
