package com.magese.music.config;

import com.magese.music.constants.RCode;
import com.magese.music.exception.ServiceException;
import com.magese.music.pojo.R;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 控制层切面异常处理
 *
 * @author Magese
 * @since 2023/4/19 14:31
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ControllerAdvice {

    private final HttpServletRequest request;

    /**
     * 服务异常
     */
    @ExceptionHandler(value = ServiceException.class)
    public R<Void> serviceExceptionHandler(ServiceException e) {
        log.warn("接口响应异常 => 接口地址：{}，错误信息：{}", request.getRequestURI(), e.getMessage());
        return R.error(RCode.SERVICE_EXCEPTION, e.getMessage());
    }

    /**
     * 通用异常处理
     */
    @ExceptionHandler(value = Exception.class)
    public R<Void> errorHandler(Exception e) {
        log.error("接口响应未捕获异常 => 接口地址：{}", request.getRequestURI(), e);
        return R.error();
    }

}
