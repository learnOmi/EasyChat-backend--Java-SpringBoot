package com.easychat.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
/**
 * 联系人查询
 * @author 'Tong'
 * @since 2025/10/17
 */
@Schema(description = "联系人查询")
public class UserContactQuery extends BaseQuery {
	// 用户ID
	@Schema(description = "用户ID")
	private String userId;
	@Schema(description = "用户ID（模糊查询）")
	private String userIdFuzzy;

	// 联系人ID或者群组ID
	@Schema(description = "联系人ID或者群组ID")
	private String contactId;
	@Schema(description = "联系人ID或者群组ID（模糊查询）")
	private String contactIdFuzzy;

	// 联系人类型0:好友1:群组
	@Schema(description = "联系人类型0:好友1:群组")
	private Byte contactType;
	// 创建时间
	@Schema(description = "创建时间")
	private Date createTime;
	@Schema(description = "创建时间起始")
	private String createTimeStart;

	@Schema(description = "创建时间结束")
	private String createTimeEnd;

	// 状态0:非好友 1:好友 2:已删除好友 3:被好友删除 4: 已拉黑好友 5: 被好友拉黑
	@Schema(description = "状态0:非好友 1:好友 2:已删除好友 3:被好友删除 4: 已拉黑好友 5: 被好友拉黑")
	private Byte status;
	// 最后更新时间
	@Schema(description = "最后更新时间")
	private Date lastUpdateTime;
	@Schema(description = "最后更新时间起始")
	private String lastUpdateTimeStart;

	@Schema(description = "最后更新时间结束")
	private String lastUpdateTimeEnd;

    @Schema(description = "是否联查用户信息")
    private Boolean queryUserInfo;

    @Schema(description = "是否联查群组信息")
    private Boolean queryGroupInfo;

    @Schema(description = "是否联查联系人用户信息")
    private Boolean queryContactUserInfo;

    @Schema(description = "是否排除我的群组")
    private Boolean excludeMyGroup;

    @Schema(description = "状态数组")
    private Integer[] statusArray;

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactType(Byte contactType) {
		this.contactType = contactType;
	}

	public Byte getContactType() {
		return contactType;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setUserIdFuzzy(String userIdFuzzy) {
		this.userIdFuzzy = userIdFuzzy;
	}

	public String getUserIdFuzzy() {
		return userIdFuzzy;
	}

	public void setContactIdFuzzy(String contactIdFuzzy) {
		this.contactIdFuzzy = contactIdFuzzy;
	}

	public String getContactIdFuzzy() {
		return contactIdFuzzy;
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

	public void setLastUpdateTimeStart(String lastUpdateTimeStart) {
		this.lastUpdateTimeStart = lastUpdateTimeStart;
	}

	public String getLastUpdateTimeStart() {
		return lastUpdateTimeStart;
	}

	public void setLastUpdateTimeEnd(String lastUpdateTimeEnd) {
		this.lastUpdateTimeEnd = lastUpdateTimeEnd;
	}

	public String getLastUpdateTimeEnd() {
		return lastUpdateTimeEnd;
	}

    public Boolean getQueryUserInfo() {
        return queryUserInfo;
    }

    public void setQueryUserInfo(Boolean queryUserInfo) {
        this.queryUserInfo = queryUserInfo;
    }

    public void setQueryGroupInfo(Boolean queryGroupInfo) {
        this.queryGroupInfo = queryGroupInfo;
    }

    public Boolean getQueryGroupInfo() {
        return queryGroupInfo;
    }

    public Boolean getQueryContactUserInfo() {
        return queryContactUserInfo;
    }

    public void setQueryContactUserInfo(Boolean queryContactUserInfo) {
        this.queryContactUserInfo = queryContactUserInfo;
    }

    public Boolean getExcludeMyGroup() {
        return excludeMyGroup;
    }

    public void setExcludeMyGroup(Boolean excludeMyGroup) {
        this.excludeMyGroup = excludeMyGroup;
    }

    public Integer[] getStatusArray() {
        return statusArray;
    }

    public void setStatusArray(Integer[] statusArray) {
        this.statusArray = statusArray;
    }
}