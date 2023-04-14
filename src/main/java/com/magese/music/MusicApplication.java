package com.magese.music;

import cn.hutool.extra.spring.EnableSpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 启动类
 *
 * @author Magese
 * @since 2023/4/14 15:21
 */
@Slf4j
@EnableSpringUtil
@SpringBootApplication(scanBasePackages = "com.magese.music")
@EnableConfigurationProperties
public class MusicApplication {
    public static void main(String[] args) {
        log.info("*********** DDNSApplication 启动开始 ***********");
        try {
            SpringApplication.run(MusicApplication.class, args);
            log.info("*********** DDNSApplication 启动成功 ***********");
        } catch (Exception e) {
            log.error("*********** DDNSApplication 启动异常 ***********", e);
        }
    }

}
