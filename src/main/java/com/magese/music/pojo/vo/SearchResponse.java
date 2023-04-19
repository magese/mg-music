package com.magese.music.pojo.vo;

import com.magese.music.pojo.result.SearchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 搜索响应
 *
 * @author Magese
 * @since 2023/4/19 10:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse implements java.io.Serializable {

    /**
     * 搜索结果列表
     */
    private List<SearchResult> results;
}
