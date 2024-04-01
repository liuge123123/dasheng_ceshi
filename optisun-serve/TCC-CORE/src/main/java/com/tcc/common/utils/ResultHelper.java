package com.tcc.common.utils;

import cn.hutool.core.convert.Convert;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResultHelper {
    /// <summary>
    /// 创建一个全球唯一的32位ID
    /// </summary>
    /// <returns>ID串</returns>
    public static String newId()
    {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String id = sdf.format(now);
        UUID suuid = UUID.randomUUID();
        String guid = suuid.toString().replace("-", "");
        id += guid.substring(0, 18);
        return id;
    }
    /// <summary>
    /// 创建一个全球唯一的32位ID
    /// </summary>
    /// <returns>ID串</returns>
    public static String orderNo()
    {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String id = sdf.format(now);
        long nowDate = System.currentTimeMillis();
        String sid = Integer.toUnsignedString((int)nowDate);
        return id+sid;
    }
    /**
     * 元转分，确保price保留两位有效数字
     * @return
     */
    public static int changeY2F(double price) {
        DecimalFormat df = new DecimalFormat("#.00");
        price = Double.valueOf(df.format(price));
        int money = (int)(price * 100);
        return money;
    }

    /**
     * 分转元，转换为bigDecimal在toString
     * @return
     */
    public static String changeF2Y(int price) {
        return BigDecimal.valueOf(Long.valueOf(price)).divide(new BigDecimal(100)).toString();
    }

    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> objectToMap(Object obj) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<String,Object>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value =field.get(obj);
            map.put(fieldName, value);
        }
        return map;
    }
    public static List<Map<String,Object>> toTree(List<Map<String, Object>> list) {

        List<Map<String,Object>> treeList = new ArrayList<Map<String,Object>>();
        for (Map<String,Object> tree : list) {
            if(Convert.toInt(tree.get("parentId"))==0){
                treeList.add(tree);
            }
        }
        for (Map<String,Object> tree : list) {
            toTreeChildren(treeList,tree);
        }
        return treeList;
    }

    private static void toTreeChildren(List<Map<String,Object>> treeList, Map<String,Object> tree) {
        for (Map<String,Object> node : treeList) {
            if(Convert.toInt(tree.get("parentId")) == node.get("value")){
                if(!node.containsKey("children")){
                    node.put("children",new ArrayList<Map<String,Object>>());
                }
                List<Map<String,Object>> list=(List<Map<String,Object>>)(node.get("children"));
                list.add(tree);
            }
            if(node.containsKey("children")){
                toTreeChildren((List<Map<String,Object>>)(node.get("children")),tree);
            }
        }
    }
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    /**
     * 驼峰转下划线
     * @param str
     * @return
     */
    public static String humpToLine2(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
