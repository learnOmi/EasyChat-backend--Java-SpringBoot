package com.easychat.controller;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.redis.RedisUtils;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController("accountController")
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Resource
    private RedisUtils redisUtils;

    @RequestMapping("/checkcode")
    public ResponseVO checkCode() {
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 43);
        String code = captcha.text();
        String checkCodeKey = UUID.randomUUID().toString();

        redisUtils.setex(Constants.REDIS_KEY_CHECK_CODE, code, Constants.REDIS_TIME_1MIN * 5L);

        String checkCodeBase64 = captcha.toBase64();
        Map<String, String> result = new HashMap<>();
        result.put("checkCode", checkCodeBase64);
        result.put("checkCodeKey", checkCodeKey);

        return  getSuccessResponse(result);
    }
}
