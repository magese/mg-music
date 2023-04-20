package com.magese.music.controller;

import cn.hutool.core.thread.ThreadUtil;
import com.magese.music.pojo.dto.DownloadProgress;
import com.magese.music.pojo.vo.DownloadRequest;
import com.magese.music.pojo.vo.DownloadResponse;
import com.magese.music.service.DownloadService;
import com.magese.music.service.SseService;
import com.magese.music.utils.CommonUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.CompletableFuture;

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

    private final ThreadPoolTaskExecutor sseExecutor;
    private final SseService sseService;
    private final DownloadService downloadService;

    @GetMapping(value = "/")
    public SseEmitter download(@Valid DownloadRequest request) {
        log.info("音乐下载 => 请求对象：{}", request);
        String clientId = request.getClientId();
        SseEmitter sseEmitter = sseService.start(clientId);

        DownloadResponse response = downloadService.download(request);
        DownloadProgress progress = response.getProgress();

        CompletableFuture.runAsync(() -> {
            long start = System.currentTimeMillis();
            while (true) {
                log.info("下载进度 => {}", progress);
                sseService.sendData(clientId, progress);
                if (progress.isCompleted()) {
                    sseService.close(clientId);
                    break;
                }
                ThreadUtil.safeSleep(500);
            }
            log.info("下载完成 => 客户端ID：{}，下载耗时：{}", clientId, CommonUtil.formatMs(System.currentTimeMillis() - start));
        }, sseExecutor);
        return sseEmitter;
    }
}
