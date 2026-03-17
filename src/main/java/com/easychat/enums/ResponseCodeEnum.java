package com.easychat.enums;

public enum ResponseCodeEnum {
    CODE_200(200, "成功"),
    CODE_500(500, "系统异常"),
    CODE_404(404, "请求资源不存在"),
    CODE_403(403, "无权限访问"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已存在"),
    CODE_602(602, "文件不存在"),
    CODE_901(901, "登录超时"),
    CODE_902(902, "您不是对方的好友，请先向好友发送好友申请"),
    CODE_903(903, "您已不在群聊，请重新加入群聊");

    private Integer code;
    private String msg;

    ResponseCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
