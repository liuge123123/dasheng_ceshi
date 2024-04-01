package com.tcc.modules.sys.service;

import cn.hutool.json.JSONObject;
import com.tcc.common.utils.R;

public interface SaasService {
    void initData(JSONObject params);

    R oauth(Long  orgId);

    void validParmas(JSONObject params);

    void resetResource(Long  orgId);

    boolean isOpen();
}
