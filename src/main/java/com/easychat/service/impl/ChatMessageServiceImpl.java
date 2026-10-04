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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
    /**
     * 自注入代理，用于让 saveMessageInNewTx 的 REQUIRES_NEW 事务注解生效。
     * <p>自调用（this.xxx）会绕过 Spring 代理导致事务静默失效，因此必须通过 self 调用。</p>
     */
    @Autowired
    @Lazy
    private ChatMessageServiceImpl self;

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
        // 幂等预检：clientMessageId 非空时先按唯一索引查询，命中则直接返回既有消息（不落库、不推送、不更新会话）
        String clientMessageId = chatMessage.getClientMessageId();
        if (!StringTools.isEmpty(clientMessageId)) {
            ChatMessage existedMessage = chatMessageMapper.selectByClientMessageId(clientMessageId);
            if (existedMessage != null) {
                logger.info("消息幂等命中，返回既有消息: clientMessageId={}, messageId={}", clientMessageId, existedMessage.getMessageId());
                return CopyTools.copy(existedMessage, MessageSendDto.class);
            }
        }

        if (!Constants.ROBOT_UID.equals(tokenUserInfoDto.getUserId())) {
            List<String> contactList = redisComponent.getUserContactList(tokenUserInfoDto.getUserId());
            if (!contactList.contains(chatMessage.getContactId())) {
                UserContactTypeEnum userContactTypeEnum = UserContactTypeEnum.getByPrefix(chatMessage.getContactId());
                if (userContactTypeEnum == UserContactTypeEnum.USER) {
                    throw new BusinessException(ResponseCodeEnum.CODE_902);
                } else {
                    throw new BusinessException(ResponseCodeEnum.CODE_903);
                }
            } else {
                UserContact userContact = userContactMapper.selectByUserIdAndContactId(tokenUserInfoDto.getUserId(), chatMessage.getContactId());
                if (userContact == null || UserContactStatusEnum.FRIEND.getStatus().byteValue() != userContact.getStatus()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_902);
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
        if (null == messageTypeEnum || !ArrayUtils.contains(new Integer[]{MessageTypeEnum.CHAT.getType(), MessageTypeEnum.MEDIA_CHAT.getType()}, chatMessage.getMessageType().intValue())) {
            throw  new BusinessException(ResponseCodeEnum.CODE_600);
        }
        Integer status = MessageTypeEnum.MEDIA_CHAT == messageTypeEnum ? MessageStatusEnum.SENDING.getStatus() : MessageStatusEnum.SENDED.getStatus();
        chatMessage.setStatus(status.byteValue());

        String messageContent = StringTools.cleanHtmlTag(chatMessage.getMessageContent());
        chatMessage.setMessageContent(messageContent);

        chatMessage.setSendUserId(sendUserId);
        chatMessage.setSendUserNickName(tokenUserInfoDto.getNickName());
        chatMessage.setContactType(contactTypeEnum.getType().byteValue());


        // 落库：insert + 更新会话摘要，放在独立事务中，保证幂等回查在独立事务中执行，避免外层事务被标记回滚导致回查读不到已提交记录；
        // 群聊摘要需带发送人昵称前缀（保持原有逻辑）
        String lastMessage = UserContactTypeEnum.GROUP == contactTypeEnum
                ? tokenUserInfoDto.getNickName() + ": " + messageContent : messageContent;
        try {
            // 必须通过 self 代理调用，否则 REQUIRES_NEW 事务静默失效
            self.saveMessageInNewTx(chatMessage, sessionId, lastMessage);
        } catch (DuplicateKeyException e) {
            // 并发下唯一索引 uk_client_msg_id 兜底：在新事务中回查已提交的既有记录并返回（不推送）
            if (!StringTools.isEmpty(clientMessageId)) {
                ChatMessage existedMessage = chatMessageMapper.selectByClientMessageId(clientMessageId);
                if (existedMessage != null) {
                    logger.info("消息并发幂等命中，返回既有消息: clientMessageId={}, messageId={}", clientMessageId, existedMessage.getMessageId());
                    return CopyTools.copy(existedMessage, MessageSendDto.class);
                }
            }
            // 回查仍为空说明并非本幂等键冲突，原样抛出，避免吞异常
            throw e;
        }

        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);

        // 推送放在事务提交之后
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

    /**
     * 在独立事务中落库消息并更新会话摘要。
     * <p>注意：必须由 {@link #self} 代理调用（不能用 this.xxx，否则事务静默失效），
     * 且方法必须保持 public，以适配 Spring 的 CGLIB 子类代理。</p>
     * @param chatMessage 已填充完毕、待落库的消息
     * @param sessionId 会话ID
     * @param lastMessage 会话摘要（群聊含发送人昵称前缀）
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveMessageInNewTx(ChatMessage chatMessage, String sessionId, String lastMessage) {
        chatMessageMapper.insert(chatMessage);
        ChatSession chatSession = new ChatSession();
        chatSession.setLastMessage(lastMessage);
        chatSession.setLastReceiveTime(chatMessage.getSendTime());
        chatSessionMapper.updateBySessionId(chatSession, sessionId);
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
        // 幂等短路：状态已是「已发送」说明文件早已上传完成，重复上传直接返回，省去磁盘覆盖写
        if (chatMessage.getStatus() != null && MessageStatusEnum.SENDED.getStatus().byteValue() == chatMessage.getStatus()) {
            logger.info("消息文件已上传完成，跳过重复上传: messageId={}", messageId);
            return;
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

        // 条件更新闸门：仅当状态仍为「发送中」时才推进为「已发送」，按影响行数判重
        ChatMessage uploadInfo = new ChatMessage();
        uploadInfo.setStatus(MessageStatusEnum.SENDED.getStatus().byteValue());
        Integer affectedRows = chatMessageMapper.updateStatusByMessageIdAndStatus(uploadInfo, messageId, MessageStatusEnum.SENDING.getStatus().byteValue());
        if (affectedRows == null || affectedRows == 0) {
            // 已被并发重试抢先推进，说明文件已完成，跳过重复推送
            logger.info("消息文件重复上传，状态已推进，跳过推送: messageId={}", messageId);
            return;
        }

        // 构建消息发送DTO并发送消息（补全 messageId/sessionId/clientMessageId，便于前端按 messageId 覆盖本地乐观消息）
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageId(messageId);
        messageSendDto.setSessionId(chatMessage.getSessionId());
        messageSendDto.setClientMessageId(chatMessage.getClientMessageId());
        messageSendDto.setStatus(MessageStatusEnum.SENDED.getStatus());
        messageSendDto.setFileName(fileName);
        // fileType 的取值域是 0=图片/1=视频/2=其它文件，须取消息自身落库的文件类型，
        // 不能误用 MessageTypeEnum（那是消息类型的取值域，FILE_UPLOAD=6）
        messageSendDto.setFileType(chatMessage.getFileType() == null ? null : chatMessage.getFileType().intValue());
        messageSendDto.setContactId(chatMessage.getContactId());
        messageSendDto.setMessageType(MessageTypeEnum.FILE_UPLOAD.getType());
        messageHandler.sendMessage(messageSendDto);
    }

    @Override
    public void ackMessage(String userId, Long messageId) {
        // 参数校验：缺一不可
        if (StringTools.isEmpty(userId) || messageId == null) {
            logger.info("ACK参数不合法，忽略: userId={}, messageId={}", userId, messageId);
            return;
        }
        // 消息必须存在
        ChatMessage message = chatMessageMapper.selectByMessageId(messageId);
        if (message == null) {
            logger.info("ACK对应的消息不存在，忽略: userId={}, messageId={}", userId, messageId);
            return;
        }
        // 防伪造校验：仅允许「单聊」中「接收方本人」对发给自己的消息回 ACK；群聊本次直接忽略
        if (message.getContactType() == null
                || message.getContactType() != UserContactTypeEnum.USER.getType().byteValue()
                || !userId.equals(message.getContactId())) {
            logger.info("ACK校验不通过，忽略: userId={}, messageId={}, contactType={}, contactId={}",
                    userId, messageId, message.getContactType(), message.getContactId());
            return;
        }
        // 幂等闸门：仅当状态仍为「已发送」时才推进为「已送达」，影响行数=0 表示已 ACK 过（重复/延迟 ACK）
        ChatMessage updateBean = new ChatMessage();
        updateBean.setStatus(MessageStatusEnum.DELIVERED.getStatus().byteValue());
        Integer affectedRows = chatMessageMapper.updateStatusByMessageIdAndStatus(
                updateBean, messageId, MessageStatusEnum.SENDED.getStatus().byteValue());
        if (affectedRows == null || affectedRows == 0) {
            logger.info("消息已为送达状态（重复或延迟ACK），跳过推送: messageId={}", messageId);
            return;
        }
        // 向发送方推送状态变更通知。注意路由与展示字段的区别：
        // contactId 决定推送目标（发送方），sendUserId 为回 ACK 的接收方；
        // sendMsg 内部会把 contactId 覆盖为 sendUserId 再下发，因此前端拿到的 contactId 即接收方（展示用）。
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.MESSAGE_STATUS_CHANGE.getType());
        messageSendDto.setMessageId(messageId);
        messageSendDto.setSessionId(message.getSessionId());
        messageSendDto.setStatus(MessageStatusEnum.DELIVERED.getStatus());
        messageSendDto.setSendUserId(userId);
        messageSendDto.setContactId(message.getSendUserId());
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
        // 如果是私聊消息，检查用户是否有权限（只有发送者或者接收者可以下载）
        if (UserContactTypeEnum.USER == contactTypeEnum && !tokenUserInfoDto.getUserId().equals(message.getSendUserId()) && !message.getContactId().equals(tokenUserInfoDto.getUserId())) {
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