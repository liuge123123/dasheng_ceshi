package com.tcc.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CommonUtils {
    public static List<Map<String, Object>> listToTree(List<Map<String, Object>> list, String parentId, String parentName){
        List<Map<String, Object>> childList = new ArrayList<>();
        for(int i = 0; i < list.size(); i++){
            Map<String, Object> item = list.get(i);
            if(parentId.equals(Convert.toStr(item.get("parent_id")))){
                item.put("parent_name", parentName);
                List<Map<String, Object>> slist = listToTree(list, Convert.toStr(item.get("id")), Convert.toStr(item.get("name")));
                if(slist.size() > 0) {
                    item.put("children", slist);
                }
                childList.add(item);
            }
        }
        return childList;
    }

    /**
     * 生成No yyyyMMddHHmmssSSSxxx
     * @return
     */
    public static String genNo(){
        String time = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_MS_PATTERN);
        String id = time + RandomUtil.randomNumbers(3);
        return id;
    }
}
