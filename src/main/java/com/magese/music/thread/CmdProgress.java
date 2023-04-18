package com.magese.music.thread;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;

/**
 * 进度条
 *
 * @author Magese
 * @since 2023/4/18 15:58
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmdProgress {

    private InputStream inputStream;
    private InputStream errorStream;
    @Builder.Default
    private String percent = "0.00%";
    private String msg;
    @Builder.Default
    private boolean completed = false;
    @Builder.Default
    private boolean error = false;
}
