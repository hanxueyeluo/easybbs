package com.easybbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 评论Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
public interface CommentMapper<T, P> extends BaseMapper {
	/**
	 * 根据CommentId查询
	 */
	T selectByCommentId(@Param("commentId") Integer commentId);

	/**
	 * 根据CommentId更新
	 */
	Integer updateByCommentId(@Param("bean") T t, @Param("commentId") Integer commentId);

	/**
	 * 根据CommentId删除
	 */
	Integer deleteByCommentId(@Param("commentId") Integer commentId);


	void updateCommentGoodCount(@Param("changeCount") Integer changeCount,@Param("commentId") Integer commentId);

	void updateTopTypeByArticleId(@Param("articleId") String articleId );

	void updateStatusBatchByUserId(@Param("status") Integer status,@Param("userId") String userId);

}