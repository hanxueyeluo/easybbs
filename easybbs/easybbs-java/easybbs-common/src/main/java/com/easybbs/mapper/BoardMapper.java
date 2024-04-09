package com.easybbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 文章板块信息Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
public interface BoardMapper<T, P> extends BaseMapper {
	/**
	 * 根据BoardId查询
	 */
	T selectByBoardId(@Param("boardId") Integer boardId);

	/**
	 * 根据BoardId更新
	 */
	Integer updateByBoardId(@Param("bean") T t, @Param("boardId") Integer boardId);

	/**
	 * 根据BoardId删除
	 */
	Integer deleteByBoardId(@Param("boardId") Integer boardId);

}