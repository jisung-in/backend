package com.jisungin.infra.crawler;

import java.util.Map;
import org.jsoup.nodes.Document;

public interface Parser {

    String parseIsbn(Document doc);
    CrawledBook parseBook(Document doc);
    Map<Long, String> parseBestSellerBookId(Document doc);

}
