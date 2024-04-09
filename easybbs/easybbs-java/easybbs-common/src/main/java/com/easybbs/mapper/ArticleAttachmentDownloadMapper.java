package com.easybbs.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 用户附件下载Mapper
 * @author hsy
 * @Date 2024/01/12
 */
@Mapper
public interface ArticleAttachmentDownloadMapper<T, P> extends BaseMapper {
	/**
	 * 根据FileIdAndUserId查询
	 */
	T selectByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

	/**
	 * 根据FileIdAndUserId更新
	 */
	Integer updateByFileIdAndUserId(@Param("bean") T t, @Param("fileId") String fileId, @Param("userId") String userId);

	/**
	 * 根据FileIdAndUserId删除
	 */
	Integer deleteByFileIdAndUserId(@Param("fileId") String fileId, @Param("userId") String userId);

}