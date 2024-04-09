package com.easybbs.controller;

import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.config.AdminConfig;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.po.Article;
import com.easybbs.entity.po.ArticleAttachment;
import com.easybbs.entity.po.Comment;
import com.easybbs.entity.query.ArticleAttachmentQuery;
import com.easybbs.entity.query.ArticleQuery;
import com.easybbs.entity.query.CommentQuery;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.ArticleAttachmentService;
import com.easybbs.service.ArticleService;
import com.easybbs.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private AdminConfig adminConfig;

    @Resource
    private CommentService commentService;

    @RequestMapping("/loadArticle")
    public ResponseVO loadArticle(ArticleQuery articleQuery){
        articleQuery.setOrderBy("post_time desc");
        return getSuccessResponseVO(articleService.findListByPage(articleQuery));
    }

    @RequestMapping("/delArticle")
    public ResponseVO delArticle(@VerifyParam(required = true) String articleIds) throws BusinessException {
        articleService.delArticle(articleIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/updateBoard")
    public ResponseVO updateBoard(@VerifyParam(required = true) String articleId,
                                  @VerifyParam(required = true) Integer pBoardId,
                                  @VerifyParam(required = true) Integer boardId) throws BusinessException {
        boardId=boardId==null?0:boardId;
        articleService.updateBoard(articleId,pBoardId,boardId);

        return getSuccessResponseVO(null);
    }

    @RequestMapping("/getAttachment")
    public ResponseVO getAttachment(@VerifyParam(required = true) String articleId) throws BusinessException {
        ArticleAttachmentQuery articleAttachmentQuery=new ArticleAttachmentQuery();
        articleAttachmentQuery.setArticleId(articleId);
        List<ArticleAttachment>attachmentList=articleAttachmentService.findListByParam(articleAttachmentQuery);
        if (attachmentList.isEmpty()) {
            throw new BusinessException("附件不存在");
        }
        return getSuccessResponseVO(attachmentList.get(0));
    }

    @RequestMapping("/attachmentDownload")
    @GlobalInterceptor(checkLogin = true,checkParams = true)
    public void attachmentDownload(HttpServletRequest request, HttpServletResponse response,
                                   @VerifyParam(required = true) String fileId) throws BusinessException {
        ArticleAttachment attachment=articleAttachmentService.getArticleAttachmentByFileId(fileId);
        InputStream in=null;
        OutputStream out=null;
        String downloadFileName=attachment.getFileName();
        String filePath=adminConfig.getProjectFolder()+Constants.FILE_FOLDER_FILE+Constants.FILE_FOLDER_ATTACHMENT+attachment.getFilePath();
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

    @RequestMapping("/topArticle")
    public ResponseVO topArticle(@VerifyParam(required = true) String articleId,Integer topType){
        Article article=new Article();
        article.setTopType(topType);
        articleService.updateArticleByArticleId(article,articleId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/auditArticle")
    public ResponseVO auditArticle(@VerifyParam(required = true) String articleIds) throws BusinessException {
        articleService.auditArticle(articleIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadComment")
    public ResponseVO loadComment(CommentQuery commentQuery) {
        commentQuery.setLoadChildren(true);
        commentQuery.setOrderBy("post_time desc");
        return getSuccessResponseVO(commentService.findListByParam(commentQuery));
    }

    @RequestMapping("/newLoadComment")
    public ResponseVO newLoadComment(CommentQuery commentQuery) {
        PaginationResultVO<Comment> resultVO = new PaginationResultVO<>();
        commentQuery.setLoadChildren(true);
        commentQuery.setOrderBy("post_time desc");
        List<Comment> result = commentService.findListByParam(commentQuery);
        resultVO.setList(result);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/loadComment4Article")
    public ResponseVO loadComment4Article(CommentQuery commentQuery) {
        commentQuery.setLoadChildren(true);
        commentQuery.setOrderBy("post_time desc");
        commentQuery.setPCommentId(0);
        return getSuccessResponseVO(commentService.findListByPage(commentQuery));
    }

    @RequestMapping("/delComment")
    public ResponseVO delComment(@VerifyParam(required = true) String commentIds) throws BusinessException {
        commentService.delComment(commentIds);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/auditComment")
    public ResponseVO auditComment(@VerifyParam(required = true) String commentIds) throws BusinessException {
        commentService.auditComment(commentIds);
        return getSuccessResponseVO(null);
    }

}
