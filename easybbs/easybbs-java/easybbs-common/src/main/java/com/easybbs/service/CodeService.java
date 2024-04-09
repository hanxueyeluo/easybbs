package com.easybbs.service;

import com.easybbs.exception.BusinessException;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Code;
import com.easybbs.entity.query.CodeQuery;

import java.util.List;
/**
 * @Description 邮箱验证码Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface  CodeService{

	/**
	 * 根据条件查询列表
	 */
	List<Code> findListByParam(CodeQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(CodeQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Code> findListByPage(CodeQuery query);

	/**
	 * 新增
	 */
	Integer add(CodeQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<CodeQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<CodeQuery> listBean);

	/**
	 * 根据EmailAndCode查询
	 */
	Code getCodeByEmailAndCode(String email, String code);

	/**
	 * 根据EmailAndCode更新
	 */
	Integer updateCodeByEmailAndCode(Code bean, String email, String code);

	/**
	 * 根据EmailAndCode删除
	 */
	Integer deleteCodeByEmailAndCode(String email, String code);

	void sendEmailCode(String email,Integer type) throws BusinessException;
	void checkCode(String email,String emailCode) throws BusinessException;
}