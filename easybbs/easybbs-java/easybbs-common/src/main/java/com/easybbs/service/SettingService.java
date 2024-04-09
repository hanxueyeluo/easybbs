package com.easybbs.service;
import com.easybbs.entity.dto.SysSettingDto;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Setting;
import com.easybbs.entity.query.SettingQuery;
import com.easybbs.exception.BusinessException;

import java.util.List;
/**
 * @Description 系统设置信息Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface SettingService{

	/**
	 * 根据条件查询列表
	 */
	List<Setting> findListByParam(SettingQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(SettingQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Setting> findListByPage(SettingQuery query);

	/**
	 * 新增
	 */
	Integer add(SettingQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<SettingQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<SettingQuery> listBean);

	/**
	 * 根据Code查询
	 */
	Setting getSettingByCode(String code);

	/**
	 * 根据Code更新
	 */
	Integer updateSettingByCode(Setting bean, String code);

	/**
	 * 根据Code删除
	 */
	Integer deleteSettingByCode(String code);

	SysSettingDto refresCache() throws BusinessException;

	void saveSetting(SysSettingDto sysSettingDto) throws BusinessException;
}