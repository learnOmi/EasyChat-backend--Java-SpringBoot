package com.easychat.service;

import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.entity.vo.UserInfoVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 用户信息表Service
 * @author 'Tong'
 * @since 2025/09/28
 */
public interface UserInfoService {
    // 根据条件查询列表
    List<UserInfo> findListByParam(UserInfoQuery query);

    // 根据条件查询总数
    Integer findCountByParam(UserInfoQuery query);

    // 分页查询
    PaginationResultVO<UserInfo> findPageByParam(UserInfoQuery query);

    // 新增
    Integer add(UserInfo bean);

    // 批量新增
    Integer addBatch(List<UserInfo> listBean);

    // 批量新增或修改
    Integer addOrUpdateBatch(List<UserInfo> listBean);

    // 根据UserId查询
    UserInfo getUserInfoByUserId(String userId);

    // 根据UserId更新
    Integer updateUserInfoByUserId(UserInfo bean, String userId);

    // 根据UserId删除
    Integer deleteUserInfoByUserId(String userId);
    // 根据Email查询
    UserInfo getUserInfoByEmail(String email);

    // 根据Email更新
    Integer updateUserInfoByEmail(UserInfo bean, String email);

    // 根据Email删除
    Integer deleteUserInfoByEmail(String email);

    // 注册
    void register(String email, String nickName, String password);

    // 登录
    UserInfoVO login(String email, String password);

    // 更新用户信息
    void updateUserInfo(UserInfo userInfo, MultipartFile avatarFile, MultipartFile avatarCover) throws IOException;

    void updateUserStatus(Integer status, String userId);

    void forceOffLine(String userId);
}