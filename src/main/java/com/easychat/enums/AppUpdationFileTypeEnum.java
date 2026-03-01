package com.easychat.enums;

public enum AppUpdationFileTypeEnum {
    LOCAL(0, "本地"),
    OUTER_LINK(1, "外链");

    AppUpdationFileTypeEnum(int type, String description) {
        this.type = type;
        this.description = description;
    }

    private Integer type;
    private String description;

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static AppUpdationFileTypeEnum getByType(Integer type) {
        for (AppUpdationFileTypeEnum typeEnum : AppUpdationFileTypeEnum.values()) {
            if (typeEnum.getType().equals(type)) {
                return typeEnum;
            }
        }
        return null;
    }
}
