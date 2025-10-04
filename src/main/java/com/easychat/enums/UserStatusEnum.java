package com.easychat.enums;

public enum UserStatusEnum {
    DISABLED(0, "禁用"),
    ENABLED(1, "启用");

    private int status;
    private String desc;

    UserStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static UserStatusEnum getEnumByStatus(int status) {
        for (UserStatusEnum statusEnum : UserStatusEnum.values()) {
            if (statusEnum.getStatus() == status) {
                return statusEnum;
            }
        }
        return null;
    }
}
