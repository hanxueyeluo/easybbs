package com.easybbs.entity.query;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * @Description 用户附件下载
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleAttachmentDownloadQuery extends BaseQuery {
	/**
	 * 文件ID
	 */
	private String fileId;

	private String fileIdFuzzy;
	/**
	 * 用户id
	 */
	private String userId;

	private String userIdFuzzy;
	/**
	 * 文章ID
	 */
	private String articleId;

	private String articleIdFuzzy;
	/**
	 * 文件下载次数
	 */
	private Integer downloadCount;

}