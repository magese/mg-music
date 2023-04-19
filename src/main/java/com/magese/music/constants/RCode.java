package com.magese.music.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码枚举
 *
 * @author Magese
 * @since 2021/12/11 00:06
 */
@Getter
@AllArgsConstructor
public enum RCode {
    SUCCESS("0000", "成功"),
    SERVICE_EXCEPTION("E1000", "服务异常"),
    ERROR("E9999", "系统未知错误"),
    ;

    private final String code;
    private final String msg;

}
