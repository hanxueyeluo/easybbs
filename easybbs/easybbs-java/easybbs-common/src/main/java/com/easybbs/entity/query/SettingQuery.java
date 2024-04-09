package com.easybbs.entity.query;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;


/**
 * @Description 系统设置信息
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingQuery extends BaseQuery {
	/**
	 * 编号
	 */
	private String code;

	private String codeFuzzy;
	/**
	 * 设置信息
	 */
	private String jsonContent;

	private String jsonContentFuzzy;
}