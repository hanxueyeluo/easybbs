package com.easybbs.service;
import com.easybbs.entity.enums.OperRecordOpTypeEnum;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Record;
import com.easybbs.entity.query.RecordQuery;
import com.easybbs.exception.BusinessException;

import java.util.List;
/**
 * @Description 点赞记录Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface RecordService{

	/**
	 * 根据条件查询列表
	 */
	List<Record> findListByParam(RecordQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(RecordQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Record> findListByPage(RecordQuery query);

	/**
	 * 新增
	 */
	Integer add(RecordQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<RecordQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<RecordQuery> listBean);

	/**
	 * 根据OpId查询
	 */
	Record getRecordByOpId(Integer opId);

	/**
	 * 根据OpId更新
	 */
	Integer updateRecordByOpId(Record bean, Integer opId);

	/**
	 * 根据OpId删除
	 */
	Integer deleteRecordByOpId(Integer opId);

	/**
	 * 根据ObjectIdAndUserIdAndOpType查询
	 */
	Record getRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType);

	/**
	 * 根据ObjectIdAndUserIdAndOpType更新
	 */
	Integer updateRecordByObjectIdAndUserIdAndOpType(Record bean, String objectId, String userId, Integer opType);

	/**
	 * 根据ObjectIdAndUserIdAndOpType删除
	 */
	Integer deleteRecordByObjectIdAndUserIdAndOpType(String objectId, String userId, Integer opType);

	void doLike(String objectId, String userId, String nikeName, OperRecordOpTypeEnum opTypeEnum) throws BusinessException;
}