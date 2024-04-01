

package com.tcc.common.aspect;

import cn.hutool.json.JSONUtil;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.base.entity.BaseLogEntity;
import com.tcc.common.base.entity.BaseUserEntity;
import com.tcc.common.base.service.LogService;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.HttpContextUtils;
import com.tcc.common.utils.IPUtils;
import com.tcc.common.utils.UserUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


/**
 * 系统日志，切面处理类
 *
 */
@Aspect
@Component
public class SysLogAspect {
	@Autowired
	private LogService logService;
	
	@Pointcut("@annotation(com.tcc.common.annotation.SysLog)")
	public void logPointCut() { 
		
	}

	@Around("logPointCut()")
	public Object around(ProceedingJoinPoint point) throws Throwable {
		long beginTime = System.currentTimeMillis();
		//执行方法
		Object result = point.proceed();
		//执行时长(毫秒)
		long time = System.currentTimeMillis() - beginTime;

		//保存日志
		saveSysLog(point, time);

		return result;
	}

	private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();

		BaseLogEntity sysLog = new BaseLogEntity();
		SysLog syslog = method.getAnnotation(SysLog.class);
		if(syslog != null){
			//注解上的描述
			sysLog.setOperation(syslog.value());
		}

		//请求的方法名
		String className = joinPoint.getTarget().getClass().getName();
		String methodName = signature.getName();
		sysLog.setMethod(className + "." + methodName + "()");

		//请求的参数
		Object[] args = joinPoint.getArgs();
		try{
			String params = JSONUtil.toJsonStr(args);
			sysLog.setParams(params);
		}catch (Exception e){

		}

		//获取request
		HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
		//设置IP地址
		sysLog.setIp(IPUtils.getIpAddr(request));

		//用户名
		if(SecurityUtils.getSubject().getPrincipal() != null) {
			String username = ((BaseUserEntity) SecurityUtils.getSubject().getPrincipal()).getUsername();
			sysLog.setUsername(username);
		}else {
			// 前端用户id
			Long userId = UserUtils.getUserId(request);
			if(userId != null){
				sysLog.setUsername("cust_" + userId);
			}
		}

		sysLog.setTime(time);
		sysLog.setCreateDate(DateUtils.getCurrentTime());
		//保存系统日志
		logService.saveLog(sysLog);
	}
}
