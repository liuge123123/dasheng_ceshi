import config from './config'
export default {
	data() {
		return {
			
		}
	},
	onShareAppMessage(res) {
		return this.getShareInfo()
	},
	onShareTimeline(res) {
		return this.getShareInfo()
	},
	getShareInfo(){
		return {}
	},
	methods: {
		getShareInfo(){
			let routes = getCurrentPages(); // 获取当前打开过的页面路由数组
			let curRoute = routes[routes.length - 1]
			let path = curRoute.$page.fullPath
			if(this.share.path && this.share.path != ''){
				path = this.share.path
			}
			return {
				title: "", // 分享标题
				desc: '', // 分享描述
				link: path, // 分享链接，该链接域名或路径必须与当前页面对应的公众号JS安全域名一致
				imgUrl: '', // 分享图标
				
				content: '', // 小程序分享内容
				imageUrl: '', // 小程序分享图标
				path: path, // 小程序分享路径
				success: function () {
					// 设置成功
				}
			}
		},
		setShareInfo(){
			let shareInfo = this.getShareInfo()
			// #ifdef H5
				this.$wx.ready(() => {
					this.$wx.updateAppMessageShareData(shareInfo)
					this.$wx.updateTimelineShareData(shareInfo)
					this.$wx.onMenuShareWeibo(shareInfo)
				})
			// #endif
		}
	}
}
