package com.easychat.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * 「我发出的消息」状态快照
 * 用于发送方重连时回补送达状态：发送方离线期间消息被接收方 ACK 后，
 * 服务端只更新了库里的 status，推送不到发送方，只能在其重连时随 INIT 一起带回。
 */
@Schema(description = "我发出的消息状态快照")
public class SentMessageStatusDto implements Serializable {
    private static final long serialVersionUID = 6120338451267104512L;
    // 消息ID
    @Schema(description = "消息ID")
    private Long messageId;
    // 会话ID（便于前端定位，本地更新时按 messageId 即可）
    @Schema(description = "会话ID")
    private String sessionId;
    // 消息状态，取值见 MessageStatusEnum（2:已送达）
    @Schema(description = "消息状态，取值见 MessageStatusEnum（2:已送达）")
    private Integer status;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
