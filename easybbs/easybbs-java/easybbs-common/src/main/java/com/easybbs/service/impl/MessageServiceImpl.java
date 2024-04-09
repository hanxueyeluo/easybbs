package com.easybbs.service.impl;
import com.easybbs.entity.dto.UserMessageCountDto;
import com.easybbs.entity.enums.MessageStatusEnum;
import com.easybbs.entity.enums.MessageTypeEnum;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Message;
import com.easybbs.entity.query.MessageQuery;
import com.easybbs.entity.enums.PageSize;import com.easybbs.mapper.MessageMapper;
import com.easybbs.service.MessageService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description 用户消息Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("MessageService")
public class MessageServiceImpl implements MessageService{

	@Resource
	private MessageMapper<Message,MessageQuery> messageMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Message> findListByParam(MessageQuery query){
		return this.messageMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(MessageQuery query){
		return this.messageMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Message> findListByPage(MessageQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Message> list = this.findListByParam(query);
		PaginationResultVO<Message> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(Message bean){
		return this.messageMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<MessageQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.messageMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<MessageQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.messageMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据MessageId查询
	 */
	@Override
	public Message getMessageByMessageId(Integer messageId){
		return this.messageMapper.selectByMessageId(messageId);
	}
	/**
	 * 根据MessageId更新
	 */
	@Override
	public Integer updateMessageByMessageId(Message bean, Integer messageId){
		return this.messageMapper.updateByMessageId(bean,messageId);
	}
	/**
	 * 根据MessageId删除
	 */
	@Override
	public Integer deleteMessageByMessageId(Integer messageId){
		return this.messageMapper.deleteByMessageId(messageId);
	}
	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType查询
	 */
	@Override
	public Message getMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType){
		return this.messageMapper.selectByArticleIdAndCommentIdAndSendUserIdAndMessageType(articleId, commentId, sendUserId, messageType);
	}
	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType更新
	 */
	@Override
	public Integer updateMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(Message bean, String articleId, Integer commentId, String sendUserId, Integer messageType){
		return this.messageMapper.updateByArticleIdAndCommentIdAndSendUserIdAndMessageType(bean,articleId, commentId, sendUserId, messageType);
	}
	/**
	 * 根据ArticleIdAndCommentIdAndSendUserIdAndMessageType删除
	 */
	@Override
	public Integer deleteMessageByArticleIdAndCommentIdAndSendUserIdAndMessageType(String articleId, Integer commentId, String sendUserId, Integer messageType){
		return this.messageMapper.deleteByArticleIdAndCommentIdAndSendUserIdAndMessageType(articleId, commentId, sendUserId, messageType);
	}

	@Override
	public UserMessageCountDto getUserMessageCount(String userId) {
		List<Map>mapList=this.messageMapper.selectUserMessageCount(userId);
		UserMessageCountDto messageCountDto=new UserMessageCountDto();
		Long totalCount=0L;
		for (Map item:mapList){
			Integer type=(Integer) item.get("messageType");
			Long count=(Long) item.get("count");
			totalCount=totalCount+count;
			MessageTypeEnum messageType=MessageTypeEnum.getByType(type);
			if (messageType != null) {
				switch (messageType){
					case SYS:
						messageCountDto.setSys(count);
						break;
					case COMMENT:
						messageCountDto.setReply(count);
						break;
					case ARTICLE_LIKE:
						messageCountDto.setLikePost(count);
						break;
					case COMMENT_LIKE:
						messageCountDto.setLikeComment(count);
						break;
					case DOWNLOAD_ATTACHMENT:
						messageCountDto.setDownloadAttachment(count);
						break;
				}
			}
		}
		messageCountDto.setTotal(totalCount);
		return messageCountDto;
	}

	@Override
	public void readMessageByType(String receivedUserId, Integer messageType) {
		this.messageMapper.updateMessageStatusBatch(receivedUserId,messageType, MessageStatusEnum.READ.getStatus());
	}
}