package com.easychat.entity.dto;

import com.easychat.utils.StringTools;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "消息发送信息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageSendDto<T> implements Serializable {
    private static final long serialVersionUID = -1045752033171142417L;
    // 消息ID
    @Schema(description = "消息ID")
    private Long messageId;
    // 会话ID
    @Schema(description = "会话ID")
    private String sessionId;
    // 发送人
    @Schema(description = "发送人ID")
    private String sendUserId;
    // 发送人昵称
    @Schema(description = "发送人昵称")
    private String sendUserNickName;
    // 联系人Id
    @Schema(description = "联系人ID")
    private String contactId;
    // 联系人名称
    @Schema(description = "联系人名称")
    private String contactName;
    // 消息内容
    @Schema(description = "消息内容")
    private String messageContent;
    // 最后的消息
    @Schema(description = "最后的消息")
    private String lastMessage;
    // 消息类型
    @Schema(description = "消息类型")
    private Integer messageType;
    // 发送时间
    @Schema(description = "发送时间")
    private Long sendTime;
    // 联系人类型
    @Schema(description = "联系人类型")
    private Integer contactType;
    // 扩展信息
    @Schema(description = "扩展信息")
    private T extendData;
    // 消息状态 0：发送中 1：已发送
    @Schema(description = "消息状态 0:发送中 1:已发送")
    private Integer status;
    // 文件信息
    @Schema(description = "文件大小")
    private Long fileSize;
    @Schema(description = "文件名")
    private String fileName;
    @Schema(description = "文件类型")
    private Integer fileType;
    // 群员数
    @Schema(description = "群员数")
    private Integer memberCount;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String senderId) {
        this.sendUserId = senderId;
    }

    public String getSendUserNickName() {
        return sendUserNickName;
    }

    public void setSendUserNickName(String senderNickName) {
        this.sendUserNickName = senderNickName;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getLastMessage() {
        if (StringTools.isEmpty(lastMessage)) {
            return messageContent;
        }
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getContactType() {
        return contactType;
    }

    public void setContactType(Integer contactType) {
        this.contactType = contactType;
    }

    public T getExtendData() {
        return extendData;
    }

    public void setExtendData(T extendData) {
        this.extendData = extendData;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }
}
