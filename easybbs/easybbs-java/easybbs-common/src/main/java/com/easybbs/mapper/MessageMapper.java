package com.easybbs.mapper;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Description 用户消息Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
public interface MessageMapper<T, P> extends BaseMapper {
	/**
	 * 根据MessageId查询
	 */
	T selectByMessageId(@Param("messageId") Integer messageId);

	/**
	 * 根据MessageId更新
	 */
	Integer updateByMessageId(@Param("bean") T t, @Param("messageId") Integer messageId);

	/**
	 * 根据MessageId删除
	 */
	Integer deleteByMessageId(@Param("messageId") Integer messageId);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType查询
	 */
	T selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("articleId") String articleId, @Param("commentId") Integer commentId, @Param("sendUserId") String sendUserId, @Param("messageType") Integer messageType);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType更新
	 */
	Integer updateByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("bean") T t, @Param("articleId") String articleId, @Param("commentId") Integer commentId, @Param("sendUserId") String sendUserId, @Param("messageType") Integer messageType);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType删除
	 */
	Integer deleteByArticleIdAndCommentIdAndSendUserIdAndMessageType(@Param("articleId") String articleId, @Param("commentId") Integer commentId, @Param("sendUserId") String sendUserId, @Param("messageType") Integer messageType);


	@MapKey("userId")
	List<Map>selectUserMessageCount(String userId);

	void updateMessageStatusBatch(@Param("receivedUserId") String receivedUserId,@Param("messageType") Integer messageType,@Param("status")Integer status);

}