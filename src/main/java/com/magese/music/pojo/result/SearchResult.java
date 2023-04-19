package com.magese.music.pojo.result;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索结果
 *
 * @author Magese
 * @since 2023/4/17 16:16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResult implements java.io.Serializable {

    /**
     * 歌曲名称
     */
    @JsonAlias("Name")
    private String name;
    /**
     * 歌手
     */
    @JsonAlias("Artist")
    private String artist;
    /**
     * 专辑
     */
    @JsonAlias("Album")
    private String album;
    /**
     * 歌曲时长，单位秒，0代表未知
     */
    @JsonAlias("Duration")
    private Integer duration;
    /**
     * 歌曲原链接
     */
    @JsonAlias("Url")
    private String url;
    /**
     * 是否为被封禁资源
     */
    @JsonAlias("ResourceForbidden")
    private Boolean resourceForbidden;
    /**
     * 来源
     */
    @JsonAlias("Source")
    private String source;
    /**
     * 是否来自音乐平台
     */
    @JsonAlias("FromMusicPlatform")
    private Boolean fromMusicPlatform;
    /**
     * 评分
     */
    @JsonAlias("Score")
    private String score;

}
