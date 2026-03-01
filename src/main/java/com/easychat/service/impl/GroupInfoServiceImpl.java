package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.po.GroupInfo;
import com.easychat.entity.po.UserContact;
import com.easychat.entity.query.GroupInfoQuery;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.query.UserContactQuery;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.enums.*;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.GroupInfoMapper;
import com.easychat.mapper.UserContactMapper;
import com.easychat.redis.RedisComponent;
import com.easychat.service.GroupInfoService;
import com.easychat.utils.StringTools;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * ServiceImpl
 * @author 'Tong'
 * @since 2025/10/17
 */
@Service("groupInfoService")
public class GroupInfoServiceImpl implements GroupInfoService {
	@Resource
	private GroupInfoMapper<GroupInfo, GroupInfoQuery> groupInfoMapper;
    @Resource
    private UserContactMapper<UserContact, UserContactQuery> userContactMapper;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private AppConfig appConfig;

	// 根据条件查询列表
	public List<GroupInfo> findListByParam(GroupInfoQuery query) {
		return this.groupInfoMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(GroupInfoQuery query) {
		return this.groupInfoMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<GroupInfo> findPageByParam(GroupInfoQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<GroupInfo> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(GroupInfo bean) {
		return this.groupInfoMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<GroupInfo> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.groupInfoMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<GroupInfo> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.groupInfoMapper.insertOrUpdateBatch(listBean);
	}

	// 根据GroupId查询
	public GroupInfo getGroupInfoByGroupId(String groupId) {
		return this.groupInfoMapper.selectByGroupId(groupId);
	}

	// 根据GroupId更新
	public Integer updateGroupInfoByGroupId(GroupInfo bean, String groupId) {
		return this.groupInfoMapper.updateByGroupId(bean, groupId);
	}

	// 根据GroupId删除
	public Integer deleteGroupInfoByGroupId(String groupId) {
		return this.groupInfoMapper.deleteByGroupId(groupId);
	}

    @Transactional(rollbackFor = Exception.class)
    public void saveGroup(GroupInfo groupInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException {
        Date currentDate = new Date();

        // 新增
        if (StringTools.isEmpty(groupInfo.getGroupId())) {
            GroupInfoQuery groupInfoQuery = new GroupInfoQuery();
            groupInfoQuery.setGroupId(groupInfo.getGroupId());
            Integer count = this.groupInfoMapper.selectCount(groupInfoQuery);
            SysSettingDto sysSettingDto = redisComponent.getSysSetting();
            if (count > sysSettingDto.getMaxGroupCount()) {
                throw new BusinessException("最多支持能创建" + sysSettingDto.getMaxGroupCount() + "个群聊");
            }
            if (null == avatarFile) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }

            groupInfo.setCreateTime(currentDate);
            groupInfo.setGroupId(StringTools.getGroupId());
            this.groupInfoMapper.insert(groupInfo);

            // 将群组添加为联系人
            UserContact userContact = new UserContact();
            userContact.setStatus(UserContactStatusEnum.FRIEND.getStatus().byteValue());
            userContact.setCreateTime(currentDate);
            userContact.setLastUpdateTime(currentDate);
            userContact.setUserId(groupInfo.getGroupOwnerId());
            userContact.setContactId(groupInfo.getGroupId());
            userContact.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());
            this.userContactMapper.insert(userContact);
        } else {
            GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupInfo.getGroupId());
            if (!dbInfo.getGroupOwnerId().equals(groupInfo.getGroupOwnerId())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            this.groupInfoMapper.updateByGroupId(groupInfo, groupInfo.getGroupId());
        }
        String baseFolder = appConfig.getProjectFolder() + Constants.FILE_FOLDER_FILE;
        File targetFileFolder = new File(baseFolder + Constants.FILE_FOLDER_AVATAR);
        if (!targetFileFolder.exists()) {
            targetFileFolder.mkdirs();

        }
        String filePath = targetFileFolder.getPath() + "/" + groupInfo.getGroupId() + Constants.IMAGE_SUFFIX;
        avatarFile.transferTo(new File(filePath));
        avatarCover.transferTo(new File(filePath + Constants.COVER_IMAGE_SUFFIX));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void dissolutionGroup(String groupOwnerId, String groupId) {
        GroupInfo dbInfo = this.groupInfoMapper.selectByGroupId(groupId);
        if (null == dbInfo || !dbInfo.getGroupOwnerId().equals(groupOwnerId)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        // 删除群组
        GroupInfo updateInfo = new GroupInfo();
        updateInfo.setStatus(GroupStatusEnum.DISSOLUTION.getStatus().byteValue());
        this.groupInfoMapper.updateByGroupId(updateInfo, groupId);

        // 更新联系人信息
        UserContactQuery userContactQuery = new UserContactQuery();
        userContactQuery.setContactId(groupId);
        userContactQuery.setContactType(UserContactTypeEnum.GROUP.getType().byteValue());

        UserContact updateUserContact = new UserContact();
        updateUserContact.setStatus(UserContactStatusEnum.DEL.getStatus().byteValue());
        this.userContactMapper.updateByParam(updateUserContact, userContactQuery);

        //TODO 移除相关群员的联系人缓存

        //TODO 发消息：更新会话信息，记录群消息，发送解散通知消息
    }
}