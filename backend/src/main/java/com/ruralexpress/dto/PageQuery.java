package com.ruralexpress.dto;

import lombok.Data;

/**
 * 分页查询基类
 */
@Data
public class PageQuery {

    /**
     * 页码
     */
    private int pageNum = 1;

    /**
     * 每页数量
     */
    private int pageSize = 10;
}