package com.easybbs.controller;


import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.po.Comment;
import com.easybbs.entity.po.Info;
import com.easybbs.entity.query.CommentQuery;
import com.easybbs.entity.query.InfoQuery;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.InfoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserInfoController extends ABaseController {
    @Resource
    private InfoService infoService;

    @RequestMapping("/loadUserList")
    public ResponseVO loadUserList(InfoQuery infoQuery){
        infoQuery.setOrderBy("join_time desc");
        return getSuccessResponseVO(infoService.findListByPage(infoQuery));
    }


    @RequestMapping("/updateUserStatus")
    public ResponseVO updateUserStatus(@VerifyParam(required = true) Integer status,@VerifyParam(required = true) String userId){
        infoService.updateUserStatus(status,userId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/sendMessage")
    public ResponseVO sendMessage(@VerifyParam(required = true) String userId,
                                  @VerifyParam(required = true) String message,
                                  @VerifyParam(required = true) Integer integral) throws BusinessException {

            infoService.sendMessage(userId,message,integral);
            return getSuccessResponseVO(null);
    }

}
