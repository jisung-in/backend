package com.jisungin.infra.crawler;

import org.jsoup.nodes.Document;

public interface Parser {

    String parseIsbn(Document doc);
    CrawlingBook parseBook(Document doc);

}
