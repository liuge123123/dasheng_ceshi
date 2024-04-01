package com.tcc.common.utils;

import cn.hutool.core.convert.Convert;

import javax.servlet.http.HttpServletRequest;

public class UserUtils {

    public static Long getUserId(HttpServletRequest request){
      String userId= Convert.toStr(request.getAttribute("userId"));
          return Convert.toLong(userId);
    }
    public static Long getOrgId(HttpServletRequest request){
        Long orgId= Convert.toLong(request.getHeader("orgId"),0L);
        return orgId;
    }
}
