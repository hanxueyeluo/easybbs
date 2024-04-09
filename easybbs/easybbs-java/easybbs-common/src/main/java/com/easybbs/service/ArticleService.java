package com.easybbs.service;
import com.easybbs.entity.po.ArticleAttachment;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Article;
import com.easybbs.entity.query.ArticleQuery;
import com.easybbs.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
/**
 * @Description 文章信息Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface ArticleService{

	/**
	 * 根据条件查询列表
	 */
	List<Article> findListByParam(ArticleQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(ArticleQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Article> findListByPage(ArticleQuery query);

	/**
	 * 新增
	 */
	Integer add(ArticleQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<ArticleQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<ArticleQuery> listBean);

	/**
	 * 根据ArticleId查询
	 */
	Article getArticleByArticleId(String articleId);

	/**
	 * 根据ArticleId更新
	 */
	Integer updateArticleByArticleId(Article bean, String articleId);

	/**
	 * 根据ArticleId删除
	 */
	Integer deleteArticleByArticleId(String articleId);


	Article readArticle(String articleId) throws BusinessException;

	void postArticle(Boolean isAdmin,Article article, ArticleAttachment articleAttachment, MultipartFile cover,MultipartFile attachment) throws BusinessException;

	void updateArticle(Boolean isAdmin,Article article,ArticleAttachment articleAttachment,MultipartFile cover,MultipartFile attachment) throws BusinessException;

	void delArticle(String articleIds) throws BusinessException;

	void delArticleSingle(String articleId) throws BusinessException;

	void updateBoard(String articleId,Integer pBoardId,Integer boardId) throws BusinessException;

	void auditArticle(String articleIds) throws BusinessException;

	void auditArticleSingle(String articleId) throws BusinessException;
}