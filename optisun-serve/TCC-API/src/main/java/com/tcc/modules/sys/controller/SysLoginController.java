

package com.tcc.modules.sys.controller;

import cn.hutool.core.convert.Convert;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.MD5Util;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.form.SysLoginForm;
import com.tcc.modules.sys.service.SaasService;
import com.tcc.modules.sys.service.SysCaptchaService;
import com.tcc.modules.sys.service.SysUserService;
import com.tcc.modules.sys.service.SysUserTokenService;
import org.apache.commons.io.IOUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;

/**
 * 登录相关
 *
 */
@RestController
public class SysLoginController extends AbstractBackController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserTokenService sysUserTokenService;
	@Autowired
	private SysCaptchaService sysCaptchaService;
	@Autowired
	private SaasService saasService;
	/**
	 * 验证码
	 */
	@GetMapping("captcha.jpg")
	public void captcha(HttpServletResponse response, String uuid)throws IOException {
		response.setHeader("Cache-Control", "no-store, no-cache");
		response.setContentType("image/jpeg");

		//获取图片验证码
		BufferedImage image = sysCaptchaService.getCaptcha(uuid);

		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(image, "jpg", out);
		IOUtils.closeQuietly(out);
	}

	/**
	 * 登录
	 */
	@SysLog("用户登录")
	@PostMapping("/sys/login")
	public Map<String, Object> login(@RequestBody SysLoginForm form)throws IOException {
		boolean captcha = sysCaptchaService.validate(form.getUuid(), form.getCaptcha());
		if(!captcha){
			return R.error("验证码不正确");
		}

		//用户信息
		SysUserEntity user = sysUserService.queryByUserName(form.getUsername());
		//账号不存在、密码错误
		if(user == null || !user.getPassword().equals(MD5Util.md5(form.getPassword()))) {
			return R.error("账号或密码不正确");
		}

		//账号锁定
		if(user.getStatus() == 0){
			return R.error("账号已被锁定,请联系管理员");
		}
		// 判断是否开启sass模式
		if(user.getUserId() != 1) {
			R oauthR = saasService.oauth(user.getOrgId());
			if (Convert.toInt(oauthR.get("code")) != 0) {
				return oauthR;
			} else {
				// 授权成功，检查授权资源是否有变化，若有变化则重置更新
				saasService.resetResource(user.getOrgId());
			}
		}
		//生成token，并保存到数据库
		R r = sysUserTokenService.createToken(user);
		return r;
	}


	/**
	 * 退出
	 */
	@SysLog("退出登录")
	@PostMapping("/sys/logout")
	public R logout() {
		sysUserTokenService.logout();
		return R.ok();
	}
	
}
