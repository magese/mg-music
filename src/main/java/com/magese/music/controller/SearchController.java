package com.magese.music.controller;

import com.magese.music.pojo.R;
import com.magese.music.pojo.vo.SearchRequest;
import com.magese.music.pojo.vo.SearchResponse;
import com.magese.music.service.SearchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索控制器
 *
 * @author Magese
 * @since 2023/4/19 10:21
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @GetMapping(value = "/")
    public R<SearchResponse> search(@Valid SearchRequest request) {
        log.info("音乐搜索 => 搜索请求：{}", request);
        SearchResponse response = searchService.search(request);
        log.info("音乐搜索 => 搜索响应成功");
        return R.ok(response);
    }

}
