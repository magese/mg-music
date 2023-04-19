package com.magese.music.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * 下载进度
 *
 * @author Magese
 * @since 2023/4/19 10:26
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadProgress implements java.io.Serializable {

    /**
     * 完成百分比
     */
    @Builder.Default
    private BigDecimal percent = BigDecimal.ZERO;
    /**
     * 返回消息
     */
    private String msg;
    /**
     * 是否已完成
     */
    @Builder.Default
    private boolean completed = false;
    /**
     * 是否错误
     */
    @Builder.Default
    private boolean error = false;

}
