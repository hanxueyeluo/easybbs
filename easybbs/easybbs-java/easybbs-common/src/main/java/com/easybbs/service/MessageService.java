package com.easybbs.service;
import com.easybbs.entity.dto.UserMessageCountDto;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Message;
import com.easybbs.entity.query.MessageQuery;

import java.util.List;
/**
 * @Description 用户消息Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface MessageService{

	/**
	 * 根据条件查询列表
	 */
	List<Message> findListByParam(MessageQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(MessageQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Message> findListByPage(MessageQuery query);

	/**
	 * 新增
	 */
	Integer add(Message bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<MessageQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<MessageQuery> listBean);

	/**
	 * 根据MessageId查询
	 */
	Message getMessageByMessageId(Integer messageId);

	/**
	 * 根据MessageId更新
	 */
	Integer updateMessageByMessageId(Message bean, Integer messageId);

	/**
	 * 根据MessageId删除
	 */
	Integer deleteMessageByMessageId(Integer messageId);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType查询
	 */
	Message getMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType更新
	 */
	Integer updateMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(Message bean, String articleId, Integer commentId, String sendUserId, Integer messageType);

	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType删除
	 */
	Integer deleteMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType);

	UserMessageCountDto getUserMessageCount(String userId);

	void readMessageByType(String receivedUserId,Integer messageType);

}