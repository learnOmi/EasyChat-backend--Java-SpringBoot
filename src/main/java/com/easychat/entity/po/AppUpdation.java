package com.easychat.entity.po;

import java.io.Serializable;
import java.util.Date;

import com.easychat.utils.StringTools;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.utils.DateUtils;
import com.easychat.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * app发布
 * @author 'Tong'
 * @since 2026/03/02
 */
public class AppUpdation implements Serializable {
	// 自增ID
	private Integer id;
	// 版本号
	private String version;
	// 更新描述
	private String updateDesc;
	// 创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	// 0:未发布 1:灰度发布 2:全网发布
	@JsonIgnore
	private Byte status;
	// 灰度id
	private String grayscaleUid;
	// 文件类型 0:本地文件 1:外链
	private Byte fileType;
	// 外链地址
	private String outerLink;

    private String[] updateDescArray;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getVersion() {
		return version;
	}

	public void setUpdateDesc(String updateDesc) {
		this.updateDesc = updateDesc;
	}

	public String getUpdateDesc() {
		return updateDesc;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	public void setGrayscaleUid(String grayscaleUid) {
		this.grayscaleUid = grayscaleUid;
	}

	public String getGrayscaleUid() {
		return grayscaleUid;
	}

	public void setFileType(Byte fileType) {
		this.fileType = fileType;
	}

	public Byte getFileType() {
		return fileType;
	}

	public void setOuterLink(String outerLink) {
		this.outerLink = outerLink;
	}

	public String getOuterLink() {
		return outerLink;
	}

    public String[] getUpdateDescArray() {
        if (!StringTools.isEmpty(updateDesc)) {
            return updateDesc.split("\\|");
        }
        return updateDescArray;
    }

    public void setUpdateDescArray(String[] updateDescArray) {
        this.updateDescArray = updateDescArray;
    }

	@Override
	public String toString() {
		return "AppUpdation [" +
			"id=" + (id == null ? "空" : id) + ", " +
			"version=" + (version == null ? "空" : version) + ", " +
			"updateDesc=" + (updateDesc == null ? "空" : updateDesc) + ", " +
			"createTime=" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ", " +
			"status=" + (status == null ? "空" : status) + ", " +
			"grayscaleUid=" + (grayscaleUid == null ? "空" : grayscaleUid) + ", " +
			"fileType=" + (fileType == null ? "空" : fileType) + ", " +
			"outerLink=" + (outerLink == null ? "空" : outerLink) + 
			"]";
	}
}