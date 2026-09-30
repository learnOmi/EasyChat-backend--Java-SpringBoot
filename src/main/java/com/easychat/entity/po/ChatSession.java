package com.easychat.entity.po;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
/**
 * 会话信息
 * @author 'Tong'
 * @since 2026/03/13
 */
@Schema(description = "会话信息")
public class ChatSession implements Serializable {
	// 会话ID
	@Schema(description = "会话ID")
	private String sessionId;
	// 最后接收的消息
	@Schema(description = "最后接收的消息")
	private String lastMessage;
	// 最后接收消息时间
	@Schema(description = "最后接收消息时间")
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

	@Override
	public String toString() {
		return "ChatSession [" +
			"sessionId=" + (sessionId == null ? "空" : sessionId) + ", " +
			"lastMessage=" + (lastMessage == null ? "空" : lastMessage) + ", " +
			"lastReceiveTime=" + (lastReceiveTime == null ? "空" : lastReceiveTime) + 
			"]";
	}
}