package com.easybbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 用户积分记录表Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
public interface IntegralRecordMapper<T, P> extends BaseMapper {
	/**
	 * 根据RecordId查询
	 */
	T selectByRecordId(@Param("recordId") Integer recordId);

	/**
	 * 根据RecordId更新
	 */
	Integer updateByRecordId(@Param("bean") T t, @Param("recordId") Integer recordId);

	/**
	 * 根据RecordId删除
	 */
	Integer deleteByRecordId(@Param("recordId") Integer recordId);

}