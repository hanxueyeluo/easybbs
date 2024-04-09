package com.easybbs.service.impl;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.enums.*;
import com.easybbs.entity.po.*;
import com.easybbs.entity.query.ArticleAttachmentDownloadQuery;
import com.easybbs.entity.query.MessageQuery;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.query.ArticleAttachmentQuery;
import com.easybbs.exception.BusinessException;
import com.easybbs.mapper.ArticleAttachmentDownloadMapper;
import com.easybbs.mapper.ArticleAttachmentMapper;
import com.easybbs.mapper.MessageMapper;
import com.easybbs.service.ArticleAttachmentService;
import com.easybbs.service.ArticleService;
import com.easybbs.service.InfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
/**
 * @Description 文件信息Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("ArticleAttachmentService")
public class ArticleAttachmentServiceImpl implements ArticleAttachmentService{

	@Resource
	private ArticleAttachmentMapper<ArticleAttachment,ArticleAttachmentQuery> articleAttachmentMapper;

	@Resource
	private ArticleAttachmentDownloadMapper<ArticleAttachmentDownload, ArticleAttachmentDownloadQuery>articleAttachmentDownloadMapper;

	@Resource
	private InfoService infoService;

	@Resource
	private ArticleService articleService;

	@Resource
	private MessageMapper<Message, MessageQuery>messageMapper;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<ArticleAttachment> findListByParam(ArticleAttachmentQuery query){
		return this.articleAttachmentMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(ArticleAttachmentQuery query){
		return this.articleAttachmentMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<ArticleAttachment> findListByPage(ArticleAttachmentQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<ArticleAttachment> list = this.findListByParam(query);
		PaginationResultVO<ArticleAttachment> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(ArticleAttachmentQuery bean){
		return this.articleAttachmentMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<ArticleAttachmentQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.articleAttachmentMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<ArticleAttachmentQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.articleAttachmentMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据FileId查询
	 */
	@Override
	public ArticleAttachment getArticleAttachmentByFileId(String fileId){
		return this.articleAttachmentMapper.selectByFileId(fileId);
	}
	/**
	 * 根据FileId更新
	 */
	@Override
	public Integer updateArticleAttachmentByFileId(ArticleAttachment bean, String fileId){
		return this.articleAttachmentMapper.updateByFileId(bean,fileId);
	}
	/**
	 * 根据FileId删除
	 */
	@Override
	public Integer deleteArticleAttachmentByFileId(String fileId){
		return this.articleAttachmentMapper.deleteByFileId(fileId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public ArticleAttachment downloadAttachment(String fileId, SessionWebUserDto sessionWebUserDto) throws BusinessException {
		ArticleAttachment articleAttachment=this.articleAttachmentMapper.selectByFileId(fileId);
		if (articleAttachment == null) {
			throw new BusinessException("附件不存在");
		}
		ArticleAttachmentDownload download=null;
		if (articleAttachment.getIntegral()>0&&!sessionWebUserDto.getUserId().equals(articleAttachment.getUserId())) {
			download=this.articleAttachmentDownloadMapper.selectByFileIdAndUserId(fileId,sessionWebUserDto.getUserId());
			if (download == null) {
				Info info=infoService.getInfoByUserId(sessionWebUserDto.getUserId());
				if (info.getCurrentIntegral()-articleAttachment.getIntegral()<0) {
					throw new BusinessException("积分不够");
				}
			}
		}
		ArticleAttachmentDownload updateDownload=new ArticleAttachmentDownload();
		updateDownload.setArticleId(articleAttachment.getArticleId());
		updateDownload.setFileId(fileId);
		updateDownload.setUserId(sessionWebUserDto.getUserId());
		updateDownload.setDownloadCount(Constants.ONE);
		this.articleAttachmentDownloadMapper.insertOrUpdate(updateDownload);
		this.articleAttachmentMapper.updateDownloadCount(fileId);
		if (sessionWebUserDto.getUserId().equals(articleAttachment.getUserId()) || download != null) {
			return articleAttachment;
		}
		//扣除下载人积分
		infoService.updateUserIntegral(sessionWebUserDto.getUserId(), UserIntegralOperTypeEnum.USER_DOWNLOAD_ATTACHMENT,
				UserIntegralChangeTypeEnum.REDUCE.getChangeType(),articleAttachment.getIntegral());
		//给附件提供者增加积分
		infoService.updateUserIntegral(sessionWebUserDto.getUserId(), UserIntegralOperTypeEnum.DOWNLOAD_ATTACHMENT,
				UserIntegralChangeTypeEnum.ADD.getChangeType(),articleAttachment.getIntegral());
		//记录消息
		Article article=articleService.getArticleByArticleId(articleAttachment.getArticleId());
		Message message=new Message();
		message.setMessageType(MessageTypeEnum.DOWNLOAD_ATTACHMENT.getType());
		message.setCreateTime(new Date());
		message.setArticleId(article.getArticleId());
		message.setArticleTitle(article.getTitle());
		message.setReceivedUserId(article.getUserId());
		message.setCommentId(Constants.ZERO);
		message.setSendUserId(sessionWebUserDto.getUserId());
		message.setSendNickName(sessionWebUserDto.getNickName());
		message.setStatus(MessageStatusEnum.NO_READ.getStatus());
		return articleAttachment;
	}
}