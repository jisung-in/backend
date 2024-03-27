package com.jisungin.infra.crawler;

import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import java.net.SocketTimeoutException;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@ConfigurationProperties(prefix = "crawler.yes24.fetcher")
public class Yes24Fetcher implements Fetcher {

    private String isbnUrl;
    private String bookUrl;
    private String bestBookUrl;
    private String userAgent;

    @Override
    public Document fetchIsbn(String isbn) {
        try {
            return Jsoup.connect(getIsbnUrl(isbn))
                    .timeout(5000)
                    .userAgent(userAgent)
                    .ignoreContentType(true)
                    .get();
        } catch (SocketTimeoutException e) {
            throw new BusinessException(ErrorCode.REQUEST_TIME_OUT);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
    }

    @Override
    public Document fetchBook(String bookId) {
        try {
            return Jsoup.connect(getBookUrl(bookId))
                    .timeout(5000)
                    .userAgent(userAgent)
                    .ignoreContentType(true)
                    .get();
        } catch (SocketTimeoutException e) {
            throw new BusinessException(ErrorCode.REQUEST_TIME_OUT);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
    }

    @Override
    public Document fetchBestSellerBookId() {
        try {
            return Jsoup.connect(bestBookUrl)
                    .timeout(5000)
                    .userAgent(userAgent)
                    .ignoreContentType(true)
                    .get();
        } catch (SocketTimeoutException e) {
            throw new BusinessException(ErrorCode.REQUEST_TIME_OUT);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
        }
    }

    private String getIsbnUrl(String isbn) {
        return isbnUrl + isbn;
    }

    private String getBookUrl(String bookId) {
        return bookUrl + bookId;
    }

}
