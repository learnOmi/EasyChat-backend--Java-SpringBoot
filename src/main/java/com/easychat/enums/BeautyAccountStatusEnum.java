package com.easychat.enums;

public enum BeautyAccountStatusEnum {
    NO_USE(0, "未使用"),
    USED(1, "已使用");

    private int status;
    private String desc;

    BeautyAccountStatusEnum(int status, String desc) {
        this.status = status;
        this.desc = desc;
    }

    public int getStatus() {
        return status;
    }

    public String getDesc() {
        return desc;
    }

    public static BeautyAccountStatusEnum getEnumByStatus(int status) {
        for (BeautyAccountStatusEnum statusEnum : BeautyAccountStatusEnum.values()) {
            if (statusEnum.getStatus() == (status)) {
                return statusEnum;
            }
        }
        return null;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
