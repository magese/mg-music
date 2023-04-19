package com.magese.music.service;

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
    String start(String clientId);

    /**
     * 关闭链接
     *
     * @param clientId 客户端ID
     */
    void close(String clientId);

    /**
     * 发送数据
     *
     * @param clientId 客户端ID
     * @param data     数据内容
     */
    void send(String clientId, Object data);
}
