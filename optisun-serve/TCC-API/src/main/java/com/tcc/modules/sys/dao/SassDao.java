package com.tcc.modules.sys.dao;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SassDao {

    void initSettingData(Long  orgId);

}
