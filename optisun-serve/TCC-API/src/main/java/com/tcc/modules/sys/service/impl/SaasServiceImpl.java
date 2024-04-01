package com.tcc.modules.sys.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.sys.dao.SassDao;
import com.tcc.modules.sys.entity.*;
import com.tcc.modules.sys.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: sass平台相关服务
 * @author 
 * @create: 2020-05-27 16:11
 **/
@Service
public class SaasServiceImpl implements SaasService {


    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private SassDao sassDao;



    /**
     * 参数校验
     * @param params
     */
    @Override
    public void validParmas(JSONObject params){
        String appId = params.getStr("appId");
        String token = params.getStr("token");
        if(StrUtil.isBlank(appId) || StrUtil.isBlank(token)){
            throw new WTDPException("参数错误");
        }
        // 判断是否开启sass模式
        String saasConfigStr = sysConfigService.getValue( Constant.SUPER_ORG,"SAAS_CONFIG");
        if(StrUtil.isNotBlank(saasConfigStr)) {
            JSONObject saasConfig = JSONUtil.parseObj(saasConfigStr);
            int status = saasConfig.getInt("status", 0);
            // status == 1 代表sass模式开启
            if (status == 1) {
                String _appId = saasConfig.getStr("appId");
                String _token = saasConfig.getStr("token");
                if(!appId.equals(_appId) || !token.equals(_token)){
                    throw new WTDPException("授权失败");
                }
            }else{
                throw new WTDPException("sass服务未开启");
            }
        }else{
            throw new WTDPException("saas服务未开启");
        }

    }

    /**
     * 重置菜单资源
     * @param orgId
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetResource(Long  orgId) {
        if(this.isOpen()){
            List<Long> resIds = getRescoure(orgId);
            SysRoleEntity role = sysRoleService.getOne(new QueryWrapper<SysRoleEntity>().eq("org_id", orgId)
                    .eq("type", 1));
            List<Long> localRes = sysRoleMenuService.queryMenuIdList(role.getRoleId());
            List<Long> ist = (List<Long>) CollectionUtil.intersection(resIds, localRes);
            List<Long> addList = (List<Long>) CollectionUtil.disjunction(resIds, ist);
            List<Long> delList = (List<Long>) CollectionUtil.disjunction(localRes, ist);
            if (CollectionUtil.isNotEmpty(addList) || CollectionUtil.isNotEmpty(delList)) {
                sysRoleMenuService.saveOrUpdate(role.getRoleId(), resIds);
                //其他角色需要移除已删除的的资源
                List<SysRoleEntity> roles = sysRoleService.list(new QueryWrapper<SysRoleEntity>().eq("org_id", orgId)
                        .eq("type", 2));
                roles.forEach(item -> {
                    sysRoleMenuService.remove(new QueryWrapper<SysRoleMenuEntity>()
                            .eq("role_id", item.getRoleId())
                            .in("menu_id", delList));
                });
            }
        }
    }

    @Override
    public boolean isOpen() {
        String saasConfigStr = sysConfigService.getValue(Constant.SUPER_ORG,"SAAS_CONFIG");
        if(StrUtil.isNotBlank(saasConfigStr)){
            JSONObject saasConfig = JSONUtil.parseObj(saasConfigStr);
            int status = saasConfig.getInt("status", 0);
            // status == 1 代表sass模式开启
            if(status == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 授权校验
     * @param orgId
     * @return
     */
    @Override
    public R oauth(Long  orgId){
        // 判断是否开启sass模式
        String saasConfigStr = sysConfigService.getValue(orgId,"SAAS_CONFIG");
        if(StrUtil.isNotBlank(saasConfigStr)){
            JSONObject saasConfig = JSONUtil.parseObj(saasConfigStr);
            int status = saasConfig.getInt("status", 0);
            // status == 1 代表sass模式开启
            if(status == 1){
                String oauthUrl = saasConfig.getStr("oauthUrl");
                String appId = saasConfig.getStr("appId");
                String token = saasConfig.getStr("token");
                Map<String, Object> params = new HashMap<>();
                params.put("appId", appId);
                params.put("token", token);
                params.put("orgId", orgId);
                String result = HttpUtil.post(oauthUrl, JSONUtil.toJsonStr(params));
                JSONObject resObj = JSONUtil.parseObj(result);
                int code = resObj.getInt("code", 0);
                String msg = resObj.getStr("msg");
                if(code != 0){
                    return R.error(code, msg);
                }
            }
        }
        return R.ok();
    }

    /**
     * 初始化
     * @param data
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void initData(JSONObject data) {
        Long  orgId = data.getLong("orgId", 0L);
        String orgName = data.getStr("orgName");
        // 初始化一个应用
        createWxApp(orgId,orgName);
        // 创建管理员
        SysUserEntity user = createAdmin(orgId);
        // 创建超级管理员角色
        SysRoleEntity role = createRole(orgId, user.getUserId());
        // 创建用户角色关联
        createUserRole(user.getUserId(), role.getRoleId());
        // 获取授权资源
        List<Long> menuIds = getRescoure(orgId);
        // 插入初始授权数据
        saveResouce(role.getRoleId(), menuIds);
        // 初始化设置数据
        sassDao.initSettingData(orgId);
    }

    /**
     * 初始化一个应用
     * @param orgId
     * @return
     */

    private SysOrgEntity createWxApp(Long  orgId,String orgName){
        // 创建机构
        sysOrgService.removeById(orgId);
        SysOrgEntity wxapp = new SysOrgEntity();
        wxapp.setId(Convert.toInt(orgId));
        wxapp.setName(orgName);
        wxapp.setStatus(1);
        wxapp.setSysTitle("云知美互客");
        sysOrgService.save(wxapp);
        return wxapp;
    }

    /**
     * 创建用户
     * @param orgId
     * @return
     */
    private SysUserEntity createAdmin(long orgId){
        sysUserService.remove(new QueryWrapper<SysUserEntity>().eq("org_id",orgId));
        SysUserEntity user = new SysUserEntity();
        user.setUsername("admin_" + orgId);
        user.setOrgId(orgId);
        user.setType(1);
        user.setPassword("123456");
        user.setStatus(1);
        user.setName("机构管理员");
        user.setAvatar("https://yunzhimei-crm.oss-cn-hangzhou.aliyuncs.com/common/boy.png");
        sysUserService.saveUser(user);
        return user;
    }
    /**
     * 创建角色
     * @param orgId
     * @param userId
     * @return
     */
    private SysRoleEntity createRole(Long  orgId, Long userId){
        //删除之前的角色及角色菜单
       List<SysRoleEntity>  delList =  sysRoleService.list(new QueryWrapper<SysRoleEntity>().eq("org_id",orgId));
        for (SysRoleEntity item:delList) {
            sysRoleService.removeById(item.getRoleId());
            sysRoleMenuService.remove(new QueryWrapper<SysRoleMenuEntity>().eq("role_id",item.getRoleId()));
            sysUserRoleService.remove(new QueryWrapper<SysUserRoleEntity>().eq("role_id",item.getRoleId()));
        }
        SysRoleEntity role = new SysRoleEntity();
        role.setRoleName("超级管理员");
        role.setRemark("系统默认创建，不可删除");
        role.setCreateUserId(userId);
        role.setCreateTime(DateUtils.getCurrentTime());
        role.setOrgId(orgId);
        role.setType(1);
        sysRoleService.save(role);
        return role;
    }

    /**
     * 创建用户角色关系
     * @param userId
     * @param roleId
     */
    private void createUserRole(Long userId, Long roleId){
        SysUserRoleEntity userRole = new SysUserRoleEntity();
        userRole.setUserId(userId);
        userRole.setRoleId(roleId);
        sysUserRoleService.save(userRole);
    }

    /**
     * 获取授权的菜单资源
     * @param orgId
     * @return
     */
    private List<Long> getRescoure(Long  orgId){
        List<Long> menuIds = new ArrayList<>();
        // 判断是否开启sass模式
        String sassConfigStr = sysConfigService.getValue(orgId,"SAAS_CONFIG");
        if(StrUtil.isNotBlank(sassConfigStr)){
            JSONObject sassConfig = JSONUtil.parseObj(sassConfigStr);
            String soureUrl = sassConfig.getStr("resourceUrl");
            String token = sassConfig.getStr("token");
            String appId = sassConfig.getStr("appId");
            Map<String, Object> params = new HashMap<>();
            params.put("orgId", orgId);
            params.put("appId", appId);
            params.put("token", token);
            String result = HttpUtil.post(soureUrl, JSONUtil.toJsonStr(params));
            JSONObject resObj = JSONUtil.parseObj(result);
            int code = resObj.getInt("code", 0);
            String msg = resObj.getStr("msg");
            if(code == 0) {
                JSONArray resoure = resObj.getJSONArray("data");
                resoure.forEach(item -> {
                    Long menuId = Convert.toLong(item);
                    menuIds.add(menuId);
                });
            }else{
                throw new WTDPException(msg, code);
            }
        }
        return menuIds;
    }
    /**
     * 添加菜单资源
     * @param roleId
     * @param menuIds
     */
    private void saveResouce(Long roleId, List<Long> menuIds){
        List<SysRoleMenuEntity> list = new ArrayList<>();
        menuIds.forEach(item -> {
            SysRoleMenuEntity roleMenu = new SysRoleMenuEntity();
            roleMenu.setMenuId(item);
            roleMenu.setRoleId(roleId);
            list.add(roleMenu);
        });
        sysRoleMenuService.saveBatch(list);
    }
}
