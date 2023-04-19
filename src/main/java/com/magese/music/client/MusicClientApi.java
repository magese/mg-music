package com.magese.music.client;

import com.magese.music.pojo.dto.DownloadProgress;
import com.magese.music.pojo.result.SearchResult;
import com.magese.music.pojo.vo.DownloadRequest;
import com.magese.music.pojo.vo.SearchRequest;

import java.util.List;

/**
 * 音乐客户端接口
 *
 * @author Magese
 * @since 2023/4/19 10:24
 */
public interface MusicClientApi {

    /**
     * 搜索
     *
     * @param request 搜索请求
     * @return 搜索结果列表
     */
    List<SearchResult> search(SearchRequest request);

    /**
     * 下载
     *
     * @param request 下载请求
     * @return 下载进度
     */
    DownloadProgress download(DownloadRequest request);
}
