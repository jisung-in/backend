package com.jisungin.infra.crawler;

import static com.jisungin.infra.crawler.Yes24CrawlerConstant.*;
import static com.jisungin.infra.crawler.Yes24CrawlerConstant.USER_AGENT;

import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class Yes24Fetcher implements Fetcher {

    @Override
    public Document fetchIsbn(String isbn) {
        try {
            return Jsoup.connect(getIsbnUrl(isbn))
                    .timeout(5000)
                    .userAgent(USER_AGENT)
                    .ignoreContentType(true)
                    .get();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
    }

    @Override
    public Document fetchBook(String bookId) {
        try {
            return Jsoup.connect(getBookUrl(bookId))
                    .timeout(5000)
                    .userAgent(USER_AGENT)
                    .ignoreContentType(true)
                    .get();
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
    }

}
