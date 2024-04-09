package com.easybbs.entity.query;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;


/**
 * @Description 文章信息
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleQuery extends BaseQuery {
	/**
	 * 文章ID
	 */
	private String articleId;

	private String articleIdFuzzy;
	/**
	 * 板块ID
	 */
	private Integer boardId;

	/**
	 * 板块名称
	 */
	private String boardName;

	private String boardNameFuzzy;
	/**
	 * 父级板块ID
	 */
	private Integer pBoardId;

	/**
	 * 父板块名称
	 */
	private String pBoardName;

	private String pBoardNameFuzzy;
	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;
	/**
	 * 昵称
	 */
	private String nickName;

	private String nickNameFuzzy;
	/**
	 * 最后登录ip地址
	 */
	private String userIpAddress;

	private String userIpAddressFuzzy;
	/**
	 * 标题
	 */
	private String title;

	private String titleFuzzy;
	/**
	 * 封面
	 */
	private String cover;

	private String coverFuzzy;
	/**
	 * 内容
	 */
	private String content;

	private String contentFuzzy;
	/**
	 * markdown内容
	 */
	private String markdownContent;

	private String markdownContentFuzzy;
	/**
	 * 0:富文本编辑器 1:markdown编辑器
	 */
	private Integer editorType;

	/**
	 * 摘要
	 */
	private String summary;

	private String summaryFuzzy;
	/**
	 * 发布时间
	 */
	private Date postTime;

	private String postTimeStart;

	private String postTimeEnd;

	/**
	 * 最后更新时间
	 */
	private Date lastUpdateTime;

	private String lastUpdateTimeStart;

	private String lastUpdateTimeEnd;

	/**
	 * 阅读数量
	 */
	private Integer readCount;

	/**
	 * 点赞数
	 */
	private Integer goodCount;

	/**
	 * 评论数
	 */
	private Integer commentCount;

	/**
	 * 0未置顶  1:已置顶
	 */
	private Integer topType;

	/**
	 * 0:没有附件  1:有附件
	 */
	private Integer attachmentType;

	/**
	 * -1已删除 0:待审核  1:已审核 
	 */
	private Integer status;


	private String currentUserId;

	private String likeUserId;

	private String commentUserId;


	public String getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(String currentUserId) {
		this.currentUserId = currentUserId;
	}


	public String getLikeUserId() {
		return likeUserId;
	}

	public void setLikeUserId(String likeUserId) {
		this.likeUserId = likeUserId;
	}

	public String getCommentUserId() {
		return commentUserId;
	}

	public void setCommentUserId(String commentUserId) {
		this.commentUserId = commentUserId;
	}
}