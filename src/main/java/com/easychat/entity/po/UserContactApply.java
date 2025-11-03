package com.easychat.entity.po;

import com.easychat.enums.UserContactStatusEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
/**
 * 联系人申请
 * @author 'Tong'
 * @since 2025/10/17
 */
public class UserContactApply implements Serializable {
	// 自增ID
	private Integer applyId;
	// 申请人id
	private String applyUserId;
	// 接收人ID
	private String receiveUserId;
	// 联系人类型0:好友1:群组
	private Byte contactType;
	// 联系人群组ID
	private String contactId;
	// 最后申请时间
	private Long lastApplyTime;
	// 状态0:待处理1:已同意2:已拒绝3:已拉黑
	@JsonIgnore
	private Byte status;
	// 申请信息
	private String applyInfo;

    private String contactName;

    private String statusName;

	public void setApplyId(Integer applyId) {
		this.applyId = applyId;
	}

	public Integer getApplyId() {
		return applyId;
	}

	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}

	public String getApplyUserId() {
		return applyUserId;
	}

	public void setReceiveUserId(String receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public String getReceiveUserId() {
		return receiveUserId;
	}

	public void setContactType(Byte contactType) {
		this.contactType = contactType;
	}

	public Byte getContactType() {
		return contactType;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setLastApplyTime(Long lastApplyTime) {
		this.lastApplyTime = lastApplyTime;
	}

	public Long getLastApplyTime() {
		return lastApplyTime;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	public void setApplyInfo(String applyInfo) {
		this.applyInfo = applyInfo;
	}

	public String getApplyInfo() {
		return applyInfo;
	}

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getStatusName() {
        UserContactStatusEnum statusEnum = UserContactStatusEnum.getByStatus(Integer.valueOf(status));
        return statusEnum == null ? null : statusEnum.getDesc();
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "UserContactApply [" +
                "applyId=" + (applyId == null ? "空" : applyId) + ", " +
                "applyUserId=" + (applyUserId == null ? "空" : applyUserId) + ", " +
                "receiveUserId=" + (receiveUserId == null ? "空" : receiveUserId) + ", " +
                "contactType=" + (contactType == null ? "空" : contactType) + ", " +
                "contactId=" + (contactId == null ? "空" : contactId) + ", " +
                "lastApplyTime=" + (lastApplyTime == null ? "空" : lastApplyTime) + ", " +
                "status=" + (status == null ? "空" : status) + ", " +
                "applyInfo=" + (applyInfo == null ? "空" : applyInfo) +
                "]";
    }
}