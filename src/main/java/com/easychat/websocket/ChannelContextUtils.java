package com.easychat.websocket;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.WsInitData;
import com.easychat.entity.po.ChatSessionUser;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.ChatSessionUserQuery;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.mapper.UserInfoMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.ChatSessionUserService;
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

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelContextUtils {
    private static final Logger logger = LoggerFactory.getLogger(ChannelContextUtils.class);
    private static final ConcurrentHashMap<String, Channel> USER_CONTEXT_MAP = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, ChannelGroup> GROUP_CONTEXT_MAP = new ConcurrentHashMap<>();

    @Resource
    private final RedisComponent redisComponent;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private ChatSessionUserService chatSessionUserService;

    public ChannelContextUtils(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }

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
        List<ChatSessionUser> chatSessionUserList = chatSessionUserService.findListByParam(sessionUserQuery);

        WsInitData wsInitData = new WsInitData();
        wsInitData.setChatSessionUserList(chatSessionUserList);


    }

    private void add2Group(String groupId, Channel channel) {
        if (channel == null) {
            return;
        }
        ChannelGroup group = GROUP_CONTEXT_MAP.get(groupId);
        if (group == null) {
            group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
            GROUP_CONTEXT_MAP.put(groupId, group);
        }
        group.add(channel);
    }

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
