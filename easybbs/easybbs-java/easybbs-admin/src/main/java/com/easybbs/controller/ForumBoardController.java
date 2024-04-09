package com.easybbs.controller;


import com.easybbs.annotation.GlobalInterceptor;
import com.easybbs.annotation.VerifyParam;
import com.easybbs.controller.base.ABaseController;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.FileUpLoadDto;
import com.easybbs.entity.enums.FileUploadTypeEnum;
import com.easybbs.entity.po.Board;
import com.easybbs.entity.vo.ResponseVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.service.BoardService;
import com.easybbs.utils.FileUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/board")
public class ForumBoardController extends ABaseController {
    @Resource
    private BoardService boardService;

    @Resource
    private FileUtils fileUtils;


    @RequestMapping("/loadBoard")
    public ResponseVO loadBoard(){
        return getSuccessResponseVO(boardService.getBoardTree(null));
    }

    @RequestMapping("/saveBoard")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO saveBoard(Integer boardId,
                                @VerifyParam(required = true) Integer pBoardId,
                                @VerifyParam(required = true) String boardName,
                                String boardDesc,
                                Integer postType,
                                MultipartFile cover) throws BusinessException {
        Board board=new Board();
        board.setBoardId(boardId);
        board.setPBoardId(pBoardId);
        board.setBoardName(boardName);
        board.setBoardDesc(boardDesc);
        board.setPostType(postType);
        if (cover!=null){
            FileUpLoadDto upLoadDto=fileUtils.uploadFile2Local(cover, Constants.FILE_FOLDER_IMAGE, FileUploadTypeEnum.ARTICLE_COVER);
            board.setCover(upLoadDto.getLocalPath());
        }
        boardService.saveForumBoard(board);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/delBoard")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO delBoard(@VerifyParam(required = true) Integer boardId){
        boardService.deleteBoardByBoardId(boardId);
        return getSuccessResponseVO(null);
    }

    @RequestMapping("/changeBoardSort")
    @GlobalInterceptor(checkParams = true)
    public ResponseVO changeSort(@VerifyParam(required = true) String boardIds){
        boardService.changeSort(boardIds);
        return getSuccessResponseVO(null);
    }

}
