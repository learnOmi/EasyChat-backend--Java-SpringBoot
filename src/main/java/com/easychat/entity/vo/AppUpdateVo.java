package com.easychat.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.List;

@Schema(description = "App更新信息")
public class AppUpdateVo implements Serializable {
    private static final long serialVersionUID = 4756060542150096340L;
    @Schema(description = "自增ID")
    private Integer id;
    @Schema(description = "版本号")
    private String version;
    @Schema(description = "更新内容列表")
    private List<String> updateList;
    @Schema(description = "文件大小")
    private Long size;
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "文件类型 0:本地文件 1:外链")
    private Integer fileType;
    @Schema(description = "外链地址")
    private String outerLink;
    public Integer getFileType() {
        return fileType;
    }
    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }
    public String getOuterLink() {
        return outerLink;
    }
    public void setOuterLink(String outerLink) {
        this.outerLink = outerLink;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public List<String> getUpdateList() {
        return updateList;
    }
    public void setUpdateList(List<String> updateList) {
        this.updateList = updateList;
    }
    public Long getSize() {
        return size;
    }
    public void setSize(Long size) {
        this.size = size;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
