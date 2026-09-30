package com.easychat.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "基础查询")
public class BaseQuery {
    @Schema(description = "分页参数")
    private SimplePage simplePage;
    @Schema(description = "页码")
    private Integer pageNo;
    @Schema(description = "每页数量")
    private Integer pageSize;
    @Schema(description = "排序字段")
    private String orderBy;

    public SimplePage getSimplePage() { return simplePage; }
    public void setSimplePage(SimplePage simplePage) { this.simplePage = simplePage; }
    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public String getOrderBy() { return orderBy; }
    public void setOrderBy(String orderBy) { this.orderBy = orderBy; }
}
