package com.magese.music.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.magese.music.exception.ServiceException;
import com.magese.music.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 服务端发送事件业务实现
 *
 * @author Magese
 * @since 2023/4/19 16:02
 */
@Slf4j
@Service
public class SseServiceImpl implements SseService {

    private static final Map<String, SseEmitter> SSE_CACHE = new ConcurrentHashMap<>();
    private static final String SSE_CLIENT_ID = "ClientId";

    /**
     * 开启链接
     *
     * @param clientId 客户端ID
     * @return 不存在时创建新客户端ID
     */
    @Override
    public String start(String clientId) {
        if (StrUtil.isBlank(clientId)) {
            clientId = IdUtil.fastSimpleUUID();
        }

        SseEmitter sseEmitter = Optional.ofNullable(SSE_CACHE.get(clientId)).orElse(new SseEmitter(0L));
        sseEmitter.onCompletion(onCompletion(clientId));
        sseEmitter.onError(onError(clientId));
        sseEmitter.onTimeout(onTimeout(clientId));
        SSE_CACHE.put(clientId, sseEmitter);

        try {
            SseEmitter.SseEventBuilder eventBuilder = SseEmitter.event()
                    .id(SSE_CLIENT_ID)
                    .data(clientId);
            sseEmitter.send(eventBuilder);
            log.info("创建SSE客户端连接 => 客户端ID：{}", clientId);
        } catch (IOException e) {
            String msg = String.format("创建SSE长链接异常，异常信息：%s", e.getMessage());
            log.error(msg, e);
            throw new ServiceException(msg);
        }

        return clientId;
    }

    /**
     * 关闭链接
     *
     * @param clientId 客户端ID
     */
    @Override
    public void close(String clientId) {
        try {
            SseEmitter sseEmitter = SSE_CACHE.get(clientId);
            if (sseEmitter != null) {
                sseEmitter.complete();
                removeFromCache(clientId);
            }
        } catch (Exception e) {
            String msg = String.format("关闭SSE连接异常，客户端ID：%s", clientId);
            log.error(msg, e);
            throw new ServiceException(msg);
        }
    }

    /**
     * 发送数据
     *
     * @param clientId 客户端ID
     * @param data     数据内容
     */
    @Override
    public void send(String clientId, Object data) {
        SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter == null) {
            String msg = String.format("SSE长链接发送数据失败，客户端不存在，客户端ID：%s", clientId);
            log.warn(msg);
            throw new ServiceException(msg);
        }

        try {
            sseEmitter.send(data);
        } catch (IOException e) {
            String msg = String.format("SSE长链接发送数据异常，异常信息：%s", e.getMessage());
            log.error(msg, e);
            throw new ServiceException(msg);
        }
    }

    /**
     * 完成回调
     *
     * @param clientId 客户端ID
     * @return 回调任务
     */
    private Runnable onCompletion(String clientId) {
        return () -> {
            log.info("SSE长链接 => 处理完成，客户端ID：{}", clientId);
            removeFromCache(clientId);
        };
    }

    /**
     * 异常回调
     *
     * @param clientId 客户端ID
     * @return 回调任务
     */
    private Consumer<Throwable> onError(String clientId) {
        return e -> {
            log.error("SSE长链接处理异常，客户端ID：{}", clientId, e);

            // retry
            for (int i = 0; i < 3; i++) {
                int retryNum = i + 1;
                try {
                    ThreadUtil.safeSleep(1000);
                    SseEmitter sseEmitter = SSE_CACHE.get(clientId);
                    if (sseEmitter == null) {
                        log.warn("SSE第{}次重推消息失败，未找到客户端，客户端ID：{}", retryNum, clientId);
                        continue;
                    }
                    sseEmitter.send(String.format("失败后第%s次重推", retryNum));
                } catch (Exception ex) {
                    log.error("SSE第{}次重推消息失败", retryNum, e);
                }
            }
        };
    }

    /**
     * 超时回调
     *
     * @param clientId 客户端ID
     * @return 回调任务
     */
    private Runnable onTimeout(String clientId) {
        return () -> {
            log.warn("SSE长链接 => 处理超时，客户端ID：{}", clientId);
            removeFromCache(clientId);
        };
    }

    /**
     * 从缓存中删除
     *
     * @param clientId 客户端ID
     */
    private void removeFromCache(String clientId) {
        SSE_CACHE.remove(clientId);
        log.info("移除SSE客户端连接 => 客户端ID：{}", clientId);
    }
}
