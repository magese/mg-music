package com.magese.music.exception;

/**
 * 服务异常
 *
 * @author Magese
 * @since 2023/4/17 10:14
 */
public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
