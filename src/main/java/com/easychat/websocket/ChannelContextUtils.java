package com.easychat.websocket;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.MessageSendDto;
import com.easychat.entity.dto.SentMessageStatusDto;
import com.easychat.entity.dto.WsInitData;
import com.easychat.entity.po.ChatMessage;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.po.UserContactApply;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.ChatMessageQuery;
import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.query.UserContactApplyQuery;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.enums.MessageStatusEnum;
import com.easychat.enums.MessageTypeEnum;
import com.easychat.enums.UserContactApplyStatusEnum;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.mapper.ChatMessageMapper;
import com.easychat.mapper.ChatSessionUserMapper;
import com.easychat.mapper.UserContactApplyMapper;
import com.easychat.mapper.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionUserService;
import com.easychat.utils.JsonUtils;
import com.easychat.utils.StringTools;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GlobalEventExecutor;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 通道上下文工具类，用于管理用户和群组的WebSocket连接
 * 提供添加、移除、发送消息等功能，并处理用户上线、下线状态
 */
@Component
public class ChannelContextUtils {
    private static final Logger logger = LoggerFactory.getLogger(ChannelContextUtils.class);
    // 存储用户ID与WebSocket通道的映射关系
    private static final ConcurrentHashMap<String, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();
    // 存储群组ID与通道组的映射关系
    private static final ConcurrentHashMap<String, ChannelGroup> GROUP_CONTEXT_MAP = new ConcurrentHashMap<>();

    @Resource
    private final RedisComponent redisComponent;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private ChatSessionUserMapper<ChatSessionUser, ChatSessionUserQuery> chatSessionUserMapper;
    @Resource
    private ChatMessageMapper<ChatMessage, ChatMessageQuery> chatMessageMapper;
    @Resource
    private UserContactApplyMapper<UserContactApply, UserContactApplyQuery> userContactApplyMapper;

    /**
     * 构造函数，注入Redis组件
     * @param redisComponent Redis组件
     */
    public ChannelContextUtils(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }

    /**
     * 添加用户上下文信息
     * @param userId 用户ID
     * @param channel WebSocket通道
     */
    public void addContext(String userId, Channel channel) {
        String channelId = channel.id().toString();
        AttributeKey attributeKey = null;
        if (!AttributeKey.exists(channelId)) {
            attributeKey = AttributeKey.newInstance(channelId);
        } else {
            attributeKey = AttributeKey.valueOf(channelId);
        }
        channel.attr(attributeKey).set(userId);

        List<String> contactIdList = redisComponent.getUserContactList(userId);
        for (String contactId : contactIdList) {
            if (contactId.startsWith(UserContactTypeEnum.GROUP.getPrefix())){
                add2Group(contactId, channel);
            }
        }

        USER_CONTEXT_MAP.put(userId, channel);
        redisComponent.saveHeartBeat(userId);

        UserInfo updUserInfo = new UserInfo();
        updUserInfo.setLastLoginTime(new Date());
        userInfoMapper.updateByUserId(updUserInfo, userId);

        UserInfo userInfo = userInfoMapper.selectByUserId(userId);
        Long sourceLastOffTime = userInfo.getLastOffTime();
        Long lastOffTime = sourceLastOffTime;
        if (sourceLastOffTime != null && System.currentTimeMillis() - Constants.MILLLSSECONDS_3DAY > sourceLastOffTime) {
            lastOffTime = System.currentTimeMillis() - Constants.MILLLSSECONDS_3DAY;
        }

        // 查询会话信息
        ChatSessionUserQuery sessionUserQuery = new ChatSessionUserQuery();
        sessionUserQuery.setUserId(userId);
        sessionUserQuery.setOrderBy("last_receive_time desc");
        List<ChatSessionUser> chatSessionUserList = chatSessionUserMapper.selectList(sessionUserQuery);

        WsInitData wsInitData = new WsInitData();
        wsInitData.setChatSessionList(chatSessionUserList);

        // 查询聊天信息（第一段：别人发给我的消息，含我所在群的消息）
        List<String> groupIdList = contactIdList.stream().filter(item -> item.startsWith(UserContactTypeEnum.GROUP.getPrefix())).collect(Collectors.toList());
        groupIdList.add(userId);
        ChatMessageQuery messageQuery = new ChatMessageQuery();
        messageQuery.setContactIdList(groupIdList);
        messageQuery.setLastReceiveTime(lastOffTime);
        List<ChatMessage> chatMessageList = chatMessageMapper.selectList(messageQuery);

        wsInitData.setChatMessageList(chatMessageList);

        // 查询聊天信息（第二段：我发出的、且已被接收方确认送达的消息）。
        // 第一段只覆盖 contact_id ∈ {群, 我}，即「收件人是我」的消息，不包含「我发出的」；
        // 若不补第二段，发送方离线期间消息被接收方 ACK 后，重连也拉不到自己发出消息的最新状态，会永远停在「已发送」。
        // 注意：这里不能用 lastOffTime 作为时间条件——lastOffTime 是「WS 断开时刻」，而待回补的消息发送于断开之前，
        // send_time < lastOffTime 会被扩展条件 send_time &gt; lastReceiveTime 过滤掉，从而永远查不到；
        // 因此改用「最近三天」作为回溯下界（该条件作用于 send_time），并按 status=已送达 精确筛选。
        ChatMessageQuery sentMessageQuery = new ChatMessageQuery();
        sentMessageQuery.setSendUserId(userId);
        sentMessageQuery.setStatus(MessageStatusEnum.DELIVERED.getStatus().byteValue());
        sentMessageQuery.setLastReceiveTime(System.currentTimeMillis() - Constants.MILLLSSECONDS_3DAY);
        List<ChatMessage> sentMessageList = chatMessageMapper.selectList(sentMessageQuery);

        // 只回传 messageId/sessionId/status 三个字段，不把「我发出的消息」混进 chatMessageList：
        // 前端 saveMessageBatch 会先按 contactId 累加未读数，混入我发出的消息会导致未读数虚增。
        List<SentMessageStatusDto> sentMessageStatusList = new ArrayList<>(sentMessageList.size());
        for (ChatMessage chatMessage : sentMessageList) {
            SentMessageStatusDto sentMessageStatusDto = new SentMessageStatusDto();
            sentMessageStatusDto.setMessageId(chatMessage.getMessageId());
            sentMessageStatusDto.setSessionId(chatMessage.getSessionId());
            // 查询已按 status=已送达 过滤，此处 status 必然非空
            sentMessageStatusDto.setStatus(chatMessage.getStatus().intValue());
            sentMessageStatusList.add(sentMessageStatusDto);
        }
        wsInitData.setSentMessageStatusList(sentMessageStatusList);

        // 查询好友申请
        UserContactApplyQuery applyQuery = new UserContactApplyQuery();
        applyQuery.setReceiveUserId(userId);
        applyQuery.setLastApplyTimestamp(lastOffTime);
        applyQuery.setStatus(UserContactApplyStatusEnum.INIT.getStatus().byteValue());
        Integer applyCount = userContactApplyMapper.selectCount(applyQuery);
        wsInitData.setApplyCount(applyCount);


        // 发送消息
        MessageSendDto messageSendDto = new MessageSendDto();
        messageSendDto.setMessageType(MessageTypeEnum.INIT.getType());
        messageSendDto.setContactId(userId);
        messageSendDto.setExtendData(wsInitData);

        sendMsg(messageSendDto, userId);
    }

    /**
     * 发送消息给指定用户
     * @param messageSendDto 消息发送DTO
     * @param reciveId 接收者ID
     */
    public static void sendMsg(MessageSendDto messageSendDto, String reciveId) {
        if (reciveId == null) {
            return;
        }

        Channel channel = USER_CONTEXT_MAP.get(reciveId);
        if (channel == null) {
            return;
        }

        if (MessageTypeEnum.ADD_FRIEND_SELF.getType().equals(messageSendDto.getMessageType())) {
            // 因为申请别人作为自己的好友，需要别人同意后再从WS触发发送消息给申请人，而不是像普通聊天时自己发送的消息直接从本地渲染
            UserInfo userInfo = (UserInfo) messageSendDto.getExtendData();
            messageSendDto.setMessageType(MessageTypeEnum.ADD_FRIEND.getType());
            messageSendDto.setContactId(userInfo.getUserId());
            messageSendDto.setContactName(userInfo.getNickName());
            messageSendDto.setExtendData(null);
        } else {
            messageSendDto.setContactId(messageSendDto.getSendUserId());
            messageSendDto.setContactName(messageSendDto.getSendUserNickName());
        }
        channel.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));
    }

    /**
     * 发送消息
     * @param messageSendDto 消息发送DTO
     */
    public void sendMessage(MessageSendDto messageSendDto) {
        UserContactTypeEnum contactTypeEnum = UserContactTypeEnum.getByPrefix(messageSendDto.getContactId());
        switch (contactTypeEnum) {
            case USER:
                send2User(messageSendDto);
                break;
            case GROUP:
                send2Group(messageSendDto);
                break;
        }
    }

    /**
     * 发送消息给指定用户
     * @param messageSendDto 消息发送DTO
     */
    private void send2User(MessageSendDto messageSendDto) {
        String contactId = messageSendDto.getContactId();
        if (StringTools.isEmpty(contactId)) return;
        sendMsg(messageSendDto, contactId);
        // 强制下线
        if (MessageTypeEnum.FORCE_OFF_LINE.getType().equals(messageSendDto.getMessageType())) {
            closeContext(contactId);
        }
    }

    /**
     * 发送消息到群组
     * @param messageSendDto 消息发送DTO
     */
    private void send2Group(MessageSendDto messageSendDto) {
        if (StringTools.isEmpty(messageSendDto.getContactId())) {
            return;
        }
        ChannelGroup group = GROUP_CONTEXT_MAP.get(messageSendDto.getContactId());
        if (group == null) {
            return;
        }
        // 移除群聊
        MessageTypeEnum messageTypeEnum = MessageTypeEnum.getByType(messageSendDto.getMessageType());
        if (MessageTypeEnum.LEAVE_GROUP == messageTypeEnum || MessageTypeEnum.REMOVE_GROUP == messageTypeEnum) {
            String userId = (String) messageSendDto.getExtendData();
            redisComponent.removeUserContact(userId, messageSendDto.getContactId());
            Channel channel = USER_CONTEXT_MAP.get(userId);
            if (channel != null) {
                group.remove(channel);
            }
        }

        group.writeAndFlush(new TextWebSocketFrame(JsonUtils.convertObj2Json(messageSendDto)));

        if (MessageTypeEnum.DISSOLUTION_GROUP == messageTypeEnum) {
            GROUP_CONTEXT_MAP.remove(messageSendDto.getContactId());
            group.close();
        }
    }

    /**
     * 添加用户到群组
     * @param userId 用户ID
     * @param groupId 群组ID
     */
    public void addUser2Group(String userId, String groupId) {
        Channel channel = USER_CONTEXT_MAP.get(userId);
        add2Group(groupId, channel);
    }

    /**
     * 添加通道到群组
     * @param groupId 群组ID
     * @param channel WebSocket通道
     */
    private void add2Group(String groupId, Channel channel) {
        ChannelGroup group = GROUP_CONTEXT_MAP.get(groupId);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            GROUP_CONTEXT_MAP.put(groupId, group);
        }
        if (channel == null) return;
        group.add(channel);
    }

    /**
     * 关闭用户上下文
     * @param userId 用户ID
     */
    public void closeContext(String userId){
        if (StringTools.isEmpty(userId)) {
            return;
        }
        redisComponent.cleanUserTokenByUserId(userId);
        Channel channel = USER_CONTEXT_MAP.get(userId);
        if (channel != null) {
            channel.close();
        }
    }

    /**
     * 移除用户上下文
     * @param channel WebSocket通道
     */
    public void removeContext(Channel channel) {
        Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attribute.get();
        if (!StringTools.isEmpty(userId)) {
            USER_CONTEXT_MAP.remove(userId);
        }
        redisComponent.removeHeartBeat(userId);

        UserInfo userInfo = new UserInfo();
        userInfo.setLastOffTime(System.currentTimeMillis());
        userInfoMapper.updateByUserId(userInfo, userId);
    }
}
