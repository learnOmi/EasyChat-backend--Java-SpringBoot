package com.easychat.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
/**
 * 用户信息表查询
 * @author 'Tong'
 * @since 2025/09/28
 */
@Schema(description = "用户信息表查询")
public class UserInfoQuery extends BaseQuery {
	// 用户id
	@Schema(description = "用户ID")
	private String userId;
    @Schema(description = "用户ID（模糊查询）")
    private String userIdFuzzy;
	// 邮箱
	@Schema(description = "邮箱")
	private String email;
	@Schema(description = "邮箱（模糊查询）")
	private String emailFuzzy;

	// 昵称
	@Schema(description = "昵称")
	private String nickName;
	@Schema(description = "昵称（模糊查询）")
	private String nickNameFuzzy;

	// 0:直接加入 1:同意后加入
	@Schema(description = "0:直接加入 1:同意后加入")
	private Byte joinType;
	// 性别 0:女 1:男
	@Schema(description = "性别 0:女 1:男")
	private Byte sex;
	// 密码
	@Schema(description = "密码")
	private String password;
	@Schema(description = "密码（模糊查询）")
	private String passwordFuzzy;

	// 个性签名
	@Schema(description = "个性签名")
	private String personalSignature;
	@Schema(description = "个性签名（模糊查询）")
	private String personalSignatureFuzzy;

	// 状态
	@Schema(description = "状态")
	private Byte status;
	// 创建时间
	@Schema(description = "创建时间")
	private Date createTime;
	@Schema(description = "创建时间起始")
	private String createTimeStart;

	@Schema(description = "创建时间结束")
	private String createTimeEnd;

	// 最后登录时间
	@Schema(description = "最后登录时间")
	private Date lastLoginTime;
	@Schema(description = "最后登录时间起始")
	private String lastLoginTimeStart;

	@Schema(description = "最后登录时间结束")
	private String lastLoginTimeEnd;

	// 地区
	@Schema(description = "地区")
	private String areaName;
	@Schema(description = "地区（模糊查询）")
	private String areaNameFuzzy;

	// 地区编号
	@Schema(description = "地区编号")
	private String areaCode;
	@Schema(description = "地区编号（模糊查询）")
	private String areaCodeFuzzy;

	// 最后离开时间
	@Schema(description = "最后离开时间")
	private Long lastOffTime;
	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

    public void setUserIdFuzzy(String userIdFuzzy) {
        this.userIdFuzzy = userIdFuzzy;
    }

    public String getUserIdFuzzy() {
        return userIdFuzzy;
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

	public void setEmailFuzzy(String emailFuzzy) {
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy() {
		return emailFuzzy;
	}

	public void setNickNameFuzzy(String nickNameFuzzy) {
		this.nickNameFuzzy = nickNameFuzzy;
	}

	public String getNickNameFuzzy() {
		return nickNameFuzzy;
	}

	public void setPasswordFuzzy(String passwordFuzzy) {
		this.passwordFuzzy = passwordFuzzy;
	}

	public String getPasswordFuzzy() {
		return passwordFuzzy;
	}

	public void setPersonalSignatureFuzzy(String personalSignatureFuzzy) {
		this.personalSignatureFuzzy = personalSignatureFuzzy;
	}

	public String getPersonalSignatureFuzzy() {
		return personalSignatureFuzzy;
	}

	public void setCreateTimeStart(String createTimeStart) {
		this.createTimeStart = createTimeStart;
	}

	public String getCreateTimeStart() {
		return createTimeStart;
	}

	public void setCreateTimeEnd(String createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	public String getCreateTimeEnd() {
		return createTimeEnd;
	}

	public void setLastLoginTimeStart(String lastLoginTimeStart) {
		this.lastLoginTimeStart = lastLoginTimeStart;
	}

	public String getLastLoginTimeStart() {
		return lastLoginTimeStart;
	}

	public void setLastLoginTimeEnd(String lastLoginTimeEnd) {
		this.lastLoginTimeEnd = lastLoginTimeEnd;
	}

	public String getLastLoginTimeEnd() {
		return lastLoginTimeEnd;
	}

	public void setAreaNameFuzzy(String areaNameFuzzy) {
		this.areaNameFuzzy = areaNameFuzzy;
	}

	public String getAreaNameFuzzy() {
		return areaNameFuzzy;
	}

	public void setAreaCodeFuzzy(String areaCodeFuzzy) {
		this.areaCodeFuzzy = areaCodeFuzzy;
	}

	public String getAreaCodeFuzzy() {
		return areaCodeFuzzy;
	}

}