package com.magese.music.controller;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.json.JSONUtil;
import com.magese.music.pojo.dto.DownloadProgress;
import com.magese.music.pojo.vo.DownloadRequest;
import com.magese.music.pojo.vo.DownloadResponse;
import com.magese.music.service.DownloadService;
import com.magese.music.service.SseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 下载控制器
 *
 * @author Magese
 * @since 2023/4/19 14:41
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/download")
public class DownloadController {

    private final SseService sseService;
    private final DownloadService downloadService;

    @GetMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public void download(@Valid DownloadRequest request) {
        log.info("音乐下载 => 请求对象：{}", request);

        String clientId = sseService.start(request.getClientId());
        DownloadResponse response = downloadService.download(request);
        DownloadProgress progress = response.getProgress();

        while (!progress.isCompleted()) {
            String progressJson = JSONUtil.toJsonStr(progress);
            log.info("下载进度 => {}", progressJson);
            sseService.send(clientId, progressJson);
            ThreadUtil.safeSleep(500);
        }

        sseService.close(clientId);
    }

}
