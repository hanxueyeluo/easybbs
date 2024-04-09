package com.easybbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 邮箱验证码Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
public interface CodeMapper<T, P> extends BaseMapper {
	/**
	 * 根据EmailAndCode查询
	 */
	T selectByEmailAndCode(@Param("email") String email, @Param("code") String code);

	/**
	 * 根据EmailAndCode更新
	 */
	Integer updateByEmailAndCode(@Param("bean") T t, @Param("email") String email, @Param("code") String code);

	/**
	 * 根据EmailAndCode删除
	 */
	Integer deleteByEmailAndCode(@Param("email") String email, @Param("code") String code);

	void  disableEmailCode(@Param("email") String email);
}