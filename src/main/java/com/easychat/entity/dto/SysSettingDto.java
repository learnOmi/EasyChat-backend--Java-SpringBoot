package com.easychat.entity.dto;

import com.easychat.entity.constants.Constants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "系统设置")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SysSettingDto implements Serializable {
    private static final long serialVersionUID = -4052701002357141142L;

    @Schema(description = "最大群组数量")
    private Integer maxGroupCount = 5;
    @Schema(description = "最大群组成员数量")
    private Integer maxGroupMemberCount = 500;
    @Schema(description = "最大图片大小")
    private Integer maxImageSize = 2;
    @Schema(description = "最大文件大小")
    private Integer maxFileSize = 5;
    @Schema(description = "最大视频大小")
    private Integer maxVideoSize = 5;
    @Schema(description = "机器人用户ID")
    private String robotUid = Constants.ROBOT_UID;
    @Schema(description = "机器人昵称")
    private String robotNickName = "EasyChat";
    @Schema(description = "机器人欢迎语")
    private String robotWelcome = "Hi~";

    public Integer getMaxGroupCount() {
        return maxGroupCount;
    }

    public void setMaxGroupCount(Integer maxGroupCount) {
        this.maxGroupCount = maxGroupCount;
    }

    public Integer getMaxGroupMemberCount() {
        return maxGroupMemberCount;
    }

    public void setMaxGroupMemberCount(Integer maxGroupMemberCount) {
        this.maxGroupMemberCount = maxGroupMemberCount;
    }

    public Integer getMaxImageSize() {
        return maxImageSize;
    }

    public void setMaxImageSize(Integer maxImageSize) {
        this.maxImageSize = maxImageSize;
    }

    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    public void setMaxFileSize(Integer maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public Integer getMaxVideoSize() {
        return maxVideoSize;
    }

    public void setMaxVideoSize(Integer maxVideoSize) {
        this.maxVideoSize = maxVideoSize;
    }

    public String getRobotUid() {
        return robotUid;
    }

    public void setRobotUid(String robotUid) {
        this.robotUid = robotUid;
    }

    public String getRobotNickName() {
        return robotNickName;
    }

    public void setRobotNickName(String robotNickName) {
        this.robotNickName = robotNickName;
    }

    public String getRobotWelcome() {
        return robotWelcome;
    }

    public void setRobotWelcome(String robotWelcome) {
        this.robotWelcome = robotWelcome;
    }
}
