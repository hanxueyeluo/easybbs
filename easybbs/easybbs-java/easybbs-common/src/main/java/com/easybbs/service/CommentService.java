package com.easybbs.service;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Comment;
import com.easybbs.entity.query.CommentQuery;
import com.easybbs.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
/**
 * @Description 评论Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface CommentService{

	/**
	 * 根据条件查询列表
	 */
	List<Comment> findListByParam(CommentQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(CommentQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Comment> findListByPage(CommentQuery query);

	/**
	 * 新增
	 */
	Integer add(CommentQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<CommentQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<CommentQuery> listBean);

	/**
	 * 根据CommentId查询
	 */
	Comment getCommentByCommentId(Integer commentId);

	/**
	 * 根据CommentId更新
	 */
	Integer updateCommentByCommentId(Comment bean, Integer commentId);

	/**
	 * 根据CommentId删除
	 */
	Integer deleteCommentByCommentId(Integer commentId);

	void changeTopType(String userId,Integer commentId,Integer topType) throws BusinessException;

	void postComment(Comment comment, MultipartFile image) throws BusinessException;

	void delComment(String commentIds) throws BusinessException;

	void delCommentSingle(Integer commentIds) throws BusinessException;

	void auditComment(String commentIds) throws BusinessException;

	void auditCommentSingle(Integer commentIds) throws BusinessException;

}