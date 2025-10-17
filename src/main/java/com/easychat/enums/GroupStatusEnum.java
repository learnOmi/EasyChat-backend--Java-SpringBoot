package com.easychat.enums;

public enum GroupStatusEnum {
    NORMAL(1, "正常"),
    DISSOLUTION(2, "解散");

    private Integer status;
    private String desc;

    GroupStatusEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public static GroupStatusEnum getEnumByStatus(Integer status) {
        for (GroupStatusEnum groupStatusEnum : GroupStatusEnum.values()) {
            if (groupStatusEnum.getStatus().equals(status)) {
                return groupStatusEnum;
            }
        }
        return null;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }
}
