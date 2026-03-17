package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.po.ChatSession;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.ChatMessageQuery;
import com.easychat.entity.query.ChatSessionQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.ChatMessageMapper;
import com.easychat.mapper.ChatSessionMapper;
import com.easychat.mapper.UserContactMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatMessageService;
import com.easychat.utils.ArrayUtils;
import com.easychat.utils.CopyTools;
import com.easychat.utils.DateUtils;
import com.easychat.utils.StringTools;
import com.easychat.websocket.MessageHandler;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * 聊天消息表ServiceImpl
 * @author 'Tong'
 * @since 2026/03/13
 */
@Service("chatMessageService")
public class ChatMessageServiceImpl implements ChatMessageService {
    private static final Logger logger = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

	@Resource
	private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;
    @Resource
    private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private AppConfig appConfig;
    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;

	// 根据条件查询列表
	public List<ChatMessage> findListByParam(ChatMessageQuery query) {
		return this.chatMessageMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(ChatMessageQuery query) {
		return this.chatMessageMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<ChatMessage> findPageByParam(ChatMessageQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<ChatMessage> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(ChatMessage bean) {
		return this.chatMessageMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<ChatMessage> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.chatMessageMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<ChatMessage> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.chatMessageMapper.insertOrUpdateBatch(listBean);
	}

	// 多条件更新
	public Integer updateByParam(ChatMessage bean, ChatMessageQuery query) {
		return this.chatMessageMapper.updateByParam(bean, query);
	}

	// 多条件删除
	public Integer deleteByParam(ChatMessageQuery query) {
		return this.chatMessageMapper.deleteByParam(query);
	}

	// 根据MessageId查询
	public ChatMessage getChatMessageByMessageId(Long messageId) {
		return this.chatMessageMapper.selectByMessageId(messageId);
	}

	// 根据MessageId更新
	public Integer updateChatMessageByMessageId(ChatMessage bean, Long messageId) {
		return this.chatMessageMapper.updateByMessageId(bean, messageId);
	}

	// 根据MessageId删除
	public Integer deleteChatMessageByMessageId(Long messageId) {
		return this.chatMessageMapper.deleteByMessageId(messageId);
	}

    @Override
    public MessageSendDto saveMessage(ChatMessage chatMessage, TokenUserInfoDto tokenUserInfoDto) {
        if (!Constants.ROBOT_UID.equals(tokenUserInfoDto.getUserId())) {
            List<String> contactList = redisComponent.getUserContactList(tokenUserInfoDto.getUserId());
            if (!contactList.contains(chatMessage.getContactId())) {
                UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(chatMessage.getContactId());
                if (userContactTypeEnum == UserContactTypeEnum.USER) {
                    throw new BusinessException(ResponseCodeEnum.CODE_902);
                } else {
                    throw new BusinessException(ResponseCodeEnum.CODE_903);
                }
            }
        }

        String sessionId = null;
        String sendUserId = tokenUserInfoDto.getUserId();
        String contactId = chatMessage.getContactId();
        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (UserContactTypeEnum.USER == contactTypeEnum) {
            sessionId = StringTools.getChatSessionId4User(new String[]{sendUserId, contactId});
        } else {
            sessionId = StringTools.getChatSessionId4Group(contactId);
        }
        chatMessage.setSessionId(sessionId);

        Long curTime = System.currentTimeMillis();
        chatMessage.setSendTime(curTime);

        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(chatMessage.getMessageType().intValue());
        if (null == messageTypeEnum || !ArrayUtils.contains(new Integer[]{MessageTypeEnum.CHAT.getType(), MessageTypeEnum.MEDIA_CHAT.getType()}, chatMessage.getMessageType())) {
            throw  new BusinessException(ResponseCodeEnum.CODE_600);
        }
        Integer status = MessageTypeEnum.MEDIA_CHAT == messageTypeEnum ? MessageStatusEnum.SENDING.getStatus() : MessageStatusEnum.SENDED.getStatus();
        chatMessage.setStatus(status.byteValue());

        String messageContent = StringTools.cleanHtmlTag(chatMessage.getMessageContent());
        chatMessage.setMessageContent(messageContent);

        ChatSession chatSession = new ChatSession();
        chatSession.setLastMessage(messageContent);
        if (UserContactTypeEnum.GROUP == contactTypeEnum) {
            chatSession.setLastMessage(tokenUserInfoDto.getNickName() + ": " + messageContent);
        }
        chatSession.setLastReceiveTime(curTime);
        chatSessionMapper.updateBySessionId(chatSession, sessionId);

        chatMessage.setSendUserId(sendUserId);
        chatMessage.setSendUserNickName(tokenUserInfoDto.getNickName());
        chatMessage.setContactType(contactTypeEnum.getType().byteValue());
        chatMessageMapper.insert(chatMessage);
        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);

        if (Constants.ROBOT_UID.equals(contactId)) {
            SysSettingDto sysSettingDto = redisComponent.getSysSetting();
            TokenUserInfoDto robot = new TokenUserInfoDto();
            robot.setUserId(sysSettingDto.getRobotUid());
            robot.setNickName(sysSettingDto.getRobotNickName());
            ChatMessage robotChatMessage = new ChatMessage();
            robotChatMessage.setContactId(sendUserId);
            robotChatMessage.setMessageContent("你好，我是机器人，您可以在未來接入大模型~");
            robotChatMessage.setMessageType(MessageTypeEnum.CHAT.getType().byteValue());
            saveMessage(robotChatMessage, robot);
        } else {
            messageHandler.sendMessage(messageSendDto);
        }

        return messageSendDto;
    }

    @Override
    /**
     * 保存消息文件
     * @param userId 用户ID
     * @param messageId 消息ID
     * @param file 上传的文件
     * @param cover 封面图片（可选）
     */
    public void saveMessageFile(String userId, Long messageId, MultipartFile file, MultipartFile cover) {
        // 根据消息ID查询聊天消息
        ChatMessage chatMessage = chatMessageMapper.selectByMessageId(messageId);
        if (chatMessage == null) {
            // 如果消息不存在，抛出业务异常
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 验证用户是否是消息发送者
        if (!chatMessage.getSendUserId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 获取系统设置
        SysSettingDto sysSettingDto = redisComponent.getSysSetting();
        // 获取文件后缀
        String fileSuffix = StringTools.getFileSuffix(file.getOriginalFilename());
        // 检查图片文件大小限制
        if (!StringTools.isEmpty(fileSuffix)
                && ArrayUtils.contains(new String[]{Constants.IMAGE_SUFFIX}, fileSuffix.toLowerCase())
                && file.getSize() > sysSettingDto.getMaxImageSize() * Constants.FILE_SIZE_MB) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        } else if (!StringTools.isEmpty(fileSuffix)
                && ArrayUtils.contains(Constants.VIDEO_SUFFIX_LIST, fileSuffix.toLowerCase())
                && file.getSize() > sysSettingDto.getMaxVideoSize() * Constants.FILE_SIZE_MB) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        } else if (!StringTools.isEmpty(fileSuffix)
                && !ArrayUtils.contains(Constants.IMAGE_SUFFIX_LIST, fileSuffix.toLowerCase())
                && !ArrayUtils.contains(Constants.VIDEO_SUFFIX_LIST, fileSuffix.toLowerCase())
                && file.getSize() > sysSettingDto.getMaxFileSize() * Constants.FILE_SIZE_MB) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        String fileName = file.getOriginalFilename(); // 获取原始文件名
        String fileExtName = StringTools.getFileSuffix(fileName); // 获取文件扩展名
        String fileRealName = messageId + fileExtName; // 生成新的文件名（使用消息ID作为文件名）
        String month = DateUtils.format(new Date(chatMessage.getSendTime()), DateTimePatternEnum.YYYYMM.getPattern()); // 根据消息发送时间获取月份
        File folder = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + month); // 创建文件存储目录
        if (!folder.exists()) {
            folder.mkdirs(); // 如果目录不存在则创建
        }
        File uplodaFile = new File(folder.getPath() + "/" + fileRealName); // 创建目标文件
        try {
            file.transferTo(uplodaFile); // 将文件保存到目标位置
            if (cover != null) cover.transferTo(new File(uplodaFile.getPath() + Constants.COVER_IMAGE_SUFFIX)); // 如果有封面图片，则保存封面
        } catch (IOException e) {
            logger.error("文件上传失败", e); // 记录错误日志
            throw new BusinessException("文件上传失败"); // 抛出业务异常
        }

        // 更新消息状态为已发送
        ChatMessage uploadInfo = new ChatMessage();
        uploadInfo.setStatus(MessageStatusEnum.SENDED.getStatus().byteValue());
        ChatMessageQuery messageQuery = new ChatMessageQuery();
        messageQuery.setMessageId(messageId);
        messageQuery.setStatus(MessageStatusEnum.SENDING.getStatus().byteValue());
        chatMessageMapper.updateByMessageId(uploadInfo, messageId);

        // 构建消息发送DTO并发送消息
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setStatus(MessageStatusEnum.SENDED.getStatus());
        messageSendDto.setFileName(fileName);
        messageSendDto.setFileType(MessageTypeEnum.FILE_UPLOAD.getType());
        messageSendDto.setContactId(chatMessage.getContactId());
        messageHandler.sendMessage(messageSendDto);
    }

    @Override
    /**
     * 下载文件方法
     * @param tokenUserInfoDto 用户信息对象，包含用户ID等认证信息
     * @param messageId 消息ID，用于定位要下载的文件
     * @param showCover 是否显示封面，如果为true则添加封面后缀
     * @return File 返回要下载的文件对象
     * @throws BusinessException 当用户无权限访问文件或文件不存在时抛出业务异常
     */
    public File downloadFile(TokenUserInfoDto tokenUserInfoDto, Long messageId, Boolean showCover) {
        // 根据消息ID查询消息记录
        ChatMessage message = chatMessageMapper.selectByMessageId(messageId);
        // 获取联系人ID
        String contactId = message.getContactId();
        // 根据联系人ID前缀获取联系人类型
        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(contactId);
        // 如果是私聊消息，检查用户是否有权限（只有发送者可以下载）
        if (UserContactTypeEnum.USER == contactTypeEnum && !tokenUserInfoDto.getUserId().equals(message.getSendUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        // 如果是群聊消息，检查用户是否是群成员
        if (UserContactTypeEnum.GROUP == contactTypeEnum) {
            UserContactQuery userContactQuery = new UserContactQuery();
            // 设置查询条件：用户ID、联系人类型（群）、联系人ID（群ID）、状态（好友）
            userContactQuery.setUserId(tokenUserInfoDto.getUserId());
            userContactQuery.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());
            userContactQuery.setContactId(contactId);
            userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
            // 查询用户联系人记录数
            Integer contactCount = userContactMapper.selectCount(userContactQuery);
            // 如果记录数为0，说明用户不是群成员，无权限下载
            if (contactCount == 0) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        // 根据消息发送时间获取月份，用于创建文件夹
        String month = DateUtils.format(new Date(message.getSendTime()), DateTimePatternEnum.YYYYMM.getPattern());
        // 创建文件存储文件夹路径
        File folder = new File(appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE + month);
        // 如果文件夹不存在则创建
        if (!folder.exists()) {
            folder.mkdirs();
        }
        // 获取原始文件名和扩展名
        String fileName = message.getFileName();
        String fileExtName = StringTools.getFileSuffix(fileName);
        // 生成新的文件名（使用消息ID作为文件名）
        String fileRealName = messageId + fileExtName;
        // 如果需要显示封面，添加封面后缀
        if (showCover != null && showCover) {
            fileRealName = fileRealName + Constants.COVER_IMAGE_SUFFIX;
        }
        // 创建完整的文件路径
        File file = new File(folder.getPath() + "/" + fileRealName);
        // 如果文件不存在，记录日志并抛出异常
        if (!file.exists()) {
            logger.info("文件不存在", messageId);
            throw new BusinessException(ResponseCodeEnum.CODE_602);
        }

        // 返回文件对象
        return file;
    }
}