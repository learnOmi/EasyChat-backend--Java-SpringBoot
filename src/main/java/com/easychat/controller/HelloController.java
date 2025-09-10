package com.easychat.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 表示这是一个REST控制器
public class HelloController {

    @GetMapping("/hello") // 处理GET请求到/hello路径
    public String sayHello() {
        return "Hello, Spring";
    }
}