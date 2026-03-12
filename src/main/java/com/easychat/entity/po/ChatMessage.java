package com.easychat.entity.po;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
/**
 * 聊天消息表
 * @author 'Tong'
 * @since 2026/03/13
 */
public class ChatMessage implements Serializable {
	// 消息自增ID
	private Long messageId;
	// 会话ID
	private String sessionId;
	// 消息类型
	private Byte messageType;
	// 消息内容
	private String messageContent;
	// 发送人工D
	private String sendUserId;
	// 发送人昵称
	private String sendUserNickName;
	// 发送时间
	private Long sendTime;
	// 接收联系人ID
	private String contactId;
	// 联系人类型0:单聊1:群聊
	private Byte contactType;
	// 文件大小
	private Long fileSize;
	// 文件名
	private String fileName;
	// 文件类型
	private Byte fileType;
	// 状态0:正在发送1:已发送
	@JsonIgnore
	private Byte status;

	public void setMessageId(Long messageId) {
		this.messageId = messageId;
	}

	public Long getMessageId() {
		return messageId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setMessageType(Byte messageType) {
		this.messageType = messageType;
	}

	public Byte getMessageType() {
		return messageType;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserNickName(String sendUserNickName) {
		this.sendUserNickName = sendUserNickName;
	}

	public String getSendUserNickName() {
		return sendUserNickName;
	}

	public void setSendTime(Long sendTime) {
		this.sendTime = sendTime;
	}

	public Long getSendTime() {
		return sendTime;
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

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileType(Byte fileType) {
		this.fileType = fileType;
	}

	public Byte getFileType() {
		return fileType;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "ChatMessage [" +
			"messageId=" + (messageId == null ? "空" : messageId) + ", " +
			"sessionId=" + (sessionId == null ? "空" : sessionId) + ", " +
			"messageType=" + (messageType == null ? "空" : messageType) + ", " +
			"messageContent=" + (messageContent == null ? "空" : messageContent) + ", " +
			"sendUserId=" + (sendUserId == null ? "空" : sendUserId) + ", " +
			"sendUserNickName=" + (sendUserNickName == null ? "空" : sendUserNickName) + ", " +
			"sendTime=" + (sendTime == null ? "空" : sendTime) + ", " +
			"contactId=" + (contactId == null ? "空" : contactId) + ", " +
			"contactType=" + (contactType == null ? "空" : contactType) + ", " +
			"fileSize=" + (fileSize == null ? "空" : fileSize) + ", " +
			"fileName=" + (fileName == null ? "空" : fileName) + ", " +
			"fileType=" + (fileType == null ? "空" : fileType) + ", " +
			"status=" + (status == null ? "空" : status) + 
			"]";
	}
}