package com.jisungin.domain.book.repository;

import com.jisungin.application.book.response.BookWithRankingResponse;
import com.jisungin.infra.JsonConverter;
import com.jisungin.infra.crawler.CrawledBook;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BestSellerRedisRepository implements BestSellerRepository {

    private static final String BEST_SELLER_REDIS_KEY = "BEST_SELLER";

    private final JsonConverter converter;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public Long count() {
        return redisTemplate.opsForZSet().zCard(BEST_SELLER_REDIS_KEY);
    }

    @Override
    public List<BookWithRankingResponse> findBooksWithRank(Integer offset, Integer limit) {
        Set<String> bookJsonSet = Optional.ofNullable(
                        redisTemplate.opsForZSet().range(BEST_SELLER_REDIS_KEY, offset, limit))
                .orElse(Collections.emptySet());

        return bookJsonSet.stream()
                .map(json -> {
                    Long rank = redisTemplate.opsForZSet().rank(BEST_SELLER_REDIS_KEY, json);
                    return BookWithRankingResponse.ofRankIncrement(rank, converter.fromJson(json, CrawledBook.class));
                })
                .toList();
    }

    @Override
    public List<BookWithRankingResponse> findAll() {
        Set<String> bookJsonSet = Optional.ofNullable(
                        redisTemplate.opsForZSet().range(BEST_SELLER_REDIS_KEY, 0, -1))
                .orElse(Collections.emptySet());

        return bookJsonSet.stream()
                .map(json -> {
                    Long rank = redisTemplate.opsForZSet().rank(BEST_SELLER_REDIS_KEY, json);
                    return BookWithRankingResponse.ofRankIncrement(rank, converter.fromJson(json, CrawledBook.class));
                })
                .toList();
    }

    @Override
    public void updateAll(Map<Long, CrawledBook> crawledBookMap) {
        redisTemplate.delete(BEST_SELLER_REDIS_KEY);

        crawledBookMap.forEach((key, value) -> redisTemplate.opsForZSet()
                .add(BEST_SELLER_REDIS_KEY, converter.toJson(value), key));
    }

}
