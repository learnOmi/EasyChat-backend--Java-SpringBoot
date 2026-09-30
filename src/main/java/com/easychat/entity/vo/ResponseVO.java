package com.easychat.entity.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "响应结果")
public class ResponseVO<T> {
    @Schema(description = "响应码")
    private Integer code;
    @Schema(description = "提示信息")
    private String message;
    @Schema(description = "是否成功")
    private String success;
    @Schema(description = "响应数据")
    private T data;

    public String getStatus() { return success; }
    public void setStatus(String success) { this.success = success; }
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
