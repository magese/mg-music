package com.magese.music.pojo.vo;

import com.magese.music.pojo.dto.DownloadProgress;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 下载响应
 *
 * @author Magese
 * @since 2023/4/19 10:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadResponse implements java.io.Serializable {

    /**
     * 下载进度
     */
    private DownloadProgress progress;

}
