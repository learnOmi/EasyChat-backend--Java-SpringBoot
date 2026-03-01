package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.po.AppUpdation;
import com.easychat.entity.query.AppUpdationQuery;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.AppUpdationService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController("adminAppUpdateController")
@RequestMapping("/admin")
public class AdminAppUpdateController extends ABaseController {
    @Resource
    private AppUpdationService appUpdationService;

    @RequestMapping("/loadUpdateList")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadUpdateList(AppUpdationQuery query) {
        query.setOrderBy("id desc");
        return getSuccessResponse(appUpdationService.findPageByParam(query));
    }

    @RequestMapping("/saveUpdate")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveUpdate(Integer id, @NotEmpty String version, @NotEmpty String updateDesc, @NotNull Integer fileType, String outerLink, MultipartFile file) throws IOException {
        AppUpdation appUpdation = new AppUpdation();
        appUpdation.setId(id);
        appUpdation.setVersion(version);
        appUpdation.setUpdateDesc(updateDesc);
        appUpdation.setFileType(fileType.byteValue());
        appUpdation.setOuterLink(outerLink);
        appUpdationService.saveUpdate(appUpdation, file);
        return getSuccessResponse(null);
    }

    @RequestMapping("/deleteUpdate")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO deleteUpdate(Integer id) {
        appUpdationService.deleteAppUpdationById(id);
        return getSuccessResponse(null);
    }

    @RequestMapping("/postUpdate")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO postUpdate(Integer id, Integer status, String grayscalUid) {
        appUpdationService.postUpdate(id, status, grayscalUid);
        return getSuccessResponse(null);
    }
}
