package com.tcc.modules.sys.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.sys.dao.DictionaryDao;
import com.tcc.modules.sys.entity.DictionaryEntity;
import com.tcc.modules.sys.service.DictionaryService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 在编辑字典时，若机构不为平台机构时，要将字典拷贝为副本，在副本上编辑，不能直接编辑平台的字典数据
 */
@Service("dictionaryService")
public class DictionaryServiceImpl extends ServiceImpl<DictionaryDao, DictionaryEntity> implements DictionaryService {

    @Override
    public List<DictionaryEntity> selAll(Map<String, Object> params) {
        Long  orgId= Convert.toLong(params.get("orgId"));
        if(orgId == Constant.SUPER_ORG){
            return baseMapper.selAll(params);
        }else{
            // 当当前用户不是默认机构时，将默认机构的数据字典与当前机构的字典合并，以当前机构为主覆盖默认机构数据
            List<DictionaryEntity> superOrgDicts = this.list(new QueryWrapper<DictionaryEntity>().eq("org_id", Constant.SUPER_ORG).eq(params.containsKey("dicType"),"dic_type",params.get("dicType")));
            List<DictionaryEntity> currentOrgDicts = this.list(new QueryWrapper<DictionaryEntity>().eq("org_id", orgId).eq(params.containsKey("dicType"),"dic_type",params.get("dicType")));
            List<DictionaryEntity> parentCurrentOrgDicts = currentOrgDicts.stream().filter(item -> {
                return "-1".equals(item.getParentCode());
            }).collect(Collectors.toList());
            List<String> parentCurrentOrgDictCodes = parentCurrentOrgDicts.stream().map(item -> {
                return item.getCode();
            }).collect(Collectors.toList());
            List<DictionaryEntity> chidrenCurrentOrgDicts = currentOrgDicts.stream().filter(item -> {
                return !"-1".equals(item.getParentCode());
            }).collect(Collectors.toList());

            List<DictionaryEntity> parentSuperOrgDicts = superOrgDicts.stream().filter(item -> {
                return "-1".equals(item.getParentCode()) && !parentCurrentOrgDictCodes.contains(item.getCode());
            }).collect(Collectors.toList());
            List<String> parentSuperOrgDictCodes = parentSuperOrgDicts.stream().map(item -> {
                return item.getCode();
            }).collect(Collectors.toList());
            List<DictionaryEntity> chidrenSuperOrgDicts = superOrgDicts.stream().filter(item -> {
                return !"-1".equals(item.getParentCode()) && parentSuperOrgDictCodes.contains(item.getParentCode());
            }).collect(Collectors.toList());
            List<DictionaryEntity> resut = new ArrayList<>();
            resut.addAll(parentCurrentOrgDicts);
            resut.addAll(parentSuperOrgDicts);
            resut.addAll(chidrenCurrentOrgDicts);
            resut.addAll(chidrenSuperOrgDicts);
            resut = resut.stream().sorted((o1, o2) -> {
                return o1.getSort().compareTo(o2.getSort());
            }).collect(Collectors.toList());
            return resut;
        }
    }

    /**
     * 获取数据字典
     * @param orgId
     * @param parentCode
     * @return
     */
    @Override
    public List<DictionaryEntity> selectByParentCode(Long  orgId, String parentCode){
        List<DictionaryEntity> diclist = baseMapper.selectByParentCode(orgId, parentCode);
        return diclist;
    }

    /**
     * 新增数据字典
     * @param dictionary
     * @return
     */
    @Caching(evict = {
            @CacheEvict(value = Constant.REDIS_CACHE_KEY, key = "'dict:' + #dictionary.orgId + ':' + #dictionary.parentCode"),
            @CacheEvict(value = Constant.REDIS_CACHE_KEY, key = "'dict:' + #dictionary.orgId + ':' + #dictionary.parentCode + ':' + #dictionary.code")
    })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveDict(DictionaryEntity dictionary) {
        if(Constant.SUPER_ORG == dictionary.getOrgId()){
            // 当机构为默认机构直接保存
            return this.save(dictionary);
        }else{
            if("-1".equals(dictionary.getParentCode())){
                // 当为数据字典时，机构设为默认机构
                dictionary.setOrgId(Constant.SUPER_ORG);
                return this.save(dictionary);
            }else{
                DictionaryEntity parent = getOne(new QueryWrapper<DictionaryEntity>()
                        .eq("org_id", dictionary.getOrgId())
                        .eq("code", dictionary.getParentCode()));
                // 当机构不为默认机构时且数据字典不存在时，将默认机构的数据字典存储为副本
                if(parent == null){
                    parent = getOne(new QueryWrapper<DictionaryEntity>()
                            .eq("org_id", Constant.SUPER_ORG)
                            .eq("code", dictionary.getParentCode()));
                    List<DictionaryEntity> children = list(new QueryWrapper<DictionaryEntity>()
                            .eq("org_id", Constant.SUPER_ORG)
                            .eq("parent_code", dictionary.getParentCode()));
                    children.forEach(item -> {
                        item.setOrgId(dictionary.getOrgId());
                    });
                    parent.setOrgId(dictionary.getOrgId());
                    children.add(0, parent);
                    children.add(dictionary);
                    return saveBatch(children);
                }else{
                    // 机构的数据字典存在，则认为已经存储过副本，只用存储选项值
                    return this.save(dictionary);
                }
            }
        }
    }

    /**
     * 更新数据字典
     * @param dictionary
     * @return
     */
    @Caching(evict = {
            @CacheEvict(value = Constant.REDIS_CACHE_KEY, key = "'dict:' + #orgId + ':' + #dictionary.parentCode"),
            @CacheEvict(value = Constant.REDIS_CACHE_KEY, key = "'dict:' + #orgId + ':' + #dictionary.parentCode + ':' + #dictionary.code")
    })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateDict(Long  orgId, DictionaryEntity dictionary) {
        DictionaryEntity oldDict = this.getById(dictionary.getId());
        if(orgId == Constant.SUPER_ORG || orgId == oldDict.getOrgId()){
            return this.updateById(dictionary);
        }else{
            if("-1".equals(dictionary.getParentCode())){
                dictionary.setOrgId(orgId);
                List<DictionaryEntity> list = list(new QueryWrapper<DictionaryEntity>().eq("org_id", Constant.SUPER_ORG).eq("parent_code", dictionary.getCode()));
                list.forEach(item -> {
                    item.setOrgId(orgId);
                });
                list.add(dictionary);
                return this.saveBatch(list);
            }else{
                DictionaryEntity parent = getOne(new QueryWrapper<DictionaryEntity>().eq("org_id", Constant.SUPER_ORG).eq("code", dictionary.getParentCode()));
                List<DictionaryEntity> list = list(new QueryWrapper<DictionaryEntity>().eq("org_id", Constant.SUPER_ORG).eq("parent_code", dictionary.getParentCode()));
                list.forEach(item ->{
                    if(item.getCode().equals(dictionary.getCode())){
                        item.setValue(dictionary.getValue());
                        item.setSort(dictionary.getSort());
                        item.setComments(dictionary.getComments());
                        item.setStatus(dictionary.getStatus());
                    }
                    item.setOrgId(orgId);
                });
                parent.setOrgId(orgId);
                list.add(parent);
                return this.saveBatch(list);
            }
        }
    }

    /**
     * 删除数据字典
     * @param dictionary
     * @return
     */
    @Caching(evict = {
            @CacheEvict(value = Constant.REDIS_CACHE_KEY, key = "'dict:' + #orgId + ':' + #dictionary.parentCode"),
            @CacheEvict(value = Constant.REDIS_CACHE_KEY, key = "'dict:' + #orgId + ':' + #dictionary.parentCode + ':' + #dictionary.code")
    })
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delDict(Long  orgId, DictionaryEntity dictionary) {
        if(orgId == Constant.SUPER_ORG){
            if("-1".equals(dictionary.getParentCode())){
                int childrenNum = count(new QueryWrapper<DictionaryEntity>()
                        .eq("org_id", orgId)
                        .eq("parent_code", dictionary.getCode()));
                if(childrenNum > 0){
                    throw new WTDPException("字典【" + dictionary.getValue() + "】含有选项值不能删除！");
                }
            }
            // 当前机构为总部机构
            return this.removeById(dictionary);
        }else {
            if("-1".equals(dictionary.getParentCode())){
                throw new WTDPException("您没有删除字典的权限");
            }else {
                if(dictionary.getOrgId() == orgId){
                    return this.removeById(dictionary.getId());
                }else{
                    DictionaryEntity parent = getOne(new QueryWrapper<DictionaryEntity>()
                            .eq("org_id", Constant.SUPER_ORG)
                            .eq("code", dictionary.getParentCode()));
                    List<DictionaryEntity> children = list(new QueryWrapper<DictionaryEntity>()
                            .eq("org_id", Constant.SUPER_ORG)
                            .eq("parent_code", dictionary.getParentCode()));
                    List<DictionaryEntity> saveList = new ArrayList<>();
                    parent.setOrgId(orgId);
                    saveList.add( parent);
                    children.forEach(item -> {
                        item.setOrgId(orgId);
                        if(!item.getCode().equals(dictionary.getCode())) {
                            saveList.add(item);
                        }
                    });
                    return saveBatch(saveList);
                }
            }
        }
    }

    /**
     * 获取数据字典列表
     * @param orgId
     * @param code
     * @return
     */
    @Cacheable(value=Constant.REDIS_CACHE_KEY, key="'dict:' + #orgId + ':' + #code")
    @Override
    public List<DictionaryEntity> getDictList(Long  orgId, String code) {
        // 优先获取机构自己的字典，若机构字典没有则取系统默认字典
        DictionaryEntity dictionary = getOne(new QueryWrapper<DictionaryEntity>().eq("code", code).eq("org_id", orgId).orderByAsc("sort"));
        if(dictionary == null){
            orgId = Constant.SUPER_ORG;
        }
        return baseMapper.selectByParentCode(orgId, code);
    }

    /**
     * 获取数据字典值
     * @param orgId
     * @param code
     * @param key
     * @return
     */
    @Cacheable(value=Constant.REDIS_CACHE_KEY, key="'dict:' + #orgId + ':' + #code + ':' + #key")
    @Override
    public String getDictValue(Long  orgId, String code, String key) {
        if(StrUtil.isBlank(key)){
            return "";
        }
        List<DictionaryEntity> dictList = getDictList(orgId, code);
        if(dictList != null && dictList.size() >= 0){
            for(int i = 0; i < dictList.size(); i++){
                if(key.equals(dictList.get(i).getCode())){
                    return dictList.get(i).getValue();
                }
            }
        }
        return "";
    }

}
