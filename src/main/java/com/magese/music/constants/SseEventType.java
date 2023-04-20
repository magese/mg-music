package com.magese.music.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SSE事件类型
 *
 * @author Magese
 * @since 2023/4/20 9:43
 */
@Getter
@AllArgsConstructor
public enum SseEventType {

    ID("id"),
    DATA("data"),
    COMPLETION("completion"),
    ;

    private final String code;

}
