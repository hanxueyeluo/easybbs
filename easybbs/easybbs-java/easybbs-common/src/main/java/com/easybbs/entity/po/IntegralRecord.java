package com.easybbs.entity.po;

import java.io.Serializable;

import com.easybbs.entity.enums.UserIntegralOperTypeEnum;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import com.easybbs.entity.enums.DateTimePatternEnum;
import com.easybbs.utils.DateUtils;


/**
 * @Description 用户积分记录表
 * @author hsy
 * @Date 2024/01/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IntegralRecord implements Serializable {
	/**
	 * 记录ID
	 */
	private Integer recordId;

	/**
	 * 用户ID
	 */
	private String userId;

	/**
	 * 操作类型
	 */
	private Integer operType;

	/**
	 * 积分
	 */
	private Integer integral;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	private String operTypeName;

	public String getOperTypeName() {
		UserIntegralOperTypeEnum operTypeEnum=UserIntegralOperTypeEnum.getByType(operType);
		return operTypeEnum==null?"":operTypeEnum.getDesc();
	}

	public void setOperTypeName(String operTypeName) {
		this.operTypeName = operTypeName;
	}

	@Override
	public String toString (){
		return "记录ID:" + ( recordId== null ? "空" : recordId) + ",用户ID:" + ( userId== null ? "空" : userId) + ",操作类型:" + ( operType== null ? "空" : operType) + ",积分:" + ( integral== null ? "空" : integral) + ",创建时间:" + ( createTime== null ? "空" : DateUtils.format(createTime, DateTimePatternEnum.YYYY_MM_DD_HH_MM_SS.getPattern()));
	}
}