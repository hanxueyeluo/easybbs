package com.easybbs;

import com.easybbs.exception.BusinessException;
import com.easybbs.service.SettingService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class InitRun implements ApplicationRunner{
    @Resource
    private SettingService settingService;
    @Override
    public void run(ApplicationArguments args) throws BusinessException {
        settingService.refresCache();
    }
}
