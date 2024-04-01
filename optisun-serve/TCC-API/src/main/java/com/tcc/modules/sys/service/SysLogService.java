

package com.tcc.modules.sys.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.base.service.LogService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.sys.entity.SysLogEntity;

import java.util.Map;


/**
 * 系统日志
 *
 */
public interface SysLogService extends IService<SysLogEntity>, LogService {

    PageUtils queryPage(Map<String, Object> params);

}
