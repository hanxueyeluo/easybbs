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
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
    @Before("requestInterceptor()")
    public void interceptorDo(JoinPoint point) throws BusinessException {
        try {
            Object target=point.getTarget();
            Object[] arguments=point.getArgs();
            String methodName=point.getSignature().getName();
            Class<?>[] praameterTypes=((MethodSignature)point.getSignature()).getMethod().getParameterTypes();
            Method method=target.getClass().getMethod(methodName,praameterTypes);
            GlobalInterceptor interceptor=method.getAnnotation(GlobalInterceptor.class);
            if (interceptor == null) {
                return;
            }

            /**
             * 校验参数
             */
            if (interceptor.checkParams()) {
                validateParams(method,arguments);
            }

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
