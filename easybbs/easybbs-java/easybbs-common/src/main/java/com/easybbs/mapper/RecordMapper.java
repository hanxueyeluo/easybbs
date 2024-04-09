package com.easybbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 点赞记录Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
public interface RecordMapper<T, P> extends BaseMapper {
	/**
	 * 根据OpId查询
	 */
	T selectByOpId(@Param("opId") Integer opId);

	/**
	 * 根据OpId更新
	 */
	Integer updateByOpId(@Param("bean") T t, @Param("opId") Integer opId);

	/**
	 * 根据OpId删除
	 */
	Integer deleteByOpId(@Param("opId") Integer opId);

	/**
	 * 根据ObjectIdAndUserIdAndOpType查询
	 */
	T selectByObjectIdAndUserIdAndOpType(@Param("objectId") String objectId, @Param("userId") String userId, @Param("opType") Integer opType);

	/**
	 * 根据ObjectIdAndUserIdAndOpType更新
	 */
	Integer updateByObjectIdAndUserIdAndOpType(@Param("bean") T t, @Param("objectId") String objectId, @Param("userId") String userId, @Param("opType") Integer opType);

	/**
	 * 根据ObjectIdAndUserIdAndOpType删除
	 */
	Integer deleteByObjectIdAndUserIdAndOpType(@Param("objectId") String objectId, @Param("userId") String userId, @Param("opType") Integer opType);

}