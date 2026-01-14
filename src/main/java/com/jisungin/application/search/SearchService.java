package com.jisungin.application.search;

import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchService {

    private final RedisTemplate<String, String> redisTemplate;

    public void searchKeyword(String keyword) {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        zset.incrementScore("ranking", keyword, 1);
    }

    public List<String> getRankKeywords() {
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        Set<String> typedTuples = zset.reverseRange("ranking", 0, 9);
        return List.copyOf(typedTuples);
    }

}
