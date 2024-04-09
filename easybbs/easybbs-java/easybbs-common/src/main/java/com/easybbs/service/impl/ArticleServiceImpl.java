package com.easybbs.service.impl;
import com.easybbs.entity.config.AppConfig;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.FileUpLoadDto;
import com.easybbs.entity.dto.SysSetting4AuditDto;
import com.easybbs.entity.enums.*;
import com.easybbs.entity.po.ArticleAttachment;
import com.easybbs.entity.po.Board;
import com.easybbs.entity.po.Message;
import com.easybbs.entity.query.ArticleAttachmentQuery;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Article;
import com.easybbs.entity.query.ArticleQuery;
import com.easybbs.exception.BusinessException;
import com.easybbs.mapper.ArticleAttachmentMapper;
import com.easybbs.mapper.ArticleMapper;
import com.easybbs.service.ArticleService;
import com.easybbs.service.BoardService;
import com.easybbs.service.InfoService;
import com.easybbs.service.MessageService;
import com.easybbs.utils.FileUtils;
import com.easybbs.utils.ImageUtils;
import com.easybbs.utils.StringTools;
import com.easybbs.utils.SysCacheUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;
/**
 * @Description 文章信息Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("ArticleService")
public class ArticleServiceImpl implements ArticleService{

	@Resource
	private ImageUtils imageUtils;

	@Resource
	private InfoService infoService;

	@Resource
	private ArticleAttachmentMapper<ArticleAttachment, ArticleAttachmentQuery>articleAttachmentMapper;

	@Resource
	private FileUtils fileUtils;

	@Resource
	private BoardService boardService;

	@Resource
	private AppConfig appConfig;

	@Lazy
	@Resource
	private ArticleService articleService;

	@Resource
	private MessageService messageService;

	@Resource
	private ArticleMapper<Article,ArticleQuery> articleMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Article> findListByParam(ArticleQuery query){
		return this.articleMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(ArticleQuery query){
		return this.articleMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Article> findListByPage(ArticleQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Article> list = this.findListByParam(query);
		PaginationResultVO<Article> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(ArticleQuery bean){
		return this.articleMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<ArticleQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.articleMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<ArticleQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.articleMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据ArticleId查询
	 */
	@Override
	public Article getArticleByArticleId(String articleId){
		return this.articleMapper.selectByArticleId(articleId);
	}
	/**
	 * 根据ArticleId更新
	 */
	@Override
	public Integer updateArticleByArticleId(Article bean, String articleId){
		return this.articleMapper.updateByArticleId(bean,articleId);
	}
	/**
	 * 根据ArticleId删除
	 */
	@Override
	public Integer deleteArticleByArticleId(String articleId){
		return this.articleMapper.deleteByArticleId(articleId);
	}

	@Override
	public Article readArticle(String articleId) throws BusinessException {
		Article article=this.articleMapper.selectByArticleId(articleId);
		if (article == null) {
			throw new BusinessException(ResponseCodeEnum.CODE_404);
		}
		if (ArticleStatusEnum.AUDIT.getStatus().equals(article.getStatus())) {
			this.articleMapper.updateArticleCount(UpdateArticleCountTypeEnum.READ_COUNT.getType(), Constants.ONE,articleId);
		}
		return article;
	}

	@Override
	public void postArticle(Boolean isAdmin, Article article, ArticleAttachment articleAttachment, MultipartFile cover, MultipartFile attachment) throws BusinessException {
		resetBoardInfo(isAdmin,article);
		Date curDate=new Date();
		String articleId= StringTools.getRandomString(Constants.LENGTH_15);
		article.setArticleId(articleId);
		article.setPostTime(curDate);
		article.setLastUpdateTime(curDate);
		if (cover != null) {
			FileUpLoadDto fileUpLoadDto=fileUtils.uploadFile2Local(cover, Constants.FILE_FOLDER_IMAGE,FileUploadTypeEnum.ARTICLE_COVER);
			article.setCover(fileUpLoadDto.getLocalPath());
		}
		if (attachment != null) {
			uploadAttachment(article,articleAttachment,attachment,false);
			article.setAttachmentType(ArticleAttachmentTypeEnum.HAVE_ATTACHMENT.getType());
		}else {
			article.setAttachmentType(ArticleAttachmentTypeEnum.NO_ATTACHMENT.getType());

		}
		//文章审核信息
		if (isAdmin) {
			article.setStatus(ArticleStatusEnum.AUDIT.getStatus());
		}else {
			SysSetting4AuditDto auditDto=SysCacheUtils.getSysSetting().getAuditSetting();
			article.setStatus(auditDto.getPostAudit()?ArticleStatusEnum.NO_AUDIT.getStatus() : ArticleStatusEnum.AUDIT.getStatus());
		}

		//替换图片
		String content=article.getContent();
		if (!StringTools.isEmpty(content)) {
			String month=imageUtils.resetImageHtml(content);
			String replaceMonth="/"+month+"/";
			content=content.replace(Constants.FILE_FOLDER_TEMP,replaceMonth);
			article.setContent(content);
			String markdownContent=article.getMarkdownContent();
			if (!StringTools.isEmpty(markdownContent)) {
				markdownContent=markdownContent.replace(Constants.FILE_FOLDER_TEMP,replaceMonth);
				article.setMarkdownContent(markdownContent);
			}
		}
		this.articleMapper.insert(article);
		//增加积分
		Integer postIntegral=SysCacheUtils.getSysSetting().getPostSetting().getPostIntegral();
		if (postIntegral>0&&ArticleStatusEnum.AUDIT.getStatus().equals(article.getStatus())) {
			this.infoService.updateUserIntegral(article.getUserId(),UserIntegralOperTypeEnum.POST_ARTICLE,UserIntegralChangeTypeEnum.ADD.getChangeType(), postIntegral);
		}
	}

	@Override
	public void updateArticle(Boolean isAdmin, Article article, ArticleAttachment articleAttachment, MultipartFile cover, MultipartFile attachment) throws BusinessException {
		Article dbInfo=articleMapper.selectByArticleId(article.getArticleId());
		if (!isAdmin&&!dbInfo.getUserId().equals(article.getUserId())) {
			throw new BusinessException(ResponseCodeEnum.CODE_600);
		}
		article.setLastUpdateTime(new Date());
		resetBoardInfo(isAdmin,article);
		if (cover != null) {
			FileUpLoadDto fileUpLoadDto=fileUtils.uploadFile2Local(cover, Constants.FILE_FOLDER_IMAGE,FileUploadTypeEnum.ARTICLE_COVER);
			article.setCover(fileUpLoadDto.getLocalPath());
		}
		if (attachment != null) {
			uploadAttachment(article,articleAttachment,attachment,true);
			article.setAttachmentType(ArticleAttachmentTypeEnum.HAVE_ATTACHMENT.getType());
		}
		ArticleAttachment dbAttachment=null;
		ArticleAttachmentQuery articleAttachmentQuery=new ArticleAttachmentQuery();
		articleAttachmentQuery.setArticleId(article.getArticleId());
		List<ArticleAttachment>articleAttachmentList=this.articleAttachmentMapper.selectList(articleAttachmentQuery);
		if (!articleAttachmentList.isEmpty()) {
			dbAttachment=articleAttachmentList.get(0);
		}
		if (dbAttachment != null) {
			if (article.getAttachmentType()==Constants.ZERO) {
				new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+Constants.FILE_FOLDER_ATTACHMENT+dbAttachment.getFilePath()).delete();
				this.articleAttachmentMapper.deleteByFileId(dbAttachment.getFileId());
			}else {
				//更新积分
				if (!dbAttachment.getIntegral().equals(articleAttachment.getArticleId())) {
					ArticleAttachment integralUpdate=new ArticleAttachment();
					integralUpdate.setIntegral(articleAttachment.getIntegral());
					this.articleAttachmentMapper.updateByFileId(integralUpdate,dbAttachment.getFileId());
				}
			}
		}

		//文章是否需要审核
		if (isAdmin) {
			article.setStatus(ArticleStatusEnum.AUDIT.getStatus());
		}else {
			SysSetting4AuditDto auditDto=SysCacheUtils.getSysSetting().getAuditSetting();
			article.setStatus(auditDto.getPostAudit()?ArticleStatusEnum.NO_AUDIT.getStatus() : ArticleStatusEnum.AUDIT.getStatus());
		}
		//替换图片
		String content=article.getContent();
		if (!StringTools.isEmpty(content)) {
			String month=imageUtils.resetImageHtml(content);
			String replaceMonth="/"+month+"/";
			content=content.replace(Constants.FILE_FOLDER_TEMP,replaceMonth);
			article.setContent(content);
			String markdownContent=article.getMarkdownContent();
			if (!StringTools.isEmpty(markdownContent)) {
				markdownContent=markdownContent.replace(Constants.FILE_FOLDER_TEMP,replaceMonth);
				article.setMarkdownContent(markdownContent);
			}
		}
		this.articleMapper.updateByArticleId(article,article.getArticleId());
	}

	private void resetBoardInfo(Boolean isAdmin,Article article) throws BusinessException {
		Board pBoard=boardService.getBoardByBoardId(article.getpBoardId());
		if (pBoard == null||pBoard.getPostType()==Constants.ZERO&&!isAdmin) {
			throw new BusinessException("一级板块不存在");
		}
		article.setpBoardName(pBoard.getBoardName());
		if (article.getBoardId()!=null&&article.getBoardId()!=0) {
			Board board=boardService.getBoardByBoardId(article.getBoardId());
			if (board == null||board.getPostType()==Constants.ZERO&&!isAdmin) {
				throw new BusinessException("二级板块不存在");
			}
			article.setBoardName(board.getBoardName());
		}else {
			article.setBoardId(0);
			article.setBoardName("");
		}
	}

	public void uploadAttachment(Article article,ArticleAttachment articleAttachment,MultipartFile file,Boolean isUpdate) throws BusinessException {
		Integer allowSizeMb= SysCacheUtils.getSysSetting().getPostSetting().getAttachmentSize();
		long allowSize=allowSizeMb* Constants.FILE_SIZE_1M;
		if (file.getSize()>allowSize) {
			throw new BusinessException("附件最大只能上传"+allowSize+"MB");
		}
		//修改
		ArticleAttachment dbInfo=null;
		if (isUpdate ) {
			ArticleAttachmentQuery articleAttachmentQuery=new ArticleAttachmentQuery();
			articleAttachmentQuery.setArticleId(article.getArticleId());
			List<ArticleAttachment>articleAttachmentList=this.articleAttachmentMapper.selectList(articleAttachmentQuery);
			if (!articleAttachmentList.isEmpty()) {
				dbInfo=articleAttachmentList.get(0);
				new File(appConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+Constants.FILE_FOLDER_ATTACHMENT+dbInfo.getFilePath()).delete();
			}
		}

		FileUpLoadDto fileUpLoadDto=fileUtils.uploadFile2Local(file,Constants.FILE_FOLDER_ATTACHMENT,FileUploadTypeEnum.ARTICLE_ATTACHMENT);
		if (dbInfo == null) {
			articleAttachment.setFileId(StringTools.getRandomNumber(Constants.LENGTH_15));
			articleAttachment.setArticleId(article.getArticleId());
			articleAttachment.setFileName(fileUpLoadDto.getOriginalFileName());
			articleAttachment.setFilePath(fileUpLoadDto.getLocalPath());
			articleAttachment.setFileSize(file.getSize());
			articleAttachment.setDownloadCount(Constants.ZERO);
			articleAttachment.setFileType(AttachmentFileTypeEnum.ZIP.getType());
			articleAttachment.setUserId(article.getUserId());
			articleAttachmentMapper.insert(articleAttachment);
		}else {
			ArticleAttachment updateInfo=new ArticleAttachment();
			updateInfo.setFileName(fileUpLoadDto.getOriginalFileName());
			updateInfo.setFileSize(file.getSize());
			updateInfo.setFilePath(fileUpLoadDto.getLocalPath());
			articleAttachmentMapper.updateByFileId(updateInfo,dbInfo.getFileId());
		}
	}

	@Override
	public void delArticle(String articleIds) throws BusinessException {
		String[] articleIdArray=articleIds.split(",");
		for (String articleId:articleIdArray){
			articleService.delArticleSingle(articleId);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void delArticleSingle(String articleId) throws BusinessException {
		Article article=getArticleByArticleId(articleId);
		if (article == null||ArticleStatusEnum.DEL.getStatus().equals(article.getStatus())) {
			return;
		}
		Article updateInfo=new Article();
		updateInfo.setStatus(ArticleStatusEnum.DEL.getStatus());
		articleMapper.updateByArticleId(updateInfo,articleId);
		
		Integer integral=SysCacheUtils.getSysSetting().getPostSetting().getPostIntegral();
		if (integral >0&&ArticleStatusEnum.AUDIT.getStatus().equals(article.getStatus())) {
			infoService.updateUserIntegral(article.getUserId(),UserIntegralOperTypeEnum.DEL_ARTICLE,UserIntegralChangeTypeEnum.REDUCE.getChangeType(),integral);
		}
		Message message=new Message();
		message.setReceivedUserId(article.getUserId());
		message.setMessageType(MessageTypeEnum.SYS.getType());
		message.setCreateTime(new Date());
		message.setStatus(MessageStatusEnum.NO_READ.getStatus());
		message.setMessageContent("文章【"+article.getTitle()+"】被管理员删除");
		messageService.add(message);
	}

	@Override
	public void updateBoard(String articleId, Integer pBoardId, Integer boardId) throws BusinessException {
		Article article=new Article();
		article.setpBoardId(pBoardId);
		article.setBoardId(boardId);
		resetBoardInfo(true,article);
		articleMapper.updateByArticleId(article,articleId);
	}

	@Override
	public void auditArticle(String articleIds) throws BusinessException {
		String[] articleIdArray=articleIds.split(",");
		for (String articleId:articleIdArray){
			articleService.auditArticleSingle(articleId);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void auditArticleSingle(String articleId) throws BusinessException {
		Article article=getArticleByArticleId(articleId);
		if (article == null||!ArticleStatusEnum.NO_AUDIT.getStatus().equals(article.getStatus())) {
			return;
		}
		Article updateInfo=new Article();
		updateInfo.setStatus(ArticleStatusEnum.AUDIT.getStatus());
		articleMapper.updateByArticleId(updateInfo,articleId);


		Integer integral=SysCacheUtils.getSysSetting().getPostSetting().getPostIntegral();
		if (integral >0&&ArticleStatusEnum.AUDIT.getStatus().equals(article.getStatus())) {
			infoService.updateUserIntegral(article.getUserId(),UserIntegralOperTypeEnum.POST_ARTICLE,UserIntegralChangeTypeEnum.ADD.getChangeType(),integral);
		}
	}
}