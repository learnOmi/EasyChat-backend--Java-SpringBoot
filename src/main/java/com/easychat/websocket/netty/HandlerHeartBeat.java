package com.easychat.websocket.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳检测处理器
 * 继承 ChannelDuplexHandler 以实现双向通道的事件处理。
 * <p>
 * 当前 pipeline 中的 IdleStateHandler 只启用了读空闲（writerIdleTime / allIdleTime 均为 0），
 * 因此这里只需处理 READER_IDLE：长时间收不到客户端数据即判定连接已失效。
 * 服务端不主动发心跳——客户端已有每 5 秒一次的上行心跳，
 * 并由 HandlerWebSocket 收到心跳后回包，足以让客户端判断链路存活。
 */
public class HandlerHeartBeat extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(HandlerHeartBeat.class);

    /** 关闭帧中携带的原因，便于客户端与抓包排查断连原因 */
    private static final String CLOSE_REASON_HEART_BEAT_TIMEOUT = "heart beat timeout";

    /**
     * 用户事件触发方法
     * @param ctx 通道处理器上下文，用于获取通道信息和执行操作
     * @param evt 触发的事件对象
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 读空闲：长时间未收到客户端任何数据，判定连接失效，主动关闭
        if (evt instanceof IdleStateEvent && ((IdleStateEvent) evt).state() == IdleState.READER_IDLE) {
            Channel channel = ctx.channel();
            Attribute<String> attribute = channel.attr(AttributeKey.valueOf(channel.id().toString()));
            String userId = attribute.get();
            logger.warn("用户心跳检测超时，主动关闭连接: userId={}, state={}", userId, IdleState.READER_IDLE);
            // 发送 WebSocket 关闭帧后再关闭，让客户端拿到 1000（正常关闭）而非 1006（异常关闭），便于定位原因。
            // 必须从通道尾部写出：本处理器位于 WebSocketServerProtocolHandler 之前，
            // 若用 ctx.writeAndFlush 则 outbound 不经过 WebSocket 帧编码器，会抛出「不支持的报文类型」。
            channel.writeAndFlush(new CloseWebSocketFrame(WebSocketCloseStatus.NORMAL_CLOSURE, CLOSE_REASON_HEART_BEAT_TIMEOUT))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        // 其它用户事件（如握手完成事件）必须继续向下游传播，否则会被静默吞掉
        ctx.fireUserEventTriggered(evt);
    }
}
