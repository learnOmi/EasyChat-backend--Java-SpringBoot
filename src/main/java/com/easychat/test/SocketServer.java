package com.easychat.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketServer {
    /**
     * 主方法，用于启动一个简单的服务器
     * 监听1027端口，当有客户端连接时，显示客户端IP地址
     */
    public static void main(String[] args) {
        // 创建ServerSocket对象，用于监听客户端连接
        ServerSocket server = null;
        try {
            // 在1027端口创建服务器套接字
            server = new ServerSocket(1027);
            // 打印服务器启动信息
            System.out.println("server start");
            // 等待客户端连接，这是一个阻塞方法
            Socket socket = server.accept();
            // 获取并打印客户端的IP地址
            String ip = socket.getInetAddress().getHostAddress();
            System.out.println(ip + " connected");

            // 创建输入流，用于接收客户端发送的数据
            InputStream inputStream = socket.getInputStream();
            // 创建缓冲字符流，方便读取客户端发送的文本数据
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            new Thread(() -> {
                while (true) {
                    try {
                        // 读取客户端发送的一行数据
                        String line = reader.readLine();
                        // 打印接收到的客户端数据
                        System.out.println(line);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            // 捕获并打印可能发生的异常
            e.printStackTrace();
        }
    }
}
