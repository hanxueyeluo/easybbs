package com.easybbs.service.impl;
import com.easybbs.entity.config.WebConfig;
import com.easybbs.entity.constants.Constants;
import com.easybbs.entity.po.Info;
import com.easybbs.entity.query.InfoQuery;
import com.easybbs.entity.query.SimplePage;
import com.easybbs.entity.vo.PaginationResultVO;
import com.easybbs.entity.po.Code;
import com.easybbs.entity.query.CodeQuery;
import com.easybbs.entity.enums.PageSize;
import com.easybbs.exception.BusinessException;
import com.easybbs.mapper.CodeMapper;
import com.easybbs.mapper.InfoMapper;
import com.easybbs.service.CodeService;
import com.easybbs.utils.StringTools;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.List;
/**
 * @Description 邮箱验证码Service
 * @author hsy
 * @Date 2024/01/12
 */
@Service("CodeService")
public class CodeServiceImpl implements CodeService{

	private static final Logger logger= LoggerFactory.getLogger(CodeServiceImpl.class);
	@Resource
	private CodeMapper<Code,CodeQuery>  CodeMapper;

	@Resource
	private InfoMapper<Info, InfoQuery> InfoMapper;

	@Resource
	private JavaMailSender javaMailSender;

	@Resource
	private WebConfig webConfig;
	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<Code> findListByParam(CodeQuery query){
		return this.CodeMapper.selectList(query);
	}
	/**
	 * 根据条件查询数量
	 */
	@Override
	public Integer findCountByParam(CodeQuery query){
		return this.CodeMapper.selectCount(query);
	}
	/**
	 * 分页查询
	 */
	@Override
	public PaginationResultVO<Code> findListByPage(CodeQuery query){
		Integer count = this.findCountByParam(query);
		Integer pageSize = query.getPageSize()==null?PageSize.SIZE15.getSize():query.getPageSize();
		SimplePage page = new SimplePage(query.getPageNo(), count, pageSize);
		query.setSimplePage(page);
		List<Code> list = this.findListByParam(query);
		PaginationResultVO<Code> result = new PaginationResultVO<>(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}
	/**
	 * 新增
	 */
	@Override
	public Integer add(CodeQuery bean){
		return this.CodeMapper.insert(bean);
	}
	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<CodeQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.CodeMapper.insertBatch(listBean);
	}
	/**
	 * 批量新增/修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<CodeQuery> listBean){
		if (listBean==null || listBean.isEmpty()) {
			return 0;
		}
		return this.CodeMapper.insertOrUpdateBatch(listBean);
	}
	/**
	 * 根据EmailAndCode查询
	 */
	@Override
	public Code getCodeByEmailAndCode(String email, String code){
		return this.CodeMapper.selectByEmailAndCode(email, code);
	}
	/**
	 * 根据EmailAndCode更新
	 */
	@Override
	public Integer updateCodeByEmailAndCode(Code bean, String email, String code){
		return this.CodeMapper.updateByEmailAndCode(bean,email, code);
	}
	/**
	 * 根据EmailAndCode删除
	 */
	@Override
	public Integer deleteCodeByEmailAndCode(String email, String code){
		return this.CodeMapper.deleteByEmailAndCode(email, code);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void sendEmailCode(String email,Integer type) throws BusinessException {
		if(Constants.ZERO==type){
			Info info= InfoMapper.selectByEmail(email);
			if(info!=null){
				throw new BusinessException("邮箱已存在");
			}
		}
		String code= StringTools.getRandomString(Constants.LENGTH_5);
		sendEmailCodeDo(email,code);
		CodeMapper.disableEmailCode(email);

		Code emailcode=new Code();
		emailcode.setCode(code);
		emailcode.setEmail(email);
		emailcode.setStatus(Constants.ZERO);
		emailcode.setCreateTime(new Date());
		CodeMapper.insert(emailcode);
	}
	@SneakyThrows
	private void sendEmailCodeDo(String toEmail, String code) {
		try{
			MimeMessage message=javaMailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(message,true);
			//邮件发送人
			helper.setFrom(webConfig.getSendUserName());
			//邮件收件人
			helper.setTo(toEmail);

			helper.setSubject("注册邮箱验证码");
			helper.setText("邮箱验证码为"+code);
			helper.setSentDate(new Date());
			javaMailSender.send(message);

		}catch (Exception e){
			logger.error("发送邮件失败",e);
			throw new BusinessException("邮件发送失败");
		}

	}

	@Override
	public void checkCode(String email, String emailCode) throws BusinessException {
		Code dbinfo=this.CodeMapper.selectByEmailAndCode(email,emailCode);
		if(null==dbinfo){
			throw new BusinessException("邮箱验证码不正确");
		}
		if(dbinfo.getStatus()!=Constants.ZERO || System.currentTimeMillis()-dbinfo.getCreateTime().getTime()>1000*60*Constants.LENGTH_15){
			throw new BusinessException("邮箱验证码已失效");
		}
		CodeMapper.disableEmailCode(email);
	}
}