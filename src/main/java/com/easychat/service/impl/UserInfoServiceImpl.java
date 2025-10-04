package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.dto.TokenUserInfoDto;
import com.easychat.entity.po.UserInfoBeauty;
import com.easychat.enums.BeautyAccountStatusEnum;
import com.easychat.enums.PageSize;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.UserInfo;
import com.easychat.entity.query.UserInfoQuery;
import com.easychat.enums.UserContactTypeEnum;
import com.easychat.enums.UserStatusEnum;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.UserInfoBeautyMapper;
import com.easychat.mapper.UserInfoMapper;import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.UserInfoService;
import com.easychat.utils.StringTools;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息表ServiceImpl
 * @author 'Tong'
 * @since 2025/09/28
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {
	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;
    @Resource
    private UserInfoBeautyMapper<UserInfoBeauty, UserInfoQuery> userInfoBeautyMapper;
    @Resource
    private AppConfig appConfig;

    // 根据条件查询列表
    public List<UserInfo> findListByParam(UserInfoQuery query) {
        return this.userInfoMapper.selectList(query);
    }

    // 根据条件查询总数
    public Integer findCountByParam(UserInfoQuery query) {
        return this.userInfoMapper.selectCount(query);
    }

    // 分页查询
    public PaginationResultVO<UserInfo> findPageByParam(UserInfoQuery query) {
        Integer count = this.findCountByParam(query);
        Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
        SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
        query.setSimplePage(page);
        List<UserInfo> list = this.findListByParam(query);
        return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
    }

    // 新增
    public Integer add(UserInfo bean) {
        return this.userInfoMapper.insert(bean);
    }

    // 批量新增
    public Integer addBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.size() == 0) {
            return 0;
        }
        return this.userInfoMapper.insertBatch(listBean);
    }

    // 批量新增或修改
    public Integer addOrUpdateBatch(List<UserInfo> listBean) {
        if (listBean == null || listBean.size() == 0) {
            return 0;
        }
        return this.userInfoMapper.insertOrUpdateBatch(listBean);
    }

    // 根据UserId查询
    public UserInfo getUserInfoByUserId(String userId) {
        return this.userInfoMapper.selectByUserId(userId);
    }

    // 根据UserId更新
    public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
        return this.userInfoMapper.updateByUserId(bean, userId);
    }

    // 根据UserId删除
    public Integer deleteUserInfoByUserId(String userId) {
        return this.userInfoMapper.deleteByUserId(userId);
    }
    // 根据Email查询
    public UserInfo getUserInfoByEmail(String email) {
        return this.userInfoMapper.selectByEmail(email);
    }

    // 根据Email更新
    public Integer updateUserInfoByEmail(UserInfo bean, String email) {
        return this.userInfoMapper.updateByEmail(bean, email);
    }

    // 根据Email删除
    public Integer deleteUserInfoByEmail(String email) {
        return this.userInfoMapper.deleteByEmail(email);
    }

    /**
     * 用户注册方法
     * @param email 用户邮箱
     * @param nickName 用户昵称
     * @param password 用户密码
     */
    public void register(String email, String nickName, String password) {
        // 创建结果Map，用于返回操作结果
        Map<String, Object> result = new HashMap<>();
        // 通过邮箱查询用户信息，检查用户是否已存在
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);

        // 用户已存在，抛出业务异常
        if (null != userInfo) {
            throw new BusinessException("邮箱/账号已经存在！");
        }

        // 生成用户ID
        String userId = StringTools.getUserId();

        // 查询靓号信息
        UserInfoBeauty beautyAccount = this.userInfoBeautyMapper.selectByEmail(email);
        // 判断是否可以使用靓号
        boolean useBeautyAccount = null != beautyAccount && BeautyAccountStatusEnum.NO_USE.getStatus() == beautyAccount.getStatus();
        // 如果可以使用靓号，则使用靓号的用户ID
        if (useBeautyAccount) {
            userId = UserContactTypeEnum.USER.getPrefix() + beautyAccount.getUserId();
        }

        // 获取当前时间
        Date curDate = new Date();
        // 创建新用户对象
        userInfo = new UserInfo();
        userInfo.setUserId(userId);          // 设置用户ID
        userInfo.setNickName(nickName);      // 设置用户昵称
        userInfo.setEmail(email);           // 设置用户邮箱
        userInfo.setPassword(StringTools.encodeMd5(password));  // 设置加密后的密码
        userInfo.setCreateTime(curDate);      // 设置创建时间
        userInfo.setStatus((byte) UserStatusEnum.ENABLED.getStatus());  // 设置用户状态为启用
        userInfo.setLastOffTime(curDate.getTime());  // 设置最后下线时间
        // 插入用户信息到数据库
        this.userInfoMapper.insert(userInfo);

        // 如果使用了靓号，更新美容师账户状态为已使用
        if (useBeautyAccount) {
            UserInfoBeauty updateBeauty = new UserInfoBeauty();
            updateBeauty.setStatus((byte) BeautyAccountStatusEnum.USED.getStatus());
            this.userInfoBeautyMapper.updateById(updateBeauty, beautyAccount.getId());
        }
    }

    /**
     * 用户登录方法
     *
     * @param email    用户邮箱/账号
     * @param password 用户密码
     * @return TokenUserInfoDto 包含用户信息和token的DTO对象
     * @throws BusinessException 当账号不存在、密码错误或账号被禁用时抛出业务异常
     */
    public TokenUserInfoDto login(String email, String password) {
        // 创建一个HashMap用于存储结果
        Map<String, Object> result = new HashMap<>();
        // 根据邮箱查询用户信息
        UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
        // 判断用户是否存在或密码是否错误
        if (null == userInfo || !StringTools.encodeMd5(password).equals(userInfo.getPassword())) {
            // 抛出业务异常，提示邮箱/账号或密码错误
            throw new BusinessException("邮箱/账号或密码错误！");
        }
        // 检查用户账号状态是否为启用状态
        if (!(userInfo.getStatus() == UserStatusEnum.ENABLED.getStatus())) {
            // 抛出业务异常，提示账号已被禁用
            throw new BusinessException("账号已被禁用！");
        }
        // 返回包含用户信息的TokenUserInfoDto对象
        return getTokenUserInfoDto(userInfo);
    }

    /**
     * 根据用户信息创建TokenUserInfoDto对象
     *
     * @param userInfo 用户信息对象
     * @return 包含用户信息和管理员状态的TokenUserInfoDto对象
     */
    private TokenUserInfoDto getTokenUserInfoDto(UserInfo userInfo) {
        // 创建TokenUserInfoDto对象
        TokenUserInfoDto tokenUserInfoDto = new TokenUserInfoDto();
        // 设置用户ID
        tokenUserInfoDto.setUserId(userInfo.getUserId());
        // 设置用户昵称
        tokenUserInfoDto.setNickName(userInfo.getNickName());

        // 获取管理员邮箱配置
        String adminEmails = appConfig.getAdminEmails();
        // 判断用户是否为管理员：检查管理员邮箱列表是否包含当前用户邮箱
        tokenUserInfoDto.setAdmin(!StringTools.isEmpty(adminEmails) && ArrayUtils.contains(adminEmails.split(","), userInfo.getEmail()));

        // 返回构建好的TokenUserInfoDto对象
        return tokenUserInfoDto;
    }
}