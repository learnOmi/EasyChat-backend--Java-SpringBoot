package com.easychat.entity.dto;

import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.po.ChatSessionUser;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "WebSocket初始化数据")
public class WsInitData {
    @Schema(description = "会话用户列表")
    private List<ChatSessionUser> chatSessionList;
    @Schema(description = "聊天消息列表")
    private List<ChatMessage> chatMessageList;
    @Schema(description = "申请数量")
    private Integer applyCount;

    public List<ChatMessage> getChatMessageList() {
        return chatMessageList;
    }

    public void setChatMessageList(List<ChatMessage> chatMessageList) {
        this.chatMessageList = chatMessageList;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public List<ChatSessionUser> getChatSessionList() {
        return chatSessionList;
    }

    public void setChatSessionList(List<ChatSessionUser> chatSessionUserList) {
        this.chatSessionList = chatSessionUserList;
    }
}
