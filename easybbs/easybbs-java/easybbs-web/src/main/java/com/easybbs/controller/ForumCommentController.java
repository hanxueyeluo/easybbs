package com.easybbs.controller;


import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.enums.*;
import com.easybbs.entity.po.Comment;
import com.easybbs.entity.po.Record;
import com.easybbs.entity.query.CommentQuery;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.CommentService;
import com.easybbs.service.RecordService;
import com.easybbs.utils.StringTools;
import com.easybbs.utils.SysCacheUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class ForumCommentController extends ABaseController {
    @Resource
    private CommentService commentService;

    @Resource
    private RecordService recordService;

    @RequestMapping("/loadComment")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadComment(HttpSession session,
                                  @VerifyParam(required = true) String articleId,
                                  Integer pageNo,
                                  Integer orderType) throws BusinessException {
        final String ORDER_TYPE0="good_count desc,comment_id asc";
        final String ORDER_TYPE1="comment_id desc";
        if (!SysCacheUtils.getSysSetting().getCommentSetting().getCommentOpen()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        CommentQuery commentQuery=new CommentQuery();
        commentQuery.setArticleId(articleId);
        String orderBy = orderType == null || orderType == Constants.ZERO ? ORDER_TYPE0 : ORDER_TYPE1;
        commentQuery.setOrderBy("top_type desc,"+orderBy);
        commentQuery.setPageNo(pageNo);

        SessionWebUserDto userDto=getUserInfoFromSession(session);
        if (userDto != null) {
            commentQuery.setQueryLikeType(true);
            commentQuery.setCurrentUserId(userDto.getUserId());
        }else {
            commentQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }
        commentQuery.setPageNo(pageNo);
        commentQuery.setPageSize(PageSize.SIZE50.getSize());
        commentQuery.setPCommentId(Constants.ZERO);
        commentQuery.setLoadChildren(true);
    return getSuccessResponseVO(commentService.findListByPage(commentQuery));
    }
    @RequestMapping("/doLike")
    @GlobalInterceptor(checkParams = true,checkLogin = true)
    public ResponseVO doLike(HttpSession session, @VerifyParam(required = true) Integer commentId) throws BusinessException {

        SessionWebUserDto userDto=getUserInfoFromSession(session);
        String objectId=String.valueOf(commentId);
        recordService.doLike(objectId,userDto.getUserId(), userDto.getNickName(), OperRecordOpTypeEnum.COMMENT_LIKE);
        Record record=recordService.getRecordByObjectIdAndUserIdAndOpType(objectId, userDto.getUserId(),OperRecordOpTypeEnum.COMMENT_LIKE.getType());
        Comment comment=commentService.getCommentByCommentId(commentId);
        comment.setLikeType(record==null?0:1);
        return getSuccessResponseVO(comment);
    }
    @RequestMapping("/changeTopType")
    @GlobalInterceptor(checkParams = true,checkLogin = true)
    public ResponseVO changeTopType(HttpSession session,
                                    @VerifyParam(required = true) Integer commentId,
                                    @VerifyParam(required = true) Integer topType) throws BusinessException {
        commentService.changeTopType(getUserInfoFromSession(session).getUserId(),commentId,topType);
        return getSuccessResponseVO(null);
    }
    @RequestMapping("/postComment")
    @GlobalInterceptor(checkParams = true,checkLogin = true,frequencyType = UserOperFrequencyTypeEnum.POST_COMMENT)
    public ResponseVO postComment(HttpSession session,
                                  @VerifyParam(required = true) String articleId,
                                  @VerifyParam(required = true) Integer pCommentId,
                                  @VerifyParam(min = 5,max = 800) String content,
                                  MultipartFile image,
                                  String replyUserId) throws BusinessException {
        if (!SysCacheUtils.getSysSetting().getCommentSetting().getCommentOpen()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        if (image == null && StringTools.isEmpty(content)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        SessionWebUserDto userDto=getUserInfoFromSession(session);
        content =StringTools.eecpapeHtml(content);
        Comment comment=new Comment();
        comment.setUserId(userDto.getUserId());
        comment.setNickName(userDto.getNickName());
        comment.setUserIpAddress(userDto.getProvince());
        comment.setpCommentId(pCommentId);
        comment.setArticleId(articleId);
        comment.setContent(content);
        comment.setReplyUserId(replyUserId);
        comment.setTopType(CommentTopTypeEnum.NO_TOP.getType());
        commentService.postComment(comment,image);
        if (pCommentId!=0) {
            CommentQuery commentQuery=new CommentQuery();
            commentQuery.setArticleId(articleId);
            commentQuery.setPCommentId(pCommentId);
            commentQuery.setOrderBy("comment_id asc");
            List<Comment> children=commentService.findListByParam(commentQuery);
            return getSuccessResponseVO(children);
        }
        return getSuccessResponseVO(comment);
    }
}
