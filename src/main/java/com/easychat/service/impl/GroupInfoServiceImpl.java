package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.*;
import com.easychat.entity.query.*;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.*;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionUserService;
import com.easychat.service.GroupInfoService;
import com.easychat.service.UserContactService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import com.easychat.websocket.ChannelContextUtils;
import com.easychat.websocket.MessageHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * ServiceImpl
 * @author 'Tong'
 * @since 2025/10/17
 */
@Service("groupInfoService")
public class GroupInfoServiceImpl implements GroupInfoService {
	@Resource
	private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;
    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private AppConfig appConfig;
    @Resource
    private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;
    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;
    @Resource
    private ChatSessionUserService chatSessionUserService;
    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private ChannelContextUtils channelContextUtils;
    @Resource
    private UserContactService userContactService;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    @Lazy
    private GroupInfoService groupInfoService;

	// 根据条件查询列表
	public List<GroupInfo> findListByParam(GroupInfoQuery query) {
		return this.groupInfoMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(GroupInfoQuery query) {
		return this.groupInfoMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<GroupInfo> findPageByParam(GroupInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<GroupInfo> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(GroupInfo bean) {
		return this.groupInfoMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<GroupInfo> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.groupInfoMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<GroupInfo> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.groupInfoMapper.insertOrUpdateBatch(listBean);
	}

	// 根据GroupId查询
	public GroupInfo getGroupInfoByGroupId(String groupId) {
		return this.groupInfoMapper.selectByGroupId(groupId);
	}

	// 根据GroupId更新
	public Integer updateGroupInfoByGroupId(GroupInfo bean, String groupId) {
		return this.groupInfoMapper.updateByGroupId(bean, groupId);
	}

	// 根据GroupId删除
	public Integer deleteGroupInfoByGroupId(String groupId) {
		return this.groupInfoMapper.deleteByGroupId(groupId);
	}

    @Transactional(rollbackFor = Exception.class)
    public void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException {
        Date currentDate = new Date();

        // 新增
        if (StringTools.isEmpty(groupInfo.getGroupId())) {
            GroupInfoQuery groupInfoQuery = new GroupInfoQuery();
            groupInfoQuery.setGroupId(groupInfo.getGroupId());
            Integer count = this.groupInfoMapper.selectCount(groupInfoQuery);
            SysSettingDto sysSettingDto = redisComponent.getSysSetting();
            if (count > sysSettingDto.getMaxGroupCount()) {
                throw new BusinessException("最多支持能创建" + sysSettingDto.getMaxGroupCount() + "个群聊");
            }
            if (null == avatarFile) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }

            groupInfo.setCreateTime(currentDate);
            groupInfo.setGroupId(StringTools.getGroupId());
            this.groupInfoMapper.insert(groupInfo);

            // 将群组添加为联系人
            UserContact userContact = new UserContact();
            userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
            userContact.setCreateTime(currentDate);
            userContact.setLastUpdateTime(currentDate);
            userContact.setUserId(groupInfo.getGroupOwnerId());
            userContact.setContactId(groupInfo.getGroupId());
            userContact.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());
            this.userContactMapper.insert(userContact);

            // 创建会话
            String sessionId = StringTools.getChatSessionId4Group(groupInfo.getGroupId());
            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(sessionId);
            chatSession.setLastMessage(MessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatSession.setLastReceiveTime(currentDate.getTime());
            this.chatSessionMapper.insertOrUpdate(chatSession);

            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setUserId(groupInfo.getGroupOwnerId());
            chatSessionUser.setContactId(groupInfo.getGroupId());
            chatSessionUser.setContactName(groupInfo.getGroupName());
            chatSessionUser.setSessionId(sessionId);
            this.chatSessionUserMapper.insert(chatSessionUser);

            // 创建消息
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(MessageTypeEnum.GROUP_CREATE.getType().byteValue());
            chatMessage.setMessageContent(MessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatMessage.setSendTime(currentDate.getTime());
            chatMessage.setContactId(groupInfo.getGroupId());
            chatMessage.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());
            chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus().byteValue());
            chatMessageMapper.insert(chatMessage);

            // 将群组添加到联系人
            redisComponent.addUserContact(groupInfo.getGroupOwnerId(), groupInfo.getGroupId());
            // 将联系人channel添加到channelGroup
            channelContextUtils.addUser2Group(groupInfo.getGroupOwnerId(), groupInfo.getGroupId());

            // 发送ws消息
            chatSessionUser.setLastMessage(MessageTypeEnum.GROUP_CREATE.getInitMessage());
            chatSessionUser.setLastReceiveTime(currentDate.getTime());
            chatSessionUser.setMemberCount(1);

            MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
            messageSendDto.setExtendData(chatSessionUser);
            messageSendDto.setLastMessage(chatSessionUser.getLastMessage());
            messageHandler.sendMessage(messageSendDto);
        } else {
            GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupInfo.getGroupId());
            if (!dbInfo.getGroupOwnerId().equals(groupInfo.getGroupOwnerId())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            this.groupInfoMapper.updateByGroupId(groupInfo, groupInfo.getGroupId());

            String contactNameUpdate = null;
            if (!dbInfo.getGroupName().equals(groupInfo.getGroupName())) {
                contactNameUpdate = groupInfo.getGroupName();
            }
            if (contactNameUpdate == null) return;

            chatSessionUserService.updateRedundantInfo(contactNameUpdate, groupInfo.getGroupId());
        }
        String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR);
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();

        }
        String filePath = targetFileFolder.getPath() + "/" + groupInfo.getGroupId() + Constants.IMAGE_SUFFIX;
        avatarFile.transferTo(new File(filePath));
        avatarCover.transferTo(new File(filePath + Constants.COVER_IMAGE_SUFFIX));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dissolutionGroup(String groupOwnerId, String groupId) {
        GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupId);
        if (null == dbInfo || !dbInfo.getGroupOwnerId().equals(groupOwnerId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 删除群组
        GroupInfo updateInfo = new GroupInfo();
        updateInfo.setStatus(GroupStatusEnum.DISSOLUTION.getStatus().byteValue());
        this.groupInfoMapper.updateByGroupId(updateInfo, groupId);

        // 更新联系人信息
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setContactId(groupId);
        userContactQuery.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());

        UserContact updateUserContact = new UserContact();
        updateUserContact.setStatus(UserContactStatusEnum.DEL.getStatus().byteValue());
        this.userContactMapper.updateByParam(updateUserContact, userContactQuery);

        List<UserContact> userContactList = this.userContactMapper.selectList(userContactQuery);
        for (UserContact userContact : userContactList) {
            redisComponent.removeUserContact(userContact.getUserId(), userContact.getContactId());
        }

        String sessionId = StringTools.getChatSessionId4Group(groupId);
        Date curDate = new Date();
        String messageContent = MessageTypeEnum.DISSOLUTION_GROUP.getInitMessage();

        ChatSession chatSession = new ChatSession();
        chatSession.setLastMessage(messageContent);
        chatSession.setLastReceiveTime(curDate.getTime());
        chatSessionMapper.updateBySessionId(chatSession, sessionId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setSendTime(curDate.getTime());
        chatMessage.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());
        chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus().byteValue());
        chatMessage.setMessageType(MessageTypeEnum.DISSOLUTION_GROUP.getType().byteValue());
        chatMessage.setContactId(groupId);
        chatMessage.setMessageContent(messageContent);
        chatMessageMapper.insert(chatMessage);

        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
        messageHandler.sendMessage(messageSendDto);
    }


    @Override
    public void addOrRemoveGroupUser(TokenUserInfoDto tokenUserInfoDto, String groupId, String contactIds, Integer opType) {
        GroupInfo groupInfo = groupInfoMapper.selectByGroupId(groupId);
        if (null == groupInfo || !groupInfo.getGroupOwnerId().equals(tokenUserInfoDto.getUserId())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        String[] contactIdList = contactIds.split(",");
        for (String contactId : contactIdList) {
            if(Constants.ZERO.equals(opType)) {
                groupInfoService.leaveGroup(tokenUserInfoDto.getUserId(), groupId, MessageTypeEnum.LEAVE_GROUP);
            } else {
                userContactService.addContact(contactId, null, groupId, UserContactTypeEnum.GROUP.getType(), null);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveGroup(String userId, String groupId, MessageTypeEnum messageTypeEnum) {
        GroupInfo groupInfo = groupInfoMapper.selectByGroupId(groupId);
        if (groupInfo == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (groupInfo.getGroupOwnerId().equals(userId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        Integer count = userContactMapper.deleteByUserIdAndContactId(userId, groupId);
        if (count == 0) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        UserInfo userInfo = userInfoMapper.selectByUserId(userId);

        String sessionId = StringTools.getChatSessionId4Group(groupId);
        Date curDate = new Date();
        String messageContent = String.format(messageTypeEnum.getInitMessage(), userInfo.getNickName());

        ChatSession chatSession = new ChatSession();
        chatSession.setLastMessage(messageContent);
        chatSession.setLastReceiveTime(curDate.getTime());
        chatSessionMapper.updateBySessionId(chatSession, sessionId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setSendTime(curDate.getTime());
        chatMessage.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());
        chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus().byteValue());
        chatMessage.setMessageType(messageTypeEnum.getType().byteValue());
        chatMessage.setContactId(groupId);
        chatMessage.setMessageContent(messageContent);
        chatMessageMapper.insert(chatMessage);

        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setContactId(groupId);
        userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
        Integer memberCount = this.userContactMapper.selectCount(userContactQuery);
        MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
        messageSendDto.setExtendData(userId);
        messageSendDto.setMemberCount(memberCount);
        messageHandler.sendMessage(messageSendDto);
    }

}