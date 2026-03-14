package com.easychat.websocket.netty;

import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.redis.RedisComponent;
import com.easychat.utils.StringTools;
import com.easychat.websocket.ChannelContextUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * WebSocket处理器类
 * 处理WebSocket连接、消息接收和用户认证等相关逻辑
 */
@Component
@ChannelHandler.Sharable
public class HandlerWebSocket extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(HandlerWebSocket.class);

    @Resource
    private RedisComponent redisComponent;
    @Resource
    private ChannelContextUtils channelContextUtils;

    /**
     * 处理接收到的WebSocket消息
     * @param ctx 通道处理上下文
     * @param msg 接收到的文本消息
     * @throws Exception 可能抛出的异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
        String userId = attribute.get();
        //logger.info("收到userId{}消息: {}",userId, msg.text());
        redisComponent.saveHeartBeat(userId);
    }

    /**
     * 当客户端连接建立时调用
     * @param ctx 通道处理上下文
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有新的连接加入……");
    }

    /**
     * 当客户端连接断开时调用
     * @param ctx 通道处理上下文
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("有连接断开……");
        channelContextUtils.removeContext(ctx.channel());
    }

    /**
     * 处理用户触发的事件
     * 主要用于处理WebSocket握手完成后的用户认证，添加用户ID和通道的对应关系
     * @param ctx ChannelHandlerContext 通道处理器上下文
     * @param evt Object 触发的事件对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 检查是否是WebSocket握手完成事件
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            WebSocketServerProtocolHandler.HandshakeComplete complete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String url = complete.requestUri();
            String token = getToken(url);
            // 如果token为空，关闭连接
            if (null == token) {
                ctx.channel().close();
                return;
            }
            // 从Redis中获取用户信息，如果不存在则关闭连接
            TokenUserInfoDto tokenUserInfoDto = redisComponent.getTokenUserInfoDto(token);
            if (null == tokenUserInfoDto) {
                ctx.channel().close();
                return;
            }

            // 将用户ID和对应的通道添加到上下文工具中
            channelContextUtils.addContext(tokenUserInfoDto.getUserId(), ctx.channel());
        }
    }

    /**
     * 从URL中提取token参数
     * @param url 完整的请求URL
     * @return 提取到的token值，如果不存在则返回null
     */
    private String getToken(String url) {
        // 检查URL是否为空或是否包含参数部分
        if (StringTools.isEmpty(url) || !url.contains("?")) {
            return null;
        }
        // 分割URL和参数部分
        String[] split = url.split("\\?");
        if (split.length != 2) {
            return null;
        }
        // 分割参数
        String[] params = split[1].split("&");
        for (String param : params) {
            // 查找token参数
            if (param.startsWith("token")) {
                String[] token = param.split("=");
                if (token.length == 2) {
                    return token[1];
                }
            }
        }
        return null;
    }
}
