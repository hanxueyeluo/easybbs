package com.easybbs.service.impl;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.enums.*;
import com.easybbs.entity.po.Article;
import com.easybbs.entity.po.Comment;
import com.easybbs.entity.po.Message;
import com.easybbs.entity.query.*;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Record;
import com.easybbs.exception.BusinessException;
import com.easybbs.mapper.ArticleMapper;
import com.easybbs.mapper.CommentMapper;
import com.easybbs.mapper.MessageMapper;
import com.easybbs.mapper.RecordMapper;
import com.easybbs.service.RecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
/**
 * @Description 点赞记录Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("RecordService")
public class RecordServiceImpl implements RecordService{

	@Resource
	private ArticleMapper<Article, ArticleQuery>articleMapper;

	@Resource
	private MessageMapper<Message, MessageQuery>messageMapper;

	@Resource
	private RecordMapper<Record,RecordQuery> recordMapper;

	@Resource
	private CommentMapper<Comment, CommentQuery>commentMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Record> findListByParam(RecordQuery query){
		return this.recordMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(RecordQuery query){
		return this.recordMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Record> findListByPage(RecordQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Record> list = this.findListByParam(query);
		PaginationResultVO<Record> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(RecordQuery bean){
		return this.recordMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<RecordQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.recordMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<RecordQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.recordMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据OpId查询
	 */
	@Override
	public Record getRecordByOpId(Integer opId){
		return this.recordMapper.selectByOpId(opId);
	}
	/**
	 * 根据OpId更新
	 */
	@Override
	public Integer updateRecordByOpId(Record bean, Integer opId){
		return this.recordMapper.updateByOpId(bean,opId);
	}
	/**
	 * 根据OpId删除
	 */
	@Override
	public Integer deleteRecordByOpId(Integer opId){
		return this.recordMapper.deleteByOpId(opId);
	}
	/**
	 * 根据ObjectIdAndUserIdAndOpType查询
	 */
	@Override
	public Record getRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType){
		return this.recordMapper.selectByObjectIdAndUserIdAndOpType(objectId, userId, opType);
	}
	/**
	 * 根据ObjectIdAndUserIdAndOpType更新
	 */
	@Override
	public Integer updateRecordByObjectIdAndUserIdAndOpType(Record bean, String objectId, String userId, Integer opType){
		return this.recordMapper.updateByObjectIdAndUserIdAndOpType(bean,objectId, userId, opType);
	}
	/**
	 * 根据ObjectIdAndUserIdAndOpType删除
	 */
	@Override
	public Integer deleteRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType){
		return this.recordMapper.deleteByObjectIdAndUserIdAndOpType(objectId, userId, opType);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void doLike(String objectId, String userId, String nikeName, OperRecordOpTypeEnum opTypeEnum) throws BusinessException {
		Message message=new Message();
		message.setCreateTime(new Date());
		switch (opTypeEnum){
			case ARTICLE_LIKE:
				Article article=articleMapper.selectByArticleId(objectId);
				if (article == null) {
					throw new BusinessException("文章不存在");
				}
				articleLike(objectId,article,userId,opTypeEnum);
				message.setArticleId(objectId);
				message.setArticleTitle(article.getTitle());
				message.setMessageType(MessageTypeEnum.ARTICLE_LIKE.getType());
				message.setCommentId(Constants.ZERO);
				message.setReceivedUserId(article.getUserId());
				break;
			case COMMENT_LIKE:
				Comment comment=commentMapper.selectByCommentId(Integer.parseInt(objectId));
				if (comment == null) {
					throw new BusinessException("评论不存在");
				}
				commentLike(objectId,comment, userId, opTypeEnum);
				article=articleMapper.selectByArticleId(comment.getArticleId());
				message.setArticleId(objectId);
				message.setArticleTitle(article.getTitle());
				message.setMessageType(MessageTypeEnum.ARTICLE_LIKE.getType());
				message.setCommentId(comment.getCommentId());
				message.setReceivedUserId(comment.getUserId());
				message.setMessageContent(comment.getContent());
				break;
		}
		message.setSendUserId(userId);
		message.setSendNickName(nikeName);
		message.setStatus(MessageStatusEnum.NO_READ.getStatus());
		if (!userId.equals(message.getReceivedUserId())) {
			Message dbInfo=messageMapper.selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(message.getArticleId(),
					message.getCommentId(), message.getSendUserId(), message.getMessageType());
			if (dbInfo == null) {
				messageMapper.insert(message);
			}
		}
	}
	public void articleLike(String objectId,Article article,String userId,OperRecordOpTypeEnum opTypeEnum) {
		Record record=this.recordMapper.selectByObjectIdAndUserIdAndOpType(objectId,userId,opTypeEnum.getType());
		if (record != null) {
			this.recordMapper.deleteByObjectIdAndUserIdAndOpType(objectId,userId,opTypeEnum.getType());
			articleMapper.updateArticleCount(UpdateArticleCountTypeEnum.GOOD_COUNT.getType(), Constants.MINUS_ONE,objectId);
		}else {
			Record likeRecord=new Record();
			likeRecord.setObjectId(objectId);
			likeRecord.setUserId(userId);
			likeRecord.setOpType(opTypeEnum.getType());
			likeRecord.setCreateTime(new Date());
			likeRecord.setAuthorUserId(article.getUserId());
			this.recordMapper.insert(likeRecord);
			articleMapper.updateArticleCount(UpdateArticleCountTypeEnum.GOOD_COUNT.getType(), Constants.ONE,objectId);
		}
	}
	public void commentLike(String objectId,Comment comment,String userId,OperRecordOpTypeEnum opTypeEnum) throws BusinessException {
		Record record=recordMapper.selectByObjectIdAndUserIdAndOpType(objectId,userId,opTypeEnum.getType());
		if (record != null) {
			recordMapper.deleteByObjectIdAndUserIdAndOpType(objectId,userId,opTypeEnum.getType());
			commentMapper.updateCommentGoodCount(Constants.MINUS_ONE,Integer.parseInt(objectId));
		}else {
			Record likeRecorde=new Record();
			likeRecorde.setObjectId(objectId);
			likeRecorde.setUserId(userId);
			likeRecorde.setOpType(opTypeEnum.getType());
			likeRecorde.setCreateTime(new Date());
			likeRecorde.setAuthorUserId(comment.getUserId());
			recordMapper.insert(likeRecorde);
			commentMapper.updateCommentGoodCount(Constants.ONE,Integer.parseInt(objectId));
		}
	}
}