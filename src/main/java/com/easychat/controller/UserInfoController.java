package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.entity.vo.UserInfoVO;
import com.easychat.service.UserInfoService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import com.easychat.websocket.ChannelContextUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "用户信息模块", description = "个人信息查询与修改、改密、退出登录")
@RestController
@RequestMapping("/userInfo")
public class UserInfoController extends ABaseController {
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private ChannelContextUtils channelContextUtils;

    @Operation(summary = "获取当前用户信息", description = "需登录；data 为 UserInfoVO")
    @RequestMapping("/getUserInfo")
    @GlobalInterceptor
    public ResponseVO getUserInfo(HttpServletRequest request) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserInfo userInfo = userInfoService.getUserInfoByUserId(tokenUserInfoDto.getUserId());
        UserInfoVO userInfoVO = CopyTools.copy(userInfo, UserInfoVO.class);
        userInfoVO.setAdmin(tokenUserInfoDto.getAdmin());
        return getSuccessResponse(userInfoVO);
    }

    @Operation(summary = "保存/更新用户信息", description = "需登录；可同时上传头像与封面；返回更新后的 UserInfoVO")
    @RequestMapping("/saveUserInfo")
    @GlobalInterceptor
    public ResponseVO saveUserInfo(HttpServletRequest request, UserInfo userInfo,
                                  @Parameter(description = "头像文件") MultipartFile avatarFile,
                                  @Parameter(description = "头像封面文件") MultipartFile avatarCover) throws IOException {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        userInfo.setUserId(tokenUserInfoDto.getUserId());
        userInfo.setPassword(null);
        userInfo.setStatus(null);
        userInfo.setCreateTime(null);
        userInfo.setLastLoginTime(null);

        this.userInfoService.updateUserInfo(userInfo, avatarFile, avatarCover);

        return getUserInfo(request);
    }

    @Operation(summary = "修改密码", description = "需登录；改密成功后强制断开当前用户的 WebSocket 连接；data 为 null")
    @RequestMapping("/updatePassword")
    @GlobalInterceptor
    public ResponseVO updatePassword(HttpServletRequest request, @Parameter(description = "新密码，需满足 REGEX_PASSWORD 规则") @NotEmpty @Pattern(regexp = Constants.REGEX_PASSWORD) String password) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        UserInfo userInfo = new UserInfo();
        userInfo.setPassword(StringTools.encodeMd5(password));
        this.userInfoService.updateUserInfoByUserId(userInfo, tokenUserInfoDto.getUserId());
        channelContextUtils.closeContext(tokenUserInfoDto.getUserId());
        return getSuccessResponse(null);
    }

    @Operation(summary = "退出登录", description = "需登录；断开当前用户的 WebSocket 连接；data 为 null")
    @RequestMapping("/logout")
    @GlobalInterceptor
    public ResponseVO logout(HttpServletRequest request) {
        TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto(request);
        channelContextUtils.closeContext(tokenUserInfoDto.getUserId());
        return getSuccessResponse(null);
    }
}
