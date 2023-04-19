package com.magese.music.pojo.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.InputStream;

/**
 * 进度条
 *
 * @author Magese
 * @since 2023/4/18 15:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"inputStream", "errorStream"})
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CmdProgress extends DownloadProgress {

    /**
     * 控制台输入流
     */
    private InputStream inputStream;
    /**
     * 控制台错误流
     */
    private InputStream errorStream;
}
