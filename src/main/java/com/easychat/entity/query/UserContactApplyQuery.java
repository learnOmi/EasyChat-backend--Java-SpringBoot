package com.easychat.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 联系人申请查询
 * @author 'Tong'
 * @since 2025/10/17
 */
@Schema(description = "联系人申请查询")
public class UserContactApplyQuery extends BaseQuery {
	// 自增ID
	@Schema(description = "自增ID")
	private Integer applyId;
	// 申请人id
	@Schema(description = "申请人ID")
	private String applyUserId;
	@Schema(description = "申请人ID（模糊查询）")
	private String applyUserIdFuzzy;

	// 接收人ID
	@Schema(description = "接收人ID")
	private String receiveUserId;
	@Schema(description = "接收人ID（模糊查询）")
	private String receiveUserIdFuzzy;

	// 联系人类型0:好友1:群组
	@Schema(description = "联系人类型0:好友1:群组")
	private Byte contactType;
	// 联系人群组ID
	@Schema(description = "联系人群组ID")
	private String contactId;
	@Schema(description = "联系人群组ID（模糊查询）")
	private String contactIdFuzzy;

	// 最后申请时间
	@Schema(description = "最后申请时间")
	private Long lastApplyTime;
	// 状态0:待处理1:已同意2:已拒绝3:已拉黑
	@Schema(description = "状态0:待处理1:已同意2:已拒绝3:已拉黑")
	private Byte status;
	// 申请信息
	@Schema(description = "申请信息")
	private String applyInfo;
	@Schema(description = "申请信息（模糊查询）")
	private String applyInfoFuzzy;

    @Schema(description = "是否联查联系人信息")
    private Boolean queryContactInfo;

    @Schema(description = "最后申请时间戳")
    private Long lastApplyTimestamp;

    public Long getLastApplyTimestamp() {
        return lastApplyTimestamp;
    }

    public void setLastApplyTimestamp(Long lastApplyTimestamp) {
        this.lastApplyTimestamp = lastApplyTimestamp;
    }

    public void setApplyId(Integer applyId) {
		this.applyId = applyId;
	}

	public Integer getApplyId() {
		return applyId;
	}

	public void setApplyUserId(String applyUserId) {
		this.applyUserId = applyUserId;
	}

	public String getApplyUserId() {
		return applyUserId;
	}

	public void setReceiveUserId(String receiveUserId) {
		this.receiveUserId = receiveUserId;
	}

	public String getReceiveUserId() {
		return receiveUserId;
	}

	public void setContactType(Byte contactType) {
		this.contactType = contactType;
	}

	public Byte getContactType() {
		return contactType;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public String getContactId() {
		return contactId;
	}

	public void setLastApplyTime(Long lastApplyTime) {
		this.lastApplyTime = lastApplyTime;
	}

	public Long getLastApplyTime() {
		return lastApplyTime;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public Byte getStatus() {
		return status;
	}

	public void setApplyInfo(String applyInfo) {
		this.applyInfo = applyInfo;
	}

	public String getApplyInfo() {
		return applyInfo;
	}

	public void setApplyUserIdFuzzy(String applyUserIdFuzzy) {
		this.applyUserIdFuzzy = applyUserIdFuzzy;
	}

	public String getApplyUserIdFuzzy() {
		return applyUserIdFuzzy;
	}

	public void setReceiveUserIdFuzzy(String receiveUserIdFuzzy) {
		this.receiveUserIdFuzzy = receiveUserIdFuzzy;
	}

	public String getReceiveUserIdFuzzy() {
		return receiveUserIdFuzzy;
	}

	public void setContactIdFuzzy(String contactIdFuzzy) {
		this.contactIdFuzzy = contactIdFuzzy;
	}

	public String getContactIdFuzzy() {
		return contactIdFuzzy;
	}

	public void setApplyInfoFuzzy(String applyInfoFuzzy) {
		this.applyInfoFuzzy = applyInfoFuzzy;
	}

	public String getApplyInfoFuzzy() {
		return applyInfoFuzzy;
	}

    public Boolean getQueryContactInfo() {
        return queryContactInfo;
    }

    public void setQueryContactInfo(Boolean queryContactInfo) {
        this.queryContactInfo = queryContactInfo;
    }
}