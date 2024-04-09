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
 * @Description 邮箱验证码
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Code extends Throwable implements Serializable {
	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 编号
	 */
	private String code;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	/**
	 * 0:未使用  1:已使用
	 */
	private Integer status;



	@Override
	public String toString (){
		return "邮箱:" + ( email== null ? "空" : email) + ",编号:" + ( code== null ? "空" : code) + ",创建时间:" + ( createTime== null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern())) + ",0:未使用  1:已使用:" + ( status== null ? "空" : status);
	}
}