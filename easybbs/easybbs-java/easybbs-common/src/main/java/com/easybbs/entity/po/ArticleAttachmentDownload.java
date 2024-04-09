package com.easybbs.entity.po;

import java.io.Serializable;
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
public class ArticleAttachmentDownload implements Serializable {
	/**
	 * 文件ID
	 */
	private String fileId;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 文章ID
	 */
	private String articleId;

	/**
	 * 文件下载次数
	 */
	private Integer downloadCount;



	@Override
	public String toString (){
		return "文件ID:" + ( fileId== null ? "空" : fileId) + ",用户id:" + ( userId== null ? "空" : userId) + ",文章ID:" + ( articleId== null ? "空" : articleId) + ",文件下载次数:" + ( downloadCount== null ? "空" : downloadCount);
	}
}