package com.easychat.test;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient {
/**
 * Java Socket客户端程序
 * 该程序用于创建一个Socket客户端，连接到指定IP和端口
 * 实现从控制台输入内容并发送到服务器的功能
 */
    public static void main(String[] args) {
        // 创建Socket连接，连接到本地IP(127.0.0.1)的1027端口
        try (Socket socket = new Socket("127.0.0.1", 1027)) {
            System.out.println("连接成功");

            // 使用try-with-resources确保流和读写器正确关闭
            try (PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(socket.getInputStream()))) {
                
                System.out.println("请输入要发送的内容");

                // 创建新线程，用于持续监听控制台输入并发送消息
                new Thread(() -> {
                    Scanner scanner = new Scanner(System.in);
                    try {
                        while (true) {
                            String message = scanner.nextLine();
                            printWriter.println(message);
                            printWriter.flush();
                            
                            // 检查退出命令
                            if ("exit".equalsIgnoreCase(message)) {
                                System.out.println("客户端准备退出");
                                break;
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("发送消息时出错: " + e.getMessage());
                    } finally {
                        scanner.close();
                    }
                }).start();

                // 创建新线程，用于持续监听服务器返回的消息
                new Thread(() -> {
                    while (true) {
                        try {
                            String line = reader.readLine();
                            if (line == null) {
                                System.out.println("服务器已关闭连接");
                                break;
                            }
                            System.out.println("收到消息:" + line);
                        } catch (Exception e) {
                            System.out.println("接收消息时出错: " + e.getMessage());
                        }
                    }
                }).start();
                
                // 保持主线程运行，避免程序退出
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("客户端连接出错: " + e.getMessage());
        }
    }
}
