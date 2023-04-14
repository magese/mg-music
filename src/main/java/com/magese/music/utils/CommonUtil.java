package com.magese.music.utils;

/**
 * 公共工具
 *
 * @author Magese
 * @since 2023/4/14 16:47
 */
public class CommonUtil {

    /**
     * 格式化毫秒数
     *
     * @param ms 毫秒数
     * @return 示例：1 day 4 hour 2 min 55 s 177 ms
     */
    public static String formatMs(long ms) {
        long s = 0;
        long min = 0;
        long hour = 0;
        long day = 0;

        if (ms >= 1000) {
            s = ms / 1000;
            ms = ms % 1000;
        }
        if (s >= 60) {
            min = s / 60;
            s = s % 60;
        }
        if (min >= 60) {
            hour = min / 60;
            min = min % 60;
        }
        if (hour >= 24) {
            day = hour / 24;
            hour = hour % 24;
        }

        StringBuilder res = new StringBuilder();
        if (day > 0) {
            res.append(day).append(" day ");
        }
        if (hour > 0) {
            res.append(hour).append(" hour ");
        }
        if (min > 0) {
            res.append(min).append(" min ");
        }
        if (s > 0) {
            res.append(s).append(" s ");
        }
        res.append(ms).append(" ms");
        return res.toString();
    }
}
