package com.easychat.enums;

public enum AppUpdationStatusEnum {
    INIT(0, "未发布"), GRAYSCALE(1, "灰度发布"), ALL(2, "全网发布");

    private Integer status;
    private String description;

    AppUpdationStatusEnum(Integer status, String description) {
        this.status = status;
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }


    public static AppUpdationStatusEnum getByStatus(Integer status) {
        for (AppUpdationStatusEnum statusEnum : AppUpdationStatusEnum.values()) {
            if (statusEnum.getStatus().equals(status)) {
                return statusEnum;
            }
        }
        return null;
    }
}
