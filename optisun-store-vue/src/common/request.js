import config from './config'
let baseUrl = config.baseUrl

function goAuth(){
	uni.redirectTo({
		url: '/pages/login/login'
	})
}

function getToken() {
	let token = uni.getStorageSync("token") || '0525c702ece5cc72d93ede78878723cbb83751d4';
	return token
}

/**
 * 获取完整接口地址
 * @param {Object} url
 */
export function getUrl(url){
	let token = getToken()
	if(url.indexOf('http://') == -1 && url.indexOf('https://') == -1) {
		url = baseUrl + url
	}
	if(url.indexOf('?') == -1){
		return `${url}?token=${token}`
	}else{
		return `${url}&token=${token}`
	}
}

/**
	封装get方法
*/
export function get(url, data = {}) {
	if(url.indexOf('http://') == -1 && url.indexOf('https://') == -1) {
		url = baseUrl + url 
	}
	return new Promise((resolve, reject) =>{
		uni.request({
			url: url,
			header: {
				'Content-Type': 'application/json',
				'token': getToken()
			},
			data: data,
			method: 'GET',
			success: res => {
				if(res.data.code == 401){
					goAuth()
				}else{
					resolve(res.data)
				}
			},
			fail: (error) => {
				reject(error)
			}
		})
	})
}

/*	
		封装post方法
*/
export function post(url, data = {}) {
	if (url.indexOf('http://') == -1 && url.indexOf('https://') == -1) {
		url = baseUrl + url
	}
	return new Promise((resolve, reject) => {
		uni.request({
			url: url,
			data: data,
			header: {
				'Content-Type': 'application/json',
				'token': getToken()
			},
			method: 'POST',
			success: res => {
				if(res.data.code == 401){
					goAuth()
				}else{
					resolve(res.data)
				}
			fail: (error) => {
				reject(error)
			}
			},
		})
	})
}

export function upload(path, form) {
	if((path.indexOf("http://") != -1 || path.indexOf("https://") != -1) && path.indexOf('http://tmp') == -1){
		return post('/applet/netUpload', {
			url: path,
			formData: form
		})
	}else{
		return new Promise((resolve, reject) => {
			let url = baseUrl + '/applet/upload'
			uni.uploadFile({
				url: url,
				header: {
					'token': getToken()
				},
				filePath: path,
				name: 'file',
				formData: form,
				success: res => {
					let data = JSON.parse(res.data)
					resolve(data)
				},
				fail: (error) => {
					reject(error)
				}
			})
		})
	}
}