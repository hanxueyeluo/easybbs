package com.easybbs.controller;

import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.enums.ArticleStatusEnum;
import com.easybbs.entity.enums.MessageTypeEnum;
import com.easybbs.entity.enums.ResponseCodeEnum;
import com.easybbs.entity.enums.UserStatusEnum;
import com.easybbs.entity.po.Info;
import com.easybbs.entity.po.IntegralRecord;
import com.easybbs.entity.po.Message;
import com.easybbs.entity.query.ArticleQuery;
import com.easybbs.entity.query.IntegralRecordQuery;
import com.easybbs.entity.query.MessageQuery;
import com.easybbs.entity.query.RecordQuery;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.entity.vo.web.ForumArticleVO;
import com.easybbs.entity.vo.web.UserInfoVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.*;
import com.easybbs.utils.CopyTools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/ucenter")
public class UserCenterController extends ABaseController {

    @Resource
    private InfoService infoService;

    @Resource
    private ArticleService articleService;

    @Resource
    private RecordService recordService;

    @Resource
    private IntegralRecordService integralRecordService;

    @Resource
    private MessageService messageService;


    @RequestMapping("/getUserInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getUserInfo(@VerifyParam(required = true) String userId) throws BusinessException {
        Info info=infoService.getInfoByUserId(userId);
        if (info == null|| UserStatusEnum.DISABLE.getStatus().equals(info.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        ArticleQuery articleQuery=new ArticleQuery();
        articleQuery.setUserId(userId);
        articleQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        Integer postCount=articleService.findCountByParam(articleQuery);
        UserInfoVO userInfoVO= CopyTools.copy(info, UserInfoVO.class);
        userInfoVO.setPostCount(postCount);
        RecordQuery recordQuery=new RecordQuery();
        recordQuery.setAuthorUserId(userId);
        Integer likeCount=recordService.findCountByParam(recordQuery);
        userInfoVO.setLikeCount(likeCount);
        return getSuccessResponseVO(userInfoVO);
    }

    @RequestMapping("/loadUserArticle")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadUserArticle(HttpSession session, @VerifyParam(required = true) String userId, @VerifyParam(required = true) Integer type,Integer pageNo) throws BusinessException {
        Info info=infoService.getInfoByUserId(userId);
        if (info == null|| UserStatusEnum.DISABLE.getStatus().equals(info.getStatus())) {
            throw new BusinessException(ResponseCodeEnum.CODE_404);
        }
        ArticleQuery articleQuery=new ArticleQuery();
        articleQuery.setOrderBy("post_time desc");
        articleQuery.setPageNo(pageNo);
        if (type == 0) {
            articleQuery.setUserId(userId);
        } else if (type==1) {
            articleQuery.setCommentUserId(userId);
        } else if (type==2) {
            articleQuery.setLikeUserId(userId);
        }
        SessionWebUserDto userDto=getUserInfoFromSession(session);
        if (userDto != null) {
            articleQuery.setCurrentUserId(userDto.getUserId());
        }else {
            articleQuery.setStatus(ArticleStatusEnum.AUDIT.getStatus());
        }
        PaginationResultVO resultVO=articleService.findListByPage(articleQuery);
        return getSuccessResponseVO(convert2PaginationVO(resultVO, ForumArticleVO.class));
    }

    @RequestMapping("/updateUserInfo")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO updateUserInfo(HttpSession session, Integer sex, @VerifyParam(max = 100) String personDescription, MultipartFile avatar) throws BusinessException {
        SessionWebUserDto userDto=getUserInfoFromSession(session);
        Info info=new Info();
        info.setUserId(userDto.getUserId());
        info.setSex(sex);
        info.setPersonDescription(personDescription);
        infoService.updateUserInfo(info,avatar);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/loadUserIntegralRecord")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO loadUserIntegralRecord(HttpSession session, Integer pageNo,String createTimeStart,String createTimeEnd) {
        IntegralRecordQuery recordQuery=new IntegralRecordQuery();
        recordQuery.setUserId(getUserInfoFromSession(session).getUserId());
        recordQuery.setPageNo(pageNo);
        recordQuery.setCreateTimeStart(createTimeStart);
        recordQuery.setCreateTimeEnd(createTimeEnd);
        recordQuery.setOrderBy("record_id desc");
        PaginationResultVO resultVO=integralRecordService.findListByPage(recordQuery);
        return getSuccessResponseVO(resultVO);
    }

    @RequestMapping("/getMessageCount")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO getMessageCount(HttpSession session) {
        SessionWebUserDto userDto=getUserInfoFromSession(session);
        return getSuccessResponseVO(messageService.getUserMessageCount(userDto.getUserId()));
    }

    @RequestMapping("/loadMessageList")
    @GlobalInterceptor(checkParams = true,checkLogin = true)
    public ResponseVO loadMessageList(HttpSession session,@VerifyParam(required = true) String code,Integer pageNo) throws BusinessException {
        MessageTypeEnum typeEnum=MessageTypeEnum.getByCode(code);
        if (typeEnum == null) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        SessionWebUserDto userDto=getUserInfoFromSession(session);

        MessageQuery messageQuery=new MessageQuery();
        messageQuery.setPageNo(pageNo);
        messageQuery.setReceivedUserId(userDto.getUserId());
        messageQuery.setMessageType(typeEnum.getType());
        messageQuery.setOrderBy("message_id desc");
        PaginationResultVO resultVO=messageService.findListByPage(messageQuery);
        if (pageNo == null||pageNo==1) {
            messageService.readMessageByType(userDto.getUserId(),typeEnum.getType());
        }
        return getSuccessResponseVO(resultVO);
    }
}
