package com.magese.music.client;

import cn.hutool.core.thread.ThreadUtil;
import com.magese.music.exception.ServiceException;
import com.magese.music.pojo.dto.DownloadProgress;
import com.magese.music.pojo.result.SearchResult;
import com.magese.music.pojo.vo.DownloadRequest;
import com.magese.music.pojo.vo.SearchRequest;
import com.magese.music.utils.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 命令行客户端测试
 *
 * @author Magese
 * @since 2023/4/19 11:01
 */
@Slf4j
public class CmdClientTest {

    @Test
    public void clientTest() {
        ThreadPoolTaskExecutor cmdExecutor = new ThreadPoolTaskExecutor();
        cmdExecutor.setCorePoolSize(20);
        cmdExecutor.setMaxPoolSize(20);
        cmdExecutor.setQueueCapacity(Integer.MAX_VALUE);
        cmdExecutor.setKeepAliveSeconds(60);
        cmdExecutor.setThreadNamePrefix("cmd-task-");
        cmdExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        cmdExecutor.initialize();

        try {
            CmdClient cmdClient = new CmdClient(cmdExecutor);

            SearchRequest searchRequest = SearchRequest.builder()
                    .keyword("歌舞伎町")
                    .build();
            AtomicInteger i = new AtomicInteger();
            List<SearchResult> searched = cmdClient.search(searchRequest);
            searched.forEach(s -> log.info(CommonUtil.fillZero(i.getAndIncrement(), 2) + "-" + s.toString()));

            System.out.println();
            System.out.println("**********************************************************");
            System.out.println();

            DownloadRequest downloadRequest = DownloadRequest.builder()
                    .url(searched.get(0).getUrl())
                    .out("C:\\Users\\Magese\\Desktop")
                    .addMediaTag(false)
                    .build();
            DownloadProgress progress = cmdClient.download(downloadRequest);

            while (!progress.isCompleted()) {
                log.info(progress.toString());
                ThreadUtil.safeSleep(1000);
            }
            log.info(progress.toString());
            if (progress.isError()) {
                String msg = String.format("执行命令行返回结果异常，msg=%s", progress.getMsg());
                log.error(msg);
                throw new ServiceException(msg);
            }
        } catch (ServiceException e) {
            log.warn(e.getMessage());
        } catch (Exception e) {
            log.error("未知异常", e);
        } finally {
            cmdExecutor.destroy();
        }
    }

}
