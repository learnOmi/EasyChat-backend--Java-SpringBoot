package com.easychat.entity.query;

import com.easychat.enums.PageSize;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "简单分页")
public class SimplePage {
    @Schema(description = "页码")
    private Integer pageNo;
    @Schema(description = "每页数量")
    private Integer pageSize;
    @Schema(description = "总数量")
    private Integer countTotal;
    @Schema(description = "总页数")
    private Integer pageTotal;
    @Schema(description = "起始位置")
    private Integer start;
    @Schema(description = "结束位置（查询条数）")
    private Integer end;

    public SimplePage() {
    }

    public SimplePage(Integer pageNo, Integer pageSize, Integer countTotal) {
        if (null == pageNo) {
            pageNo = 0;
        }
        this.pageNo = pageNo;
        this.countTotal = countTotal;
        this.pageSize = pageSize;
        action();
    }

    public SimplePage(Integer start, Integer end) {
        this.start = start;
        this.end = end;
    }

    public void action() {
        if (this.pageSize <= 0) {
            this.pageSize = PageSize.SIZE20.getSize();
        }
        if (this.countTotal > 0) {
            this.pageTotal = this.countTotal % this.pageSize == 0 ? this.countTotal / this.pageSize : this.countTotal / this.pageSize + 1;
        } else {
            pageTotal = 1;
        }

        if (pageNo <= 1) {
            pageNo = 1;
        }

        if (pageNo > pageTotal) {
            pageNo = pageTotal;
        }
        this.start = (pageNo - 1) * pageSize;
        this.end = this.pageSize;
    }

    public Integer getStart () { return start; }
    public void setStart(Integer start) { this.start = start; }
    public Integer getEnd () { return end; }
    public void setEnd(Integer end) { this.end = end; }
    public Integer getPageNo() { return pageNo; }
    public void setPageNo(Integer pageNo) { this.pageNo = pageNo; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public Integer getCountTotal() { return countTotal; }
    public void setCountTotal(Integer countTotal) { this.countTotal = countTotal; }
    public Integer getPageTotal() { return pageTotal; }
    public void setPageTotal(Integer pageTotal) { this.pageTotal = pageTotal; }
}
