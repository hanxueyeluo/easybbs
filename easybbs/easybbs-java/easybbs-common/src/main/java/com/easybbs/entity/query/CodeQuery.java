package com.easybbs.entity.query;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;


/**
 * @Description 邮箱验证码
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CodeQuery extends BaseQuery {
	/**
	 * 邮箱
	 */
	private String email;

	private String emailFuzzy;
	/**
	 * 编号
	 */
	private String code;

	private String codeFuzzy;
	/**
	 * 创建时间
	 */
	private Date createTime;

	private String createTimeStart;

	private String createTimeEnd;

	/**
	 * 0:未使用  1:已使用
	 */
	private Integer status;

}