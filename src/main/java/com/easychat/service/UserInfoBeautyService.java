package com.easychat.service;

import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.entity.query.UserInfoBeautyQuery;
import com.easychat.entity.vo.PaginationResultVO;
import java.util.List;

/**
 * 靓号表Service
 * @author 'Tong'
 * @since 2025/09/28
 */
public interface UserInfoBeautyService {
    // 根据条件查询列表
    List<UserInfoBeauty> findListByParam(UserInfoBeautyQuery query);

    // 根据条件查询总数
    Integer findCountByParam(UserInfoBeautyQuery query);

    // 分页查询
    PaginationResultVO<UserInfoBeauty> findPageByParam(UserInfoBeautyQuery query);

    // 新增
    Integer add(UserInfoBeauty bean);

    // 批量新增
    Integer addBatch(List<UserInfoBeauty> listBean);

    // 批量新增或修改
    Integer addOrUpdateBatch(List<UserInfoBeauty> listBean);

    // 根据Id查询
    UserInfoBeauty getUserInfoBeautyById(Integer id);

    // 根据Id更新
    Integer updateUserInfoBeautyById(UserInfoBeauty bean, Integer id);

    // 根据Id删除
    Integer deleteUserInfoBeautyById(Integer id);
    // 根据UserId查询
    UserInfoBeauty getUserInfoBeautyByUserId(String userId);

    // 根据UserId更新
    Integer updateUserInfoBeautyByUserId(UserInfoBeauty bean, String userId);

    // 根据UserId删除
    Integer deleteUserInfoBeautyByUserId(String userId);
    // 根据Email查询
    UserInfoBeauty getUserInfoBeautyByEmail(String email);

    // 根据Email更新
    Integer updateUserInfoBeautyByEmail(UserInfoBeauty bean, String email);

    // 根据Email删除
    Integer deleteUserInfoBeautyByEmail(String email);
}