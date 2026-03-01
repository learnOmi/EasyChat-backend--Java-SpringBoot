package com.easychat.entity.query;

import java.util.Date;
/**
 * app发布查询
 * @author 'Tong'
 * @since 2026/03/02
 */
public class AppUpdationQuery extends BaseQuery {
	// 自增ID
	private Integer id;
	// 版本号
	private String version;
	private String versionFuzzy;

	// 更新描述
	private String updateDesc;
	private String updateDescFuzzy;

	// 创建时间
	private Date createTime;
	private String createTimeStart;

	private String createTimeEnd;

	// 0:未发布 1:灰度发布 2:全网发布
	private Byte status;
	// 灰度id
	private String grayscaleUid;
	private String grayscaleUidFuzzy;

	// 文件类型 0:本地文件 1:外链
	private Byte fileType;
	// 外链地址
	private String outerLink;
	private String outerLinkFuzzy;

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

	public void setVersionFuzzy(String versionFuzzy) {
		this.versionFuzzy = versionFuzzy;
	}

	public String getVersionFuzzy() {
		return versionFuzzy;
	}

	public void setUpdateDescFuzzy(String updateDescFuzzy) {
		this.updateDescFuzzy = updateDescFuzzy;
	}

	public String getUpdateDescFuzzy() {
		return updateDescFuzzy;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setGrayscaleUidFuzzy(String grayscaleUidFuzzy) {
		this.grayscaleUidFuzzy = grayscaleUidFuzzy;
	}

	public String getGrayscaleUidFuzzy() {
		return grayscaleUidFuzzy;
	}

	public void setOuterLinkFuzzy(String outerLinkFuzzy) {
		this.outerLinkFuzzy = outerLinkFuzzy;
	}

	public String getOuterLinkFuzzy() {
		return outerLinkFuzzy;
	}

}