package com.magese.music.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.magese.music.constants.RCode;
import lombok.Data;

/**
 * 通用响应对象
 *
 * @author Magese
 * @since 2021/3/29 15:15
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements java.io.Serializable {

    private T data;
    private String code;
    private String msg;

    private R(T data, String code, String msg) {
        this.data = data;
        this.code = code;
        this.msg = msg;
    }

    public static <T> R<T> of(RCode rcode) {
        return of(null, rcode.getCode(), rcode.getMsg());
    }

    public static <T> R<T> of(T data, RCode rcode) {
        return of(data, rcode.getCode(), rcode.getMsg());
    }

    public static <T> R<T> of(T data, String code, String msg) {
        return new R<>(data, code, msg);
    }

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return of(data, RCode.SUCCESS.getCode(), RCode.SUCCESS.getMsg());
    }

    public static <T> R<T> error() {
        return error(null, RCode.ERROR.getCode(), RCode.ERROR.getMsg());
    }

    public static <T> R<T> error(RCode rcode) {
        return error(null, rcode.getCode(), rcode.getMsg());
    }

    public static <T> R<T> error(T data, RCode rcode) {
        return error(data, rcode.getCode(), rcode.getMsg());
    }

    public static <T> R<T> error(T data) {
        return error(data, RCode.ERROR.getCode(), RCode.ERROR.getMsg());
    }

    public static <T> R<T> error(String msg) {
        return error(null, RCode.ERROR.getCode(), msg);
    }

    public static <T> R<T> error(RCode rcode, String msg) {
        return error(null, rcode.getCode(), msg);
    }

    public static <T> R<T> error(T data, String code, String msg) {
        return of(data, code, msg);
    }

}
