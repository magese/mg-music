package com.magese.music.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * media-get 命令行枚举
 *
 * @author Magese
 * @since 2023/4/17 9:39
 */
public class Cmd {

    public static final String MEDIA_GET = "media-get";
    public static final Integer SUCCESS_CODE = 0;

    /**
     * Application Options
     */
    @Getter
    @AllArgsConstructor
    public enum App {
        URL(" -u ", "以 http[s]:// 开头的 url"),
        OUT(" -o ", "下载文件存储目录, 文件名可选. (默认: 当前目录)"),
        TYPE(" -t ", "希望下载的媒体类型 [auto/audio/video/all] (默认: auto)"),
        ADD_MEDIA_TAG(" --addMediaTag", "将歌曲名、专辑名、歌手等 tag 信息添加到歌曲文件里"),
        META_ONLY(" -m ", "只获取媒体信息，不下载文件"),
        INFO_FORMAT(" --infoFormat=", "媒体信息以何种形式展示。支持 plain/json. 默认为 plain"),
        LOG_LEVEL(" -l ", "日志输出级别。支持 silence/error/warn/info/debug. 默认为 info"),
        ;

        private final String code;
        private final String msg;

        public String append(String s) {
            return this.code + s;
        }
    }

    /**
     * Search Options
     */
    @Getter
    @AllArgsConstructor
    public enum Search {
        KEYWORD(" -k ", "若想使用搜索功能， keyword 或 searchSongName 是必传的. 如果都传入的话，会使用 keyword 进行搜索. Such as \"歌名 歌手名\""),
        SEARCH_SONG_NAME(" --searchSongName=", "歌名"),
        SEARCH_ARTIST(" --searchArtist=", "歌手名"),
        SEARCH_ALBUM(" --searchAlbum=", "专辑名"),
        SEARCH_TYPE(" --searchType=", "暂时只支持: song, 默认: song"),
        SOURCES(" --sources=", "在指定的网站中搜索，使用英文逗号隔开. 目前支持: bilibili,douyin,kugou,kuwo,migu,netease,qq,youtube,qmkg. 默认在全部网站中搜索"),
        EXCLUDE_SOURCE(" --excludeSource=", "排除指定的网站，使用英文逗号隔开"),
        ;

        private final String code;
        private final String msg;

        public String append(String s) {
            return this.code + s;
        }

        public String appendKeyword(String keyword) {
            return this.code + Const.DUBBO_QUOTE + keyword + Const.DUBBO_QUOTE;
        }
    }

    /**
     * Help Options
     */
    @Getter
    @AllArgsConstructor
    public enum Help {
        Help(" -h ", "Show this help message"),
        ;

        private final String code;
        private final String msg;
    }

    /**
     * 来源 bilibili,douyin,kugou,kuwo,migu,netease,qq,youtube,qmkg
     */
    @Getter
    @AllArgsConstructor
    public enum Source {
        BILIBILI("bilibili", "哔哩哔哩"),
        DOUYIN("douyin", "抖音"),
        KUGOU("kugou", "酷狗"),
        KUWO("kuwo", "酷我"),
        MIGU("migu", "咪咕音乐"),
        NETEASE("netease", "网易云音乐"),
        QQ("qq", "QQ音乐"),
        YOUTUBE("youtube", "YouTube"),
        QMKG("qmkg", "全民K歌"),
        ;

        private final String code;
        private final String msg;
    }

}
