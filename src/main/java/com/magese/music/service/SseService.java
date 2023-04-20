package com.magese.music.service;

import com.magese.music.constants.SseEventType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 服务端发送事件业务
 *
 * @author Magese
 * @since 2023/4/19 16:01
 */
public interface SseService {

    /**
     * 开启链接
     *
     * @param clientId 客户端ID
     * @return 不存在时创建新客户端ID
     */
    SseEmitter start(String clientId);

    /**
     * 关闭链接
     *
     * @param clientId 客户端ID
     */
    void close(String clientId);

    /**
     * 发送data数据
     *
     * @param clientId 客户端ID
     * @param data     数据内容
     */
    void sendData(String clientId, Object data);

    /**
     * 发送数据
     *
     * @param clientId  客户端ID
     * @param eventType 事件类型
     * @param data      数据内容
     */
    void send(String clientId, SseEventType eventType, Object data);
}
