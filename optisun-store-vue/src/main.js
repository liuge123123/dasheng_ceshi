import Vue from 'vue'
import App from './App'
import { get, post, upload, getUrl } from './common/request'
import 'remixicon/fonts/remixicon.css'

import UniNavBar from './components/uni-nav-bar/uni-nav-bar.vue'
import Container from './components/container/container.vue'
import Empty from './components/empty/empty.vue'
import Loading from './components/loading/loading.vue'

import messages from './locale/index'

let i18nConfig = {
	locale: 'fr',
	//   locale: 'en',
	// locale : 'zh-Hans',
	messages
}
import VueI18n from 'vue-i18n'
Vue.use(VueI18n)
const i18n = new VueI18n(i18nConfig)

Vue.component('UniNavBar', UniNavBar)
Vue.component('Container', Container)
Vue.component('Empty', Empty)
Vue.component('Loading', Loading)

// 路由
import { router, RouterMount } from './router/router.js'
Vue.use(router)

// 分享
import Share from './common/share.js'
Vue.mixin(Share)

// 页面共通
import PageCommon from './common/pageCommon.js'
Vue.mixin(PageCommon)

import uni from './common/uni'
Vue.prototype.$uni = uni

// 分享
import validate from './common/validate.js'
Vue.prototype.$validate = validate

// 状态管理
import store from './store'
Vue.prototype.$store = store


Vue.config.productionTip = false

App.mpType = 'app'


Vue.prototype.$get = get
Vue.prototype.$post = post
Vue.prototype.$upload = upload
Vue.prototype.$getUrl = getUrl
Vue.prototype.$moneyFormat = (val) => {
	return Math.floor(val * 10000) / 10000
}

Vue.prototype.$goUrl = (url) => {
	if (!url || url == '') {
		return
	} else if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0) {
		wx.navigateTo({
			url: '/pages/web/web?url=' + url
		})
	} else if (url.indexOf("wxma://") == 0) {
		url = url.replace("wxma://", "")
		wx.navigateToMiniProgram({
			appId: url,
			path: '',
			envVersion: 'release', //打开正式版
			success(res) {
				//打开成功
			},
			fail: function (err) {
				console.log(err);
			}
		})
	} else if (url.indexOf("redirect://") == 0) {
		url = url.replace("redirect://", "")
		wx.redirectTo({
			url: url
		})
	} else if (url.indexOf("relanch://") == 0) {
		url = url.replace("relanch://", "")
		wx.reLaunch({
			url: url
		})
	} else {
		wx.navigateTo({
			url: url,
			success(result) {
				console.log('navigateTo success', result)
			},
			fail(error) {
				console.log('navigateTo fail', error)

			},
		})
	}
}

// #ifdef H5
let jweixin = require('jweixin-module')
Vue.prototype.$wx = jweixin

import jwx from '@/common/jwx.js';
Vue.use(jwx, app);
// #endif



import config from './common/config'
Vue.prototype.$config = config


const app = new Vue({
	i18n,
	store,
	...App
})

// #ifdef H5
RouterMount(app, router, '#app')
// #endif

// #ifndef H5
app.$mount(); //为了兼容小程序及app端必须这样写才有效果
// #endif