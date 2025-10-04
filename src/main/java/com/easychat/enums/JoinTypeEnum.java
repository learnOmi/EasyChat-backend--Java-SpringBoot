package com.easychat.enums;

import com.easychat.utils.StringTools;

public enum JoinTypeEnum {
    JOIN(0, "直接加入"),
    APPLY(1, "需要审核");

    private int type;
    private String desc;

    JoinTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static JoinTypeEnum getByName(String name) {
        try {
            if (StringTools.isEmpty(name)) {
                return null;
            }
            return JoinTypeEnum.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static JoinTypeEnum getByType(int type) {
        for (JoinTypeEnum joinTypeEnum : JoinTypeEnum.values()) {
            if (joinTypeEnum.getType() == type) {
                return joinTypeEnum;
            }
        }
        return null;
    }
}
