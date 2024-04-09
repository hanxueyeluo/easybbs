package com.easybbs.controller;


import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.config.AdminConfig;
import com.easybbs.entity.dto.*;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.SettingService;
import com.easybbs.utils.JsonUtils;
import com.easybbs.utils.OKHttpUtils;
import com.easybbs.utils.StringTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@RequestMapping("/setting")
public class SysSettingController extends ABaseController {
    @Resource
    private SettingService settingService;

    @Resource
    private AdminConfig adminConfig;

    @RequestMapping("/getSetting")
    public ResponseVO getSetting() throws BusinessException {
        return getSuccessResponseVO(settingService.refresCache());
    }

    @RequestMapping("/saveSetting")
    public ResponseVO saveSetting(@VerifyParam SysSetting4AuditDto auditDto,
                                  @VerifyParam SysSetting4CommentDto commentDto,
                                  @VerifyParam SysSetting4PostDto postDto,
                                  @VerifyParam SysSetting4LikeDto likeDto,
                                  @VerifyParam SysSetting4RegisterDto registerDto,
                                  @VerifyParam SysSetting4EmailDto emailDto) throws BusinessException {
        SysSettingDto sysSettingDto=new SysSettingDto();
        sysSettingDto.setAuditSetting(auditDto);
        sysSettingDto.setCommentSetting(commentDto);
        sysSettingDto.setPostSetting(postDto);
        sysSettingDto.setLikeSetting(likeDto);
        sysSettingDto.setRegisterSetting(registerDto);
        sysSettingDto.setEmailSetting(emailDto);
        settingService.saveSetting(sysSettingDto);
        return getSuccessResponseVO("");
    }

    private void sendWebRequest() throws BusinessException, IOException {
        String appKey=adminConfig.getInnerApiAppKey();
        String appSecret=adminConfig.getInnerApiAppSecret();
        Long timestamp=System.currentTimeMillis();
        String sign= StringTools.encodeMd5(appKey+timestamp+appSecret);
        String url=adminConfig.getWebApiUrl()+"?appKey="+appKey+"?timestamp="+timestamp+"?sign="+sign;
        String responseJson= OKHttpUtils.getRequest(url);
        ResponseVO responseVO= JsonUtils.convertJson2Obj(responseJson, ResponseVO.class);
        if (!STATUC_SUCCESS.equals(responseVO.getStatus())) {
            throw new BusinessException("刷新缓存失败");
        }
    }

}
