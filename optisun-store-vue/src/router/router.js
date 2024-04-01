// router.js
import {RouterMount,createRouter} from 'uni-simple-router'
import {get, post} from '../common/request'
import config from '../common/config'

const router = createRouter({
	platform: process.env.VUE_APP_PLATFORM,  
	routes: [...ROUTES]
})

//全局路由前置守卫
router.beforeEach((to, from, next) => {
	// console.log(ROUTES)
	// console.log(to, from, next)
	if(to.path == '/pages/login/login'){
		next()
	}else{ 
		if(to.nologin){
			next()
		}else if(!checkToken()){
			next({ path: '/pages/login/login' })
		} else{
			next()
		}
	}
})

// 全局路由后置守卫
router.afterEach((to, from) => {
	// console.log(to, from)
})

export {
	router,
	RouterMount
}

const checkToken = () => {
	let tokenExpireTime = uni.getStorageSync("tokenExpireTime")
	
	if(!tokenExpireTime || tokenExpireTime < new Date().getTime() / 1000){
		return false
	}
	return true
}