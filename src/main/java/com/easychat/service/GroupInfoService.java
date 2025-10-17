package com.easychat.service;

import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Service
 * @author 'Tong'
 * @since 2025/10/17
 */
public interface GroupInfoService {
	// 根据条件查询列表
	List<GroupInfo> findListByParam(GroupInfoQuery query);

	// 根据条件查询总数
	Integer findCountByParam(GroupInfoQuery query);

	// 分页查询
	PaginationResultVO<GroupInfo> findPageByParam(GroupInfoQuery query);

	// 新增
	Integer add(GroupInfo bean);

	// 批量新增
	Integer addBatch(List<GroupInfo> listBean);

	// 批量新增或修改
	Integer addOrUpdateBatch(List<GroupInfo> listBean);

	// 根据GroupId查询
	GroupInfo getGroupInfoByGroupId(String groupId);

	// 根据GroupId更新
	Integer updateGroupInfoByGroupId(GroupInfo bean, String groupId);

	// 根据GroupId删除
	Integer deleteGroupInfoByGroupId(String groupId);

    void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException;
}