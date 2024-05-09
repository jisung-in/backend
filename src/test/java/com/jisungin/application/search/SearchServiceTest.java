package com.jisungin.application.search;

import com.jisungin.RedisTestContainer;
import com.jisungin.infra.s3.S3FileManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
public class SearchServiceTest extends RedisTestContainer {

    @Autowired
    private SearchService searchService;

    @Autowired
    private @Qualifier("redisTemplateSecond") RedisTemplate<String, String> redisTemplate;

    @MockBean
    private S3FileManager s3FileManager;

    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @DisplayName("사용자가 검색한 키워드의 점수가 1 증가한다.")
    @org.junit.jupiter.api.Test
    void searchSaveRanking() {
        //given
        String keyword = "testKeyword";

        //when
        searchService.searchKeyword(keyword);

        //then
        ZSetOperations<String, String> zset = redisTemplate.opsForZSet();
        Double score = zset.score("ranking", keyword);
        assertThat(score).isEqualTo(1.0); // 검색어의 점수가 1.0인지 확인

    }

    @DisplayName("키워드 검색 횟수 상위 10개를 가져온다.")
    @Test
    void getRankKeywords() {
        //given
        String keyword = "testKeyword";

        //when
        searchService.searchKeyword(keyword);

        //then
        List<String> rankKeywords = searchService.getRankKeywords();
        Assertions.assertThat(rankKeywords).contains(keyword); // 랭킹에 추가된 검색어가 있는지 확인
    }

}