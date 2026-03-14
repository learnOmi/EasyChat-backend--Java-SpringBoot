package com.easychat.websocket;

import com.easychat.entity.dto.MessageSendDto;
import com.easychat.utils.JsonUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 消息处理器组件
 * 用于处理消息的订阅和发送功能
 */
@Component("messageHandler")
public class MessageHandler {
    // 日志记录器
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);
    // 消息主题常量
    private static final String MESSAGE_TOPIC = "message.topic";

    // Redisson客户端，用于Redis操作
    @Resource
    private RedissonClient redissonClient;
    // 通道上下文工具类，用于发送消息
    @Resource
    private ChannelContextUtils channelContextUtils;

    /**
     * 初始化方法，用于订阅消息主题
     * 使用@PostConstruct注解，在Bean初始化后自动执行
     */
    @PostConstruct
    public void lisMessage() {
        // 获取Redis主题
        RTopic rTopic = redissonClient.getTopic(MESSAGE_TOPIC);
        // 添加监听器，监听MessageSendDto类型的消息
        rTopic.addListener(MessageSendDto.class, (MessageSendDto, sendDto) ->{
            // 记录接收到的消息日志
            logger.info("收到消息:{}", JsonUtils.convertObj2Json(sendDto));
            // 通过通道上下文工具类发送消息
            channelContextUtils.sendMessage(sendDto);
        });
    }

    /**
     * 发送消息方法
     * @param sendDto 要发送的消息对象
     */
    public void sendMessage(MessageSendDto sendDto) {
        // 获取Redis主题
        RTopic rTopic = redissonClient.getTopic(MESSAGE_TOPIC);
        // 发布消息到主题
        rTopic.publish(sendDto);
    }
}
