package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.ChatMessageService;
import com.easychat.service.ChatSessionUserService;
import com.easychat.utils.StringTools;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

@Tag(name = "聊天消息模块", description = "发送消息、上传/下载聊天文件")
@RestController
@RequestMapping("/chat")
public class ChatController extends ABaseController {
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    @Resource
    private ChatMessageService chatMessageService;
    @Resource
    private ChatSessionUserService chatSessionUserService;
    @Resource
    private AppConfig appConfig;

    @Operation(summary = "发送消息", description = "需登录；data 为 MessageSendDto（会话、消息内容、联系人信息等）")
    @RequestMapping("/sendMessage")
    @GlobalInterceptor
    public ResponseVO sendMessage(HttpServletRequest request,
                                  @Parameter(description = "接收方ID（用户ID或群ID）") @NotEmpty String contactId,
                                  @Parameter(description = "消息内容，最长 500 字") @NotEmpty @Size(max = 500) String messageContent,
                                  @Parameter(description = "消息类型") @NotNull Integer messageType,
                                  @Parameter(description = "文件大小（文件/媒体消息）") Long fileSize,
                                  @Parameter(description = "文件名（文件/媒体消息）") String fileName,
                                  @Parameter(description = "文件类型（文件/媒体消息）") Integer fileType,
                                  @Parameter(description = "客户端幂等ID（UUID，用于消息去重；可不传，兼容老客户端）") @Size(max = 64) String clientMessageId) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContactId(contactId);
        chatMessage.setMessageType(messageType.byteValue());
        chatMessage.setMessageContent(messageContent);
        chatMessage.setFileSize(fileSize);
        chatMessage.setFileName(fileName);
        chatMessage.setFileType(fileType != null ? fileType.byteValue() : null);
        // 空串归一化为 null：MySQL 唯一索引允许多个 NULL，但把空串视为有效值会导致多条空串互相冲突
        chatMessage.setClientMessageId(StringTools.isEmpty(clientMessageId) ? null : clientMessageId);
        MessageSendDto messageSendDto = chatMessageService.saveMessage(chatMessage, tokenUserInfoDto);

        return getSuccessResponse(messageSendDto);
    }

    @Operation(summary = "上传消息文件", description = "需登录；上传文件及其封面并绑定到消息；data 为 null")
    @RequestMapping("/uploadFile")
    @GlobalInterceptor
    public ResponseVO uploadFile(HttpServletRequest request,
                                 @Parameter(description = "消息ID") @NotNull Long messageId,
                                 @Parameter(description = "文件") @NotNull MultipartFile file,
                                 @Parameter(description = "文件封面") @NotNull MultipartFile cover) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        chatMessageService.saveMessageFile(tokenUserInfoDto.getUserId(), messageId, file, cover);
        return getSuccessResponse(null);
    }

    @Operation(summary = "下载文件/头像", description = "需登录；直接以附件流写出（无统一响应体）；fileId 为数字时按消息文件下载，否则按用户头像文件名下载")
    @RequestMapping("/downloadFile")
    @GlobalInterceptor
    public void downloadFile(HttpServletRequest request, HttpServletResponse response,
                             @Parameter(description = "文件ID（消息文件ID）或头像文件名") @NotEmpty String fileId,
                             @Parameter(description = "是否下载封面") @NotNull Boolean showCover) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = null;
            if (!StringTools.isNumber(fileId)) {
                String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR;
                String avatarPath = appConfig.getProjectFolder() + avatarFolderName + fileId + Constants.IMAGE_SUFFIX;
                if (showCover) {
                    avatarPath = avatarPath + Constants.COVER_IMAGE_SUFFIX;
                }
                file = new File(avatarPath);
                if (!file.exists()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
            } else {
               file = chatMessageService.downloadFile(tokenUserInfoDto, Long.valueOf(fileId), showCover);
            }

            response.setContentType("application/x-msdownload;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;");
            response.setContentLengthLong(file.length());
            in = new FileInputStream(file);
            byte[] byteData = new byte[1024];
            out = response.getOutputStream();
            int len;
            while ((len = in.read(byteData)) != -1) {
                out.write(byteData, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            logger.error("下载文件失败", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    logger.error("关闭流失败", e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    logger.error("关闭流失败", e);
                }
            }
        }
    }
}
