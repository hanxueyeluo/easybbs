package com.easybbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 文章信息Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
public interface ArticleMapper<T, P> extends BaseMapper {
	/**
	 * 根据ArticleId查询
	 */
	T selectByArticleId(@Param("articleId") String articleId);

	/**
	 * 根据ArticleId更新
	 */
	Integer updateByArticleId(@Param("bean") T t, @Param("articleId") String articleId);

	/**
	 * 根据ArticleId删除
	 */
	Integer deleteByArticleId(@Param("articleId") String articleId);


	void updateArticleCount(@Param("updateType") Integer updateType,@Param("changeCount") Integer changeCount,
							@Param("articleId") String articleId);

	void updateBoardNameBatch(@Param("boardType") Integer boardType,@Param("boardName") String boardName,@Param("boardId") Integer boardId);

	void updateStatusBatchByUserId(@Param("status") Integer status,@Param("userId") String userId);

}