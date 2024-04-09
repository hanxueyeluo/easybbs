package com.easybbs.service.impl;
import com.easybbs.entity.dto.SysSettingDto;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Setting;
import com.easybbs.entity.query.SettingQuery;
import com.easybbs.entity.enums.PageSize;
import com.easybbs.entity.enums.SysSettingCodeEnum;
import com.easybbs.exception.BusinessException;
import com.easybbs.mapper.SettingMapper;
import com.easybbs.service.SettingService;
import com.easybbs.utils.JsonUtils;
import com.easybbs.utils.StringTools;
import com.easybbs.utils.SysCacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description 系统设置信息Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("SettingService")
public class SettingServiceImpl implements SettingService{

	private static final Logger logger=  LoggerFactory.getLogger(SettingServiceImpl.class);
	@Resource
	private SettingMapper<Setting,SettingQuery> settingMapper;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Setting> findListByParam(SettingQuery query){
		return this.settingMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(SettingQuery query){
		return this.settingMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Setting> findListByPage(SettingQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Setting> list = this.findListByParam(query);
		PaginationResultVO<Setting> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(SettingQuery bean){
		return this.settingMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<SettingQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.settingMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<SettingQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.settingMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据Code查询
	 */
	@Override
	public Setting getSettingByCode(String code){
		return this.settingMapper.selectByCode(code);
	}
	/**
	 * 根据Code更新
	 */
	@Override
	public Integer updateSettingByCode(Setting bean, String code){
		return this.settingMapper.updateByCode(bean,code);
	}
	/**
	 * 根据Code删除
	 */
	@Override
	public Integer deleteSettingByCode(String code){
		return this.settingMapper.deleteByCode(code);
	}

	/**
	 * 刷新缓存
	 */
	@Override
	public SysSettingDto refresCache() throws BusinessException {
		try {
			SysSettingDto sysSettingDto=new SysSettingDto();
			List <Setting> list=this.settingMapper.selectList(new SettingQuery());
			for (Setting setting : list) {
				String jsonContent = setting.getJsonContent();
				if (StringTools.isEmpty(jsonContent)) {
					continue;
				}
				String code =setting.getCode();
				SysSettingCodeEnum sysSettingCodeEnum=SysSettingCodeEnum.getByCode(code);
				PropertyDescriptor pd=new PropertyDescriptor(sysSettingCodeEnum.getPropName(),SysSettingDto.class);
				Method method= pd.getWriteMethod();
				Class subClassz=Class.forName(sysSettingCodeEnum.getClassz());
				method.invoke(sysSettingDto,JsonUtils.convertJson2Obj(jsonContent,subClassz));
			}
			SysCacheUtils.refresh(sysSettingDto);
			return sysSettingDto;
		} catch (Exception e) {
			logger.error("刷新缓存失败",e);
			throw new BusinessException("刷新缓存失败");
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveSetting(SysSettingDto sysSettingDto) throws BusinessException {
		try {
			Class classz= SysSettingDto.class;
			for (SysSettingCodeEnum codeEnum:SysSettingCodeEnum.values()){
				PropertyDescriptor pd = new PropertyDescriptor(codeEnum.getPropName(),classz);
				Method method= pd.getReadMethod();
				Object obj=method.invoke(sysSettingDto);
				Setting setting=new Setting();
				setting.setCode(codeEnum.getCode());
				setting.setJsonContent(JsonUtils.convertObj2Json(obj));
				this.settingMapper.insertOrUpdate(setting);
			}
		}catch (Exception e){
			logger.error("保存设置失败",e);
			throw new BusinessException("保存设置失败");
		}
	}
}