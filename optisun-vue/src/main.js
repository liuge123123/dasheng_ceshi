import Vue from 'vue'
import App from '@/App'
import router from '@/router'                 // api: https://github.com/vuejs/vue-router
import store from '@/store'                   // api: https://github.com/vuejs/vuex
import VueCookie from 'vue-cookie'            // api: https://github.com/alfhen/vue-cookie
import '@/element-ui'                         // api: https://github.com/ElemeFE/element
import '@/icons'                              // api: http://www.iconfont.cn/
import '@/element-ui-theme'
import '@/assets/scss/index.scss'
import httpRequest from '@/utils/httpRequest' // api: https://github.com/axios/axios
import {isAuth} from '@/utils'
import cloneDeep from 'lodash/cloneDeep'

import VueAreaLinkage from 'vue-area-linkage'
import 'vue-area-linkage/dist/index.css'
import 'default-passive-events'
import BaiduMap from 'vue-baidu-map' //地图
import WEditor from '@/components/editor/weditor'  //https://www.wangeditor.com/
import ImgUpload from "@/components/upload/img-upload";
Vue.use(VueAreaLinkage)
Vue.component('RichEditor', WEditor)

// 图片预览
import ImagePreview from 'vue-img-viewer'
Vue.use(ImagePreview)

// 复制
import VueClipboard from 'vue-clipboard2'
Vue.use(VueClipboard)

// // 图表
// import ECharts from 'vue-echarts'
// import 'echarts/lib/chart/bar'
// import 'echarts/lib/chart/line'
// import 'echarts/lib/chart/pie'
// import 'echarts/lib/chart/map'
// import 'echarts/lib/chart/radar'
// import 'echarts/lib/chart/scatter'
// import 'echarts/lib/chart/effectScatter'
// import 'echarts/lib/component/toolbox'
// import 'echarts/lib/component/tooltip'
// import 'echarts/lib/component/polar'
// import 'echarts/lib/component/geo'
// import 'echarts/lib/component/legend'
// import 'echarts/lib/component/title'
// import 'echarts/lib/component/visualMap'
// import 'echarts/lib/component/dataset'
// Vue.component('v-chart', ECharts)

// 自定义控件
import AreaSpan from '@/components/area/area-span'
import DictSpan from '@/components/dict/DictSpan'
import DictInput from '@/components/dict/DictInput'
import SearchForm from '@/components/form/SearchForm'
import AudioSimple from '@/components/audio/simple'
import AudioFull from '@/components/audio/full'
import CustTagEdit from "@/components/mutil-tag/CustTagEdit"
import DeptPersonMutil from "@/components/dept/DeptPersonMutil";
import ImgListPicker from "@/components/upload/img-list-picker"

Vue.component('AreaSpan', AreaSpan)
Vue.component('DictSpan', DictSpan)
Vue.component('DictInput', DictInput)
Vue.component('SearchForm', SearchForm)
Vue.component('AudioSimple', AudioSimple)
Vue.component('AudioFull', AudioFull)
Vue.component('CustTagEdit', CustTagEdit)
Vue.component('DeptPersonMutil', DeptPersonMutil)
Vue.component('ImgUpload',ImgUpload)
Vue.component('ImgListPicker',ImgListPicker)

// 百度地图
Vue.use(BaiduMap, {
  // ak 是在百度地图开发者平台申请的密钥 详见 http://lbsyun.baidu.com/apiconsole/key */
  ak: 'iXMpzdRyjUGViBnkpnqzCwGeAHr0KM69'
})

// 全局过滤器
import * as utils from '@/utils/index'
Object.keys(utils).forEach(key => {
  Vue.filter(key, utils[key])
})

// 消息服务
import MqUtils from '@/utils/mqUtils'
let msgClient = new MqUtils(window.SITE_CONFIG['mqUrl'],
  window.SITE_CONFIG['mqUsername'],
  window.SITE_CONFIG['mqPassword'],
  window.SITE_CONFIG['mqHost']);

Vue.use(VueCookie)
Vue.config.productionTip = false

// 非生产环境, 适配mockjs模拟数据                 // api: https://github.com/nuysoft/Mock
if (process.env.NODE_ENV !== 'production') {
  require('@/mock')
}

// 挂载全局
Vue.prototype.$http = httpRequest // ajax请求方法
Vue.prototype.isAuth = isAuth     // 权限方法
Vue.prototype.$utils = utils //工具类
Vue.prototype.$msgClient = msgClient //mq消息客户端

// 保存整站vuex本地储存初始状态
window.SITE_CONFIG['storeState'] = cloneDeep(store.state)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: {App}
})
