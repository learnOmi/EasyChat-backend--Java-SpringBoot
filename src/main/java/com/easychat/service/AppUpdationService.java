package com.easychat.service;

import com.easychat.entity.po.AppUpdation;
import com.easychat.entity.query.AppUpdationQuery;
import com.easychat.entity.vo.PaginationResultVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * app发布Service
 * @author 'Tong'
 * @since 2026/03/02
 */
public interface AppUpdationService {
	// 根据条件查询列表
	List<AppUpdation> findListByParam(AppUpdationQuery query);

	// 根据条件查询总数
	Integer findCountByParam(AppUpdationQuery query);

	// 分页查询
	PaginationResultVO<AppUpdation> findPageByParam(AppUpdationQuery query);

	// 新增
	Integer add(AppUpdation bean);

	// 批量新增
	Integer addBatch(List<AppUpdation> listBean);

	// 批量新增或修改
	Integer addOrUpdateBatch(List<AppUpdation> listBean);

	// 多条件更新
	Integer updateByParam(AppUpdation bean, AppUpdationQuery query);

	// 多条件更新
	Integer deleteByParam(AppUpdationQuery query);

	// 根据Id查询
	AppUpdation getAppUpdationById(Integer id);

	// 根据Id更新
	Integer updateAppUpdationById(AppUpdation bean, Integer id);

	// 根据Id删除
	Integer deleteAppUpdationById(Integer id);

    // 根据Version查询
    AppUpdation getAppUpdationByVersion(String version);

    // 根据Version更新
    Integer updateAppUpdationByVersion(AppUpdation bean, String version);

    // 根据Version删除
    Integer deleteAppUpdationByVersion(String version);

    void saveUpdate(AppUpdation appUpdation, MultipartFile file) throws IOException;

    void postUpdate(Integer id, Integer status, String grayscalUid);
}