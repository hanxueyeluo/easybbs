package com.easybbs.entity.po;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * @Description 文章板块信息
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board implements Serializable {
	/**
	 * 板块ID
	 */
	private Integer boardId;

	/**
	 * 父级板块ID
	 */
	private Integer pBoardId;

	/**
	 * 板块名
	 */
	private String boardName;

	/**
	 * 封面
	 */
	private String cover;

	/**
	 * 描述
	 */
	private String boardDesc;

	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 0:只允许管理员发帖 1:任何人可以发帖
	 */
	private Integer postType;

	private List<Board> children;

	public List<Board> getChildren() {
		return children;
	}

	public void setChildren(List<Board> children) {
		this.children = children;
	}




	public String toString (){
		return "板块ID:" + ( boardId== null ? "空" : boardId) + ",父级板块ID:" + ( pBoardId== null ? "空" : pBoardId) + ",板块名:" + ( boardName== null ? "空" : boardName) + ",封面:" + ( cover== null ? "空" : cover) + ",描述:" + ( boardDesc== null ? "空" : boardDesc) + ",排序:" + ( sort== null ? "空" : sort) + ",0:只允许管理员发帖 1:任何人可以发帖:" + ( postType== null ? "空" : postType);
	}
}