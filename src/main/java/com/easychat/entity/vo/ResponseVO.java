package com.easychat.entity.vo;

public class ResponseVO<T> {
    private Integer code;
    private String message;
    private String success;
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
