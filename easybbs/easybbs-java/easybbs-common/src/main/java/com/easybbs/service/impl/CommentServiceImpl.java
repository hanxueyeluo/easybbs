package com.easybbs.service.impl;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.FileUpLoadDto;
import com.easybbs.entity.enums.*;
import com.easybbs.entity.po.Article;
import com.easybbs.entity.po.Info;
import com.easybbs.entity.po.Message;
import com.easybbs.entity.query.ArticleQuery;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Comment;
import com.easybbs.entity.query.CommentQuery;
import com.easybbs.exception.BusinessException;
import com.easybbs.mapper.ArticleMapper;
import com.easybbs.mapper.CommentMapper;
import com.easybbs.service.CommentService;
import com.easybbs.service.InfoService;
import com.easybbs.service.MessageService;
import com.easybbs.utils.FileUtils;
import com.easybbs.utils.StringTools;
import com.easybbs.utils.SysCacheUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description 评论Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("CommentService")
public class CommentServiceImpl implements CommentService{

	@Resource
	private ArticleMapper<Article, ArticleQuery>articleMapper;

	@Resource
	private CommentMapper<Comment,CommentQuery> commentMapper;

	@Resource
	private InfoService infoService;

	@Resource
	private MessageService messageService;

	@Resource
	private FileUtils fileUtils;

	@Lazy
	@Resource
	private CommentService commentService;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Comment> findListByParam(CommentQuery query){
		List<Comment> list=this.commentMapper.selectList(query);
		//获取二级评论
		if (query.getLoadChildren() != null && query.getLoadChildren()) {
			CommentQuery subQuery=new CommentQuery();
			subQuery.setQueryLikeType(query.getQueryLikeType());
			subQuery.setCurrentUserId(query.getCurrentUserId());
			subQuery.setArticleId(query.getArticleId());
			subQuery.setStatus(query.getStatus());
			List<Integer> pcommentIdList=list.stream().map(Comment::getCommentId).distinct().collect(Collectors.toList());
			subQuery.setPcommentIdList(pcommentIdList);
			List<Comment> subCommentList=this.commentMapper.selectList(subQuery);

			Map<Integer,List<Comment>> tempMap=subCommentList.stream().collect(Collectors.groupingBy(Comment::getpCommentId));

			list.forEach(item ->{
				item.setChildren(tempMap.get(item.getCommentId()));
			});
		}
		return list;
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(CommentQuery query){
		return this.commentMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Comment> findListByPage(CommentQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Comment> list = this.findListByParam(query);
		PaginationResultVO<Comment> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(CommentQuery bean){
		return this.commentMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<CommentQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.commentMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<CommentQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.commentMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据CommentId查询
	 */
	@Override
	public Comment getCommentByCommentId(Integer commentId){
		return this.commentMapper.selectByCommentId(commentId);
	}
	/**
	 * 根据CommentId更新
	 */
	@Override
	public Integer updateCommentByCommentId(Comment bean, Integer commentId){
		return this.commentMapper.updateByCommentId(bean,commentId);
	}
	/**
	 * 根据CommentId删除
	 */
	@Override
	public Integer deleteCommentByCommentId(Integer commentId){
		return this.commentMapper.deleteByCommentId(commentId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeTopType(String userId, Integer commentId, Integer topType) throws BusinessException {
		CommentTopTypeEnum topTypeEnum=CommentTopTypeEnum.getByType(topType);
		if (topTypeEnum == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		Comment comment=commentMapper.selectByCommentId(commentId);
		if (comment == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		Article article=articleMapper.selectByArticleId(comment.getArticleId());
		if (article == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (!article.getUserId().equals(userId)||comment.getpCommentId() !=0) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		if (comment.getTopType().equals(topType)) {
			return;
		}
		if (CommentTopTypeEnum.TOP.getType().equals(topType)) {
			commentMapper.updateTopTypeByArticleId(article.getArticleId());
		}
		Comment updateInfo=new Comment();
		updateInfo.setTopType(topType);
		commentMapper.updateByCommentId(updateInfo,commentId);
	}

	@Override
	public void postComment(Comment comment, MultipartFile image) throws BusinessException {
		Article article=articleMapper.selectByArticleId(comment.getArticleId());
		if (article == null || !ArticleStatusEnum.AUDIT.getStatus().equals(article.getStatus())) {
			throw new BusinessException("评论文章不存在");
		}
		Comment pComment=null;
		if (comment.getpCommentId()!=0) {
			pComment=commentMapper.selectByCommentId(comment.getpCommentId());
			if (pComment == null) {
				throw new BusinessException("回复的评论不存在");
			}
		}
		//判断回复的用户是否存在
		if (!StringTools.isEmpty(comment.getReplyNickName())) {
			Info info=infoService.getInfoByUserId(comment.getReplyUserId());
			if (info == null) {
				throw new BusinessException("回复的用户不存在");
			}
			comment.setReplyNickName(info.getNickName());
		}
		comment.setPostTime(new Date());
		if (image != null) {
			FileUpLoadDto upLoadDto=fileUtils.uploadFile2Local(image,Constants.FILE_FOLDER_IMAGE,FileUploadTypeEnum.COMMENT_IMAGE);
			comment.setImgPath(upLoadDto.getLocalPath());
		}
		Boolean needAudit = SysCacheUtils.getSysSetting().getAuditSetting().getCommentAudit();
		comment.setStatus(needAudit? CommentStatusEnum.NO_AUDIT.getStatus() : CommentStatusEnum.AUDIT.getStatus());
		this.commentMapper.insert(comment);

		if (needAudit) {
			return;
		}
		updateCommentInfo(comment,article,pComment);
	}
	public void updateCommentInfo(Comment comment,Article article,Comment pComment) throws BusinessException {
		Integer commentIntegral=SysCacheUtils.getSysSetting().getCommentSetting().getCommentIntegral();
		if (commentIntegral>0) {
			this.infoService.updateUserIntegral(comment.getUserId(),UserIntegralOperTypeEnum.POST_COMMENT,UserIntegralChangeTypeEnum.ADD.getChangeType(), commentIntegral);
		}
		if (comment.getpCommentId()==0) {
			this.articleMapper.updateArticleCount(UpdateArticleCountTypeEnum.COMMENT_COUNT.getType(), Constants.ONE, comment.getArticleId());
		}
		//记录消息
		Message message=new Message();
		message.setMessageType(MessageTypeEnum.COMMENT.getType());
		message.setCreateTime(new Date());
		message.setArticleId(comment.getArticleId());
		message.setCommentId(comment.getCommentId());
		message.setSendUserId(comment.getUserId());
		message.setSendNickName(comment.getNickName());
		message.setStatus(MessageStatusEnum.NO_READ.getStatus());
		message.setArticleTitle(article.getTitle());
		if (comment.getpCommentId() == 0) {
			message.setReceivedUserId(article.getUserId());
		} else if (comment.getpCommentId()!=0&&StringTools.isEmpty(comment.getReplyUserId())) {
			message.setReceivedUserId(pComment.getUserId());
		} else if (comment.getpCommentId()!=0 && !StringTools.isEmpty(comment.getReplyUserId())) {
			message.setReceivedUserId(comment.getReplyUserId());
		}
		if (!comment.getUserId().equals(message.getReceivedUserId())) {
			messageService.add(message);
		}
	}

	@Override
	public void delComment(String commentIds) throws BusinessException {
		String[] commentIdArray=commentIds.split(",");
		for (String commentIdStr : commentIdArray){
			Integer commentId=Integer.parseInt(commentIdStr);
			commentService.delCommentSingle(commentId);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delCommentSingle(Integer commentId) throws BusinessException {
		Comment comment=commentMapper.selectByCommentId(commentId);
		if (comment == null||CommentStatusEnum.DEL.getStatus().equals(comment.getStatus())) {
			return;
		}
		Comment forumcomment=new Comment();
		forumcomment.setStatus(CommentStatusEnum.DEL.getStatus());
		commentMapper.updateByCommentId(forumcomment,commentId);

		//删除已审核的文章，更新文章数量
		if (CommentStatusEnum.AUDIT.getStatus().equals(comment.getStatus())) {
			if (comment.getpCommentId()==0) {
				articleMapper.updateArticleCount(UpdateArticleCountTypeEnum.COMMENT_COUNT.getType(), Constants.MINUS_ONE, comment.getArticleId());
			}
			Integer integral=SysCacheUtils.getSysSetting().getCommentSetting().getCommentIntegral();
			infoService.updateUserIntegral(comment.getUserId(), UserIntegralOperTypeEnum.DEL_COMMENT,UserIntegralChangeTypeEnum.REDUCE.getChangeType(), integral);
		}

		Message message=new Message();
		message.setReceivedUserId(comment.getUserId());
		message.setMessageType(MessageTypeEnum.SYS.getType());
		message.setCreateTime(new Date());
		message.setStatus(MessageStatusEnum.NO_READ.getStatus());
		message.setMessageContent("评论【"+comment.getContent()+"】被管理员删除");
		messageService.add(message);

	}

	@Override
	public void auditComment(String commentIds) throws BusinessException {
		String[] commentIdArray=commentIds.split(",");
		for (String commentStr:commentIdArray) {
			Integer commentId=Integer.parseInt(commentStr);
			commentService.auditCommentSingle(commentId);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void auditCommentSingle(Integer commentId) throws BusinessException {
		Comment comment=commentMapper.selectByCommentId(commentId);
		if (!CommentStatusEnum.NO_AUDIT.getStatus().equals(comment.getStatus())) {
			return;
		}
		Comment comment1=new Comment();
		comment1.setStatus(CommentStatusEnum.AUDIT.getStatus());
		commentMapper.updateByCommentId(comment1,commentId);
		Article article=articleMapper.selectByArticleId(comment.getArticleId());
		Comment pComment=null;
		if (comment.getpCommentId() != 0&&StringTools.isEmpty(comment.getReplyUserId())) {
			pComment=commentMapper.selectByCommentId(comment.getpCommentId());
		}
		updateCommentInfo(comment,article,pComment);
	}
}