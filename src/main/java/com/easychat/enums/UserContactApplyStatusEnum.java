package com.easychat.enums;

import com.easychat.utils.StringTools;

public enum UserContactApplyStatusEnum {
    INIT(0, "待处理"),
    PASS(1, "已通过"),
    REJECT(2, "已拒绝"),
    BLACKLIST(3, "黑名单");

    private Integer status;
    private String desc;

    UserContactApplyStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static UserContactApplyStatusEnum getByStatus(Integer status) {
        for (UserContactApplyStatusEnum e : UserContactApplyStatusEnum.values()) {
            if (e.getStatus().equals(status))
                return e;
        }
        return null;
    }

    public static UserContactApplyStatusEnum getByStatus(String status) {
        try {
            if (StringTools.isEmpty(status)) {
                return null;
            }
            return UserContactApplyStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
