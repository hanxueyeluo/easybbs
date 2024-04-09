package com.easybbs.annotation;


import com.easybbs.entity.enums.UserOperFrequencyTypeEnum;

import java.lang.annotation.*;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface GlobalInterceptor {
    /**
     * 是否需要登录
     */
    boolean checkLogin() default  false;
    /**
     * 是否校验参数
     */
    boolean checkParams() default false;
    /**
     * 校验频次
     */
    UserOperFrequencyTypeEnum frequencyType() default UserOperFrequencyTypeEnum.NO_CHECK;

}
