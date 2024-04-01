/**
 * 开发环境
 */
;(function () {
  window.SITE_CONFIG = {};

  // api接口请求地址
  window.SITE_CONFIG['baseUrl'] = 'http://localhost:8099/api/v1';
  window.SITE_CONFIG['frontUrl'] = 'http://localhost:8080';
  window.SITE_CONFIG['mqUrl'] = 'ws://127.0.0.1:15674/ws';
  window.SITE_CONFIG['mqUsername'] = 'guest';
  window.SITE_CONFIG['mqPassword'] = 'guest';
  window.SITE_CONFIG['mqHost'] = '/';


  // cdn地址 = 域名 + 版本号
  window.SITE_CONFIG['domain']  = './'; // 域名
  window.SITE_CONFIG['version'] = '';   // 版本号(年月日时分)
  window.SITE_CONFIG['cdnUrl']  = window.SITE_CONFIG.domain + window.SITE_CONFIG.version;
})();
