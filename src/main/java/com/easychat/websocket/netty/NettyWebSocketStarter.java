package com.easychat.websocket.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Netty WebSocket启动类
 * 负责初始化和启动Netty服务器，配置WebSocket相关的处理器
 */
public class NettyWebSocketStarter {
    // 创建日志记录器，用于记录应用运行时的日志信息
    private static Logger logger = LoggerFactory.getLogger(NettyWebSocketStarter.class);

    // 创建Boss线程组，负责接收客户端连接，线程数设置为1
    private static EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    // 创建Worker线程组，负责处理I/O操作，默认线程数根据CPU核心数决定
    private static EventLoopGroup workerGroup = new NioEventLoopGroup();

    public static void main(String[] args) {
        try {
            // 创建服务器引导对象，用于配置和启动服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 设置Boss和Worker线程组
            serverBootstrap.group(bossGroup, workerGroup);
            // 配置服务器Bootstrap，设置通道类型为NIO服务器套接字通道
            serverBootstrap.channel(NioServerSocketChannel.class)
                    // 添加日志处理器，用于记录DEBUG级别的日志信息
                    .handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            // 获取通道的pipeline，用于添加处理器
                            ChannelPipeline pipeline = channel.pipeline();
                            // 重要处理器
                            /**
                             * Netty服务器管道配置，添加各种处理器以支持HTTP和WebSocket协议
                             * 这些处理器按照顺序添加到ChannelPipeline中，每个处理器负责特定的功能
                             */
                            pipeline.addLast(new HttpServerCodec()); // 添加HTTP编解码器，用于处理HTTP请求和响应
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024)); // 添加HTTP对象聚合器，将多个HTTP消息聚合为完整的FullHttpRequest或FullHttpResponse，设置最大聚合大小为64KB
                            pipeline.addLast(new IdleStateHandler(6, 0, 0, TimeUnit.SECONDS)); // 添加空闲状态处理器，当连接在6秒内没有读写操作时，会触发IdleStateEvent事件
                            pipeline.addLast(new HanlderHeartBeat()); // 添加心跳处理器，用于处理WebSocket连接的心跳检测，保持连接活跃
                            pipeline.addLast(new WebSocketServerProtocolHandler("/ws")); // 添加WebSocket协议处理器，处理WebSocket握手协议，指定WebSocket URI为"/ws"
                            pipeline.addLast(new HandlerWebSocket()); // 添加WebSocket业务处理器，处理实际的WebSocket消息
                        }

                    });
            ChannelFuture channelFuture = serverBootstrap.bind(5051).sync();
            logger.info("netty启动成功！");
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            logger.error("启动netty失败：", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
