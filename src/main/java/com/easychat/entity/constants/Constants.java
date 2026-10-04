package com.easychat.entity.constants;

import com.easychat.enums.UserContactTypeEnum;

public class Constants {
    public static final String REDIS_KEY_CHECK_CODE = "easychat:checkcode:";
    public static final String REDIS_KEY_WS_USER_HEART_BEAT = "easychat:ws:user:heart:beat:";
    public static final String REDIS_KEY_WS_TOKEN = "easychat:ws:token:";
    public static final String REDIS_KEY_WS_TOKEN_USERID = "easychat:ws:token:userid:";
    public static final String REDIS_KEY_SYS_SETTING = "easychat:sys:setting:";
    public static final String REDIS_KEY_USER_CONTACT = "easychat:ws:user:contact:";
    public static final Integer REDIS_TIME_1MIN = 60;
    public static final Integer REDIS_TIME_1DAY = 86400;
    public static final Integer REDIS_TIME_2DAY = REDIS_TIME_1DAY * 2;

    /** 客户端心跳间隔（秒），需与前端 easy-chat/src/main/wsClient.js 中的 HEARTBEAT_INTERVAL 保持一致 */
    public static final Integer WS_HEART_BEAT_INTERVAL_SECONDS = 5;
    /**
     * 服务端空闲判定阈值（秒）：取心跳间隔的 4 倍，可容忍连续 3 次心跳延迟或丢失。
     * 阈值必须显著大于心跳间隔，否则一次心跳抖动就会触发误判（原实现为 6 秒，仅比 5 秒心跳多 1 秒余量）。
     */
    public static final Integer WS_IDLE_TIMEOUT_SECONDS = WS_HEART_BEAT_INTERVAL_SECONDS * 4;
    /**
     * 心跳（登录态）键过期时间（秒）：与服务端空闲阈值保持一致，
     * 避免在线状态在两次心跳之间闪断（原实现 6 秒，会出现 1 秒的“假离线”窗口）。
     */
    public static final Integer REDIS_KEY_EXPIRES_HEART_BEAT = WS_IDLE_TIMEOUT_SECONDS;

    /** 客户端心跳上行内容（纯文本，非 JSON） */
    public static final String WS_HEART_BEAT_CONTENT = "heart beat";
    /** 服务端心跳下行应答内容（纯文本，非 JSON），用于向客户端证明链路存活 */
    public static final String WS_HEART_BEAT_REPLY = "heart";

    public static final String ROBOT_UID = UserContactTypeEnum.USER.getPrefix() + "robot";

    public static final Integer LENGTH_11 = 11;
    public static final Integer LENGTH_20 = 20;

    public static final String FILE_FOLDER_FILE = "file/";
    public static final String FILE_FOLDER_AVATAR = "avatar/";
    public static final Long FILE_SIZE_MB = 1024 * 1024L;

    public static final String IMAGE_SUFFIX = ".png";
    public static final String COVER_IMAGE_SUFFIX = "_cover.png";
    public static final String[] IMAGE_SUFFIX_LIST = new String[]{".jpeg", ".jpg", ".png", ".gif", ".bmp", ".webp"};
    public static final String[] VIDEO_SUFFIX_LIST = new String[]{".mp4", ".avi", ".mov", ".mkv", ".flv", ".rmvb"};

    public static final String APPLY_INFO_TEMPLATE = "我是%s";

    public static final String REGEX_PASSWORD = "^(?=.*\\d)(?=.*[a-zA-Z])[0-9A-Za-z~!@#$%^&*_]{8,10}$";

    public static final String APP_UPDATE_FOLDER = "appUpdate/";
    public static final String APP_EXE_SUFFIX = ".exe";
    public static final String APP_NAME = "EasyChat";

    public static final Long MILLLSSECONDS_3DAY = 3 * 24 * 60 * 60 * 1000L;

    public static final Integer ZERO = 0;
    public static final Integer ONE = 1;
}
