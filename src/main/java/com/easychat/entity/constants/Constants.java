package com.easychat.entity.constants;

import com.easychat.enums.UserContactTypeEnum;

public class Constants {
    public static final String REDIS_KEY_CHECK_CODE = "easychat:checkcode:";
    public static final String REDIS_KEY_WS_USER_HEART_BEAT = "easychat:ws:user:heart:beat:";
    public static final String REDIS_KEY_WS_TOKEN = "easychat:ws:token:";
    public static final String REDIS_KEY_WS_TOKEN_USERID = "easychat:ws:token:userid:";
    public static final String REDIS_KEY_SYS_SETTING = "easychat:sys:setting:";
    public static final Integer REDIS_TIME_1MIN = 60;
    public static final Integer REDIS_TIME_1DAY = 86400;

    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix() + "robot";

    public static final Integer LENGTH_11 = 11;
    public static final Integer LENGTH_20 = 20;

    public static final String FILE_FOLDER_FILE = "/file/";
    public static final String FILE_FOLDER_AVATAR = "/avatar/";

    public static final String IMAGE_SUFFIX = ".png";
    public static final String COVER_IMAGE_SUFFIX = "_cover.png";

    public static final String APPLY_INFO_TEMPLATE = "我是%s";

    public static final String REGEX_PASSWORD = "^(?=.*\\d)(?=.*[a-zA-Z])[0-9A-Za-z~!@#$%^&*_]{8,10}$";

    public static final String APP_UPDATE_FOLDER = "/appUpdate/";
    public static final String APP_EXE_SUFFIX = ".exe";
}
