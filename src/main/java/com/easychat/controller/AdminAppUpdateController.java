package com.easychat.controller;

import com.easychat.annotation.GlobalInterceptor;
import com.easychat.entity.constants.Constants;
import com.easychat.entity.dto.SysSettingDto;
import com.easychat.entity.po.AppUpdation;
import com.easychat.entity.query.AppUpdationQuery;
import com.easychat.entity.vo.ResponseVO;
import com.easychat.service.AppUpdationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Tag(name = "管理端-APP更新", description = "应用版本发布管理（仅管理员）")
@RestController("adminAppUpdateController")
@RequestMapping("/admin")
public class AdminAppUpdateController extends ABaseController {
    @Resource
    private AppUpdationService appUpdationService;

    @Operation(summary = "分页查询更新记录", description = "仅管理员；data 为 PaginationResultVO<AppUpdation>")
    @RequestMapping("/loadUpdateList")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO loadUpdateList(AppUpdationQuery query) {
        query.setOrderBy("id desc");
        return getSuccessResponse(appUpdationService.findPageByParam(query));
    }

    @Operation(summary = "保存/发布更新", description = "仅管理员；id 为空时新增；fileType 为本地文件时可上传升级包；data 为 null")
    @RequestMapping("/saveUpdate")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO saveUpdate(@Parameter(description = "更新记录ID，新增时为空") Integer id,
                                 @Parameter(description = "版本号") @NotEmpty String version,
                                 @Parameter(description = "更新描述") @NotEmpty String updateDesc,
                                 @Parameter(description = "文件类型：0:本地文件 1:外链") @NotNull Integer fileType,
                                 @Parameter(description = "外链地址（fileType=1 时使用）") String outerLink,
                                 @Parameter(description = "升级包文件（fileType=0 时使用）") MultipartFile file) throws IOException {
        AppUpdation appUpdation = new AppUpdation();
        appUpdation.setId(id);
        appUpdation.setVersion(version);
        appUpdation.setUpdateDesc(updateDesc);
        appUpdation.setFileType(fileType.byteValue());
        appUpdation.setOuterLink(outerLink);
        appUpdationService.saveUpdate(appUpdation, file);
        return getSuccessResponse(null);
    }

    @Operation(summary = "删除更新记录", description = "仅管理员；data 为 null")
    @RequestMapping("/deleteUpdate")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO deleteUpdate(@Parameter(description = "更新记录ID") Integer id) {
        appUpdationService.deleteAppUpdationById(id);
        return getSuccessResponse(null);
    }

    @Operation(summary = "发布/下线更新", description = "仅管理员；status 0:未发布 1:灰度发布 2:全网发布；data 为 null")
    @RequestMapping("/postUpdate")
    @GlobalInterceptor(checkAdmin = true)
    public ResponseVO postUpdate(@Parameter(description = "更新记录ID") Integer id,
                                 @Parameter(description = "发布状态：0:未发布 1:灰度发布 2:全网发布") Integer status,
                                 @Parameter(description = "灰度用户ID，多个以逗号分隔") String grayscalUid) {
        appUpdationService.postUpdate(id, status, grayscalUid);
        return getSuccessResponse(null);
    }
}
