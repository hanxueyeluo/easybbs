package com.easybbs.entity.query;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;


/**
 * @Description 用户信息
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoQuery extends BaseQuery {
	/**
	 * 用户ID
	 */
	private String userId;

	private String userIdFuzzy;
	/**
	 * 昵称
	 */
	private String nickName;

	private String nickNameFuzzy;
	/**
	 * 邮箱
	 */
	private String email;

	private String emailFuzzy;
	/**
	 * 密码
	 */
	private String password;

	private String passwordFuzzy;
	/**
	 * 0:女 1:男
	 */
	private Integer sex;

	/**
	 * 个人描述
	 */
	private String personDescription;

	private String personDescriptionFuzzy;
	/**
	 * 加入时间
	 */
	private Date joinTime;

	private String joinTimeStart;

	private String joinTimeEnd;

	/**
	 * 最后登录时间
	 */
	private Date lastLoginTime;

	private String lastLoginTimeStart;

	private String lastLoginTimeEnd;

	/**
	 * 最后登录IP
	 */
	private String lastLoginIp;

	private String lastLoginIpFuzzy;
	/**
	 * 最后登录ip地址
	 */
	private String lastLoginIpAddress;

	private String lastLoginIpAddressFuzzy;
	/**
	 * 积分
	 */
	private Integer totalIntegral;

	/**
	 * 当前积分
	 */
	private Integer currentIntegral;

	/**
	 * 0:禁用 1:正常
	 */
	private Integer status;

}