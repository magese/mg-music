package com.magese.music.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 命令行执行结果
 *
 * @author Magese
 * @since 2023/4/17 14:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmdExecResult implements java.io.Serializable {

    /**
     * 成功信息
     */
    private String successMsg;
    /**
     * 错误信息
     */
    private String errorMsg;
    /**
     * 返回编码
     */
    private Integer code;

}
