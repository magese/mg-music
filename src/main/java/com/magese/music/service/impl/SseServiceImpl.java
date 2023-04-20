package com.magese.music.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.magese.music.constants.SseEventType;
import com.magese.music.exception.ServiceException;
import com.magese.music.pojo.R;
import com.magese.music.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
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
    private static final Map<String, AtomicInteger> ID_CACHE = new ConcurrentHashMap<>();

    /**
     * 开启链接
     *
     * @param clientId 客户端ID
     * @return 不存在时创建新客户端ID
     */
    @Override
    public SseEmitter start(String clientId) {
        if (StrUtil.isBlank(clientId)) {
            clientId = IdUtil.fastSimpleUUID();
        }

        SseEmitter sseEmitter;
        if ((sseEmitter = SSE_CACHE.get(clientId)) != null) {
            log.info("SSE服务 => 获取连接，客户端ID：{}，客户端数量：{}", clientId, SSE_CACHE.size());
        } else {
            sseEmitter = new SseEmitter(0L);
            sseEmitter.onCompletion(onCompletion(clientId));
            sseEmitter.onError(onError(clientId));
            sseEmitter.onTimeout(onTimeout(clientId));
            SSE_CACHE.put(clientId, sseEmitter);
            log.info("SSE服务 => 创建连接，客户端ID：{}，客户端数量：{}", clientId, SSE_CACHE.size());
        }

        send(clientId, SseEventType.ID, clientId);
        return sseEmitter;
    }

    /**
     * 关闭链接
     *
     * @param clientId 客户端ID
     */
    @Override
    public void close(String clientId) {
        SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter != null) {
            send(clientId, SseEventType.COMPLETION, R.ok());
            sseEmitter.complete();
        }
    }

    /**
     * 发送data数据
     *
     * @param clientId 客户端ID
     * @param data     数据内容
     */
    @Override
    public void sendData(String clientId, Object data) {
        send(clientId, SseEventType.DATA, data);
    }

    /**
     * 发送数据
     *
     * @param clientId  客户端ID
     * @param eventType 事件类型
     * @param data      数据内容
     */
    @Override
    public void send(String clientId, SseEventType eventType, Object data) {
        SseEmitter sseEmitter = SSE_CACHE.get(clientId);
        if (sseEmitter == null) {
            String msg = String.format("SSE服务发送数据失败，客户端不存在，客户端ID：%s", clientId);
            log.warn(msg);
            throw new ServiceException(msg);
        }

        try {
            JSONConfig config = JSONConfig.create()
                    .setIgnoreError(true)
                    .setIgnoreNullValue(false)
                    .setCheckDuplicate(false);
            String dataJson = JSONUtil.toJsonStr(data, config);
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .id(getIdAndIncrement(clientId))
                    .name(eventType.getCode())
                    .data(dataJson, MediaType.APPLICATION_JSON);
            sseEmitter.send(event);
        } catch (IOException e) {
            log.warn("SSE服务发送数据异常，异常信息：{}", e.getMessage());
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
            log.info("SSE服务 => 处理完成回调，客户端ID：{}", clientId);
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
        return e -> log.error("SSE服务处理异常回调，客户端ID：{}", clientId, e);
    }

    /**
     * 超时回调
     *
     * @param clientId 客户端ID
     * @return 回调任务
     */
    private Runnable onTimeout(String clientId) {
        return () -> {
            log.warn("SSE服务 => 处理超时回调，客户端ID：{}", clientId);
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
        ID_CACHE.remove(clientId);
        log.info("SSE服务 => 移除客户端连接，客户端ID：{}，客户端数量：{}", clientId, SSE_CACHE.size());
    }

    /**
     * 获取ID并自增
     *
     * @param clientId 客户端ID
     * @return ID值
     */
    private String getIdAndIncrement(String clientId) {
        AtomicInteger atomicInteger = ID_CACHE.get(clientId);
        if (atomicInteger == null) {
            atomicInteger = new AtomicInteger();
            ID_CACHE.put(clientId, atomicInteger);
        }
        return String.valueOf(atomicInteger.getAndIncrement());
    }
}
