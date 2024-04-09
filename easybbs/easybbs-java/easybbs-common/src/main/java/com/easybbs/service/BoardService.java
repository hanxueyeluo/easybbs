package com.easybbs.service;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Board;
import com.easybbs.entity.query.BoardQuery;
import com.easybbs.exception.BusinessException;

import java.util.List;
/**
 * @Description 文章板块信息Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface BoardService{

	/**
	 * 根据条件查询列表
	 */
	List<Board> findListByParam(BoardQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(BoardQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Board> findListByPage(BoardQuery query);

	/**
	 * 新增
	 */
	Integer add(BoardQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<BoardQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<BoardQuery> listBean);

	/**
	 * 根据BoardId查询
	 */
	Board getBoardByBoardId(Integer boardId);

	/**
	 * 根据BoardId更新
	 */
	Integer updateBoardByBoardId(Board bean, Integer boardId);

	/**
	 * 根据BoardId删除
	 */
	Integer deleteBoardByBoardId(Integer boardId);

	List<Board> getBoardTree(Integer postType);

	void saveForumBoard(Board board) throws BusinessException;

	void changeSort(String boardIds);

}