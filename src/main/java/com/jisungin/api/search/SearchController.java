package com.jisungin.api.search;

import com.jisungin.api.ApiResponse;
import com.jisungin.api.search.request.SearchKeywordRequest;
import com.jisungin.application.search.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/v1/search")
@RequiredArgsConstructor
@RestController
public class SearchController {

    private final SearchService searchService;

    @PostMapping("/rank")
    public ApiResponse<Void> addScoreSearchKeyword(@ModelAttribute @Valid SearchKeywordRequest request) {
        log.info("키워드 = {}", request.getKeyword());
        searchService.searchKeyword(request.getKeyword());
        return ApiResponse.ok(null);
    }

    @GetMapping("/rank")
    public ApiResponse<List<String>> getSearchRanking() {
        return ApiResponse.ok(searchService.getRankKeywords());
    }

}
