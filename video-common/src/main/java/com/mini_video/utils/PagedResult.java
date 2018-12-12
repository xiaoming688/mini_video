package com.mini_video.utils;

import java.util.List;

/**
 * @Description: 封装分页后的数据格式
 */
public class PagedResult {

    private Integer page;            // 当前页数
    private Integer total;            // 总页数
    private Long records;        // 总记录数
    private List<?> rows;        // 每行显示的内容

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return this.total;
    }

    public void setTotal(Integer pageSize) {
        this.total = this.records % pageSize == 0 ? (int) (this.records / pageSize) : (int) (this.records / pageSize) + 1;
    }

    public Long getRecords() {
        return records;
    }

    public void setRecords(Long records) {
        this.records = records;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

}
