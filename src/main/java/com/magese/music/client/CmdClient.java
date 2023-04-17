package com.magese.music.client;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.magese.music.constants.Const;
import com.magese.music.exception.ServiceException;
import com.magese.music.pojo.CmdExecResult;
import com.magese.music.pojo.CmdOptionVo;
import com.magese.music.pojo.SearchResult;
import com.magese.music.utils.CommonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static com.magese.music.constants.Cmd.*;

/**
 * 命令行客户端
 *
 * @author Magese
 * @since 2023/4/14 16:10
 */
@RequiredArgsConstructor
@Slf4j
public class CmdClient {

    private static final boolean IS_WIN = System.getProperty("os.name").toLowerCase().contains("win");
    private static final boolean IS_LINUX = System.getProperty("os.name").toLowerCase().contains("linux");

    private final ThreadPoolTaskExecutor cmdExecutor;


    /**
     * 搜索
     *
     * @param cmdOptionVo 命令行选项
     * @return 搜索结果
     */
    public List<SearchResult> search(CmdOptionVo cmdOptionVo) {
        CmdExecResult result = exec(cmdOptionVo);
        String msg = result.getSuccessMsg();
        JSONConfig jsonConfig = JSONConfig.create()
                .setIgnoreCase(true)
                .setIgnoreError(true);
        return JSONUtil.parseArray(msg, jsonConfig).toList(SearchResult.class);
    }

    /**
     * 解析转换命令行
     *
     * @param cmdOptionVo 命令行Vo
     * @return 可执行命令行
     */
    private String[] parseCmdLines(CmdOptionVo cmdOptionVo) {
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
                (StrUtil.isBlank(cmdOptionVo.getUrl()) ? Const.EMPTY : App.URL.append(cmdOptionVo.getUrl())) +
                (StrUtil.isBlank(cmdOptionVo.getOut()) ? Const.EMPTY : App.OUT.append(cmdOptionVo.getOut())) +
                (StrUtil.isBlank(cmdOptionVo.getType()) ? Const.EMPTY : App.TYPE.append(cmdOptionVo.getType())) +
                (cmdOptionVo.getAddMediaTag() != null && cmdOptionVo.getAddMediaTag() ? App.ADD_MEDIA_TAG.getCode() : Const.EMPTY) +
                (StrUtil.isBlank(cmdOptionVo.getMetaOnly()) ? Const.EMPTY : App.META_ONLY.append(cmdOptionVo.getMetaOnly())) +
                (StrUtil.isBlank(cmdOptionVo.getLogLevel()) ? Const.EMPTY : App.LOG_LEVEL.append(cmdOptionVo.getLogLevel())) +
                (StrUtil.isBlank(cmdOptionVo.getKeyword()) ? Const.EMPTY : Search.KEYWORD.appendKeyword(cmdOptionVo.getKeyword())) +
                (StrUtil.isBlank(cmdOptionVo.getSearchSongName()) ? Const.EMPTY : Search.SEARCH_SONG_NAME.appendKeyword(cmdOptionVo.getSearchSongName())) +
                (StrUtil.isBlank(cmdOptionVo.getSearchArtist()) ? Const.EMPTY : Search.SEARCH_ARTIST.appendKeyword(cmdOptionVo.getSearchArtist())) +
                (StrUtil.isBlank(cmdOptionVo.getSearchAlbum()) ? Const.EMPTY : Search.SEARCH_ALBUM.appendKeyword(cmdOptionVo.getSearchAlbum())) +
                (StrUtil.isBlank(cmdOptionVo.getSearchType()) ? Const.EMPTY : Search.SEARCH_TYPE.append(cmdOptionVo.getSearchType())) +
                (StrUtil.isBlank(cmdOptionVo.getSources()) ? Const.EMPTY : Search.SOURCES.append(cmdOptionVo.getSources())) +
                (StrUtil.isBlank(cmdOptionVo.getExcludeSource()) ? Const.EMPTY : Search.EXCLUDE_SOURCE.append(cmdOptionVo.getExcludeSource())) +
                (StrUtil.isBlank(cmdOptionVo.getInfoFormat()) ? Const.EMPTY : App.INFO_FORMAT.append(cmdOptionVo.getInfoFormat()));
        cmdLines.add(cmd);

        log.info("解析转换命令行结果 => {}", cmdLines);

        String[] arr = new String[cmdLines.size()];
        return cmdLines.toArray(arr);
    }

    /**
     * 执行命令行
     *
     * @param cmdLines 命令数组
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
     * 执行命令行
     *
     * @param cmdOptionVo 命令行选项
     * @return 执行结果
     */
    private CmdExecResult exec(CmdOptionVo cmdOptionVo) {
        long start = System.currentTimeMillis();

        String[] cmdLines = parseCmdLines(cmdOptionVo);

        CmdExecResult result;
        try {
            result = doExec(cmdLines);
            log.info("执行命令行结果 => {} => 耗时：{}", result, CommonUtil.formatMs(System.currentTimeMillis() - start));
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

        return result;
    }

    public static void main(String[] args) {
        ThreadPoolTaskExecutor cmdExecutor = new ThreadPoolTaskExecutor();
        cmdExecutor.setCorePoolSize(20);
        cmdExecutor.setMaxPoolSize(20);
        cmdExecutor.setQueueCapacity(Integer.MAX_VALUE);
        cmdExecutor.setKeepAliveSeconds(60);
        cmdExecutor.setThreadNamePrefix("cmd-task-");
        cmdExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        cmdExecutor.initialize();
        CmdClient cmdClient = new CmdClient(cmdExecutor);

        CmdOptionVo cmdOptionVo = CmdOptionVo.builder()
                .searchSongName("Won't you stand")
                .searchArtist("")
                .build();

        List<SearchResult> searched = cmdClient.search(cmdOptionVo);
        searched.forEach(s -> log.info(s.toString()));

        cmdExecutor.destroy();
    }

}
