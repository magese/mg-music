package com.magese.music.service;

import com.magese.music.pojo.vo.DownloadRequest;
import com.magese.music.pojo.vo.DownloadResponse;

/**
 * 下载业务
 *
 * @author Magese
 * @since 2023/4/19 14:41
 */
public interface DownloadService {

    /**
     * 下载
     *
     * @param request 下载请求
     * @return 下载响应
     */
    DownloadResponse download(DownloadRequest request);

}
