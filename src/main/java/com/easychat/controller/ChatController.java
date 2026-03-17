package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.MessageTypeEnum;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.service.ChatMessageService;
import com.easychat.service.ChatSessionUserService;
import com.easychat.utils.ArrayUtils;
import com.easychat.utils.StringTools;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

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

    @RequestMapping("/sendMessage")
    @GlobalInterceptor
    public ResponseVO sendMessage(HttpServletRequest request, @NotEmpty String contactId,
                                  @NotEmpty @Max(500) String messageContent,
                                  @NotNull Integer messageType,
                                  Long fileSize,
                                  String fileName,
                                  Integer fileType) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setContactId(contactId);
        chatMessage.setMessageType(messageType.byteValue());
        chatMessage.setMessageContent(messageContent);
        chatMessage.setFileSize(fileSize);
        chatMessage.setFileName(fileName);
        chatMessage.setFileType(fileType.byteValue());
        MessageSendDto messageSendDto = chatMessageService.saveMessage(chatMessage, tokenUserInfoDto);

        return getSuccessResponse(messageSendDto);
    }

    @RequestMapping("/uploadFile")
    @GlobalInterceptor
    public ResponseVO uploadFile(HttpServletRequest request,
                                 @NotNull Long messageId,
                                 @NotNull MultipartFile file,
                                 @NotNull MultipartFile cover) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        chatMessageService.saveMessageFile(tokenUserInfoDto.getUserId(), messageId, file, cover);
        return getSuccessResponse(null);
    }

    @RequestMapping("/downloadFile")
    @GlobalInterceptor
    public void downloadFile(HttpServletRequest request, HttpServletResponse response, @NotEmpty String fileId, @NotNull Boolean showCover) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);

        OutputStream out = null;
        FileInputStream in = null;
        try {
            File file = null;
            if (!StringTools.isNumber(fileId)) {
                String avatarFolderName = Constants.FILE_FOLDER_FILE + Constants.FILE_FOLDER_AVATAR;
                String avatarPath = appConfig.getProjectFolder() + avatarFolderName + Constants.IMAGE_SUFFIX;
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
