package com.easybbs.entity.query;

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
public class BoardQuery extends BaseQuery {
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

	private String boardNameFuzzy;
	/**
	 * 封面
	 */
	private String cover;

	private String coverFuzzy;
	/**
	 * 描述
	 */
	private String boardDesc;

	private String boardDescFuzzy;
	/**
	 * 排序
	 */
	private Integer sort;

	/**
	 * 0:只允许管理员发帖 1:任何人可以发帖
	 */
	private Integer postType;

}