package com.easychat.entity.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "令牌用户信息")
public class TokenUserInfoDto implements Serializable {
    private static final long serialVersionUID = -3244262035649152692L;

    @Schema(description = "用户ID")
    private String userId;
    @Schema(description = "昵称")
    private String nickName;
    @Schema(description = "登录令牌")
    private String token;
    @Schema(description = "是否管理员")
    private Boolean admin;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
