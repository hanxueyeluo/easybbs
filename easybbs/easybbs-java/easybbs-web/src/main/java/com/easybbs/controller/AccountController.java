package com.easybbs.controller;

import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.CreateImageCode;
import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.dto.SysSetting4CommentDto;
import com.easybbs.entity.dto.SysSettingDto;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.entity.enums.ResponseCodeEnum;
import com.easybbs.entity.enums.VerifyRegexEnum;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.InfoService;
import com.easybbs.utils.StringTools;
import com.easybbs.utils.SysCacheUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.easybbs.service.CodeService;
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
    private CodeService CodeService;

    @Resource
    private InfoService infoService;

    @RequestMapping("/checkCode")
    public void checkCode(HttpServletResponse response, HttpSession session, Integer type) throws IOException {
        CreateImageCode vCode = new CreateImageCode(130, 38, 5, 10);
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        String code = vCode.getCode();
        //登录注册
        if (type == null || type == 0) {
            session.setAttribute(Constants.CHECK_CODE_KEY, code);
        } else {
            //获取邮箱
            session.setAttribute(Constants.CHECK_CODE_KEY_EMAIL, code);
        }
        vCode.write(response.getOutputStream());
    }


    @RequestMapping("/sendEmailCode")
    public ResponseVO sendEmailCode(HttpSession session,
                                    @VerifyParam(required = true) String email,
                                    @VerifyParam(required = true)String checkCode,
                                    @VerifyParam(required = true)Integer type) throws BusinessException {
        try {
            if (StringTools.isEmpty(email) || StringTools.isEmpty(checkCode) || type == null) {
                throw new BusinessException(ResponseCodeEnum.CODE_600);
            }
            if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY_EMAIL))){
                throw new BusinessException("图片验证码错误");
            }
            CodeService.sendEmailCode(email, type);
            return getSuccessResponseVO(null);
        } finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY_EMAIL);
        }
    }


    @RequestMapping("/register")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO register(HttpSession session,
                               @VerifyParam(required = true,regex = VerifyRegexEnum.EMAIL,max = 150) String email,
                               @VerifyParam(required = true) String emailCode,
                               @VerifyParam(required = true,max = 20) String nickName,
                               @VerifyParam(required = true,min = 8,max = 18,regex = VerifyRegexEnum.PASSWORD) String password,
                               @VerifyParam(required = true) String checkCode) throws BusinessException {
        try{
            if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
                throw new BusinessException("图片验证码错误");
            }
            infoService.register(email,emailCode,nickName,password);
            return getSuccessResponseVO(null);
        }finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }

    @RequestMapping("/login")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO login(HttpSession session,
                               HttpServletRequest request,
                               @VerifyParam(required = true) String email,
                               @VerifyParam(required = true) String password,
                               @VerifyParam(required = true) String checkCode) throws BusinessException {
        try{
            if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))){
                throw new BusinessException("图片验证码错误");
            }
            SessionWebUserDto sessionWebUserDto=infoService.login(email,password,getIpAddr(request));
            session.setAttribute(Constants.SESSION_KEY,sessionWebUserDto);
            return getSuccessResponseVO(sessionWebUserDto);
        }finally {
            session.removeAttribute(Constants.CHECK_CODE_KEY);
        }
    }
    @RequestMapping("/getUserInfo")
    @GlobalInterceptor()
    public ResponseVO getUserInfo(HttpSession session) {
        return getSuccessResponseVO(getUserInfoFromSession(session));
    }

    @RequestMapping("/logout")
    @GlobalInterceptor()
    public ResponseVO loguot(HttpSession session) {
        session.invalidate();
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getSysSetting")
    @GlobalInterceptor()
    public ResponseVO getSysSetting() {
        SysSettingDto settingDto= SysCacheUtils.getSysSetting();
        SysSetting4CommentDto commentDto= settingDto.getCommentSetting();
        Map<String,Object> result=new HashMap<>();
        result.put("commentOpen",commentDto.getCommentOpen());
        return getSuccessResponseVO(result);
    }

    @RequestMapping("/resetPwd")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO resetPwd(HttpSession session,
                               @VerifyParam(required = true) String email,
                               @VerifyParam(required = true,min = 8,max = 18,regex = VerifyRegexEnum.PASSWORD) String password,
                               @VerifyParam(required = true) String emailCode,
                               @VerifyParam(required = true) String checkCode) throws BusinessException {

        try {
            if(!checkCode.equalsIgnoreCase((String) session.getAttribute(Constants.CHECK_CODE_KEY))) {
                throw new BusinessException("图片验证码错误");
            }
            infoService.resetPwd(email,password,emailCode);
            return getSuccessResponseVO(null);
        }finally {
            session.removeAttribute(Constants.SESSION_KEY);
        }

    }

}
