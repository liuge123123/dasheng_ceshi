

package com.tcc.modules.sys.controller;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.validator.ValidatorUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.sys.entity.SysConfigEntity;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.third.aliyun.SmsService;
import com.tcc.modules.third.mail.MailService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置信息
 *
 */
@RestController
@RequestMapping("/sys/config")
public class SysConfigController extends AbstractBackController {
	@Autowired
	private SysConfigService sysConfigService;
	
	/**
	 * 所有配置列表
	 */
	@GetMapping("/list")
	@RequiresPermissions("sys:config:list")
	public R list(){
		List list = sysConfigService.list(getOrgId());
		return R.ok().put("list", list);
	}
	
	
	/**
	 * 配置信息
	 */
	@GetMapping("/info/{id}")
	@RequiresPermissions("sys:config:info")
	public R info(@PathVariable("id") Long id){
		SysConfigEntity config = sysConfigService.getById(id);
		
		return R.ok().put("config", config);
	}
	
	/**
	 * 保存配置
	 */
	@SysLog("保存配置")
	@PostMapping("/save")
	@RequiresPermissions("sys:config:save")
	public R save(@RequestBody SysConfigEntity config){
		ValidatorUtils.validateEntity(config);

		sysConfigService.saveConfig(getOrgId(), config);
		
		return R.ok();
	}
	
	/**
	 * 修改配置
	 */
	@SysLog("修改配置")
	@PostMapping("/update")
	@RequiresPermissions("sys:config:update")
	public R update(@RequestBody SysConfigEntity config){
		ValidatorUtils.validateEntity(config);
		
		sysConfigService.update(getOrgId(), config);
		
		return R.ok();
	}
	
	/**
	 * 删除配置
	 */
	@SysLog("删除配置")
	@PostMapping("/delete")
	@RequiresPermissions("sys:config:delete")
	public R delete(@RequestBody Long[] ids){
		sysConfigService.deleteBatch(getOrgId(), ids);
		
		return R.ok();
	}

	/**
	 * 获取配置
	 */
	@SysLog("获取配置")
	@GetMapping("/getVal")
	public R getVal(String key){
		return R.ok().put("data", sysConfigService.getValue(getOrgId(), key));
	}

	/**
	 * 获取配置
	 */
	@SysLog("获取配置")
	@PostMapping("/saveVal")
	public R saveVal(@RequestBody Map<String, Object> params){
		String key = Convert.toStr(params.get("key"));
		String val = Convert.toStr(params.get("val"));
		sysConfigService.saveConfig(getOrgId(), key, val);
		return R.ok();
	}

	/**
	 * 发送测试邮件
	 * @param params
	 * @return
	 */
	@SysLog("发送测试邮件")
	@PostMapping("/sendTestMail")
	public R sendTestMail(@RequestBody Map<String, Object> params){
		JSONObject config = JSONUtil.parseObj(params);
		String mailTo = config.getStr("mailTo");
		if(StrUtil.isNotBlank(mailTo)) {
			JSONObject res = MailService.sendMail(config, mailTo, "测试邮件", "这是云知美互客给您发送的一封测试邮件，收到该邮件表示邮件服务配置正确，若邮件内容与您无关请忽略！");
			if(res.getInt("code", 0) == 0){
				return R.ok();
			}else{
				return R.error(res.getInt("code"), res.getStr("msg"));
			}
		}else{
			throw new WTDPException("接受者邮箱不能为空");
		}
	}

	/**
	 * 发送测试邮件
	 * @param params
	 * @return
	 */
	@SysLog("发送测试短信")
	@PostMapping("/sendTestSms")
	public R sendTestSms(@RequestBody Map<String, Object> params){
		JSONObject config = JSONUtil.parseObj(params);
		String smsTo = config.getStr("smsTo");
		if(StrUtil.isNotBlank(smsTo)) {
			JSONObject res = null;
			try {
				JSONObject smsParams = new JSONObject();
				smsParams.putOpt("customer", "云知美互客用户");
				res = SmsService.sendMsg(config, smsTo, config.getStr("sign"), config.getStr("tplCode"), smsParams.toString(), "");
			} catch (Exception e) {
				e.printStackTrace();
				throw new WTDPException("应用配置错误");
			}
			if(res.getInt("code", 0) == 0){
				return R.ok();
			}else{
				return R.error(res.getInt("code"), res.getStr("msg"));
			}
		}else{
			throw new WTDPException("接受者手机号不能为空");
		}
	}
}
