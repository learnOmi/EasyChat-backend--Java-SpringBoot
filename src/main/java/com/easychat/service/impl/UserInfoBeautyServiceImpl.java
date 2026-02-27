package com.easychat.service.impl;

import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.enums.BeautyAccountStatusEnum;
import com.easychat.enums.PageSize;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.UserInfoBeautyMapper;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.mapper.UserInfoMapper;
import com.easychat.service.UserInfoBeautyService;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import java.util.List;

/**
 * 靓号表ServiceImpl
 * @author 'Tong'
 * @since 2025/09/28
 */
@Service("userInfoBeautyService")
public class UserInfoBeautyServiceImpl implements UserInfoBeautyService {
    @Resource
    private UserInfoBeautyMapper<UserInfoBeauty, UserInfoBeautyQuery> userInfoBeautyMapper;
    @Resource
    private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

    // 根据条件查询列表
    public List<UserInfoBeauty> findListByParam(UserInfoBeautyQuery query) {
        return this.userInfoBeautyMapper.selectList(query);
    }

    // 根据条件查询总数
    public Integer findCountByParam(UserInfoBeautyQuery query) {
        return this.userInfoBeautyMapper.selectCount(query);
    }

    // 分页查询
    public PaginationResultVO<UserInfoBeauty> findPageByParam(UserInfoBeautyQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
        query.setSimplePage(page);
        List<UserInfoBeauty> list = this.findListByParam(query);
        return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
    }

    // 新增
    public Integer add(UserInfoBeauty bean) {
        return this.userInfoBeautyMapper.insert(bean);
    }

    // 批量新增
    public Integer addBatch(List<UserInfoBeauty> listBean) {
        if (listBean == null || listBean.size() == 0) {
            return 0;
        }
        return this.userInfoBeautyMapper.insertBatch(listBean);
    }

    // 批量新增或修改
    public Integer addOrUpdateBatch(List<UserInfoBeauty> listBean) {
        if (listBean == null || listBean.size() == 0) {
            return 0;
        }
        return this.userInfoBeautyMapper.insertOrUpdateBatch(listBean);
    }

    // 根据Id查询
    public UserInfoBeauty getUserInfoBeautyById(Integer id) {
        return this.userInfoBeautyMapper.selectById(id);
    }

    // 根据Id更新
    public Integer updateUserInfoBeautyById(UserInfoBeauty bean, Integer id) {
        return this.userInfoBeautyMapper.updateById(bean, id);
    }

    // 根据Id删除
    public Integer deleteUserInfoBeautyById(Integer id) {
        return this.userInfoBeautyMapper.deleteById(id);
    }
    // 根据UserId查询
    public UserInfoBeauty getUserInfoBeautyByUserId(String userId) {
        return this.userInfoBeautyMapper.selectByUserId(userId);
    }

    // 根据UserId更新
    public Integer updateUserInfoBeautyByUserId(UserInfoBeauty bean, String userId) {
        return this.userInfoBeautyMapper.updateByUserId(bean, userId);
    }

    // 根据UserId删除
    public Integer deleteUserInfoBeautyByUserId(String userId) {
        return this.userInfoBeautyMapper.deleteByUserId(userId);
    }
    // 根据Email查询
    public UserInfoBeauty getUserInfoBeautyByEmail(String email) {
        return this.userInfoBeautyMapper.selectByEmail(email);
    }

    // 根据Email更新
    public Integer updateUserInfoBeautyByEmail(UserInfoBeauty bean, String email) {
        return this.userInfoBeautyMapper.updateByEmail(bean, email);
    }

    // 根据Email删除
    public Integer deleteUserInfoBeautyByEmail(String email) {
        return this.userInfoBeautyMapper.deleteByEmail(email);
    }

    @Override
    public void saveAccount(UserInfoBeauty beauty) {
        if (beauty.getId() != null) {
            UserInfoBeauty dbInfo = this.userInfoBeautyMapper.selectById(beauty.getId());
            if (BeautyAccountStatusEnum.USED.getStatus() == dbInfo.getStatus()) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        UserInfoBeauty dbInfo = this.userInfoBeautyMapper.selectByEmail(beauty.getEmail());
        // 新增的时候判断邮箱是否存在
        if (beauty.getId() == null && dbInfo != null) {
            throw new BusinessException("靓号邮箱已经存在");
        }

        // 修改时判断邮箱是否存在
        if (beauty.getId() != null && dbInfo != null && dbInfo.getId() != null && !beauty.getId().equals(dbInfo.getId())) {
            throw new BusinessException("靓号邮箱已经存在");
        }

        // 判断靓号是否存在
        dbInfo = this.userInfoBeautyMapper.selectByUserId(beauty.getUserId());
        if (beauty.getId() == null && dbInfo != null) {
            throw new BusinessException("靓号已经存在");
        }

        if (beauty.getId() != null && dbInfo != null && dbInfo.getId() != null && !beauty.getId().equals(dbInfo.getId())) {
            throw new BusinessException("靓号已经存在");
        }

        // 判断邮箱是否已经注册
        UserInfo userInfo = this.userInfoMapper.selectByEmail(beauty.getEmail());
        if (userInfo != null) {
            throw new BusinessException("靓号邮箱已经注册");
        }
        userInfo = this.userInfoMapper.selectByUserId(beauty.getUserId());
        if (userInfo != null) {
            throw new BusinessException("靓号已经注册");
        }

        if (beauty.getId() != null){
            this.userInfoBeautyMapper.updateById(beauty, beauty.getId());
        } else {
            beauty.setStatus((byte)BeautyAccountStatusEnum.NO_USE.getStatus());
            this.userInfoBeautyMapper.insert(beauty);
        }
    }
}