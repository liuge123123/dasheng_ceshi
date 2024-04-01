package com.tcc.modules.s.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.s.entity.SSmsOriginalEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 原始短信表
 *
 * @author 
 * @email 
 * @date 2022-08-29 14:00:49
 */
public interface SSmsOriginalService extends IService<SSmsOriginalEntity> {

    PageUtils queryPage(Map<String, Object> params);

    Map getCount(@Param("condition") Map<String, Object> params);

    void parseSms(List<SSmsOriginalEntity> list);

    void downExcel(HttpServletResponse response, Map<String,Object> params) throws IOException;
}

