package com.easybbs.entity.query;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;


/**
 * @Description 用户消息
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageQuery extends BaseQuery {
	/**
	 * 自增ID
	 */
	private Integer messageId;

	/**
	 * 接收人用户ID
	 */
	private String receivedUserId;

	private String receivedUserIdFuzzy;
	/**
	 * 文章ID
	 */
	private String articleId;

	private String articleIdFuzzy;
	/**
	 * 文章标题
	 */
	private String articleTitle;

	private String articleTitleFuzzy;
	/**
	 * 评论ID
	 */
	private Integer commentId;

	/**
	 * 发送人用户ID
	 */
	private String sendUserId;

	private String sendUserIdFuzzy;
	/**
	 * 发送人昵称
	 */
	private String sendNickName;

	private String sendNickNameFuzzy;
	/**
	 * 0:系统消息 1:评论 2:文章点赞  3:评论点赞 4:附件下载
	 */
	private Integer messageType;

	/**
	 * 消息内容
	 */
	private String messageContent;

	private String messageContentFuzzy;
	/**
	 * 1:未读 2:已读
	 */
	private Integer status;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

}