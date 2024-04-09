package com.easybbs.controller;

import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.config.AdminConfig;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.*;
import com.easybbs.entity.enums.ResponseCodeEnum;
import com.easybbs.entity.enums.VerifyRegexEnum;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.CodeService;
import com.easybbs.service.InfoService;
import com.easybbs.utils.StringTools;
import com.easybbs.utils.SysCacheUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@RestController
public class AccountController extends ABaseController {
    /**
     * 验证码
     *
     * @param response
     * @param session
     * @param type
     */



    @Resource
    private AdminConfig adminConfig;

    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();

        session.setAttribute(Constants.CHECK_CODE_KEY, code);
        vCode.write(response.getOutputStream());
    }

    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO login(HttpSession session,
                               @VerifyParam(required = true) String account,
                               @VerifyParam(required = true) String password,
                               @VerifyParam(required = true) String checkCode) throws BusinessException {
        try{
            if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
                throw new BusinessException("图片验证码错误");
            }

            if (!adminConfig.getAdminAccount().equals(account)||!StringTools.encodeMd5(adminConfig.getAdminPassword()).equals(password)) {
                throw new BusinessException("账号或者密码错误");
            }

            SessionAdminUserDto sessionAdminUserDto=new SessionAdminUserDto();
            sessionAdminUserDto.setAccount(account);
            session.setAttribute(Constants.SESSION_KEY,sessionAdminUserDto);
            return getSuccessResponseVO(sessionAdminUserDto);
        }finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @RequestMapping("/logout")
    @GlobalInterceptor()
    public ResponseVO loguot(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }

}
