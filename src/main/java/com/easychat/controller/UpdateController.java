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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Arrays;

@Tag(name = "APP 更新模块", description = "客户端版本检查与更新信息下发")
@RestController("updateController")
@RequestMapping("/update")
public class UpdateController extends ABaseController {
    @Resource
    private AppUpdationService appUpdationService;
    @Resource
    private AppConfig appConfig;

    @Operation(summary = "检查版本更新", description = "需登录；根据客户端版本与灰度Uid返回最新更新信息，无更新时 data 为 null，否则为 AppUpdateVo")
    @RequestMapping("/checkVersion")
    @GlobalInterceptor
    public ResponseVO checkVersion(@Parameter(description = "客户端当前版本号") String appVersion,
                                   @Parameter(description = "用户ID，用于灰度发布匹配") String uid) {
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
