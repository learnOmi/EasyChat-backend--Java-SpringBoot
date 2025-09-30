package com.easychat.entity.vo;

import java.util.List;

import java.util.ArrayList;

public class PaginationResultVO<T> {
    private Integer pageNo;
    private Integer pageSize;
    private Integer totalCount;
    private Integer pageTotal;
    private List<T> list = new ArrayList<T>();

    public PaginationResultVO(Integer totalCount, Integer pageNo, Integer pageSize, List<T> list) {
        this.totalCount = totalCount;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.list = list;
    }

    public PaginationResultVO(Integer totalCount, Integer pageNo, Integer pageSize, List<T> list, Integer pageTotal) {
        if (pageNo == 0) {
            pageNo = 1;
        }
        this.totalCount = totalCount;
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.list = list;
        this.pageTotal = pageTotal;
    }

    public PaginationResultVO(List<T> list) { this.list = list; }

    public PaginationResultVO() {}

    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public Integer getTotalCount() { return totalCount; }
    public void setTotalCount(Integer totalCount) { this.totalCount = totalCount; }
    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public Integer getPageTotal() { return pageTotal; }
    public void setPageTotal(Integer pageTotal) { this.pageTotal = pageTotal; }
}
