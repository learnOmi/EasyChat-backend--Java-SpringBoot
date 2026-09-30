package com.easychat.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 会话用户查询
 * @author 'Tong'
 * @since 2026/03/13
 */
@Schema(description = "会话用户查询")
public class ChatSessionUserQuery extends BaseQuery {
	// 用户ID
	@Schema(description = "用户ID")
	private String userId;
	@Schema(description = "用户ID（模糊查询）")
	private String userIdFuzzy;

	// 联系人ID
	@Schema(description = "联系人ID")
	private String contactId;
	@Schema(description = "联系人ID（模糊查询）")
	private String contactIdFuzzy;

	// 会话ID
	@Schema(description = "会话ID")
	private String sessionId;
	@Schema(description = "会话ID（模糊查询）")
	private String sessionIdFuzzy;

	// 联系人名称
	@Schema(description = "联系人名称")
	private String contactName;
	@Schema(description = "联系人名称（模糊查询）")
	private String contactNameFuzzy;

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

	public void setSessionIdFuzzy(String sessionIdFuzzy) {
		this.sessionIdFuzzy = sessionIdFuzzy;
	}

	public String getSessionIdFuzzy() {
		return sessionIdFuzzy;
	}

	public void setContactNameFuzzy(String contactNameFuzzy) {
		this.contactNameFuzzy = contactNameFuzzy;
	}

	public String getContactNameFuzzy() {
		return contactNameFuzzy;
	}

}