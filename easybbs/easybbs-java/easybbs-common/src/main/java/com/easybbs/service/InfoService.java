package com.easybbs.service;

import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.po.Info;
import com.easybbs.entity.query.InfoQuery;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.enums.UserIntegralOperTypeEnum;
import com.easybbs.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;
/**
 * @Description 用户信息Service
 * @author hsy
 * @Date 2024/01/12
 */
public interface InfoService{

	/**
	 * 根据条件查询列表
	 */
	List<Info> findListByParam(InfoQuery query);

	/**
	 * 根据条件查询数量
	 */
	Integer findCountByParam(InfoQuery query);

	/**
	 * 分页查询
	 */
	PaginationResultVO<Info> findListByPage(InfoQuery query);

	/**
	 * 新增
	 */
	Integer add(InfoQuery bean);

	/**
	 * 批量新增
	 */
	Integer addBatch(List<InfoQuery> listBean);

	/**
	 * 批量新增/修改
	 */
	Integer addOrUpdateBatch(List<InfoQuery> listBean);



	/**
	 * 根据UserId查询
	 */
	Info getInfoByUserId(String userId);

	/**
	 * 根据UserId更新
	 */


    /**
	 * 根据UserId删除
	 */
	Integer deleteInfoByUserId(String userId);

	/**
	 * 根据Email查询
	 */
	Info getInfoByEmail(String email);

	/**
	 * 根据Email更新
	 */
	Integer updateInfoByEmail(Info bean, String email);

	/**
	 * 根据Email删除
	 */
	Integer deleteInfoByEmail(String email);

	/**
	 * 根据NickName查询
	 */
	Info getInfoByNickName(String nickName);

	/**
	 * 根据NickName更新
	 */
	Integer updateInfoByNickName(Info bean, String nickName);

	/**
	 * 根据NickName删除
	 */
	Integer deleteInfoByNickName(String nickName);

	Integer updateInfoByUserId(Info bean, String userId);

	void register(String email,String emailCode,String nickName,String password) throws BusinessException;

	void updateUserIntegral(String userId, UserIntegralOperTypeEnum operTypeEnum, Integer changeType, Integer integral) throws BusinessException;

	SessionWebUserDto login(String email,String password,String ip) throws BusinessException;

	void resetPwd(String email,String password,String emailCode) throws BusinessException;

	void updateUserInfo(Info userInfo, MultipartFile avatar) throws BusinessException;

	void updateUserStatus(Integer status,String userId);

	void sendMessage(String userId,String message,Integer integral) throws BusinessException;

}