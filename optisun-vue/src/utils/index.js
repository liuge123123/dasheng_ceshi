import Vue from 'vue'
import router from '@/router'
import store from '@/store'

/**
 * 获取uuid
 */
export function getUUID () {
  return 'xxxxxxxxxxxx4xxxyxxxxxxxxxxxxxxx'.replace(/[xy]/g, c => {
    return (c === 'x' ? (Math.random() * 16 | 0) : ('r&0x3' | '0x8')).toString(16)
  })
}

/**
 * 是否有权限
 * @param {*} key
 */
export function isAuth (key) {
  return JSON.parse(sessionStorage.getItem('permissions') || '[]').indexOf(key) !== -1 || false
}

/**
 * 树形数据转换
 * @param {*} data
 * @param {*} id
 * @param {*} pid
 */
export function treeDataTranslate (data, id = 'id', pid = 'parentId', name = 'name', parentName = 'parentName') {
  var res = []
  var temp = {}
  for (var i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (var k = 0; k < data.length; k++) {
    if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
      if (!temp[data[k][pid]]['children']) {
        temp[data[k][pid]]['children'] = []
      }
      if (!temp[data[k][pid]]['_level']) {
        temp[data[k][pid]]['_level'] = 1
      }
      data[k]['_level'] = temp[data[k][pid]]._level + 1
      data[k][parentName] =  temp[data[k][pid]][name]
      temp[data[k][pid]]['children'].push(data[k])
    } else {
      res.push(data[k])
    }
  }
  return res
}

/**
 * 清除登录信息
 */
export function clearLoginInfo () {
  Vue.cookie.delete('token')
  store.commit('resetStore')
  sessionStorage.clear()
  localStorage.clear()
  router.options.isAddDynamicMenuRoutes = false
}

/**
 * Get the first item that pass the test
 * by second argument function
 *
 * @param {Array} list
 * @param {Function} f
 * @return {*}
 */
export function find (list, f) {
  return list.filter(f)[0]
}

/**
 * Deep copy the given object considering circular structure.
 * This function caches all nested objects and its copies.
 * If it detects circular structure, use cached copy to avoid infinite loop.
 *
 * @param {*} obj
 * @param {Array<Object>} cache
 * @return {*}
 */
export function deepCopy (obj, cache = []) {
  // just return if obj is immutable value
  if (obj === null || typeof obj !== 'object') {
    return obj
  }

  // if obj is hit, it is in circular structure
  const hit = find(cache, c => c.original === obj)
  if (hit) {
    return hit.copy
  }

  const copy = Array.isArray(obj) ? [] : {}
  // put the copy into cache at first
  // because we want to refer it in recursive deepCopy
  cache.push({
    original: obj,
    copy
  })

  Object.keys(obj).forEach(key => {
    copy[key] = deepCopy(obj[key], cache)
  })

  return copy
}

/**
 * 将时间格式化为yyyy-MM-dd
 * @param value
 * @returns {*}
 */
export function dateFormat(value){
  if(value == ''){
    return '';
  }
  if(!(value instanceof Date)){
    value = new Date(value);
  }
  var time = value;
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  return y + '-' + add0(m) + '-' + add0(d);
}
/**
 * 将时间格式化为yyyy-MM-dd HH:mm:ss
 * @param value
 * @returns {*}
 */
export function dateTimeFormat(value){
  if(!value ||value == ''){
    return '';
  }
  if(!(value instanceof Date)){
    value = new Date(value);
  }
  var time = value;
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  var h = time.getHours();
  var ms = time.getMinutes();
  var s= time.getSeconds();
  return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(ms) + ':' + add0(s);
}
/**
 * 将时间戳格式化为yyyy-MM-dd HH:mm:ss
 * @param value
 * @returns {*}
 */
export function timeStampDateTimeFormat(value){
  const CURRENT_TIME_ZONE_NUM = -1;
		const LOCAL_TIME_ZONE_NUM = new Date().getTimezoneOffset() / 60;
		const TRANS_NUM = LOCAL_TIME_ZONE_NUM - CURRENT_TIME_ZONE_NUM;
  if(!value ||value == ''){
    return '';
  }

  value = +value * 1000
  let sq = TRANS_NUM;//new Date().getTimezoneOffset()/60
  value = value + sq * 3600 * 1000
 
  var time = new Date(value);
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  var h = time.getHours();
  var ms = time.getMinutes();
  var s= time.getSeconds();
  return y + '-' + add0(m) + '-' + add0(d) + ' ' + add0(h) + ':' + add0(ms) + ':' + add0(s);
}

/**
 * 将时间戳格式化为yyyy-MM-dd
 * @param value
 * @returns {*}
 */
export function timeStampDateFormat(value){
  if(!value ||value == ''){
    return '';
  }
  value = +value * 1000
  value = new Date(value)

  var time = value;
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  var h = time.getHours();
  var ms = time.getMinutes();
  var s= time.getSeconds();
  return y + '-' + add0(m) + '-' + add0(d);
}

/**
 * 将时间戳格式化为yyyy-MM-dd
 * @param value
 * @returns {*}
 */
export function timeStampDateFormat1(value){
  if(!value ||value == ''){
    return '';
  }
  value = +value * 1000
  value = new Date(value)

  var time = value;
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  var h = time.getHours();
  var ms = time.getMinutes();
  var s= time.getSeconds();
  return add0(m) + '-' + add0(d);
}

/**
 * 将时间转换成时间戳
 * @param value
 * @returns {*}
 */
export function getTimestamp(value) {
  if(!(value instanceof Date)){
    value = value.replace(/-/g, '/');
    value = new Date(value);
  }
  return (value).getTime() / 1000
}

/**
 * 左边加指定数目的0
 * @param m
 * @param num
 * @returns {string}
 */
export function add0(m, num) {
  if(!num){
    num = 1;
  }
  var maxNum = 10;
  var max0 = '0';
  for(var i = 1; i < num; i++){
    maxNum = maxNum*10;
    max0 = max0 + '0';
  }
  return m < maxNum ? max0 + m : m
}

/*
  在原有时间上增加秒
 */
export function timeAdd(value,add) {
  value = new Date(value);
  var t_s = value.getTime();//转化为时间戳毫秒数
  value.setTime(t_s + add*1000);
  return value
}
/*
  在原有时间上减少秒
 */
export function timeDown(value,down) {
  value = new Date(value);
  var t_s = value.getTime();//转化为时间戳毫秒数
  value.setTime(t_s - down*1000);
  return value
}
// 分类树
export function listClassTree (list) {
  let root = null
  if (list && list.length) {
    root = { id: 0, parentId: null, children: [] }
    const group = {}
    for (let index = 0; index < list.length; index += 1) {
      if (list[index].parentId !== null && list[index].parentId !== undefined) {
        if (!group[list[index].parentId]) {
          group[list[index].parentId] = []
        }
        group[list[index].parentId].push(list[index])
      }
    }
    const queue = []
    queue.push(root)
    while (queue.length) {
      const node = queue.shift()
      node.children = group[node.id] && group[node.id].length ? group[node.id] : null
      if (node.children) {
        queue.push(...node.children)
      }
    }
  }
  return root
}
/**
 * 将时间格式化为yyyyMMdd
 * @param value
 * @returns {*}
 */
export function dateFormatStr(value){
  if(value == ''){
    return '';
  }
  if(!(value instanceof Date)){
    if (value instanceof String){
      value = value.replace(/-/g, '/');
    }
    value = new Date(value);
  }
  var time = value;
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  return y + '-' + add0(m) + '-' + add0(d);
}

/**
 * 将时间格式化为HH:mm:ss
 * @param value
 * @returns {*}
 */
export function timeFormat(value){
  if(!value ||value == ''){
    return '';
  }
  if(!(value instanceof Date)){
    if (value instanceof String){
      value = value.replace(/-/g, '/');
    }
    value = new Date(value);
  }
  var time = value;
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  var h = time.getHours();
  var ms = time.getMinutes();
  var s= time.getSeconds();
  return  add0(h) + ':' + add0(ms) + ':' + add0(s);
}

export  function getUUIDCode() {
  var time = new Date();
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  var d = time.getDate();
  var h = time.getHours();
  var ms = time.getMinutes();
  var s= time.getSeconds();
  return y  + add0(m)  + add0(d) + add0(h)  + add0(ms) + add0(s)  + randomNum(1000,9999);
}
//生成从minNum到maxNum的随机数
export function randomNum(minNum,maxNum){
  switch(arguments.length){
    case 1:
      return parseInt(Math.random()*minNum+1,10);
      break;
    case 2:
      return parseInt(Math.random()*(maxNum-minNum+1)+minNum,10);
      break;
    default:
      return 0;
      break;
  }
}
// 树形数据转换
export function listWorkClassTree (list) {
  let root = null
  if (list && list.length) {
    root = { classId: 0, parentId: null, children: [] }
    const group = {}
    for (let index = 0; index < list.length; index += 1) {
      if (list[index].parentId !== null && list[index].parentId !== undefined) {
        if (!group[list[index].parentId]) {
          group[list[index].parentId] = []
        }
        group[list[index].parentId].push(list[index])
      }
    }
    const queue = []
    queue.push(root)
    while (queue.length) {
      const node = queue.shift()
      node.children = group[node.classId] && group[node.classId].length ? group[node.classId] : null
      if (node.children) {
        queue.push(...node.children)
      }
    }
  }
  return root
}

/**
 * @param {Function} func
 * @param {number} wait
 * @param {boolean} immediate
 * @return {*}
 */
export function debounce(func, wait, immediate) {
  let timeout, args, context, timestamp, result

  const later = function () {
    // 据上一次触发时间间隔
    const last = +new Date() - timestamp

    // 上次被包装函数被调用时间间隔 last 小于设定时间间隔 wait
    if (last < wait && last > 0) {
      timeout = setTimeout(later, wait - last)
    } else {
      timeout = null
      // 如果设定为immediate===true，因为开始边界已经调用过了此处无需调用
      if (!immediate) {
        result = func.apply(context, args)
        if (!timeout) context = args = null
      }
    }
  }
}

/**
 * 将时间格式化为yyyy-MM-dd
 * @param value
 * @returns {*}
 */
export function dateFormatEx(value) {
  if (!value || value == '') {
    return '';
  }
  value = new Date(parseInt(value) * 1000);
  let time = value;
  let y = time.getFullYear();
  let m = time.getMonth() + 1;
  let d = time.getDate();
  return y + '-' + add0(m) + '-' + add0(d);
}

//用户组分组列表
export function GroupListTree (list) {
  let root = null
  if (list && list.length) {
    root = {groupId: 0, parentId: null, children: []}
    const group = {}
    for (let index = 0; index < list.length; index += 1) {
      if (list[index].parentId !== null && list[index].parentId !== undefined) {
        if (!group[list[index].parentId]) {
          group[list[index].parentId] = []
        }
        group[list[index].parentId].push(list[index])
      }
    }
    const queue = []
    queue.push(root)
    while (queue.length) {
      const node = queue.shift()
      node.children = group[node.deptId] && group[node.deptId].length ? group[node.deptId] : null
      if (node.children) {
        queue.push(...node.children)
      }
    }
  }
  return root
}

export function fieldValueType(fieldType){
    switch (parseInt(fieldType)){
      case  1,2,3,4,5,6,8,9,10,18:
        return 'string'
      case 7,17:
        return 'array'
    }
    return 'string'
}
/**
 * 将时间格式化为yyyy-MM
 * @param value
 * @returns {*}
 */
export function dateFormatMonth(value){
  if(!value ||value == ''){
    return '';
  }
  if(!(value instanceof Date)){
    value = value.replace(/-/g, '/');
    value = new Date(value);
  }
  var time = value;
  var y = time.getFullYear();
  var m = time.getMonth() + 1;
  return y + '-' + add0(m);
}

/**
 * 计算两个日期中间间隔的day数组
 * @param starDay
 * @param endDay
 * @returns {[]}
 */
export function getDayAll(starDay, endDay) {
  var arr = [];
  var dates = [];
  // 设置两个日期UTC时间
  var db = new Date(starDay);
  var de = new Date(endDay);
  // 获取两个日期GTM时间
  var s = db.getTime() - 24 * 60 * 60 * 1000;
  var d = de.getTime() - 24 * 60 * 60 * 1000;
  // 获取到两个日期之间的每一天的毫秒数
  for (var i = s; i <= d;) {
    i = i + 24 * 60 * 60 * 1000;
    arr.push(parseInt(i))
  }
  // 获取每一天的时间  YY-MM-DD
  for( var j in arr ){
    var time = new Date(arr[j]);
    var year = time.getFullYear(time);
    var mouth = (time.getMonth() + 1)>=10?(time.getMonth() + 1):('0'+(time.getMonth() + 1));
    var day = time.getDate()>=10?time.getDate():('0'+time.getDate());
    var YYMMDD = year + '-' + mouth + '-' + day;
    dates.push(YYMMDD)
  }
  return dates
}
/**
 * 计算两个日期中间间隔的nonth数组
 * @param starDay
 * @param endDay
 * @returns {[]}
 */
export function getMonthAll(startMonth, endMonth) {
  //初始化数组
  var result = [];
  //切割起始年月
  var s = startMonth.split("-");
  //切割结束年月
  var e = endMonth.split("-");
  //获取时间对象
  var min = new Date();
  var max = new Date();
  //设置起始时间
  min.setFullYear(s[0],s[1]);
  //设置结束时间
  max.setFullYear(e[0],e[1]);

  //复制一份起始时间对象
  var curr = min;
  //定义字符串
  var str = "";
  //起始时间在结束时间之前
  while(curr <= max){
    //获取此时间的月份
    var month = curr.getMonth();
    //如果月份为0，也就是代表12月份
    if(month===0){
      str=(curr.getFullYear()-1)+"-"+12;
    }else{//正常月份
      str=curr.getFullYear()+"-"+(month<10?("0"+month):month);
    }
    //将此年月加入数组
    result.push(str);
    //更新此时间月份
    curr.setMonth(month+1);
  }
  return result;
}
