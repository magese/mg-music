package com.magese.music.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * media-get命令行对象
 *
 * @author Magese
 * @since 2023/4/17 10:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmdOptionVo implements java.io.Serializable {

    /**
     * 以 http[s]:// 开头的 url
     */
    private String url;
    /**
     * 下载文件存储目录, 文件名可选. (默认: 当前目录)
     */
    private String out;
    /**
     * 希望下载的媒体类型 [auto/audio/video/all] (默认: auto)
     */
    private String type;
    /**
     * 将歌曲名、专辑名、歌手等 tag 信息添加到歌曲文件里
     */
    private Boolean addMediaTag;
    /**
     * 只获取媒体信息，不下载文件
     */
    private String metaOnly;
    /**
     * "媒体信息以何种形式展示。支持 plain/json. 默认为 plain
     */
    @Builder.Default
    private String infoFormat = "json";
    /**
     * 日志输出级别。支持 silence/error/warn/info/debug. 默认为 info
     */
    private String logLevel;

    /**
     * 若想使用搜索功能， keyword 或 searchSongName 是必传的. 如果都传入的话，会使用 keyword 进行搜索. Such as \"歌名 歌手名\"
     */
    private String keyword;
    /**
     * 歌名
     */
    private String searchSongName;
    /**
     * 歌手名
     */
    private String searchArtist;
    /**
     * 专辑名
     */
    private String searchAlbum;
    /**
     * 暂时只支持: song, 默认: song
     */
    private String searchType;
    /**
     * 在指定的网站中搜索，使用英文逗号隔开. 目前支持: bilibili,douyin,kugou,kuwo,migu,netease,qq,youtube,qmkg. 默认在全部网站中搜索
     */
    private String sources;
    /**
     * 排除指定的网站，使用英文逗号隔开
     */
    @Builder.Default
    private String excludeSource = "youtube";

}
