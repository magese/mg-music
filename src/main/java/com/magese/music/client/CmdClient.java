package com.magese.music.client;

import cn.hutool.core.io.IoUtil;
import com.magese.music.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * 命令行客户端
 *
 * @author Magese
 * @since 2023/4/14 16:10
 */
@Slf4j
public class CmdClient {

    public static void main(String[] args) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        // String[] cmdLine = {"cmd", "/c", "media-get", "--searchSongName=\"一路向北\"", "--searchArtist=\"周杰伦\"", "--infoFormat=json"};
        String[] cmdLine = {"cmd", "/c", "media-get --searchSongName=\"一路向北\" --searchArtist=\"周杰伦\" --infoFormat=json"};
        // String[] cmdLine = {"cmd", "/c", "media-get.exe -h"};
        // String[] cmdLine = {"cmd", "/c", "dir"};
        // String process = RuntimeUtil.execForStr(cmdLine);
        // Runtime runtime = Runtime.getRuntime();
        // Process process = runtime.exec(cmdLine);

        Process process = new ProcessBuilder()
                .redirectErrorStream(true)
                .command(cmdLine)
                .start();

        // int code = process.waitFor();
        log.info("process time => {} => code:{}", CommonUtil.formatMs(System.currentTimeMillis() - start), 1);

        InputStream inputStream = process.getInputStream();
        String read = IoUtil.read(inputStream, Charset.forName("GBK"));

        log.info("stream => {}", read);
        log.info("stream time => {}", CommonUtil.formatMs(System.currentTimeMillis() - start));
    }

}
