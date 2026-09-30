package com.easychat.entity.po;

import com.easychat.enums.UserContactTypeEnum;
import com.easychat.utils.StringTools;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
/**
 * 会话用户
 * @author 'Tong'
 * @since 2026/03/13
 */
@Schema(description = "会话用户")
public class ChatSessionUser implements Serializable {
    private static final long serialVersionUID = -882472298829182760L;

	// 用户ID
	@Schema(description = "用户ID")
	private String userId;
	// 联系人ID
	@Schema(description = "联系人ID")
	private String contactId;
	// 会话ID
	@Schema(description = "会话ID")
	private String sessionId;
	// 联系人名称
	@Schema(description = "联系人名称")
	private String contactName;

    @Schema(description = "最后的消息")
    private String lastMessage;

    @Schema(description = "最后接收消息时间")
    private Long lastReceiveTime;

    @Schema(description = "成员数")
    private Integer memberCount;

    @Schema(description = "联系人类型")
    private Integer contactType;

    public Integer getContactType() {
        if (StringTools.isEmpty(contactId)) {
            return null;
        }
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

    public Long getLastReceiveTime() {
        return lastReceiveTime;
    }

    public void setLastReceiveTime(Long lastReceiveTime) {
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