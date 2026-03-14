package com.easychat.service.impl;

import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.dto.UserContactSearchResultDto;
import com.easychat.entity.po.*;
import com.easychat.entity.query.*;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.*;
import com.easychat.redis.RedisComponent;
import com.easychat.service.UserContactService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import com.easychat.websocket.ChannelContextUtils;
import com.easychat.websocket.MessageHandler;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 联系人ServiceImpl
 * @author 'Tong'
 * @since 2025/10/17
 */
@Service("userContactService")
public class UserContactServiceImpl implements UserContactService {
	@Resource
	private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;
    @Resource
    private ChatSessionMapper<ChatSession, ChatSessionQuery> chatSessionMapper;
    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;
    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private ChannelContextUtils channelContextUtils;

    // 根据条件查询列表
	public List<UserContact> findListByParam(UserContactQuery query) {
		return this.userContactMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(UserContactQuery query) {
		return this.userContactMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<UserContact> findPageByParam(UserContactQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<UserContact> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(UserContact bean) {
		return this.userContactMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<UserContact> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userContactMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<UserContact> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.userContactMapper.insertOrUpdateBatch(listBean);
	}

	// 根据UserIdAndContactId查询
	public UserContact getUserContactByUserIdAndContactId(String userId, String contactId) {
		return this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
	}

	// 根据UserIdAndContactId更新
	public Integer updateUserContactByUserIdAndContactId(UserContact bean, String userId, String contactId) {
		return this.userContactMapper.updateByUserIdAndContactId(bean, userId, contactId);
	}

	// 根据UserIdAndContactId删除
	public Integer deleteUserContactByUserIdAndContactId(String userId, String contactId) {
		return this.userContactMapper.deleteByUserIdAndContactId(userId, contactId);
	}

    // 多条件更新
    public Integer updateByParam(UserContact bean, UserContactQuery query) {
        return this.userContactMapper.updateByParam(bean, query);
    }

    // 多条件删除
    public Integer deleteByParam(UserContactQuery query) {
        return this.userContactMapper.deleteByParam(query);
    }

    public UserContactSearchResultDto searchContact(String userId, String contactId) {
        UserContactTypeEnum typeEnum = UserContactTypeEnum.getByPrefix(contactId);
        if (typeEnum == null) {
            return null;
        }

        UserContactSearchResultDto resultDto = new UserContactSearchResultDto();
        switch (typeEnum) {
            case USER:
                UserInfo userInfo = userInfoMapper.selectByUserId(contactId);
                if (userInfo == null) {
                    return null;
                }
                resultDto = CopyTools.copy(userInfo, UserContactSearchResultDto.class);
                break;
            case GROUP:
                GroupInfo groupInfo = groupInfoMapper.selectByGroupId(contactId);
                if (groupInfo == null) {
                    return null;
                }
                resultDto.setNickName(groupInfo.getGroupName());
                break;
        }
        resultDto.setContactType(typeEnum.toString());
        resultDto.setContactId(contactId);
        if (userId.equals(contactId)) {
            resultDto.setStatus(UserContactStatusEnum.FRIEND.getStatus());
            return resultDto;
        }

        UserContact userContact = this.userContactMapper.selectByUserIdAndContactId(userId, contactId);
        resultDto.setStatus(Integer.valueOf(userContact == null ? null : userContact.getStatus()));
        return resultDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addContact(String applyUserId, String receiveUserId, String contactId, Integer contactType, String applyInfo) {
        if (UserContactTypeEnum.GROUP.getType().equals(contactType)) {
            UserContactQuery userContactQuery = new UserContactQuery();
            userContactQuery.setContactId(contactId);
            userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
            Integer count = userContactMapper.selectCount(userContactQuery);
            SysSettingDto sysSettingDto = redisComponent.getSysSetting();
            if (count >= sysSettingDto.getMaxGroupCount()) {
                throw new BusinessException("群聊人数已满！");
            }
        }

        Date curDate = new Date();
        List<UserContact> contactList = new ArrayList<>();
        UserContact userContact = new UserContact();
        userContact.setUserId(applyUserId);
        userContact.setContactId(contactId);
        userContact.setContactType(contactType.byteValue());
        userContact.setCreateTime(curDate);
        userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
        userContact.setLastUpdateTime(curDate);
        contactList.add(userContact);

        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            userContact = new UserContact();
            userContact.setUserId(receiveUserId);
            userContact.setContactId(applyUserId);
            userContact.setContactType(contactType.byteValue());
            userContact.setCreateTime(curDate);
            userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
            userContact.setLastUpdateTime(curDate);
            contactList.add(userContact);
        }
        userContactMapper.insertOrUpdateBatch(contactList);

        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            redisComponent.addUserContact(receiveUserId, applyUserId);
        }

        redisComponent.addUserContact(applyUserId, contactId);

        String sessionId = null;
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            sessionId = StringTools.getChatSessionId4User(new String[]{applyUserId, contactId});
        } else {
            sessionId = StringTools.getChatSessionId4Group(contactId);
        }
        List<ChatSessionUser> chatSessionUserList = new ArrayList<>();
        if (UserContactTypeEnum.USER.getType().equals(contactType)) {
            // 创建会话
            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(sessionId);
            chatSession.setLastMessage(applyInfo);
            chatSession.setLastReceiveTime(curDate.getTime());
            this.chatSessionMapper.insertOrUpdate(chatSession);

            // 申请人session
            ChatSessionUser applySessionUser = new ChatSessionUser();
            applySessionUser.setUserId(applyUserId);
            applySessionUser.setContactId(contactId);
            applySessionUser.setSessionId(sessionId);
            UserInfo contactUser = this.userInfoMapper.selectByUserId(contactId);
            applySessionUser.setContactName(contactUser.getNickName());
            chatSessionUserList.add(applySessionUser);

            // 接收人session
            ChatSessionUser contactSessionUser = new ChatSessionUser();
            contactSessionUser.setUserId(contactId);
            contactSessionUser.setContactId(applyUserId);
            contactSessionUser.setSessionId(sessionId);
            UserInfo applyUser = this.userInfoMapper.selectByUserId(applyUserId);
            contactSessionUser.setContactName(applyUser.getNickName());
            chatSessionUserList.add(contactSessionUser);
            this.chatSessionUserMapper.insertOrUpdateBatch(chatSessionUserList);

            // 记录消息表
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(MessageTypeEnum.ADD_FRIEND.getType().byteValue());
            chatMessage.setMessageContent(applyInfo);
            chatMessage.setSendUserId(applyUserId);
            chatMessage.setSendUserNickName(applyUser.getNickName());
            chatMessage.setSendTime(curDate.getTime());
            chatMessage.setContactId(contactId);
            chatMessage.setContactType(UserContactTypeEnum.USER.getType().byteValue());
            chatMessageMapper.insert(chatMessage);

            MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
            // 发送消息给接收好友申请的人
            messageHandler.sendMessage(messageSendDto);
            // 发送消息给申请人
            messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND_SELF.getType());
            messageSendDto.setContactId(applyUserId);
            messageSendDto.setExtendData(contactUser);
            messageHandler.sendMessage(messageSendDto);
        } else {
            ChatSessionUser chatSessionUser = new ChatSessionUser();
            chatSessionUser.setUserId(applyUserId);
            chatSessionUser.setContactId(contactId);
            GroupInfo groupInfo = this.groupInfoMapper.selectByGroupId(contactId);
            chatSessionUser.setContactName(groupInfo.getGroupName());
            chatSessionUser.setSessionId(sessionId);
            this.chatSessionUserMapper.insert(chatSessionUser);

            UserInfo applyUser = this.userInfoMapper.selectByUserId(applyUserId);
            String sendMessage = String.format(MessageTypeEnum.ADD_GROUP.getInitMessage(), applyUser.getNickName());
            ChatSession chatSession = new ChatSession();
            chatSession.setSessionId(sessionId);
            chatSession.setLastReceiveTime(curDate.getTime());
            chatSession.setLastMessage(sendMessage);
            this.chatSessionMapper.insertOrUpdate(chatSession);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessageType(MessageTypeEnum.ADD_GROUP.getType().byteValue());
            chatMessage.setMessageContent(sendMessage);
            chatMessage.setSendTime(curDate.getTime());
            chatMessage.setContactId(contactId);
            chatMessage.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());
            chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus().byteValue());
            this.chatMessageMapper.insert(chatMessage);

            redisComponent.addUserContact(applyUserId, groupInfo.getGroupId());
            channelContextUtils.addUser2Group(applyUserId, groupInfo.getGroupId());

            UserContactQuery userContactQuery = new UserContactQuery();
            userContactQuery.setContactId(contactId);
            userContactQuery.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
            Integer memberCount = this.userContactMapper.selectCount(userContactQuery);
            MessageSendDto messageSendDto = CopyTools.copy(chatMessage, MessageSendDto.class);
            messageSendDto.setContactId(contactId);
            messageSendDto.setMemberCount(memberCount);
            messageSendDto.setContactName(groupInfo.getGroupName());
            messageHandler.sendMessage(messageSendDto);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserContact (String userId, String contactId, UserContactStatusEnum statusEnum) {
        UserContact userContact = new UserContact();
        userContact.setStatus(statusEnum.getStatus().byteValue());
        userContactMapper.updateByUserIdAndContactId(userContact, userId, contactId);

        UserContact friendContact = new UserContact();
        if (UserContactStatusEnum.DEL == statusEnum) {
            friendContact.setStatus(UserContactStatusEnum.DEL_BE.getStatus().byteValue());
        } else if (UserContactStatusEnum.BLACKLIST == statusEnum) {
            friendContact.setStatus(UserContactStatusEnum.BLACKLIST_BE.getStatus().byteValue());
        }
        userContactMapper.updateByUserIdAndContactId(friendContact, contactId, userId);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addContactRobot(String userId) {
        // 添加联系人信息
        Date curDate = new Date();
        SysSettingDto sysSettingDto = redisComponent.getSysSetting();
        String contactId = sysSettingDto.getRobotUid();
        String contactName = sysSettingDto.getRobotNickName();
        String sendMessage = sysSettingDto.getRobotWelcome();
        sendMessage = StringTools.cleanHtmlTag(sendMessage);

        UserContact userContact = new UserContact();
        userContact.setUserId(userId);
        userContact.setContactId(contactId);
        userContact.setContactType(UserContactTypeEnum.USER.getType().byteValue());
        userContact.setCreateTime(curDate);
        userContact.setLastUpdateTime(curDate);
        userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
        userContactMapper.insert(userContact);

        // 添加会话信息
        String sessionId = StringTools.getChatSessionId4User(new String[]{userId, contactId});
        ChatSession chatSession = new ChatSession();
        chatSession.setSessionId(sessionId);
        chatSession.setLastMessage(sendMessage);
        chatSession.setLastReceiveTime(curDate.getTime());
        this.chatSessionMapper.insert(chatSession);

        ChatSessionUser chatSessionUser = new ChatSessionUser();
        chatSessionUser.setUserId(userId);
        chatSessionUser.setContactId(contactId);
        chatSessionUser.setContactName(contactName);
        chatSessionUser.setSessionId(sessionId);
        this.chatSessionUserMapper.insert(chatSessionUser);

        // 添加消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setMessageType(MessageTypeEnum.CHAT.getType().byteValue());
        chatMessage.setMessageContent(sendMessage);
        chatMessage.setSendUserId(contactId);
        chatMessage.setSendUserNickName(contactName);
        chatMessage.setSendTime(curDate.getTime());
        chatMessage.setContactId(userId);
        chatMessage.setContactType(UserContactTypeEnum.USER.getType().byteValue());
        chatMessage.setStatus(MessageStatusEnum.SENDED.getStatus().byteValue());
        chatMessageMapper.insert(chatMessage);
    }
}