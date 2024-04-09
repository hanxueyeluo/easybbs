package com.easybbs.controller;

import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.config.WebConfig;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.enums.*;
import com.easybbs.entity.po.*;
import com.easybbs.entity.query.ArticleAttachmentQuery;
import com.easybbs.entity.query.ArticleQuery;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.entity.vo.web.ForumArticleAttachmentVO;
import com.easybbs.entity.vo.web.ForumArticleDetailVO;
import com.easybbs.entity.vo.web.ForumArticleVO;
import com.easybbs.entity.vo.web.UserDownloadInfoVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.*;
import com.easybbs.utils.CopyTools;
import com.easybbs.utils.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/forum")
public class ForumArticleController extends ABaseController {

    private static final Logger logger= LoggerFactory.getLogger(ForumArticleController.class);

    @Resource
    private ArticleService articleService;

    @Resource
    private ArticleAttachmentService articleAttachmentService;

    @Resource
    private RecordService recordService;

    @Resource
    private InfoService infoService;

    @Resource
    private ArticleAttachmentDownloadService articleAttachmentDownloadService;

    @Resource
    WebConfig webConfig;

    @Resource
    private BoardService boardService;

    @RequestMapping("/loadArticle")
    public ResponseVO loadArticle(HttpSession session, Integer boardId, Integer pBoardId, Integer orderType, Integer pageNo) {
        ArticleQuery articleQuery = new ArticleQuery();
        articleQuery.setBoardId((boardId != null && boardId != 0) ? boardId : null);
        articleQuery.setPBoardId(pBoardId);
        articleQuery.setPageNo(pageNo);

        SessionWebUserDto userDto = getUserInfoFromSession(session);
        if (userDto != null) {
            articleQuery.setCurrentUserId(userDto.getUserId());
        } else {
            articleQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }

        // 检查并设置排序类型
        ArticleOrderTypeEnum orderTypeEnum = ArticleOrderTypeEnum.getByType(orderType);
        if (orderTypeEnum == null || orderTypeEnum == ArticleOrderTypeEnum.SEND) {
            orderTypeEnum = ArticleOrderTypeEnum.HOT; // 默认排序类型
        }
        articleQuery.setOrderBy(orderTypeEnum.getOrderSql());

        PaginationResultVO resultVO = articleService.findListByPage(articleQuery);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, ForumArticleVO.class));
    }



    @RequestMapping("/getArticleDetail")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getArticleDetail(HttpSession session,@VerifyParam(required = true) String articleId) throws BusinessException {
        SessionWebUserDto sessionWebUserDto=getUserInfoFromSession(session);
        Article  forumArticle=articleService.readArticle(articleId);
        if (forumArticle == null ||
                (ArticleStatusEnum.NO_AUDIT.getStatus().equals(forumArticle.getStatus())
        && (sessionWebUserDto==null || !sessionWebUserDto.getUserId().equals(forumArticle.getUserId()) && !sessionWebUserDto.getAdmin()))
        || ArticleStatusEnum.DEL.getStatus().equals(forumArticle.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        ForumArticleDetailVO detailVO=new ForumArticleDetailVO();
        detailVO.setForumArticle(CopyTools.copy(forumArticle,ForumArticleVO.class));
        //有附件
        if (forumArticle.getAttachmentType()== Constants.ONE) {
            ArticleAttachmentQuery articleAttachmentQuery=new ArticleAttachmentQuery();
            articleAttachmentQuery.setArticleId(articleId);
            List<ArticleAttachment> forumArticleAttachmentList=articleAttachmentService.findListByParam(articleAttachmentQuery);
            if (!forumArticleAttachmentList.isEmpty()) {
                detailVO.setAttachment(CopyTools.copy(forumArticleAttachmentList.get(0), ForumArticleAttachmentVO.class));
            }
        }
        //是否已经点赞
        if (sessionWebUserDto != null) {
            Record record= recordService.getRecordByObjectIdAndUserIdAndOpType(articleId,sessionWebUserDto.getUserId(), OperRecordOpTypeEnum.ARTICLE_LIKE.getType());
            if (record != null) {
                detailVO.setHaveLike(true);
            }
        }
        return getSuccessResponseVO(detailVO);
    }

    @RequestMapping("/doLike")
    @GlobalInterceptor(checkLogin = true,checkParams = true,frequencyType = UserOperFrequencyTypeEnum.DO_LIKE)
    public ResponseVO doLike(HttpSession session,@VerifyParam(required = true) String articleId) throws BusinessException {
        SessionWebUserDto sessionWebUserDto=getUserInfoFromSession(session);
        recordService.doLike(articleId,sessionWebUserDto.getUserId(),sessionWebUserDto.getNickName(),OperRecordOpTypeEnum.ARTICLE_LIKE);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getUserDownloadInfo")
    @GlobalInterceptor(checkLogin = true,checkParams = true)
    public ResponseVO getUserDownloadInfo(HttpSession session,@VerifyParam(required = true) String fileId) throws BusinessException {
        SessionWebUserDto webUserDto=getUserInfoFromSession(session);
        Info info=infoService.getInfoByUserId(webUserDto.getUserId());
        UserDownloadInfoVO downloadInfoVO=new UserDownloadInfoVO();
        downloadInfoVO.setUserIntegral(info.getCurrentIntegral());
        ArticleAttachmentDownload attachmentDownload=articleAttachmentDownloadService.getArticleAttachmentDownloadByFileIdAndUserId(fileId, webUserDto.getUserId());
        if (attachmentDownload != null) {
            downloadInfoVO.setHaveDownload(true);
        }
        return getSuccessResponseVO(downloadInfoVO);
    }

    @RequestMapping("/attachmentDownload")
    @GlobalInterceptor(checkLogin = true,checkParams = true)
    public void attachmentDownload(HttpSession session, HttpServletRequest request, HttpServletResponse response,
                                   @VerifyParam(required = true) String fileId) throws BusinessException {
        ArticleAttachment attachment=articleAttachmentService.downloadAttachment(fileId,getUserInfoFromSession(session));
        InputStream in=null;
        OutputStream out=null;
        String downloadFileName=attachment.getFileName();
        String filePath=webConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+Constants.FILE_FOLDER_ATTACHMENT+attachment.getFilePath();
        File file=new File(filePath);
        try{
            in=new FileInputStream(file);
            out=response.getOutputStream();
            response.setContentType("application/x-msdownload; charset=UTF-8");
            //解决中文文件名乱码问题
            if (request.getHeader("User-Agent").toLowerCase().indexOf("msie")>0) {
                downloadFileName= URLEncoder.encode(downloadFileName,"UTF-8");
            }else{
                downloadFileName=new String(downloadFileName.getBytes("UTF-8"),"ISO8859-1");
            }
            response.setHeader("Content-Disposition","attachment;filename=\""+downloadFileName+"\"");
            byte [] byteData=new byte[1024];
            int len=0;
            while ((len=in.read(byteData))!=-1){
                out.write(byteData,0,len);//write
            }
            out.flush();
        }catch (Exception e){
            logger.error("下载异常",e);
            throw new BusinessException("下载失败");
        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            }catch (IOException e){
                logger.error("IO异常",e);
            }
            try {
                if (out != null) {
                    out.close();
                }
            }catch (IOException e){
                logger.error("IO异常");
            }
        }
    }

    @RequestMapping("/loadBoard4Post")
    @GlobalInterceptor(checkLogin = true)
    public ResponseVO loadBoard4Post(HttpSession session) {
        SessionWebUserDto userDto=getUserInfoFromSession(session);
        Integer postType=null;
        if (!userDto.getAdmin()) {
            postType=Constants.ONE;
        }
        return getSuccessResponseVO(boardService.getBoardTree(postType));
    }

    @RequestMapping("/postArticle")
    @GlobalInterceptor(checkLogin = true,checkParams = true,frequencyType = UserOperFrequencyTypeEnum.POST_ARTICLE)
    public ResponseVO postArticle(HttpSession session,
                                  MultipartFile cover,
                                  MultipartFile attachment,
                                  Integer integral,
                                  @VerifyParam(required = true,max = 150) String title,
                                  @VerifyParam(required = true) Integer pBoardId,
                                  Integer boardId,
                                  @VerifyParam(max = 200) String summary,
                                  @VerifyParam(required = true) Integer editorType,
                                  @VerifyParam(required = true) String content,
                                  String markdownContent) throws BusinessException {
        title= StringTools.eecpapeHtml(title);
        SessionWebUserDto webUserDto=getUserInfoFromSession(session);
        Article article=new Article();
        article.setpBoardId(pBoardId);
        article.setBoardId(boardId);
        article.setTitle(title);
        article.setSummary(summary);
        article.setContent(content);
        EditorTypeEnum typeEnum=EditorTypeEnum.getByType(editorType);
        if (typeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (EditorTypeEnum.MARKDOWN.getType().equals(editorType) && StringTools.isEmpty(markdownContent)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        article.setMarkdownContent(markdownContent);
        article.setEditorType(editorType);
        article.setUserId(webUserDto.getUserId());
        article.setNickName(webUserDto.getNickName());
        article.setUserIpAddress(webUserDto.getProvince());
        //附件信息
        ArticleAttachment articleAttachment=new ArticleAttachment();
        articleAttachment.setIntegral(integral==null?0:integral);
        logger.info("当前用户角色为:{}", webUserDto.getAdmin());
        articleService.postArticle(webUserDto.getAdmin(),article,articleAttachment,cover,attachment);
        return getSuccessResponseVO(article.getArticleId());
    }

    @RequestMapping("/articleDetail4Update")
    @GlobalInterceptor(checkLogin = true,checkParams = true)
    public ResponseVO articleDetail4Update(HttpSession session,@VerifyParam(required = true) String articleId) throws BusinessException {
        SessionWebUserDto userDto=getUserInfoFromSession(session);
        Article article=articleService.getArticleByArticleId(articleId);
        if (article == null||!article.getUserId().equals(userDto.getUserId())) {
            throw new BusinessException("文章不存在或者你无权编辑改文章");
        }
        ForumArticleDetailVO detailVO=new ForumArticleDetailVO();
        detailVO.setForumArticle(CopyTools.copy(article, ForumArticleVO.class));
        if (article.getAttachmentType()== Constants.ONE) {
            ArticleAttachmentQuery articleAttachmentQuery=new ArticleAttachmentQuery();
            articleAttachmentQuery.setArticleId(articleId);
            List<ArticleAttachment> forumArticleAttachmentList=articleAttachmentService.findListByParam(articleAttachmentQuery);
            if (!forumArticleAttachmentList.isEmpty()) {
                detailVO.setAttachment(CopyTools.copy(forumArticleAttachmentList.get(0), ForumArticleAttachmentVO.class));
            }
        }
        return getSuccessResponseVO(detailVO);
    }
    @RequestMapping("/updateArticle")
    @GlobalInterceptor(checkLogin = true,checkParams = true)
    public ResponseVO updateArticle(HttpSession session,
                                  MultipartFile cover,
                                  MultipartFile attachment,
                                  Integer integral,
                                  @VerifyParam(required = true) String articleId,
                                  @VerifyParam(required = true,max = 150) String title,
                                  @VerifyParam(required = true) Integer pBoardId,
                                  Integer boardId,
                                  @VerifyParam(max = 200) String summary,
                                  @VerifyParam(required = true) Integer editorType,
                                  @VerifyParam(required = true) String content,
                                  String markdownContent,
                                  @VerifyParam Integer attachmentType) throws BusinessException {
        title= StringTools.eecpapeHtml(title);
        SessionWebUserDto userDto=getUserInfoFromSession(session);
        Article article=new Article();
        article.setArticleId(articleId);
        article.setpBoardId(pBoardId);
        article.setBoardId(boardId);
        article.setTitle(title);
        article.setContent(content);
        article.setMarkdownContent(markdownContent);
        article.setEditorType(editorType);
        article.setSummary(summary);
        article.setUserIpAddress(userDto.getProvince());
        article.setAttachmentType(attachmentType);
        article.setUserId(userDto.getUserId());
        //附件信息
        ArticleAttachment articleAttachment=new ArticleAttachment();
        articleAttachment.setIntegral(integral==null?0:integral);
        articleService.updateArticle(userDto.getAdmin(),article,articleAttachment,cover,attachment);
        return getSuccessResponseVO(article.getArticleId());
    }
    @RequestMapping("/search")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO search(@VerifyParam(required = true,min = 1) String keyword){
        ArticleQuery articleQuery=new ArticleQuery();
        articleQuery.setTitleFuzzy(keyword);
        PaginationResultVO resultVO=articleService.findListByPage(articleQuery);
        return getSuccessResponseVO(resultVO);
    }
}
