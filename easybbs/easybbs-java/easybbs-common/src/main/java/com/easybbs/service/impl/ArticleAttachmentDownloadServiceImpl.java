package com.easybbs.service.impl;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.ArticleAttachmentDownload;
import com.easybbs.entity.query.ArticleAttachmentDownloadQuery;
import com.easybbs.entity.enums.PageSize;import com.easybbs.mapper.ArticleAttachmentDownloadMapper;
import com.easybbs.service.ArticleAttachmentDownloadService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
/**
 * &#064;Description  用户附件下载Service  &#064;Date  2024/01/12
 */
@Service("ArticleAttachmentDownloadService")
public class ArticleAttachmentDownloadServiceImpl implements ArticleAttachmentDownloadService{

	@Resource
	private ArticleAttachmentDownloadMapper<ArticleAttachmentDownload,ArticleAttachmentDownloadQuery> articleAttachmentDownloadMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<ArticleAttachmentDownload> findListByParam(ArticleAttachmentDownloadQuery query){
		return this.articleAttachmentDownloadMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(ArticleAttachmentDownloadQuery query){
		return this.articleAttachmentDownloadMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<ArticleAttachmentDownload> findListByPage(ArticleAttachmentDownloadQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ArticleAttachmentDownload> list = this.findListByParam(query);
		PaginationResultVO<ArticleAttachmentDownload> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(ArticleAttachmentDownloadQuery bean){
		return this.articleAttachmentDownloadMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<ArticleAttachmentDownloadQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.articleAttachmentDownloadMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<ArticleAttachmentDownloadQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.articleAttachmentDownloadMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据FileIdAndUserId查询
	 */
	@Override
	public ArticleAttachmentDownload getArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId){
		return this.articleAttachmentDownloadMapper.selectByFileIdAndUserId(fileId, userId);
	}
	/**
	 * 根据FileIdAndUserId更新
	 */
	@Override
	public Integer updateArticleAttachmentDownloadByFileIdAndUserId(ArticleAttachmentDownload bean, String fileId, String userId){
		return this.articleAttachmentDownloadMapper.updateByFileIdAndUserId(bean,fileId, userId);
	}
	/**
	 * 根据FileIdAndUserId删除
	 */
	@Override
	public Integer deleteArticleAttachmentDownloadByFileIdAndUserId(String fileId, String userId){
		return this.articleAttachmentDownloadMapper.deleteByFileIdAndUserId(fileId, userId);
	}
}