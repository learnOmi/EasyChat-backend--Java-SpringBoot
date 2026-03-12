package com.easychat.entity.query;

/**
 * 会话信息查询
 * @author 'Tong'
 * @since 2026/03/13
 */
public class ChatSessionQuery extends BaseQuery {
	// 会话ID
	private String sessionId;
	private String sessionIdFuzzy;

	// 最后接收的消息
	private String lastMessage;
	private String lastMessageFuzzy;

	// 最后接收消息时间
	private Long lastReceiveTime;
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public void setLastReceiveTime(Long lastReceiveTime) {
		this.lastReceiveTime = lastReceiveTime;
	}

	public Long getLastReceiveTime() {
		return lastReceiveTime;
	}

	public void setSessionIdFuzzy(String sessionIdFuzzy) {
		this.sessionIdFuzzy = sessionIdFuzzy;
	}

	public String getSessionIdFuzzy() {
		return sessionIdFuzzy;
	}

	public void setLastMessageFuzzy(String lastMessageFuzzy) {
		this.lastMessageFuzzy = lastMessageFuzzy;
	}

	public String getLastMessageFuzzy() {
		return lastMessageFuzzy;
	}

}