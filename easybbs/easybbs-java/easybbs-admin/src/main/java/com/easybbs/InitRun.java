package com.easybbs;


import com.easybbs.exception.BusinessException;
import com.easybbs.service.SettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("initRun")
public class InitRun {
    private static final Logger logger= LoggerFactory.getLogger(InitRun.class);
    @Resource
    private SettingService settingService;

    @Resource
    public void run(ApplicationArguments args) throws BusinessException {
        //刷新系统设置缓存
        settingService.refresCache();
        logger.info("服务启动成功，开始愉快的开发吧");
    }
}
