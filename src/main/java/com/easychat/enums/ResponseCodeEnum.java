package com.easychat.enums;

public enum ResponseCodeEnum {
    CODE_200(200, "成功"),
    CODE_500(500, "系统异常"),
    CODE_404(404, "请求资源不存在"),
    CODE_403(403, "无权限访问"),
    CODE_600(600, "请求参数错误"),
    CODE_601(601, "信息已存在");

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
