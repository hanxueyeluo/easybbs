package com.easybbs.service;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.ArticleAttachmentDownload;
import com.easybbs.entity.query.ArticleAttachmentDownloadQuery;

import java.util.List;
/**
 * @Description 用户附件下载Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface ArticleAttachmentDownloadService{

	/**
	 * 根据条件查询列表
	 */
	List<ArticleAttachmentDownload> findListByParam(ArticleAttachmentDownloadQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ArticleAttachmentDownloadQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<ArticleAttachmentDownload> findListByPage(ArticleAttachmentDownloadQuery query);

	/**
	 * 新增
	 */
	Integer add(ArticleAttachmentDownloadQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ArticleAttachmentDownloadQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ArticleAttachmentDownloadQuery> listBean);

	/**
	 * 根据FileIdAndUserId查询
	 */
	ArticleAttachmentDownload getArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId);

	/**
	 * 根据FileIdAndUserId更新
	 */
	Integer updateArticleAttachmentDownloadByFileIdAndUserId(ArticleAttachmentDownload bean, String fileId, String userId);

	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId);

}