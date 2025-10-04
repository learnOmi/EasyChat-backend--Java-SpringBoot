package com.easychat.entity.query;

/**
 * 靓号表查询
 * @author 'Tong'
 * @since 2025/09/28
 */
public class UserInfoBeautyQuery extends BaseQuery {
	// 
	private Integer id;
	// 邮箱
	private String email;
	private String emailFuzzy;

	// 用户id
	private String userId;
    private String userIdFuzzy;
	// 0: 未启用 1:启用
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

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	public void setEmailFuzzy(String emailFuzzy) {
		this.emailFuzzy = emailFuzzy;
	}

	public String getEmailFuzzy() {
		return emailFuzzy;
	}

}