package com.easychat.entity.po;

import com.easychat.enums.UserContactTypeEnum;

import java.io.Serializable;
/**
 * 会话用户
 * @author 'Tong'
 * @since 2026/03/13
 */
public class ChatSessionUser implements Serializable {
    private static final long serialVersionUID = -882472298829182760L;

	// 用户ID
	private String userId;
	// 联系人ID
	private String contactId;
	// 会话ID
	private String sessionId;
	// 联系人名称
	private String contactName;

    private String lastMessage;

    private String lastReceiveTime;

    private Integer memberCount;

    private Integer contactType;

    public Integer getContactType() {
        return UserContactTypeEnum.getByPrefix(contactId).getType();
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

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

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactName() {
		return contactName;
	}

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastReceiveTime() {
        return lastReceiveTime;
    }

    public void setLastReceiveTime(String lastReceiveTime) {
        this.lastReceiveTime = lastReceiveTime;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

	@Override
	public String toString() {
		return "ChatSessionUser [" +
			"userId=" + (userId == null ? "空" : userId) + ", " +
			"contactId=" + (contactId == null ? "空" : contactId) + ", " +
			"sessionId=" + (sessionId == null ? "空" : sessionId) + ", " +
			"contactName=" + (contactName == null ? "空" : contactName) + 
			"]";
	}
}