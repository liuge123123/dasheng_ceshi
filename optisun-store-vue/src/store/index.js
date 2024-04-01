// 页面路径：store/index.js 
import Vue from 'vue'
import Vuex from 'vuex'
import cart from './cart'

Vue.use(Vuex);//vue的插件机制

//Vuex.Store 构造器选项
const store = new Vuex.Store({
	modules: {
		cart
	}
})
export default store
