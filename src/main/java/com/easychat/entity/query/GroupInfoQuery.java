package com.easychat.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;
/**
 * 查询
 * @author 'Tong'
 * @since 2025/10/17
 */
@Schema(description = "群组信息查询")
public class GroupInfoQuery extends BaseQuery {
	// 群ID
	@Schema(description = "群ID")
	private String groupId;
	@Schema(description = "群ID（模糊查询）")
	private String groupIdFuzzy;

	// 群组名
	@Schema(description = "群组名")
	private String groupName;
	@Schema(description = "群组名（模糊查询）")
	private String groupNameFuzzy;

	// 群主id
	@Schema(description = "群主ID")
	private String groupOwnerId;
	@Schema(description = "群主ID（模糊查询）")
	private String groupOwnerIdFuzzy;

	// 创建时间
	@Schema(description = "创建时间")
	private Date createTime;
	@Schema(description = "创建时间起始")
	private String createTimeStart;

	@Schema(description = "创建时间结束")
	private String createTimeEnd;

	// 群公告
	@Schema(description = "群公告")
	private String groupNotice;
	@Schema(description = "群公告（模糊查询）")
	private String groupNoticeFuzzy;

	//  0 :直接加入 1 :管理员同意后加入 
	@Schema(description = "0:直接加入 1:管理员同意后加入")
	private Byte joinType;
	// 状态1:正常0:解散
	@Schema(description = "状态1:正常0:解散")
	private Byte status;

    @Schema(description = "是否联查群主名称")
    private Boolean queryGroupOwnerName;

    @Schema(description = "是否联查成员数量")
    private Boolean queryMemberCount;

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupOwnerId(String groupOwnerId) {
		this.groupOwnerId = groupOwnerId;
	}

	public String getGroupOwnerId() {
		return groupOwnerId;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setGroupNotice(String groupNotice) {
		this.groupNotice = groupNotice;
	}

	public String getGroupNotice() {
		return groupNotice;
	}

	public void setJoinType(Byte joinType) {
		this.joinType = joinType;
	}

	public Byte getJoinType() {
		return joinType;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	public void setGroupIdFuzzy(String groupIdFuzzy) {
		this.groupIdFuzzy = groupIdFuzzy;
	}

	public String getGroupIdFuzzy() {
		return groupIdFuzzy;
	}

	public void setGroupNameFuzzy(String groupNameFuzzy) {
		this.groupNameFuzzy = groupNameFuzzy;
	}

	public String getGroupNameFuzzy() {
		return groupNameFuzzy;
	}

	public void setGroupOwnerIdFuzzy(String groupOwnerIdFuzzy) {
		this.groupOwnerIdFuzzy = groupOwnerIdFuzzy;
	}

	public String getGroupOwnerIdFuzzy() {
		return groupOwnerIdFuzzy;
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

	public void setGroupNoticeFuzzy(String groupNoticeFuzzy) {
		this.groupNoticeFuzzy = groupNoticeFuzzy;
	}

	public String getGroupNoticeFuzzy() {
		return groupNoticeFuzzy;
	}

    public void setQueryGroupOwnerName(Boolean queryGroupOwnerName) {
        this.queryGroupOwnerName = queryGroupOwnerName;
    }
    public Boolean getQueryGroupOwnerName() {
        return queryGroupOwnerName;
    }

    public void setQueryMemberCount(Boolean queryMemberCount) {
        this.queryMemberCount = queryMemberCount;
    }

    public Boolean getQueryMemberCount() {
        return queryMemberCount;
    }
}