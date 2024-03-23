package com.jisungin.infra.crawler;

public class Yes24CrawlerConstant {

    public static final String BASE_URL = "https://www.yes24.com/Product";
    public static final String ISBN_URL = BASE_URL + "/Search?domain=BOOK&query=";
    public static final String BOOK_URL = BASE_URL + "/Goods/";
    public static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36";
    public static final String ISBN_CSS = "ul#yesSchList > li";
    public static final String ISBN_ATTR = "data-goods-no";
    public static final String BOOK_IMAGE_CSS = "span.gd_img > em.imgBdr > img.gImg";
    public static final String BOOK_IMAGE_ATTR = "src";
    public static final String BOOK_CONTENT_CSS = "div.infoWrap_txt > div.infoWrap_txtInner";

    public static String getIsbnUrl(String isbn) {
        return ISBN_URL + isbn;
    }

    public static String getBookUrl(String bookId) {
        return BOOK_URL + bookId;
    }

}
