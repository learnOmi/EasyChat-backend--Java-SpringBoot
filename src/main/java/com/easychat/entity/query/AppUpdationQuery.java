package com.easychat.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
/**
 * app发布查询
 * @author 'Tong'
 * @since 2026/03/02
 */
@Schema(description = "app发布查询")
public class AppUpdationQuery extends BaseQuery {
	// 自增ID
	@Schema(description = "自增ID")
	private Integer id;
	// 版本号
	@Schema(description = "版本号")
	private String version;
	@Schema(description = "版本号（模糊查询）")
	private String versionFuzzy;

	// 更新描述
	@Schema(description = "更新描述")
	private String updateDesc;
	@Schema(description = "更新描述（模糊查询）")
	private String updateDescFuzzy;

	// 创建时间
	@Schema(description = "创建时间")
	private Date createTime;
	@Schema(description = "创建时间起始")
	private String createTimeStart;

	@Schema(description = "创建时间结束")
	private String createTimeEnd;

	// 0:未发布 1:灰度发布 2:全网发布
	@Schema(description = "0:未发布 1:灰度发布 2:全网发布")
	private Byte status;
	// 灰度id
	@Schema(description = "灰度ID")
	private String grayscaleUid;
	@Schema(description = "灰度ID（模糊查询）")
	private String grayscaleUidFuzzy;

	// 文件类型 0:本地文件 1:外链
	@Schema(description = "文件类型 0:本地文件 1:外链")
	private Byte fileType;
	// 外链地址
	@Schema(description = "外链地址")
	private String outerLink;
	@Schema(description = "外链地址（模糊查询）")
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