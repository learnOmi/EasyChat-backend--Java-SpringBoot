package com.easychat.entity.po;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 靓号表
 * @author 'Tong'
 * @since 2025/09/28
 */
public class UserInfoBeauty implements Serializable {
	// 
	private Integer id;
	// 邮箱
	private String email;
	// 用户id
	private Integer userId;
	// 0: 未启用 1:启用
	@JsonIgnore
	private Byte status;

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	@Override
	public String toString() {
		return "UserInfoBeauty [" +
			"id=" + (id == null ? "空" : id) + ", " +
			"email=" + (email == null ? "空" : email) + ", " +
			"userId=" + (userId == null ? "空" : userId) + ", " +
			"status=" + (status == null ? "空" : status) + 
			"]";
	}
}