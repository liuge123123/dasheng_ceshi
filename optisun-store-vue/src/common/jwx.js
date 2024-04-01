const install = (Vue, vm) => {
	let getJsapiSign = () => {
		return new Promise((resolve, reject) => {
			vm.$post('/app/wx/oauth/jsSign', {
				url: window.location.href.split('#')[0],
				orgId: getApp().globalData.siteInfo.orgId
			}).then(res => {
				if(res.code == 0){
					resolve(res.data)
				}else{
					reject(res)
				}
			}).catch(error => {
				reject(error)
			})
		})
	}
	
	let init = () => {
		return new Promise((resolve, reject) => {
			getJsapiSign().then(data => {
				vm.$wx.config({
						debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
						appId: data.appId, // 必填，企业微信的corpID
						timestamp: data.timestamp , // 必填，生成签名的时间戳
						nonceStr: data.nonceStr, // 必填，生成签名的随机串
						signature: data.signature,// 必填，签名，见 附录-JS-SDK使用权限签名算法
						jsApiList: [
							"updateAppMessageShareData",
							"updateTimelineShareData",
							"onMenuShareWeibo",
							"onMenuShareQZone",
							"startRecord",
							"stopRecord",
							"onVoiceRecordEnd",
							"playVoice",
							"pauseVoice",
							"stopVoice",
							"onVoicePlayEnd",
							"uploadVoice",
							"downloadVoice",
							"chooseImage",
							"previewImage",
							"uploadImage",
							"downloadImage",
							"translateVoice",
							"getNetworkType",
							"openLocation",
							"getLocation",
							"hideOptionMenu",
							"showOptionMenu",
							"hideMenuItems",
							"showMenuItems",
							"hideAllNonBaseMenuItem",
							"showAllNonBaseMenuItem",
							"closeWindow",
							"scanQRCode",
							"chooseWXPay",
							"openProductSpecificView",
							"addCard",
							"chooseCard",
							"openCard"
						] // 必填，需要使用的JS接口列表，凡是要调用的接口都需要传进来
				});
				vm.$wx.error(res => {
					reject(res)
				})
				vm.$wx.ready(res => {
					resolve()
				})
			}).catch(err => {
				reject(err)
			})
		})
	}
	
	let getShareUrl = (type, params) => {
		let url = `${vm.$config.baseUrl}/app/wx/oauth`
		let authId = vm.$config.shareAuthIds[type]
		url = `${url}?authId=${authId}`
		if(params){
			for(let key in params){
				url = `${url}&${key}=${params[key]}`
			}
		}
		if(url.indexOf("orgId=") == -1){
			url = `${url}&orgId=${vm.globalData.siteInfo.orgId}`
		}
		return url
	}
	
	let getSharePath = (type, params) => {
		let items = {
			'index': 'pages/index/index',
			'diy': 'pages/diy/diy',
			'article': 'pages/article_detail/article_detail',
			'form': 'pages/form_detail/form_detail',
			'file': 'pages/file_detail/file_detail',
			'product': 'pages/product_detail/product_detail'
		}
		let path = `${items[type]}?share=1`
		if(params){
			for(let key in params){
				path = `${path}&${key}=${params[key]}`
			}
		}
		if(path.indexOf("orgId=") == -1){
			path = `${path}&orgId=${vm.globalData.siteInfo.orgId}`
		}
		return path
	}
	
	Vue.prototype.$jwx = {
		getJsapiSign,
		init,
		getShareUrl,
		getSharePath
	};
}

export default {
	install
}
