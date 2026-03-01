package com.easychat.service.impl;

import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.enums.AppUpdationFileTypeEnum;
import com.easychat.enums.AppUpdationStatusEnum;
import com.easychat.enums.PageSize;
import com.easychat.entity.query.SimplePage;
import com.easychat.entity.po.AppUpdation;
import com.easychat.entity.query.AppUpdationQuery;
import com.easychat.enums.ResponseCodeEnum;
import com.easychat.exception.BusinessException;
import com.easychat.mapper.AppUpdationMapper;import com.easychat.entity.vo.PaginationResultVO;
import com.easychat.service.AppUpdationService;
import com.easychat.utils.StringTools;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * app发布ServiceImpl
 * @author 'Tong'
 * @since 2026/03/02
 */
@Service("appUpdationService")
public class AppUpdationServiceImpl implements AppUpdationService {
	@Resource
	private AppUpdationMapper<AppUpdation, AppUpdationQuery> appUpdationMapper;
    @Resource
    private AppConfig appConfig;

	// 根据条件查询列表
	public List<AppUpdation> findListByParam(AppUpdationQuery query) {
		return this.appUpdationMapper.selectList(query);
	}

	// 根据条件查询总数
	public Integer findCountByParam(AppUpdationQuery query) {
		return this.appUpdationMapper.selectCount(query);
	}

	// 分页查询
	public PaginationResultVO<AppUpdation> findPageByParam(AppUpdationQuery query) {
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize() == null ? PageSize.SIZE15.getSize() : query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), pageSize, count);
		query.setSimplePage(page);
		List<AppUpdation> list = this.findListByParam(query);
		return new PaginationResultVO<>(count, page.getPageNo(), page.getPageSize(), list, page.getPageTotal());
	}

	// 新增
	public Integer add(AppUpdation bean) {
		return this.appUpdationMapper.insert(bean);
	}

	// 批量新增
	public Integer addBatch(List<AppUpdation> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.appUpdationMapper.insertBatch(listBean);
	}

	// 批量新增或修改
	public Integer addOrUpdateBatch(List<AppUpdation> listBean) {
		if (listBean == null || listBean.size() == 0) {
			return 0;
		}
		return this.appUpdationMapper.insertOrUpdateBatch(listBean);
	}

	// 多条件更新
	public Integer updateByParam(AppUpdation bean, AppUpdationQuery query) {
		return this.appUpdationMapper.updateByParam(bean, query);
	}

	// 多条件删除
	public Integer deleteByParam(AppUpdationQuery query) {
		return this.appUpdationMapper.deleteByParam(query);
	}

	// 根据Id查询
	public AppUpdation getAppUpdationById(Integer id) {
		return this.appUpdationMapper.selectById(id);
	}

	// 根据Id更新
	public Integer updateAppUpdationById(AppUpdation bean, Integer id) {
		return this.appUpdationMapper.updateById(bean, id);
	}

	// 根据Id删除
	public Integer deleteAppUpdationById(Integer id) {
        AppUpdation dbInfo = this.getAppUpdationById(id);
        if (!AppUpdationStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        return this.appUpdationMapper.deleteById(id);
	}

    // 根据Version查询
    public AppUpdation getAppUpdationByVersion(String version) {
        return this.appUpdationMapper.selectByVersion(version);
    }

    // 根据Version更新
    public Integer updateAppUpdationByVersion(AppUpdation bean, String version) {
        return this.appUpdationMapper.updateByVersion(bean, version);
    }

    // 根据Version删除
    public Integer deleteAppUpdationByVersion(String version) {
        return this.appUpdationMapper.deleteByVersion(version);
    }

    @Override
    public void saveUpdate(AppUpdation appUpdation, MultipartFile file) throws IOException {
        AppUpdationFileTypeEnum fileTypeEnum = AppUpdationFileTypeEnum.getByType(appUpdation.getFileType().intValue());
        if (null == fileTypeEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (appUpdation.getId() != null) {
            AppUpdation dbInfo = this.getAppUpdationById(appUpdation.getId());
            if (!AppUpdationStatusEnum.INIT.getStatus().equals(dbInfo.getStatus())) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
        }

        AppUpdationQuery updateQuery = new AppUpdationQuery();
        updateQuery.setOrderBy("id desc");
        updateQuery.setSimplePage(new SimplePage(0, 1));
        List<AppUpdation> appUpdationList = appUpdationMapper.selectList(updateQuery);
        if (!appUpdationList.isEmpty()) {
            AppUpdation latest = appUpdationList.get(0);
            Long dbVersion = Long.parseLong(latest.getVersion().replace(",", ""));
            Long currentVersion = Long.parseLong(appUpdation.getVersion().replace(",", ""));
            if (appUpdation.getId() == null && currentVersion <= dbVersion) {
                throw new BusinessException("版本号不能小于等于最新版本号");
            }
            if (appUpdation.getId() != null && currentVersion >= dbVersion && !appUpdation.getId().equals(latest.getId())) {
                throw new BusinessException("版本号不能小于等于最新版本号");
            }

            AppUpdation versionDb = appUpdationMapper.selectByVersion(appUpdation.getVersion());
            if (appUpdation.getId() != null && versionDb != null && !versionDb.getId().equals(appUpdation.getId())) {
                throw new BusinessException("版本号已存在");
            }

        }

        if (appUpdation.getId() == null) {
            appUpdation.setCreateTime(new Date());
            appUpdation.setStatus(AppUpdationStatusEnum.INIT.getStatus().byteValue());
            appUpdationMapper.insert(appUpdation);
        } else {
            appUpdationMapper.updateById(appUpdation, appUpdation.getId());
        }

        if (file != null) {
            File folder = new File(appConfig.getProjectFolder() + Constants.APP_UPDATE_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            file.transferTo(new File(folder.getAbsolutePath() + "/" + appUpdation.getId() + Constants.APP_EXE_SUFFIX));
        }
    }

    @Override
    public void postUpdate(Integer id, Integer status, String grayscalUid) {
        AppUpdationStatusEnum statusEnum = AppUpdationStatusEnum.getByStatus(status);
        if (null == statusEnum) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (AppUpdationStatusEnum.GRAYSCALE == statusEnum && StringTools.isEmpty(grayscalUid)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }

        if (AppUpdationStatusEnum.GRAYSCALE != statusEnum) {
            grayscalUid = "";
        }
        AppUpdation appUpdation = new AppUpdation();
        appUpdation.setStatus(status.byteValue());
        appUpdation.setGrayscaleUid(grayscalUid);
        appUpdationMapper.updateById(appUpdation, id);
    }

    @Override
    public AppUpdation getLatestUpdate(String appVersion, String uid) {
        return appUpdationMapper.selectLatestUpdate(appVersion, uid);
    }
}