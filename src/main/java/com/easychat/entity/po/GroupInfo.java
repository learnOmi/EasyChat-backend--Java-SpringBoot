package com.easychat.entity.po;

import com.easychat.enums.DateTimePatternEnum;
import com.easychat.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 * @author 'Tong'
 * @since 2025/10/17
 */
public class GroupInfo implements Serializable {
	// 群ID
	private String groupId;
	// 群组名
	private String groupName;
	// 群主id
	private String groupOwnerId;
	// 创建时间
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	// 群公告
	private String groupNotice;
	//  0 :直接加入 1 :管理员同意后加入 
	private Byte joinType;
	// 状态1:正常0:解散
	@JsonIgnore
	private Byte status;
    // 成员数
    private Integer memberCount;
    private String groupOwnerNickName;

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

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public String getGroupOwnerNickName() {
        return groupOwnerNickName;
    }

    public void setGroupOwnerNickName(String groupOwnerNickName) {
        this.groupOwnerNickName = groupOwnerNickName;
    }

	@Override
	public String toString() {
		return "GroupInfo [" +
			"groupId=" + (groupId == null ? "空" : groupId) + ", " +
			"groupName=" + (groupName == null ? "空" : groupName) + ", " +
			"groupOwnerId=" + (groupOwnerId == null ? "空" : groupOwnerId) + ", " +
			"createTime=" + (createTime == null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ", " +
			"groupNotice=" + (groupNotice == null ? "空" : groupNotice) + ", " +
			"joinType=" + (joinType == null ? "空" : joinType) + ", " +
			"status=" + (status == null ? "空" : status) + "," +
            "memberCount=" + (memberCount == null ? "空" : memberCount) +
			"]";
	}
}