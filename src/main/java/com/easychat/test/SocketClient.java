package com.easychat.test;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient {
/**
 * Java Socket客户端程序
 * 该程序用于创建一个Socket客户端，连接到指定IP和端口
 * 实现从控制台输入内容并发送到服务器的功能
 */
    public static void main(String[] args) {
    // 创建Socket对象，初始值为null
        Socket socket = null;
        try {
        // 创建Socket连接，连接到本地IP(127.0.0.1)的1027端口
            socket = new Socket("127.0.0.1", 1027);
            System.out.println("连接成功");

        // 获取输出流
            OutputStream outputStream = socket.getOutputStream();
        // 创建PrintWriter对象，用于发送数据
            PrintWriter printWriter = new PrintWriter(outputStream);
            System.out.println("请输入要发送的内容");

        // 创建新线程，用于持续监听控制台输入并发送消息
            new Thread(() -> {
                while (true) {
                // 创建Scanner对象读取控制台输入
                    Scanner scanner = new Scanner(System.in);
                // 读取输入的一行内容
                    String message = scanner.nextLine();
                // 发送消息
                    printWriter.println(message);
                // 刷新输出流，确保消息立即发送
                    printWriter.flush();
                }
            }).start();
        } catch (Exception e) {
        // 捕获并打印异常信息
            e.printStackTrace();
        }
    }
}
