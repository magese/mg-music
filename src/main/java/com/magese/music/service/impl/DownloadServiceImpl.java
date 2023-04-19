package com.magese.music.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.magese.music.client.MusicClientApi;
import com.magese.music.pojo.dto.DownloadProgress;
import com.magese.music.pojo.vo.DownloadRequest;
import com.magese.music.pojo.vo.DownloadResponse;
import com.magese.music.service.DownloadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 下载业务实现
 *
 * @author Magese
 * @since 2023/4/19 14:42
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DownloadServiceImpl implements DownloadService {

    private final MusicClientApi cmdClient;

    /**
     * 下载
     *
     * @param request 下载请求
     * @return 下载响应
     */
    @Override
    public DownloadResponse download(DownloadRequest request) {
        DownloadProgress progress = cmdClient.download(request);
        return DownloadResponse.builder().progress(progress).build();
    }
}
