package com.magese.music.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索请求
 *
 * @author Magese
 * @since 2023/4/19 10:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest implements java.io.Serializable {

    /**
     * 关键词
     */
    private String keyword;
    /**
     * 歌名
     */
    private String songName;
    /**
     * 歌手名
     */
    private String artist;
    /**
     * 专辑名
     */
    private String album;

}
