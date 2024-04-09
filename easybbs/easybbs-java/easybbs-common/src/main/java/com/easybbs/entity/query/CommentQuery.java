package com.easybbs.entity.query;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;


/**
 * @Description 评论
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentQuery extends BaseQuery {
	/**
	 * 评论ID
	 */
	private Integer commentId;

	/**
	 * 父级评论ID
	 */
	private Integer pCommentId;

	/**
	 * 文章ID
	 */
	private String articleId;

	private String articleIdFuzzy;
	/**
	 * 回复内容
	 */
	private String content;

	private String contentFuzzy;
	/**
	 * 图片
	 */
	private String imgPath;

	private String imgPathFuzzy;
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
	 * 用户ip地址
	 */
	private String userIpAddress;

	private String userIpAddressFuzzy;
	/**
	 * 回复人ID
	 */
	private String replyUserId;

	private String replyUserIdFuzzy;
	/**
	 * 回复人昵称
	 */
	private String replyNickName;

	private String replyNickNameFuzzy;
	/**
	 * 0:未置顶  1:置顶
	 */
	private Integer topType;

	/**
	 * 发布时间
	 */
	private Date postTime;

	private String postTimeStart;

	private String postTimeEnd;

	/**
	 * good数量
	 */
	private Integer goodCount;

	/**
	 * 0:待审核  1:已审核
	 */
	private Integer status;

	private Boolean loadChildren;

	private String currentUserId;


	private Boolean queryLikeType;



	private List<Integer>pcommentIdList;

	public List<Integer> getPcommentIdList() {
		return pcommentIdList;
	}

	public void setPcommentIdList(List<Integer> pcommentIdList) {
		this.pcommentIdList = pcommentIdList;
	}

	public Boolean getQueryLikeType() {
		return queryLikeType;
	}

	public void setQueryLikeType(Boolean queryLikeType) {
		this.queryLikeType = queryLikeType;
	}

	public String getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(String currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Boolean getLoadChildren() {
		return loadChildren;
	}

	public void setLoadChildren(Boolean loadChildren) {
		this.loadChildren = loadChildren;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}