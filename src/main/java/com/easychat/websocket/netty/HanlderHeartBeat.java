package com.easychat.websocket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳检测处理器
 * 继承ChannelDuplexHandler以实现双向通道的事件处理
 */
public class HanlderHeartBeat extends ChannelDuplexHandler {
    // 创建日志记录器，用于记录心跳检测相关信息
    private static final Logger looger = LoggerFactory.getLogger(HanlderHeartBeat.class);

    /**
     * 用户事件触发方法
     * @param ctx 通道处理器上下文，用于获取通道信息和执行操作
     * @param evt 触发的事件对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 判断事件是否为空闲状态事件
        if (evt instanceof IdleStateEvent) {
            // 将事件对象转换为IdleStateEvent
            IdleStateEvent event = (IdleStateEvent) evt;
            // 检查是否为读空闲状态（即长时间没有收到数据）
            if (event.state() == IdleState.READER_IDLE){
                Channel channel = ctx.channel();
                Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
                String userId = attribute.get();
                // 记录读空闲超时的日志信息
                looger.info("用户userId:{}心跳检测超时:{}", userId, event.state());
                ctx.close();  // 当读空闲超时时，关闭通道连接
            } else if (event.state() == IdleState.WRITER_IDLE) {
                ctx.writeAndFlush("heart");  // 当写空闲时，发送心跳消息
            }
        }
    }
}
