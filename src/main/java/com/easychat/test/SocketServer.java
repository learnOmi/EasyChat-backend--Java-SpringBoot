package com.easychat.test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

public class SocketServer {
    // 存储所有客户端连接的集合
    private static ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();
    
    /**
     * 主方法，用于启动一个简单的服务器
     * 监听1027端口，当有客户端连接时，显示客户端IP地址
     * 使用多线程处理客户端连接，实现简单的通信功能
     */
    public static void main(String[] args) {
        // 使用try-with-resources管理ServerSocket
        try (ServerSocket server = new ServerSocket(1027)) {
            // 打印服务器启动信息
            System.out.println("server start");
            
            // 主循环，不断接受新的客户端连接
            while (true) {
                try {
                    // 等待客户端连接，这是一个阻塞方法
                    Socket socket = server.accept();
                    // 获取并打印客户端的IP地址
                    String ip = socket.getInetAddress().getHostAddress();
                    System.out.println(ip + " connected : " + socket.getPort());
                    
                    // 为每个客户端创建一个新的线程处理通信
                    new Thread(() -> {
                        String clientKey = null;
                        try {
                            // 使用try-with-resources管理流和读写器
                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(socket.getInputStream()));
                                 PrintWriter printWriter = new PrintWriter(socket.getOutputStream())) {
                                
                                // 将客户端添加到集合中
                                clientKey = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
                                clients.put(clientKey, printWriter);
                                
                                // 向客户端发送欢迎消息
                                printWriter.println("欢迎连接到聊天服务器！");
                                printWriter.flush();
                                
                                // 向所有客户端广播新用户加入的消息
                                broadcastMessage("系统消息: " + clientKey + " 加入了聊天室");
                                
                                // 处理客户端消息
                                while (true) {
                                    // 读取客户端发送的一行数据
                                    String line = reader.readLine();
                                    if (line == null) {
                                        // 客户端已断开连接
                                        System.out.println(clientKey + " 已断开连接");
                                        break;
                                    }
                                    // 打印接收到的客户端数据
                                    System.out.println(clientKey + ": " + line);
                                    
                                    // 向所有客户端广播消息
                                    broadcastMessage(clientKey + ": " + line);
                                }
                            }
                        } catch (Exception e) {
                            System.out.println("客户端通信异常: " + e.getMessage());
                        } finally {
                            // 从集合中移除客户端
                            if (clientKey != null) {
                                clients.remove(clientKey);
                                // 向所有客户端广播用户离开的消息
                                broadcastMessage("系统消息: " + clientKey + " 离开了聊天室");
                            }
                            
                            // 确保socket被关闭
                            try {
                                if (socket != null && !socket.isClosed()) {
                                    socket.close();
                                }
                            } catch (IOException e) {
                                System.out.println("关闭Socket时出错: " + e.getMessage());
                            }
                        }
                    }).start();
                } catch (IOException e) {
                    System.out.println("接受客户端连接时出错: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }
    
    /**
     * 向所有连接的客户端广播消息
     * @param message 要广播的消息
     */
    private static void broadcastMessage(String message) {
        // 遍历所有客户端并发送消息
        clients.forEach((key, printWriter) -> {
            try {
                printWriter.println(message);
                printWriter.flush();
            } catch (Exception e) {
                System.out.println("向客户端 " + key + " 发送消息时出错: " + e.getMessage());
            }
        });
    }
}
