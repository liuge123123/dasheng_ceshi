package com.tcc.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.modules.sys.entity.DictionaryEntity;

import java.util.List;
import java.util.Map;

/**
 * 数据字典
 *
 * @author 
 * @email 
 * @date 2018-07-30 20:13:21
 */
public interface DictionaryService extends IService<DictionaryEntity> {

    List<DictionaryEntity> selAll(Map<String, Object> params);

    List<DictionaryEntity> selectByParentCode(Long  orgId, String parentCode);

    boolean saveDict(DictionaryEntity dictionary);

    boolean updateDict(Long  orgId, DictionaryEntity dictionary);

    boolean delDict(Long  orgId, DictionaryEntity dictionary);

    /**
     * 获取字典列表
     * @param code
     * @return
     */
    List<DictionaryEntity> getDictList(Long  orgId, String code);

    /**
     * 获取字典值
     * @param code
     * @param key
     * @return
     */
    String getDictValue(Long  orgId, String code, String key);
}

