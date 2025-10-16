package com.easychat.entity.query;

import java.util.Date;
/**
 * 联系人查询
 * @author 'Tong'
 * @since 2025/10/17
 */
public class UserContactQuery extends BaseQuery {
	// 用户ID
	private String userId;
	private String userIdFuzzy;

	// 联系人ID或者群组ID
	private String contactId;
	private String contactIdFuzzy;

	// 联系人类型0:好友1:群组
	private Byte contactType;
	// 创建时间
	private Date createTime;
	private String createTimeStart;

	private String createTimeEnd;

	// 状态0:非好友 1:好友 2:已删除好友 3:被好友删除 4: 已拉黑好友 5: 被好友拉黑
	private Byte status;
	// 最后更新时间
	private Date lastUpdateTime;
	private String lastUpdateTimeStart;

	private String lastUpdateTimeEnd;

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

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy() {
		return userIdFuzzy;
	}

	public void setContactIdFuzzy(String contactIdFuzzy) {
		this.contactIdFuzzy = contactIdFuzzy;
	}

	public String getContactIdFuzzy() {
		return contactIdFuzzy;
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

	public void setLastUpdateTimeStart(String lastUpdateTimeStart) {
		this.lastUpdateTimeStart = lastUpdateTimeStart;
	}

	public String getLastUpdateTimeStart() {
		return lastUpdateTimeStart;
	}

	public void setLastUpdateTimeEnd(String lastUpdateTimeEnd) {
		this.lastUpdateTimeEnd = lastUpdateTimeEnd;
	}

	public String getLastUpdateTimeEnd() {
		return lastUpdateTimeEnd;
	}

}