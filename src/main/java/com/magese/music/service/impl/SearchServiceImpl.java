package com.magese.music.service.impl;

import com.magese.music.client.MusicClientApi;
import com.magese.music.pojo.result.SearchResult;
import com.magese.music.pojo.vo.SearchRequest;
import com.magese.music.pojo.vo.SearchResponse;
import com.magese.music.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 搜索业务实现
 *
 * @author Magese
 * @since 2023/4/19 10:23
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    private final MusicClientApi musicClientApi;

    /**
     * 搜索
     *
     * @param request 搜索请求
     * @return 搜索结果
     */
    @Override
    public SearchResponse search(SearchRequest request) {
        List<SearchResult> search = musicClientApi.search(request);
        return SearchResponse.builder().results(search).build();
    }
}
