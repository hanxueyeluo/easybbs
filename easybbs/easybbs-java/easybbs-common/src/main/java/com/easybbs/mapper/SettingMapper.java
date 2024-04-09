package com.easybbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * @Description 系统设置信息Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
@Component
public interface SettingMapper<T, P> extends BaseMapper {
	/**
	 * 根据Code查询
	 */
	T selectByCode(@Param("code") String code);

	/**
	 * 根据Code更新
	 */
	Integer updateByCode(@Param("bean") T t, @Param("code") String code);

	/**
	 * 根据Code删除
	 */
	Integer deleteByCode(@Param("code") String code);

}