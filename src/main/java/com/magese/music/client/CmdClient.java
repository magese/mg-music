package com.magese.music.client;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.magese.music.constants.Const;
import com.magese.music.exception.ServiceException;
import com.magese.music.pojo.dto.CmdOption;
import com.magese.music.pojo.dto.DownloadProgress;
import com.magese.music.pojo.result.CmdExecResult;
import com.magese.music.pojo.result.SearchResult;
import com.magese.music.pojo.vo.DownloadRequest;
import com.magese.music.pojo.vo.SearchRequest;
import com.magese.music.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.MatchResult;

import static com.magese.music.constants.Cmd.*;

/**
 * 命令行客户端
 *
 * @author Magese
 * @since 2023/4/14 16:10
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CmdClient implements MusicClientApi {

    private static final boolean IS_WIN = System.getProperty("os.name").toLowerCase().contains("win");
    private static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().contains("linux");

    private final ThreadPoolTaskExecutor cmdExecutor;


    /**
     * 搜索
     *
     * @param request 搜索请求
     * @return 搜索结果
     */
    @Override
    public List<SearchResult> search(SearchRequest request) {
        long start = System.currentTimeMillis();

        CmdOption cmdOption = CmdOption.builder()
                .keyword(request.getKeyword())
                .searchSongName(request.getSongName())
                .searchArtist(request.getArtist())
                .searchAlbum(request.getAlbum())
                .build();
        String[] cmdLines = parseCmdLines(cmdOption);
        CmdExecResult result;
        try {
            result = doExec(cmdLines);
            log.info("执行搜索命令行完成 => 耗时：{}", CommonUtil.formatMs(System.currentTimeMillis() - start));
        } catch (IOException e) {
            String msg = String.format("执行命令行IO异常：%s", Arrays.toString(cmdLines));
            log.error(msg, e);
            throw new ServiceException(msg, e);
        } catch (ExecutionException e) {
            String msg = String.format("执行命令行异步获取结果异常：%s", Arrays.toString(cmdLines));
            log.error(msg, e);
            throw new ServiceException(msg, e);
        } catch (InterruptedException e) {
            String msg = String.format("执行命令行线程中断异常：%s", Arrays.toString(cmdLines));
            log.error(msg, e);
            throw new ServiceException(msg, e);
        } catch (TimeoutException e) {
            String msg = String.format("执行命令行超时异常：%s", Arrays.toString(cmdLines));
            log.error(msg, e);
            throw new ServiceException(msg, e);
        }

        if (!SUCCESS_CODE.equals(result.getCode())) {
            String msg = String.format("执行命令行返回结果异常，code=%s, msg=%s", result.getCode(), result.getErrorMsg());
            log.error(msg);
            throw new ServiceException(msg);
        }

        String msg = result.getSuccessMsg();

        String regex = "\\[\\s*\\{\\s*\"Name\"";
        MatchResult matchResult = ReUtil.indexOf(regex, msg);
        if (matchResult == null) {
            return Collections.emptyList();
        }
        msg = msg.substring(matchResult.start(0));

        JSONConfig jsonConfig = JSONConfig.create()
                .setIgnoreCase(true)
                .setIgnoreError(true);
        JSONArray array = JSONUtil.parseArray(msg, jsonConfig);
        log.info("搜索结果数量 => {}", array.size());
        return array.toList(SearchResult.class);
    }

    /**
     * 异步下载
     *
     * @param request 下载请求
     * @return 下载进度结果
     */
    @Override
    public DownloadProgress download(DownloadRequest request) {
        long start = System.currentTimeMillis();

        CmdOption cmdOption = CmdOption.builder()
                .url(request.getUrl())
                .out(request.getOut())
                .addMediaTag(request.getAddMediaTag())
                .infoFormat(Format.PLAIN.getCode())
                .build();
        String[] cmdLines = parseCmdLines(cmdOption);
        DownloadProgress progress;
        try {
            progress = doExecAsync(cmdLines);
            log.info("执行异步下载命令行完成 => 耗时：{}", CommonUtil.formatMs(System.currentTimeMillis() - start));
        } catch (IOException e) {
            String msg = String.format("执行命令行IO异常：%s", Arrays.toString(cmdLines));
            log.error(msg, e);
            throw new ServiceException(msg, e);
        }

        return progress;
    }

    /**
     * 解析转换命令行
     *
     * @param cmdOption 命令行Vo
     * @return 可执行命令行
     */
    private String[] parseCmdLines(CmdOption cmdOption) {
        List<String> cmdLines = new ArrayList<>();

        if (IS_WIN) {
            cmdLines.add("cmd");
            cmdLines.add("/c");
        } else if (IS_LINUX) {
            cmdLines.add("/bin/bash");
            cmdLines.add("-c");
        } else {
            String msg = String.format("不支持的操作系统：%s", System.getProperty("os.name"));
            log.error(msg);
            throw new ServiceException(msg);
        }

        String cmd = MEDIA_GET +
                (StrUtil.isBlank(cmdOption.getUrl()) ? Const.EMPTY : App.URL.append(cmdOption.getUrl())) +
                (StrUtil.isBlank(cmdOption.getOut()) ? Const.EMPTY : App.OUT.append(cmdOption.getOut())) +
                (StrUtil.isBlank(cmdOption.getType()) ? Const.EMPTY : App.TYPE.append(cmdOption.getType())) +
                (cmdOption.getAddMediaTag() != null && cmdOption.getAddMediaTag() ? App.ADD_MEDIA_TAG.getCode() : Const.EMPTY) +
                (StrUtil.isBlank(cmdOption.getMetaOnly()) ? Const.EMPTY : App.META_ONLY.append(cmdOption.getMetaOnly())) +
                (StrUtil.isBlank(cmdOption.getLogLevel()) ? Const.EMPTY : App.LOG_LEVEL.append(cmdOption.getLogLevel())) +
                (StrUtil.isBlank(cmdOption.getKeyword()) ? Const.EMPTY : Search.KEYWORD.appendKeyword(cmdOption.getKeyword())) +
                (StrUtil.isBlank(cmdOption.getSearchSongName()) ? Const.EMPTY : Search.SEARCH_SONG_NAME.appendKeyword(cmdOption.getSearchSongName())) +
                (StrUtil.isBlank(cmdOption.getSearchArtist()) ? Const.EMPTY : Search.SEARCH_ARTIST.appendKeyword(cmdOption.getSearchArtist())) +
                (StrUtil.isBlank(cmdOption.getSearchAlbum()) ? Const.EMPTY : Search.SEARCH_ALBUM.appendKeyword(cmdOption.getSearchAlbum())) +
                (StrUtil.isBlank(cmdOption.getSearchType()) ? Const.EMPTY : Search.SEARCH_TYPE.append(cmdOption.getSearchType())) +
                (StrUtil.isBlank(cmdOption.getSources()) ? Const.EMPTY : Search.SOURCES.append(cmdOption.getSources())) +
                (StrUtil.isBlank(cmdOption.getExcludeSource()) ? Const.EMPTY : Search.EXCLUDE_SOURCE.append(cmdOption.getExcludeSource())) +
                (StrUtil.isBlank(cmdOption.getInfoFormat()) ? Const.EMPTY : App.INFO_FORMAT.append(cmdOption.getInfoFormat()));
        cmdLines.add(cmd);

        log.info("解析命令行 => {}", cmdLines);

        String[] arr = new String[cmdLines.size()];
        return cmdLines.toArray(arr);
    }

    /**
     * 执行命令行
     *
     * @param cmdLines 命令行数组
     * @return 执行结果
     * @throws IOException          读写异常
     * @throws InterruptedException 线程中断异常
     * @throws ExecutionException   异步线程获取结果异常
     * @throws TimeoutException     执行超时异常
     */
    private CmdExecResult doExec(String[] cmdLines) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmdLines);

        CompletableFuture<String> successMsgFuture = CompletableFuture
                .supplyAsync(() -> IoUtil.read(process.getInputStream(), StandardCharsets.UTF_8), cmdExecutor);
        CompletableFuture<String> errorMsgFuture = CompletableFuture
                .supplyAsync(() -> IoUtil.read(process.getErrorStream(), StandardCharsets.UTF_8), cmdExecutor);

        int code = process.waitFor();

        return CmdExecResult.builder()
                .successMsg(successMsgFuture.get(60, TimeUnit.SECONDS))
                .errorMsg(errorMsgFuture.get(60, TimeUnit.SECONDS))
                .code(code)
                .build();
    }

    /**
     * 异步执行命令行
     *
     * @param cmdLines 命令行数组
     * @return 执行结果，会持续更新至 completed = true
     * @throws IOException 读写异常
     */
    private DownloadProgress doExecAsync(String[] cmdLines) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmdLines);

        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();
        DownloadProgress progress = new DownloadProgress();

        // errorStream
        CompletableFuture.runAsync(() -> {
            String errorMsg = IoUtil.read(errorStream, StandardCharsets.UTF_8);
            if (StrUtil.isNotBlank(errorMsg)) {
                progress.setCompleted(true);
                progress.setError(true);
                progress.setMsg(errorMsg);
            }
        }, cmdExecutor);

        // inputStream
        CompletableFuture.runAsync(() -> {
            StringBuilder builder = new StringBuilder();
            try (InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 BufferedReader reader = new BufferedReader(streamReader)) {
                String line;
                String prefix = "downloading percent: ";
                String regex = "\\d+\\.\\d+";
                while ((line = reader.readLine()) != null) {
                    if (ReUtil.contains(prefix + regex, line)) {
                        BigDecimal percent = new BigDecimal(ReUtil.getGroup0(regex, line))
                                .divide(BigDecimal.valueOf(100), 4, RoundingMode.DOWN);
                        progress.setPercent(percent);
                    } else {
                        builder.append(line);
                    }
                }
                progress.setMsg(builder.toString());
            } catch (IOException e) {
                String msg = String.format("读取控制台输出消息IO异常：%s", e.getMessage());
                progress.setCompleted(true);
                progress.setError(true);
                progress.setMsg(msg);
                log.error(msg);
                throw new ServiceException(msg);
            } finally {
                progress.setCompleted(true);
            }
        });

        return progress;
    }

}
