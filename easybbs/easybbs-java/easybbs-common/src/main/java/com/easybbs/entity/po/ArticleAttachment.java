package com.easybbs.entity.po;

import java.io.Serializable;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * @Description 文件信息
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleAttachment implements Serializable {
	/**
	 * 文件ID
	 */
	private String fileId;

	/**
	 * 文章ID
	 */
	private String articleId;

	/**
	 * 用户id
	 */
	private String userId;

	/**
	 * 文件大小
	 */
	private Long fileSize;

	/**
	 * 文件名称
	 */
	private String fileName;

	/**
	 * 下载次数
	 */
	private Integer downloadCount;

	/**
	 * 文件路径
	 */
	private String filePath;

	/**
	 * 文件类型
	 */
	private Integer fileType;

	/**
	 * 下载所需积分
	 */
	private Integer integral;



	@Override
	public String toString (){
		return "文件ID:" + ( fileId== null ? "空" : fileId) + ",文章ID:" + ( articleId== null ? "空" : articleId) + ",用户id:" + ( userId== null ? "空" : userId) + ",文件大小:" + ( fileSize== null ? "空" : fileSize) + ",文件名称:" + ( fileName== null ? "空" : fileName) + ",下载次数:" + ( downloadCount== null ? "空" : downloadCount) + ",文件路径:" + ( filePath== null ? "空" : filePath) + ",文件类型:" + ( fileType== null ? "空" : fileType) + ",下载所需积分:" + ( integral== null ? "空" : integral);
	}
}