package com.easybbs.entity.po;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easybbs.entity.enums.DateTimePatternEnum;
import com.easybbs.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description 评论
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment implements Serializable {
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

	/**
	 * 回复内容
	 */
	private String content;

	/**
	 * 图片
	 */
	private String imgPath;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 昵称
	 */
	private String nickName;

	/**
	 * 用户ip地址
	 */
	private String userIpAddress;

	/**
	 * 回复人ID
	 */
	private String replyUserId;

	/**
	 * 回复人昵称
	 */
	private String replyNickName;

	/**
	 * 0:未置顶  1:置顶
	 */
	private Integer topType;

	/**
	 * 发布时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date postTime;

	/**
	 * good数量
	 */
	private Integer goodCount;

	/**
	 * 0:待审核  1:已审核
	 */
	private Integer status;


	/**
	 * 点赞类型 0:未点赞 1:已点赞
	 */
	private Integer likeType;

	private List<Comment> children;

	public List<Comment> getChildren() {
		return children;
	}

	public void setChildren(List<Comment> children) {
		this.children = children;
	}

	public Integer getLikeType() {
		return likeType;
	}

	public void setLikeType(Integer likeType) {
		this.likeType = likeType;
	}

	public Integer getpCommentId() {
		return pCommentId;
	}

	public void setpCommentId(Integer pCommentId) {
		this.pCommentId = pCommentId;
	}

	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	@Override
	public String toString (){
		return "父级评论ID:" + ",文章ID:" + ( articleId== null ? "空" : articleId) + ",回复内容:" + ( content== null ? "空" : content) + ",图片:" + ( imgPath== null ? "空" : imgPath) + ",用户ID:" + ( userId== null ? "空" : userId) + ",昵称:" + ( nickName== null ? "空" : nickName) + ",用户ip地址:" + ( userIpAddress== null ? "空" : userIpAddress) + ",回复人ID:" + ( replyUserId== null ? "空" : replyUserId) + ",回复人昵称:" + ( replyNickName== null ? "空" : replyNickName) + ",0:未置顶  1:置顶:" + ( topType== null ? "空" : topType) + ",发布时间:" + ( postTime== null ? "空" : DateUtils.format(postTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",good数量:" + ( goodCount== null ? "空" : goodCount) + ",0:待审核  1:已审核:" + ( status== null ? "空" : status);
	}
}