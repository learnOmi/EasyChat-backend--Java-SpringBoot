package com.easychat.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import java.util.ArrayList;

@Schema(description = "分页结果")
public class PaginationResultVO<T> {
    @Schema(description = "页码")
    private Integer pageNo;
    @Schema(description = "每页数量")
    private Integer pageSize;
    @Schema(description = "总数量")
    private Integer totalCount;
    @Schema(description = "总页数")
    private Integer pageTotal;
    @Schema(description = "数据列表")
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
