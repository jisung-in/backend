package com.jisungin.infra.crawler;

import static com.jisungin.infra.crawler.Yes24CrawlerConstant.BOOK_CONTENT_CSS;
import static com.jisungin.infra.crawler.Yes24CrawlerConstant.BOOK_IMAGE_ATTR;
import static com.jisungin.infra.crawler.Yes24CrawlerConstant.BOOK_IMAGE_CSS;
import static com.jisungin.infra.crawler.Yes24CrawlerConstant.ISBN_ATTR;
import static com.jisungin.infra.crawler.Yes24CrawlerConstant.ISBN_CSS;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class Yes24Parser implements Parser {
    @Override
    public String parseIsbn(Document doc) {
        return doc.select(ISBN_CSS).attr(ISBN_ATTR);
    }

    @Override
    public CrawlingBook parseBook(Document doc) {
        String image = doc.select(BOOK_IMAGE_CSS).attr(BOOK_IMAGE_ATTR);
        String content = Jsoup.clean(doc.select(BOOK_CONTENT_CSS).text(), Safelist.none());

        return CrawlingBook.of(image, content);
    }

}
