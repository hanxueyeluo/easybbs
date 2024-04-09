package com.easybbs.service.impl;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.IntegralRecord;
import com.easybbs.entity.query.IntegralRecordQuery;
import com.easybbs.entity.enums.PageSize;
import com.easybbs.mapper.IntegralRecordMapper;
import com.easybbs.service.IntegralRecordService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
/**
 * @Description 用户积分记录表Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("IntegralRecordService")
public class IntegralRecordServiceImpl implements IntegralRecordService{

	@Resource
	private IntegralRecordMapper<IntegralRecord,IntegralRecordQuery> integralRecordMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<IntegralRecord> findListByParam(IntegralRecordQuery query){
		return this.integralRecordMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(IntegralRecordQuery query){
		return this.integralRecordMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<IntegralRecord> findListByPage(IntegralRecordQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<IntegralRecord> list = this.findListByParam(query);
		PaginationResultVO<IntegralRecord> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(IntegralRecordQuery bean){
		return this.integralRecordMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<IntegralRecordQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.integralRecordMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<IntegralRecordQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.integralRecordMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据RecordId查询
	 */
	@Override
	public IntegralRecord getIntegralRecordByRecordId(Integer recordId){
		return this.integralRecordMapper.selectByRecordId(recordId);
	}
	/**
	 * 根据RecordId更新
	 */
	@Override
	public Integer updateIntegralRecordByRecordId(IntegralRecord bean, Integer recordId){
		return this.integralRecordMapper.updateByRecordId(bean,recordId);
	}
	/**
	 * 根据RecordId删除
	 */
	@Override
	public Integer deleteIntegralRecordByRecordId(Integer recordId){
		return this.integralRecordMapper.deleteByRecordId(recordId);
	}
}