package com.easychat.controller;

import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.enums.ResponseCodeEnum;

import com.easychat.entity.vo.ResponseVO;
import com.easychat.exception.BusinessException;
import com.easychat.redis.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

public class ABaseController {
    protected static final String STATUS_SUCCESS = "success";
    protected static final String STATUS_ERROR = "error";

    @Resource
    private RedisUtils redisUtils;

    protected <T> ResponseVO getSuccessResponse(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUS_SUCCESS);
        responseVO.setData(t);
        responseVO.setMessage(ResponseCodeEnum.CODE_200.getMsg());
        responseVO.setCode(ResponseCodeEnum.CODE_200.getCode());
        return responseVO;
    }

    protected <T> ResponseVO getBusinessErrorResponse(BusinessException e, T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUS_ERROR);
        if (e.getCode() != null) {
            responseVO.setCode(e.getCode());
        } else {
            responseVO.setCode(ResponseCodeEnum.CODE_600.getCode());
        }
        responseVO.setMessage(e.getMessage());
        responseVO.setData(t);

        return responseVO;
    }

    protected <T> ResponseVO getServerErrorResponse(T t) {
        ResponseVO<T> responseVO = new ResponseVO<>();
        responseVO.setStatus(STATUS_ERROR);
        responseVO.setCode(ResponseCodeEnum.CODE_500.getCode());
        responseVO.setMessage(ResponseCodeEnum.CODE_500.getMsg());
        responseVO.setData(t);
        return responseVO;
    }

    protected TokenUserInfoDto getTokenUserInfoDto(HttpServletRequest request) {
        String token = request.getHeader("token");
        TokenUserInfoDto tokenUserInfoDto = (TokenUserInfoDto) redisUtils.get(Constants.REDIS_KEY_WS_TOKEN + token);
        return tokenUserInfoDto;
    }
}
