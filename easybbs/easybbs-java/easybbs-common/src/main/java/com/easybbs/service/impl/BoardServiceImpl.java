package com.easybbs.service.impl;
import com.easybbs.entity.po.Article;
import com.easybbs.entity.query.ArticleQuery;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Board;
import com.easybbs.entity.query.BoardQuery;
import com.easybbs.entity.enums.PageSize;
import com.easybbs.exception.BusinessException;
import com.easybbs.mapper.ArticleMapper;
import com.easybbs.mapper.BoardMapper;
import com.easybbs.service.BoardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
/**
 * @Description 文章板块信息Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("BoardService")
public class BoardServiceImpl implements BoardService{

	@Resource
	private BoardMapper<Board,BoardQuery> boardMapper;

	@Resource
	private ArticleMapper<Article, ArticleQuery> articleMapper;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Board> findListByParam(BoardQuery query){
		return this.boardMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(BoardQuery query){
		return this.boardMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Board> findListByPage(BoardQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Board> list = this.findListByParam(query);
		PaginationResultVO<Board> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(BoardQuery bean){
		return this.boardMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<BoardQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.boardMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<BoardQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.boardMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据BoardId查询
	 */
	@Override
	public Board getBoardByBoardId(Integer boardId){
		return this.boardMapper.selectByBoardId(boardId);
	}
	/**
	 * 根据BoardId更新
	 */
	@Override
	public Integer updateBoardByBoardId(Board bean, Integer boardId){
		return this.boardMapper.updateByBoardId(bean,boardId);
	}
	/**
	 * 根据BoardId删除
	 */
	@Override
	public Integer deleteBoardByBoardId(Integer boardId){
		return this.boardMapper.deleteByBoardId(boardId);
	}

	@Override
	public List<Board> getBoardTree(Integer postType) {
		BoardQuery boardQuery=new BoardQuery();
		boardQuery.setOrderBy("sort asc");
		boardQuery.setPostType(postType);
		List<Board> boardList=this.boardMapper.selectList(boardQuery);
		return convertLine2Tree(boardList,0);
	}

	private List<Board>convertLine2Tree(List<Board> dataList,Integer pid){
		List<Board> children=new ArrayList<>();
		for (Board m:dataList){
			if (m.getPBoardId()!=null&&m.getPBoardId().equals(pid)){
				m.setChildren(convertLine2Tree(dataList,m.getBoardId()));
				children.add(m);
			}
		}
		return children;
	}

	@Override
	public void saveForumBoard(Board board) throws BusinessException {
		if (board.getBoardId() == null) {
			BoardQuery query=new BoardQuery();
			query.setPBoardId(board.getPBoardId());
			Integer count=this.boardMapper.selectCount(query);
			board.setSort(count+1);
			this.boardMapper.insert(board);
		}else {
			Board dbInfo=this.boardMapper.selectByBoardId(board.getBoardId());
			if (dbInfo == null) {
				throw new BusinessException("板块信息不存在");
			}
			this.boardMapper.updateByBoardId(board,board.getBoardId());
			if (dbInfo.getBoardName().equals(board.getBoardName())) {
				articleMapper.updateBoardNameBatch(dbInfo.getPBoardId()==0?0:1,board.getBoardName(),board.getBoardId());
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void changeSort(String boardIds) {
		String[] boardIdArray=boardIds.split(",");
		Integer index=1;
		for (String boardIdStr:boardIdArray){
			Integer boardId=Integer.parseInt(boardIdStr);
			Board board=new Board();
			board.setSort(index);
			boardMapper.updateByBoardId(board,boardId);
			index++;
		}
	}
}