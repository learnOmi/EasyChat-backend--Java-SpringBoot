package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.config.AppConfig;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.po.AppUpdation;
import com.easychat.entity.vo.AppUpdateVo;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.enums.AppUpdationFileTypeEnum;
import com.easychat.service.AppUpdationService;
import com.easychat.utils.CopyTools;
import com.easychat.utils.StringTools;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;

@RestController("updateController")
@RequestMapping("/update")
public class UpdateController extends ABaseController {
    @Resource
    private AppUpdationService appUpdationService;
    @Resource
    private AppConfig appConfig;

    @RequestMapping("/checkVersion")
    @GlobalInterceptor
    public ResponseVO checkVersion(String appVersion, String uid) {
        if (StringTools.isEmpty(appVersion)) {
            return getSuccessResponse(null);
        }

        AppUpdation appUpdation = appUpdationService.getLatestUpdate(appVersion, uid);
        if (appUpdation == null) {
            return getSuccessResponse(null);
        }
        AppUpdateVo updateVo = CopyTools.copy(appUpdation, AppUpdateVo.class);
        if (AppUpdationFileTypeEnum.LOCAL.getType().equals(appUpdation.getFileType())) {
            File file = new File(appConfig.getProjectFolder() + Constants.APP_UPDATE_FOLDER + appUpdation.getId() + Constants.APP_EXE_SUFFIX);
            updateVo.setSize(file.length());
        } else {
            updateVo.setSize(0L);
        }
        updateVo.setUpdateList(Arrays.asList(appUpdation.getUpdateDescArray()));
        String fileName = Constants.APP_NAME + appUpdation.getVersion() + Constants.APP_EXE_SUFFIX;
        updateVo.setFileName(fileName);
        return getSuccessResponse(null);
    }
}
