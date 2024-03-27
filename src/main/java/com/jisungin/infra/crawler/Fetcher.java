package com.jisungin.infra.crawler;

import org.jsoup.nodes.Document;

public interface Fetcher {

    Document fetchIsbn(String isbn);
    Document fetchBook(String bookId);
    Document fetchBestSellerBookId();

}
