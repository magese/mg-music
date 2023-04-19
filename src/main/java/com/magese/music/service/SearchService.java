package com.magese.music.service;

import com.magese.music.pojo.vo.SearchRequest;
import com.magese.music.pojo.vo.SearchResponse;

/**
 * 搜索业务
 *
 * @author Magese
 * @since 2023/4/19 10:22
 */
public interface SearchService {

    /**
     * 搜索
     *
     * @param request 搜索请求
     * @return 搜索结果
     */
    SearchResponse search(SearchRequest request);

}
