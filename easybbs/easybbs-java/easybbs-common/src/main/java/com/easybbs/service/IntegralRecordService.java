package com.easybbs.service;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.IntegralRecord;
import com.easybbs.entity.query.IntegralRecordQuery;

import java.util.List;
/**
 * @Description 用户积分记录表Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface IntegralRecordService{

	/**
	 * 根据条件查询列表
	 */
	List<IntegralRecord> findListByParam(IntegralRecordQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(IntegralRecordQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<IntegralRecord> findListByPage(IntegralRecordQuery query);

	/**
	 * 新增
	 */
	Integer add(IntegralRecordQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<IntegralRecordQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<IntegralRecordQuery> listBean);

	/**
	 * 根据RecordId查询
	 */
	IntegralRecord getIntegralRecordByRecordId(Integer recordId);

	/**
	 * 根据RecordId更新
	 */
	Integer updateIntegralRecordByRecordId(IntegralRecord bean, Integer recordId);

	/**
	 * 根据RecordId删除
	 */
	Integer deleteIntegralRecordByRecordId(Integer recordId);

}