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
 * @Description 用户消息
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message implements Serializable {
	/**
	 * 自增ID
	 */
	private Integer messageId;

	/**
	 * 接收人用户ID
	 */
	private String receivedUserId;

	/**
	 * 文章ID
	 */
	private String articleId;

	/**
	 * 文章标题
	 */
	private String articleTitle;

	/**
	 * 评论ID
	 */
	private Integer commentId;

	/**
	 * 发送人用户ID
	 */
	private String sendUserId;

	/**
	 * 发送人昵称
	 */
	private String sendNickName;

	/**
	 * 0:系统消息 1:评论 2:文章点赞  3:评论点赞 4:附件下载
	 */
	private Integer messageType;

	/**
	 * 消息内容
	 */
	private String messageContent;

	/**
	 * 1:未读 2:已读
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;



	@Override
	public String toString (){
		return "自增ID:" + ( messageId== null ? "空" : messageId) + ",接收人用户ID:" + ( receivedUserId== null ? "空" : receivedUserId) + ",文章ID:" + ( articleId== null ? "空" : articleId) + ",文章标题:" + ( articleTitle== null ? "空" : articleTitle) + ",评论ID:" + ( commentId== null ? "空" : commentId) + ",发送人用户ID:" + ( sendUserId== null ? "空" : sendUserId) + ",发送人昵称:" + ( sendNickName== null ? "空" : sendNickName) + ",0:系统消息 1:评论 2:文章点赞  3:评论点赞 4:附件下载:" + ( messageType== null ? "空" : messageType) + ",消息内容:" + ( messageContent== null ? "空" : messageContent) + ",1:未读 2:已读:" + ( status== null ? "空" : status) + ",创建时间:" + ( createTime== null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}