package com.easychat.entity.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

/**
 * WebSocket 上行消息载体
 * 用于承载客户端主动通过 WebSocket 发送给服务端的消息（如消息送达 ACK）
 */
@Schema(description = "WebSocket上行消息")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WsUpstreamDto implements Serializable {
    private static final long serialVersionUID = 8789045671234567890L;
    // 上行消息类型，取值见 MessageTypeEnum（如 14:消息送达确认）
    @Schema(description = "上行消息类型，取值见 MessageTypeEnum（如 14:消息送达确认）")
    private Integer messageType;
    // 消息ID
    @Schema(description = "消息ID")
    private Long messageId;

    public Integer getMessageType() {
        return messageType;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
}
