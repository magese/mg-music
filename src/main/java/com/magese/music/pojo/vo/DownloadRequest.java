package com.magese.music.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 下载请求
 *
 * @author Magese
 * @since 2023/4/19 10:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DownloadRequest implements java.io.Serializable {

    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 音乐URL地址
     */
    private String url;
    /**
     * 下载文件存储目录, 文件名可选
     */
    private String out;
    /**
     * 希望下载的媒体类型 [auto/audio/video/all] (默认: auto)
     */
    private String type;
    /**
     * 将歌曲名、专辑名、歌手等 tag 信息添加到歌曲文件里
     */
    @Builder.Default
    private Boolean addMediaTag = false;

}
