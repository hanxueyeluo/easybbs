package com.easybbs.entity.query;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;


/**
 * @Description 点赞记录
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordQuery extends BaseQuery {
	/**
	 * 自增ID
	 */
	private Integer opId;

	/**
	 * 操作类型0:文章点赞 1:评论点赞
	 */
	private Integer opType;

	/**
	 * 主体ID
	 */
	private String objectId;

	private String objectIdFuzzy;
	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;
	/**
	 * 发布时间
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 主体作者ID
	 */
	private String authorUserId;

	private String authorUserIdFuzzy;
}