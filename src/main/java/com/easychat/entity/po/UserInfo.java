package com.easychat.entity.po;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easychat.utils.DateUtils;
import com.easychat.enums.DateTimePatternEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * 用户信息表
 * @author 'Tong'
 * @since 2025/09/28
 */
public class UserInfo implements Serializable {
	// 用户id
	private String userId;
	// 邮箱
	private String email;
	// 昵称
	private String nickName;
	// 0:直接加入 1:同意后加入
	private Byte joinType;
	// 性别 0:女 1:男
	private Byte sex;
	// 密码
	private String password;
	// 个性签名
	private String personalSignature;
	// 状态
	@JsonIgnore
	private Byte status;
	// 创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	// 最后登录时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date lastLoginTime;
	// 地区
	private String areaName;
	// 地区编号
	private String areaCode;
	// 最后离开时间
	private Long lastOffTime;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setJoinType(Byte joinType) {
		this.joinType = joinType;
	}

	public Byte getJoinType() {
		return joinType;
	}

	public void setSex(Byte sex) {
		this.sex = sex;
	}

	public Byte getSex() {
		return sex;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPersonalSignature(String personalSignature) {
		this.personalSignature = personalSignature;
	}

	public String getPersonalSignature() {
		return personalSignature;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setLastOffTime(Long lastOffTime) {
		this.lastOffTime = lastOffTime;
	}

	public Long getLastOffTime() {
		return lastOffTime;
	}

	@Override
	public String toString() {
		return "UserInfo [" +
			"userId=" + (userId == null ? "空" : userId) + ", " +
			"email=" + (email == null ? "空" : email) + ", " +
			"nickName=" + (nickName == null ? "空" : nickName) + ", " +
			"joinType=" + (joinType == null ? "空" : joinType) + ", " +
			"sex=" + (sex == null ? "空" : sex) + ", " +
			"password=" + (password == null ? "空" : password) + ", " +
			"personalSignature=" + (personalSignature == null ? "空" : personalSignature) + ", " +
			"status=" + (status == null ? "空" : status) + ", " +
			"createTime=" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ", " +
			"lastLoginTime=" + (lastLoginTime == null ? "空" : DateUtils.format(lastLoginTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ", " +
			"areaName=" + (areaName == null ? "空" : areaName) + ", " +
			"areaCode=" + (areaCode == null ? "空" : areaCode) + ", " +
			"lastOffTime=" + (lastOffTime == null ? "空" : lastOffTime) + 
			"]";
	}
}