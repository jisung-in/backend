package com.jisungin.infra.crawler;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@ConfigurationProperties(prefix = "crawler.yes24.parser")
public class Yes24Parser implements Parser {

    private String isbnCss;
    private String isbnAttr;
    private String bookContentCss;
    private String bookJsonCss;
    private String bestRankingCss;
    private String bestIdCss;
    private String bestIdAttrs;

    @Override
    public String parseIsbn(Document doc) {
        return doc.select(isbnCss).attr(isbnAttr);
    }

    @Override
    public CrawlingBook parseBook(Document doc) {
        String json = doc.select(bookJsonCss).html();

        String title = parseJsonToString(json, "$.name");
        String isbn = parseJsonToString(json, "$.workExample[0].isbn");
        String imageUrl = parseJsonToString(json, "$.image");
        String publisher = parseJsonToString(json, "$.publisher.name");
        String authors = parseJsonToString(json, "$.author.name");
        String thumbnail = imageUrl.replace("XL", "M");
        String content = Jsoup.clean(doc.select(bookContentCss).text(), Safelist.none());
        LocalDateTime dateTime = parseDate(parseJsonToString(json, "$.workExample[0].datePublished"));

        return CrawlingBook.of(title, content, isbn, publisher, imageUrl, thumbnail, authors, dateTime);
    }

    @Override
    public Map<Long, String> parseBestSellerBookId(Document doc) {
        Elements rankings = doc.select(bestRankingCss);
        List<String> bookIds = doc.select(bestIdCss)
                .eachAttr(bestIdAttrs);

        return IntStream.range(0, rankings.size())
                .boxed()
                .collect(Collectors.toMap(
                        i -> parseRanking(rankings.get(i)),
                        bookIds::get));
    }

    private Long parseRanking(Element rankingElement) {
        return Long.parseLong(rankingElement.text());
    }

    private String parseJsonToString(String json, String path) {
        return JsonPath.read(json, path);
    }

    private LocalDateTime parseDate(String dateString) {
        return LocalDate.parse(dateString).atStartOfDay();
    }

}
