
function pluralize(time, label) {
  if (time === 1) {
    return time + label
  }
  return time + label + 's'
}
/**
 * 日期格式化
 */
export function dateFormat(date) {
  let format = 'yyyy-MM-dd hh:mm:ss';
  if (date != 'Invalid Date') {
    var o = {
      "M+": date.getMonth() + 1, //month
      "d+": date.getDate(), //day
      "h+": date.getHours(), //hour
      "m+": date.getMinutes(), //minute
      "s+": date.getSeconds(), //second
      "q+": Math.floor((date.getMonth() + 3) / 3), //quarter
      "S": date.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
      (date.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
      if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
          RegExp.$1.length == 1 ? o[k] :
            ("00" + o[k]).substr(("" + o[k]).length));
    return format;
  }
  return '';

}
export function timeAgo(time) {
  const between = Date.now() / 1000 - Number(time)
  if (between < 3600) {
    return pluralize(~~(between / 60), ' minute')
  } else if (between < 86400) {
    return pluralize(~~(between / 3600), ' hour')
  } else {
    return pluralize(~~(between / 86400), ' day')
  }
}


export function parseTime(time, cFormat) {
  time = Date.parse(new Date(time));
  if (arguments.length === 0) {
    return null
  }

  if ((time + '').length === 10) {
    time = +time * 1000
  }

  const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    date = new Date(parseInt(time))
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    if (key === 'a') return ['一', '二', '三', '四', '五', '六', '日'][value - 1]
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}

export function formatTime(time, option) {
  time = +time * 1000
  const d = new Date(time)
  const now = Date.now()

  const diff = (now - d) / 1000

  if (diff < 30) {
    return '刚刚'
  } else if (diff < 3600) { // less 1 hour
    return Math.ceil(diff / 60) + '分钟前'
  } else if (diff < 3600 * 24) {
    return Math.ceil(diff / 3600) + '小时前'
  } else if (diff < 3600 * 24 * 2) {
    return '1天前'
  }
  if (option) {
    return parseTime(time, option)
  } else {
    return d.getMonth() + 1 + '月' + d.getDate() + '日' + d.getHours() + '时' + d.getMinutes() + '分'
  }
}

/* 数字 格式化*/
export function nFormatter(num, digits) {
  const si = [
    { value: 1E18, symbol: 'E' },
    { value: 1E15, symbol: 'P' },
    { value: 1E12, symbol: 'T' },
    { value: 1E9, symbol: 'G' },
    { value: 1E6, symbol: 'M' },
    { value: 1E3, symbol: 'k' }
  ]
  for (let i = 0; i < si.length; i++) {
    if (num >= si[i].value) {
      return (num / si[i].value + 0.1).toFixed(digits).replace(/\.0+$|(\.[0-9]*[1-9])0+$/, '$1') + si[i].symbol
    }
  }
  return num.toString()
}

export function html2Text(val) {
  const div = document.createElement('div')
  div.innerHTML = val
  return div.textContent || div.innerText
}

export function toThousandslsFilter(num) {
  return (+num || 0).toString().replace(/^-?\d+/g, m => m.replace(/(?=(?!\b)(\d{3})+$)/g, ','))
}


/**
 * 获取阿里云OSS图片地址
 * @param imgUrl
 * @param w
 * @param h
 * @returns {*}
 */
export function imgUrlFixed(imgUrl, w, h) {
  if(!imgUrl || imgUrl == ''){
    imgUrl = ''
    return imgUrl
  }else if(imgUrl.indexOf('http://') != -1 || imgUrl.indexOf('https://') != -1){
    var process = '?x-oss-process=image/resize,m_fill,w_' + w + ',h_' + h + ',limit_0';
    return imgUrl + process;
  }
}

/**
 * 获取阿里云OSS图片地址
 * @param imgUrl
 * @param w
 * @param h
 * @returns {*}
 */
export function imgUrlFixedLarge(imgUrl) {
  if(!imgUrl || imgUrl == ''){
    imgUrl = '/404.png'
  }else if(imgUrl.indexOf('http://') != -1 || imgUrl.indexOf('https://') != -1){
    return imgUrl;
  }
  if(imgUrl.indexOf('#')>0)
  {
    imgUrl=imgUrl.split('#')[0]
  }
  return window.SITE_CONFIG.fileUrl + imgUrl;
}

/**
 * 获取阿里云OSS音频地址
 * @param value
 * @returns {*}
 */
export const voiceUrlFixed = (value) => {
  var url = value;
  if(!value || value == ''){
    url = value;
  }else if(value.indexOf('http://') != -1 || value.indexOf('https://') != -1){
    url = value;
  }else {
    var pathArr = value.split('/');
    var fileName = pathArr[pathArr.length - 1];
    url = voiceClient.signatureUrl(value, {
      response: {
        'content-disposition': 'attachment; filename="' + fileName + '"'
      }
    });
  }
  return url;
}

/**
 * 文本剪切
 * @param value
 * @param length
 * @returns {string}
 */
export const cut = (value, length) => {
  let result = "";
  if(value){
    if(value.length > length){
      result = value.substr(0, length) + '...';
    }else{
      result = value;
    }
  }
  return result;
}
export function getUrlPath() {
  return window.SITE_CONFIG.fileUrl
}
