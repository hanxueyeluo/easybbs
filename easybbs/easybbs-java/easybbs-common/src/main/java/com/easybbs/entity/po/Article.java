package com.easybbs.entity.po;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easybbs.entity.enums.DateTimePatternEnum;
import com.easybbs.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @Description 文章信息
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article implements Serializable {
	/**
	 * 文章ID
	 */
	private String articleId;

	/**
	 * 板块ID
	 */
	private Integer boardId;

	/**
	 * 板块名称
	 */
	private String boardName;

	/**
	 * 父级板块ID
	 */
	private Integer pBoardId;

	/**
	 * 父板块名称
	 */
	private String pBoardName;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 昵称
	 */
	private String nickName;

	/**
	 * 最后登录ip地址
	 */
	private String userIpAddress;

	/**
	 * 标题
	 */
	private String title;

	/**
	 * 封面
	 */
	private String cover;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * markdown内容
	 */
	private String markdownContent;

	/**
	 * 0:富文本编辑器 1:markdown编辑器
	 */
	private Integer editorType;

	/**
	 * 摘要
	 */
	private String summary;

	/**
	 * 发布时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date postTime;

	/**
	 * 最后更新时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date lastUpdateTime;

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


	public Integer getpBoardId() {
		return pBoardId;
	}

	public void setpBoardId(Integer pBoardId) {
		this.pBoardId = pBoardId;
	}

	public String getpBoardName() {
		return pBoardName;
	}

	public void setpBoardName(String pBoardName) {
		this.pBoardName = pBoardName;
	}

	@Override
	public String toString (){
		return "文章ID:" + ( articleId== null ? "空" : articleId) + ",板块ID:" + ( boardId== null ? "空" : boardId) + ",板块名称:" + ( boardName== null ? "空" : boardName)  + ",用户ID:" + ( userId== null ? "空" : userId) + ",昵称:" + ( nickName== null ? "空" : nickName) + ",最后登录ip地址:" + ( userIpAddress== null ? "空" : userIpAddress) + ",标题:" + ( title== null ? "空" : title) + ",封面:" + ( cover== null ? "空" : cover) + ",内容:" + ( content== null ? "空" : content) + ",markdown内容:" + ( markdownContent== null ? "空" : markdownContent) + ",0:富文本编辑器 1:markdown编辑器:" + ( editorType== null ? "空" : editorType) + ",摘要:" + ( summary== null ? "空" : summary) + ",发布时间:" + ( postTime== null ? "空" : DateUtils.format(postTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",最后更新时间:" + ( lastUpdateTime== null ? "空" : DateUtils.format(lastUpdateTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",阅读数量:" + ( readCount== null ? "空" : readCount) + ",点赞数:" + ( goodCount== null ? "空" : goodCount) + ",评论数:" + ( commentCount== null ? "空" : commentCount) + ",0未置顶  1:已置顶:" + ( topType== null ? "空" : topType) + ",0:没有附件  1:有附件:" + ( attachmentType== null ? "空" : attachmentType) + ",-1已删除 0:待审核  1:已审核 :" + ( status== null ? "空" : status);
	}


}