package com.easybbs.service.impl;
import com.easybbs.entity.config.WebConfig;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.dto.SessionWebUserDto;
import com.easybbs.entity.enums.*;
import com.easybbs.entity.po.*;
import com.easybbs.entity.query.*;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.exception.BusinessException;
import com.easybbs.mapper.*;
import com.easybbs.service.CodeService;
import com.easybbs.service.InfoService;
import com.easybbs.service.MessageService;
import com.easybbs.utils.*;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @Description 用户信息Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("InfoService")
public class InfoServiceImpl implements InfoService{

	private static final Logger logger= LoggerFactory.getLogger(InfoServiceImpl.class);
	@Resource
	private InfoMapper<Info,InfoQuery> infoMapper;

	@Resource
	private CodeService codeService;

	@Resource
	private MessageMapper<Message,MessageQuery> messageMapper;

	@Resource
	private IntegralRecordMapper<IntegralRecord, IntegralRecordQuery> integralRecordMapper;

	@Resource
	private WebConfig webConfig;

	@Resource
	FileUtils fileUtils;

	@Resource
	private ArticleMapper<Article, ArticleQuery>articleMapper;

	@Resource
	private CommentMapper<Comment,CommentQuery>commentMapper;

	@Resource
	private MessageService messageService;


	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Info> findListByParam(InfoQuery query){
		return this.infoMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(InfoQuery query){
		return this.infoMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Info> findListByPage(InfoQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Info> list = this.findListByParam(query);
		PaginationResultVO<Info> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(InfoQuery bean){
		return this.infoMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<InfoQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.infoMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<InfoQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.infoMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据UserId查询
	 */
	@Override
	public Info getInfoByUserId(String userId){
		return this.infoMapper.selectByUserId(userId);
	}
	/**
	 * 根据UserId更新
	 *
	 * @return
	 */
	@Override
	public Integer updateInfoByUserId(Info bean, String userId){
		return this.infoMapper.updateByUserId(bean,userId);
	}



	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteInfoByUserId(String userId){
		return this.infoMapper.deleteByUserId(userId);
	}
	/**
	 * 根据Email查询
	 */
	@Override
	public Info getInfoByEmail(String email){
		return this.infoMapper.selectByEmail(email);
	}
	/**
	 * 根据Email更新
	 */
	@Override
	public Integer updateInfoByEmail(Info bean, String email){
		return this.infoMapper.updateByEmail(bean,email);
	}
	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteInfoByEmail(String email){
		return this.infoMapper.deleteByEmail(email);
	}
	/**
	 * 根据NickName查询
	 */
	@Override
	public Info getInfoByNickName(String nickName){
		return this.infoMapper.selectByNickName(nickName);
	}
	/**
	 * 根据NickName更新
	 */
	@Override
	public Integer updateInfoByNickName(Info bean, String nickName){
		return this.infoMapper.updateByNickName(bean,nickName);
	}
	/**
	 * 根据NickName删除
	 */
	@Override
	public Integer deleteInfoByNickName(String nickName){
		return this.infoMapper.deleteByNickName(nickName);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void register(String email, String emailCode, String nickName, String password) throws BusinessException {
		Info info=this.infoMapper.selectByEmail(email);

		if(info !=null){
			throw new BusinessException("邮箱账号已存在");
		}
		info=this.infoMapper.selectByNickName(nickName);
		if (info!=null){
			throw new BusinessException("昵称已存在");
		}
		codeService.checkCode(email,emailCode);
		String userId= StringTools.getRandomNumber(Constants.LENGTH_10);
		Info insertinfo=new Info();
		insertinfo.setUserId(userId);
		insertinfo.setNickName(nickName);
		insertinfo.setEmail(email);
		insertinfo.setPassword(StringTools.encodeMd5(password));
		insertinfo.setJoinTime(new Date());
		insertinfo.setStatus(UserStatusEnum.ENABLE.getStatus());
		insertinfo.setTotalIntegral(Constants.ZERO);
		insertinfo.setCurrentIntegral(Constants.ZERO);
		this.infoMapper.insert(insertinfo);

		//更新用户积分
		updateUserIntegral(userId, UserIntegralOperTypeEnum.REGISTER, UserIntegralChangeTypeEnum.ADD.getChangeType(),Constants.INTEGRAL_5);
		//记录消息
		Message message=new Message();
		message.setReceivedUserId(userId);
		message.setMessageType(MessageTypeEnum.SYS.getType());
		message.setCreateTime(new Date());
		message.setStatus(MessageStatusEnum.NO_READ.getStatus());
		message.setMessageContent(SysCacheUtils.getSysSetting().getRegisterSetting().getRegisterWelcomInfo());
		messageMapper.insert(message);
	}

	/**
	 * 更新用户积分
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateUserIntegral(String userId, UserIntegralOperTypeEnum operTypeEnum,Integer changeType,Integer integral) throws BusinessException {
		integral=changeType+integral;
		if (integral == 0) {
			return;
		}
		Info info=infoMapper.selectByUserId(userId);
		if (UserIntegralChangeTypeEnum.REDUCE.getChangeType().equals(changeType)&&info.getCurrentIntegral()+integral<0) {
			integral=changeType*info.getCurrentIntegral();
		}

		IntegralRecord record=new IntegralRecord();
		record.setUserId(userId);
		record.setOperType(operTypeEnum.getOperType());
		record.setCreateTime(new Date());
		record.setIntegral(integral);
		this.integralRecordMapper.insert(record);

		Integer count=this.infoMapper.updateIntegral(userId, integral);
		if (count == 0) {
			throw new BusinessException("更新用户积分失败");
		}
	}

	@Override
	public SessionWebUserDto login(String email, String password, String ip) throws BusinessException {
		Info info=this.infoMapper.selectByEmail(email);
		if (info == null|| !info.getPassword().equals(password)) {
			throw new BusinessException("账号或者密码错误");
		}
		if(!UserStatusEnum.ENABLE.getStatus().equals(info.getStatus())){
			throw new BusinessException("账号已禁用");
		}
		String ipAddress=getIpAddress(ip);
		Info updateInfo=new Info();
		updateInfo.setLastLoginTime(new Date());
		updateInfo.setLastLoginIp(ip);
		updateInfo.setLastLoginIpAddress(ipAddress);
		this.infoMapper.updateByUserId(updateInfo,info.getUserId());
		SessionWebUserDto sessionWebUserDto=new SessionWebUserDto();
		sessionWebUserDto.setNickName(info.getNickName());
		sessionWebUserDto.setProvince(ipAddress);
		sessionWebUserDto.setUserId(info.getUserId());
		if (!StringTools.isEmpty(webConfig.getAdminEmails())&& ArrayUtils.contains(webConfig.getAdminEmails().split(","),info.getEmail())) {
		sessionWebUserDto.setAdmin(true);
		}else {
			sessionWebUserDto.setAdmin(false);
		}
		 return sessionWebUserDto;
	}
	public String getIpAddress(String ip){
		try {
			String url="http://whois.pconline.com.cn/ipJson.jsp?json=true&ip="+ip;
			String responseJson = OKHttpUtils.getRequest(url);
			if (responseJson == null) {
				return Constants.NO_ADDRESS;
			}
			Map<String,String> addressInfo = JsonUtils.convertJson2Obj(responseJson,Map.class);
			return addressInfo.get("pro");
		}catch (Exception e){
			logger.error("获取IP地址失败",e);
		}
		return Constants.NO_ADDRESS;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void resetPwd(String email, String password, String emailCode) throws BusinessException {
		Info userInfo=this.infoMapper.selectByEmail(email);
		if (userInfo == null) {
			throw new BusinessException("邮箱不存在");
		}
		codeService.checkCode(email,emailCode);
		Info updateInfo=new Info();
		updateInfo.setPassword(StringTools.encodeMd5(password));
		this.infoMapper.updateByEmail(updateInfo,email);
	}

	@Override
	public void updateUserInfo(Info userInfo, MultipartFile avatar) throws BusinessException {
		infoMapper.updateByUserId(userInfo, userInfo.getUserId());
		if (avatar != null) {
			fileUtils.uploadFile2Local(avatar,userInfo.getUserId(),FileUploadTypeEnum.AVATAR);
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateUserStatus(Integer status, String userId) {
		if (UserStatusEnum.DISABLE.getStatus().equals(status)) {
			this.articleMapper.updateStatusBatchByUserId(ArticleStatusEnum.DEL.getStatus(), userId);
			this.commentMapper.updateStatusBatchByUserId(CommentStatusEnum.DEL.getStatus(), userId);
		}
		Info info=new Info();
		info.setStatus(status);
		infoMapper.updateByUserId(info,userId);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sendMessage(String userId, String message, Integer integral) throws BusinessException {
		Message usermessage=new Message();
		usermessage.setReceivedUserId(userId);
		usermessage.setMessageType(MessageTypeEnum.SYS.getType());
		usermessage.setCreateTime(new Date());
		usermessage.setStatus(MessageStatusEnum.NO_READ.getStatus());
		usermessage.setMessageContent(message);
		messageService.add(usermessage);
		UserIntegralChangeTypeEnum changeTypeEnum=UserIntegralChangeTypeEnum.ADD;
		if (integral!=null&&integral!=0) {
			if (integral<0) {
				integral=integral*-1;
				changeTypeEnum=UserIntegralChangeTypeEnum.REDUCE;
			}
			updateUserIntegral(userId,UserIntegralOperTypeEnum.ADMIN,changeTypeEnum.getChangeType(),integral);
		}
	}
}

