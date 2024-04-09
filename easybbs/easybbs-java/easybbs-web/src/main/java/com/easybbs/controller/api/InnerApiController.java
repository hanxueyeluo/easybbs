package com.easybbs.controller.api;


import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.config.WebConfig;
import com.easybbs.entity.enums.ResponseCodeEnum;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.SettingService;
import com.easybbs.utils.StringTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController("innerApiController")
@RequestMapping("/innerApi")
public class InnerApiController extends ABaseController {
    @Resource
    private WebConfig webConfig;
    @Resource
    private SettingService settingService;

    @RequestMapping("/refresSysSetting")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO refresSysSetting(@VerifyParam(required = true) String appKey,
                                       @VerifyParam(required = true) Long timestamp,
                                       @VerifyParam(required = true) String sign) throws BusinessException {
        if (!webConfig.getInnerApiAppKey().equals(appKey)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (System.currentTimeMillis()-timestamp>1000*10) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        String mySign= StringTools.encodeMd5(appKey+timestamp+webConfig.getInnerApiAppSecret());
        if (!mySign.equals(sign)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        settingService.refresCache();
        return getSuccessResponseVO(null);
    }

}
