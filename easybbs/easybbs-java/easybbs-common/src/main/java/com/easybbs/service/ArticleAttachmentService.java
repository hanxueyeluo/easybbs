package com.easybbs.service;
import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.ArticleAttachment;
import com.easybbs.entity.query.ArticleAttachmentQuery;
import com.easybbs.exception.BusinessException;

import java.util.List;
/**
 * @Description 文件信息Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface ArticleAttachmentService{

	/**
	 * 根据条件查询列表
	 */
	List<ArticleAttachment> findListByParam(ArticleAttachmentQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ArticleAttachmentQuery query);

	/**
     * 分页查询
     */
	PaginationResultVO<ArticleAttachment> findListByPage(ArticleAttachmentQuery query);

	/**
	 * 新增
	 */
	Integer add(ArticleAttachmentQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ArticleAttachmentQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ArticleAttachmentQuery> listBean);

	/**
	 * 根据FileId查询
	 */
	ArticleAttachment getArticleAttachmentByFileId(String fileId);

	/**
	 * 根据FileId更新
	 */
	Integer updateArticleAttachmentByFileId(ArticleAttachment bean, String fileId);

	/**
	 * 根据FileId删除
	 */
	Integer deleteArticleAttachmentByFileId(String fileId);

	ArticleAttachment downloadAttachment(String fileId, SessionWebUserDto sessionWebUserDto) throws BusinessException;

}