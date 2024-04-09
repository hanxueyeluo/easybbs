package com.easybbs.entity.po;

import java.io.Serializable;
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
public class Setting implements Serializable {
	/**
	 * 编号
	 */
	private String code;

	/**
	 * 设置信息
	 */
	private String jsonContent;



	@Override
	public String toString (){
		return "编号:" + ( code== null ? "空" : code) + ",设置信息:" + ( jsonContent== null ? "空" : jsonContent);
	}
}