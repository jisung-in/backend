package com.jisungin.domain.book.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jisungin.application.PageResponse;
import com.jisungin.application.book.request.BookServicePageRequest;
import com.jisungin.application.book.response.BestSellerResponse;
import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import com.jisungin.infra.crawler.CrawlingBook;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BestSellerRedisRepository implements BestSellerRepository {

    private static final String BEST_SELLER_REDIS_KEY = "BEST_SELLER";
    private final ObjectMapper om;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public PageResponse<BestSellerResponse> findBestSellerByPage(BookServicePageRequest request) {
        List<BestSellerResponse> queryResponse = redisTemplate.opsForHash()
                .multiGet(BEST_SELLER_REDIS_KEY, createHashKeys(request.extractStartIndex(), request.extractEndIndex()))
                .stream()
                .map(this::parseToBestSellerResponse)
                .sorted(Comparator.comparing(BestSellerResponse::getRanking))
                .toList();

        return PageResponse.<BestSellerResponse>builder()
                .size(request.getSize())
                .totalCount(redisTemplate.opsForHash().size(BEST_SELLER_REDIS_KEY))
                .queryResponse(queryResponse)
                .build();
    }

    @Override
    public List<BestSellerResponse> findAll() {
        Map<Object, Object> bestSellers = redisTemplate.opsForHash().entries(BEST_SELLER_REDIS_KEY);

        return bestSellers.values().stream()
                .map(this::parseToBestSellerResponse)
                .sorted(Comparator.comparing(BestSellerResponse::getRanking))
                .toList();
    }

    @Override
    public void updateAll(Map<Long, CrawlingBook> bestSellers) {
        bestSellers.forEach((key, value) -> redisTemplate.opsForHash()
                .put(BEST_SELLER_REDIS_KEY, String.valueOf(key), parseToBestSellerResponseJson(key, value)));
    }

    private BestSellerResponse parseToBestSellerResponse(Object value) {
        try {
            return om.readValue((String) value, BestSellerResponse.class);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.BOOK_INVALID_INFO);
        }
    }

    private String parseToBestSellerResponseJson(Long key, CrawlingBook book) {
        try {
            return om.writeValueAsString(
                    BestSellerResponse.of(key, book.getIsbn(), book.getTitle(), book.getPublisher(),
                            book.getThumbnail(), book.getAuthors(), book.getDateTime()));
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.BOOK_INVALID_INFO);
        }
    }

    private List<Object> createHashKeys(Integer startIndex, Integer endIndex) {
        return IntStream.rangeClosed(startIndex, endIndex)
                .boxed()
                .map(Object::toString)
                .map(obj -> (Object) obj)
                .toList();
    }

}
