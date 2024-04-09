package com.easybbs.aspect;

import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.dto.SysSettingDto;
import com.easybbs.entity.enums.DateTimePatternEnum;
import com.easybbs.entity.enums.ResponseCodeEnum;
import com.easybbs.entity.enums.UserOperFrequencyTypeEnum;
import com.easybbs.entity.query.ArticleQuery;
import com.easybbs.entity.query.CommentQuery;
import com.easybbs.entity.query.RecordQuery;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.ArticleService;
import com.easybbs.service.CommentService;
import com.easybbs.service.RecordService;
import com.easybbs.service.impl.CodeServiceImpl;
import com.easybbs.utils.DateUtils;
import com.easybbs.utils.StringTools;
import com.easybbs.utils.SysCacheUtils;
import com.easybbs.utils.VerifyUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Date;


@Component
@Aspect
public class OperactionAspect {

    private static final Logger logger= LoggerFactory.getLogger(CodeServiceImpl.class);

    private static final String[] TYPE_BASE={"java.lang.String,java.lang.Integer,java.lang.Long"};

    @Resource
    private ArticleService articleService;

    @Resource
    private CommentService commentService;

    @Resource
    RecordService recordService;


    @Pointcut("@annotation(com.easybbs.annotation.GlobalInterceptor)")
    private void requestInterceptor(){

    }
    @Around("requestInterceptor()")
    public Object interceptorDo(ProceedingJoinPoint point) throws BusinessException {
        try {
            Object target=point.getTarget();
            Object[] arguments=point.getArgs();
            String methodName=point.getSignature().getName();
            Class<?>[] praameterTypes=((MethodSignature)point.getSignature()).getMethod().getParameterTypes();
            Method method=target.getClass().getMethod(methodName,praameterTypes);
            GlobalInterceptor interceptor=method.getAnnotation(GlobalInterceptor.class);
            if (interceptor == null) {
                return null;
            }

            //检验登录
            if (interceptor.checkLogin()) {
                checkLogin();
            }
            /**
             * 校验参数
             */
            if (interceptor.checkParams()) {
                validateParams(method,arguments);
            }
            /**
             * 频次校验
             */
            this.checkFrequency(interceptor.frequencyType());

            Object pointResult=point.proceed();

            if (pointResult instanceof ResponseVO) {
                ResponseVO responseVO=(ResponseVO) pointResult;
                if (Constants.STATUS_SUCCESS.equals(responseVO.getStatus())) {
                    addOpCount(interceptor.frequencyType());
                }
            }

            return pointResult;
        }catch (BusinessException e){
            logger.error("全局拦截器异常",e);
            throw e;
        }catch (Exception e){
            logger.error("全局拦截器异常",e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }catch (Throwable e){
            logger.error("全局拦截器异常",e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }
    }

    void checkFrequency(UserOperFrequencyTypeEnum typeEnum) throws BusinessException {
        if (typeEnum == null||typeEnum==UserOperFrequencyTypeEnum.NO_CHECK) {
            return;
        }
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session= request.getSession();
        SessionWebUserDto webUserDto=(SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        String curDate= DateUtils.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        String sessionKey=Constants.SESSION_KEY_FREQUENCY+curDate+typeEnum.getOperType();
        Integer count=(Integer) session.getAttribute(sessionKey);
        SysSettingDto sysSettingDto= SysCacheUtils.getSysSetting();

        switch (typeEnum){
            case POST_ARTICLE:
                if (count == null) {
                    ArticleQuery articleQuery=new ArticleQuery();
                    articleQuery.setUserId(webUserDto.getUserId());
                    articleQuery.setPostTimeStart(curDate);
                    articleQuery.setPostTimeEnd(curDate);
                    count=articleService.findCountByParam(articleQuery);
                    if (count >=sysSettingDto.getPostSetting().getPostDayCountThreshold()) {
                        throw new BusinessException(ResponseCodeEnum.CODE_602);
                    }
                }
            break;
            case POST_COMMENT:
                if (count == null) {
                    CommentQuery commentQuery=new CommentQuery();
                    commentQuery.setUserId(webUserDto.getUserId());
                    commentQuery.setPostTimeStart(curDate);
                    commentQuery.setPostTimeEnd(curDate);
                    count=commentService.findCountByParam(commentQuery);
                }
                if (count>=sysSettingDto.getCommentSetting().getCommentDayCountThreshold()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
            break;
            case DO_LIKE:
                if (count == null) {
                    RecordQuery recordQuery=new RecordQuery();
                    recordQuery.setUserId(webUserDto.getUserId());
                    recordQuery.setCreateTimeStart(curDate);
                    recordQuery.setCreateTimeEnd(curDate);
                    count=recordService.findCountByParam(recordQuery);
                }
                if (count>=sysSettingDto.getLikeSetting().getLikeDayCountThreshold()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
            break;
            case IMAGE_UPLOAD:
                if (count == null) {
                    count=0;
                }
                if (count >= sysSettingDto.getPostSetting().getDayImageUploadCount()) {
                    throw new BusinessException(ResponseCodeEnum.CODE_602);
                }
                break;
        }
        session.setAttribute(sessionKey,count);
    }

    private void addOpCount(UserOperFrequencyTypeEnum typeEnum){
        if (typeEnum == null||typeEnum==UserOperFrequencyTypeEnum.NO_CHECK) {
            return;
        }
        HttpServletRequest request=((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session= request.getSession();
        SessionWebUserDto webUserDto=(SessionWebUserDto) session.getAttribute(Constants.SESSION_KEY);
        String curDate= DateUtils.format(new Date(), DateTimePatternEnum.YYYY_MM_DD.getPattern());
        String sessionKey=Constants.SESSION_KEY_FREQUENCY+curDate+typeEnum.getOperType();
        Integer count=(Integer) session.getAttribute(sessionKey);

        session.setAttribute(sessionKey,count+1);
    }

    private void checkLogin() throws BusinessException {
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session=request.getSession();
        Object obj=session.getAttribute(Constants.SESSION_KEY);
        if (obj == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_901);
        }
    }
    private void validateParams(Method m,Object[] arguments) throws BusinessException {
        Parameter[] parameters=m.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter=parameters[i];
            Object value=arguments[i];
            VerifyParam verifyParam=parameter.getAnnotation(VerifyParam.class);
            if (verifyParam == null) {
                continue;
            }
            if (ArrayUtils.contains(TYPE_BASE,parameter.getParameterizedType().getTypeName())) {
                checkValue(value,verifyParam);
            }else {

            }
        }
    }

    private void checkObjValue(Parameter parameter,Object value) throws BusinessException {
        try{
            String typeName=parameter.getParameterizedType().getTypeName();
            Class classz=Class.forName(typeName);
            Field[] fields=classz.getDeclaredFields();
            for (Field field:fields){
                VerifyParam fieldVerifyParam=field.getAnnotation(VerifyParam.class);
                if (fieldVerifyParam == null) {
                    continue;
                }
                field.setAccessible(true);
                Object resultValue=field.get(value);
                checkValue(resultValue,fieldVerifyParam);
            }
        }catch (Exception e){
            logger.error("校验参数失败",e);
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

    private void checkValue(Object value,VerifyParam verifyParam) throws BusinessException {
        Boolean isEmpty = value==null|| StringTools.isEmpty(value.toString());
        Integer length=value==null?0:value.toString().length();
        /**
         * 校验空
         */
        if (isEmpty&&verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        /**
         * 校验长度
         */
        if (!isEmpty&&(verifyParam.max()!=-1&&verifyParam.max()<length||verifyParam.min()!=-1)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        /**
         * 校验正则
         */
        if(!isEmpty&& !StringTools.isEmpty(verifyParam.regex().getRegex())&& !VerifyUtils.verify(verifyParam.regex(),String.valueOf(value))){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }
}
