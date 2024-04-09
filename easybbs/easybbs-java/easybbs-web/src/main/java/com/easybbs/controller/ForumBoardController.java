package com.easybbs.controller;

import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.service.BoardService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/board")
public class ForumBoardController extends ABaseController{
    @Resource
    private BoardService boardService;

    @RequestMapping("/loadBoard")
    public ResponseVO loadBoard(){
        return getSuccessResponseVO(boardService.getBoardTree(null));
    }
}
