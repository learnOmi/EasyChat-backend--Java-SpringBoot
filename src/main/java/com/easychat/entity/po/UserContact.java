package com.easychat.entity.po;

import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
/**
 * 联系人
 * @author 'Tong'
 * @since 2025/10/17
 */
public class UserContact implements Serializable {
	// 用户ID
	private String userId;
	// 联系人ID或者群组ID
	private String contactId;
	// 联系人类型0:好友1:群组
	private Byte contactType;
	// 创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	// 状态0:非好友 1:好友 2:已删除好友 3:被好友删除 4: 已拉黑好友 5: 被好友拉黑
	@JsonIgnore
	private Byte status;
	// 最后更新时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date lastUpdateTime;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactType(Byte contactType) {
		this.contactType = contactType;
	}

	public Byte getContactType() {
		return contactType;
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

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	@Override
	public String toString() {
		return "UserContact [" +
			"userId=" + (userId == null ? "空" : userId) + ", " +
			"contactId=" + (contactId == null ? "空" : contactId) + ", " +
			"contactType=" + (contactType == null ? "空" : contactType) + ", " +
			"createTime=" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ", " +
			"status=" + (status == null ? "空" : status) + ", " +
			"lastUpdateTime=" + (lastUpdateTime == null ? "空" : DateUtils.format(lastUpdateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + 
			"]";
	}
}