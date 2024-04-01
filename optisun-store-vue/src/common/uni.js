import {get} from './request'
/**
 * 工具类
 */

export default {

	/**
	 * scene解码
	 */
	scene_decode(e) {
		if (e === undefined)
			return {};
		let scene = decodeURIComponent(e),
			params = scene.split(','),
			data = {};
		for (let i in params) {
			var val = params[i].split(':');
			val.length > 0 && val[0] && (data[val[0]] = val[1] || null)
		}
		return data;
	},

	/**
	 * 格式化日期格式 (用于兼容ios Date对象)
	 */
	format_date(time) {
		// 将xxxx-xx-xx的时间格式，转换为 xxxx/xx/xx的格式 
		return time.replace(/\-/g, "/");
	},

	dateTimeFormat(timeStamp) {
		if ((timeStamp + '').length == 10) {
			timeStamp = timeStamp * 1000
		}
		let date = new Date(timeStamp);
		const year = date.getFullYear();
		const month = date.getMonth() + 1;
		const day = date.getDate();
		return year + "-" + this.changeDate(month) + "-" + this.changeDate(day);
	},

	changeDate(m) {
		if (m < 10) {
			return m = '0' + m
		}
		return m
	},

	dataToTimeStamp(time) {
		let date = time
		if (date) {
			date = date.substring(0, 19);
			date = date.replace(/-/g, '/'); //必须把日期'-'转为'/'
			var timestamp = new Date(date).getTime() / 1000;
			return timestamp
		} else {
			return 0
		}
	},


	timeAgo(timeStamp) {
		if (!timeStamp || timeStamp == 0) {
			return ''
		}
		let time = new Date().getTime() / 1000 - timeStamp
		let date = new Date(timeStamp * 1000);
		const year = date.getFullYear();
		const month = this.changeDate(date.getMonth() + 1);
		const day = this.changeDate(date.getDate());
		let hour = this.changeDate(date.getHours())
		let minutes = this.changeDate(date.getMinutes())
		let seconds = this.changeDate(date.getSeconds())
		if (time < 0) {
			return year + "年" + month + "月" + day + "日 " + hour + ':' + minutes + ':' + seconds;
		} else if (time < 30) {
			return '刚刚'
		} else if (time < 60) {
			return parseInt(time) + '秒之前'
		} else if (time < 60 * 60) {
			return parseInt(time / 60) + '分钟之前'
		} else if (time < 60 * 60 * 24) {
			return parseInt(time / 60 / 60) + '小时之前'
		} else if (time < 60 * 60 * 24 * 30) {
			return parseInt(time / 60 / 60 / 24) + '天之前'
		} else if (time < 60 * 60 * 24 * 365) {
			return month + "月" + day + "日 " + hour + ':' + minutes + ':' + seconds;
		} else {
			return year + "年" + month + "月" + day + "日 " + hour + ':' + minutes + ':' + seconds;
		}
	},

	timeAgo1(timeStamp) {
		if (!timeStamp || timeStamp == 0) {
			return '未知时间'
		}
		let time = new Date().getTime() / 1000 - timeStamp
		let date = new Date(timeStamp * 1000);
		const year = date.getFullYear();
		const month = this.changeDate(date.getMonth() + 1);
		const day = this.changeDate(date.getDate());
		let hour = this.changeDate(date.getHours())
		let minutes = this.changeDate(date.getMinutes())
		let seconds = this.changeDate(date.getSeconds())
		if (time < 0) {
			return month + "月" + day + "日 " + hour + ':' + minutes;
		} else if (time < 30) {
			return '刚刚'
		} else if (time < 60) {
			return parseInt(time) + '秒之前'
		} else if (time < 60 * 60) {
			return parseInt(time / 60) + '分钟之前'
		} else if (time < 60 * 60 * 24) {
			return parseInt(time / 60 / 60) + '小时之前'
		} else if (time < 60 * 60 * 24 * 30) {
			return parseInt(time / 60 / 60 / 24) + '天之前'
		} else if (time < 60 * 60 * 24 * 365) {
			return month + "月" + day + "日 " + hour + ':' + minutes;
		} else {
			return month + "月" + day + "日 " + hour + ':' + minutes;
		}
	},

	/**
	 * 对象转URL
	 */
	urlEncode(data) {
		var _result = [];
		for (var key in data) {
			var value = data[key];
			if (value.constructor == Array) {
				value.forEach(_value => {
					_result.push(key + "=" + _value);
				});
			} else {
				_result.push(key + '=' + value);
			}
		}
		return _result.join('&');
	},

	/**
	 * 遍历对象
	 */
	objForEach(obj, callback) {
		Object.keys(obj).forEach((key) => {
			callback(obj[key], key);
		});
	},

	/**
	 * 是否在数组内
	 */
	inArray(search, array) {
		for (var i in array) {
			if (array[i] == search) {
				return true;
			}
		}
		return false;
	},

	/**
	 * 判断是否为正整数
	 */
	isPositiveInteger(value) {
		return /(^[0-9]\d*$)/.test(value);
	},

	/**
	 * 对Date的扩展，将 Date 转化为指定格式的String
	 * 月(Y)、月(m)、日(d)、小时(H)、分(M)、秒(S) 可以用 1-2 个占位符，
	 * 例子：
	 * dateFormat('YYYY-mm-dd HH:MM:SS', new Date()) ==> 2020-01-01 08:00:00
	 */
	dateFormat(fmt, date) {
		let day = date.getDay();
		let weeks = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
		var week = weeks[day];
		const opt = {
			"Y+": date.getFullYear().toString(), // 年
			"m+": (date.getMonth() + 1).toString(), // 月
			"d+": date.getDate().toString(), // 日
			"H+": date.getHours().toString(), // 时
			"M+": date.getMinutes().toString(), // 分
			"S+": date.getSeconds().toString(), // 秒
			"W+": week
			// 有其他格式化字符需求可以继续添加，必须转化成字符串
		};
		let ret;
		for (let k in opt) {
			ret = new RegExp("(" + k + ")").exec(fmt);
			if (ret) {
				fmt = fmt.replace(ret[1], (ret[1].length == 1) ? (opt[k]) : (opt[k].padStart(ret[1].length, "0")))
			};
		};
		return fmt;
	},


	// timeFormat(timeStamp, fmt = 'YYYY-mm-dd HH:MM:SS') {
	// 	if(timeStamp){
	// 		if ((timeStamp + '').length == 10) {
	// 			timeStamp = timeStamp * 1000
	// 		}
	// 		let sq = new Date().getTimezoneOffset()/60
	// 		timeStamp = timeStamp + sq * 3600 * 1000
	// 		let date = new Date(timeStamp);
	// 		return this.dateFormat(fmt, date)
	// 	}else{
	// 		return ''
	// 	}
	// },
	timeFormat(timeStamp, fmt = 'dd/mm/YYYY HH:MM:SS') {
		const CURRENT_TIME_ZONE_NUM = -1;
		const LOCAL_TIME_ZONE_NUM = new Date().getTimezoneOffset() / 60;
		const TRANS_NUM = LOCAL_TIME_ZONE_NUM - CURRENT_TIME_ZONE_NUM;
		if(timeStamp){
			if ((timeStamp + '').length == 10) {
				timeStamp = timeStamp * 1000
			}
			let sq = TRANS_NUM;//new Date().getTimezoneOffset()/60
			timeStamp = timeStamp + sq * 3600 * 1000
			let date = new Date(timeStamp);
			return this.dateFormat(fmt, date)
		}else{
			return ''
		}
	},

	timeFormat1(timeStamp) {
		return this.timeFormat(timeStamp, 'YYYY.mm.dd HH:MM:SS');
	},

	timeFormat2(timeStamp) {
		return this.timeFormat(timeStamp, 'YYYY.mm.dd HH:MM');
	},
	timeFormat3(timeStamp, fmt = 'mm-dd HH:MM') {
		if ((timeStamp + '').length == 10) {
			timeStamp = timeStamp * 1000
		}
		let date = new Date(timeStamp);
		return this.dateFormat(fmt, date)
	},

	formatRichText(html) { //控制小程序中图片大小
		if (!html || html == '') {
			return ''
		}
		let newContent = html.replace(/<img[^>]*>/gi, function(match, capture) {
			match = match.replace(/style="[^"]+"/gi, '').replace(/style='[^']+'/gi, '');
			match = match.replace(/width="[^"]+"/gi, '').replace(/width='[^']+'/gi, '');
			match = match.replace(/height="[^"]+"/gi, '').replace(/height='[^']+'/gi, '');
			return match;
		});
		newContent = newContent.replace(/style="[^"]+"/gi, function(match, capture) {
			match = match.replace(/width:[^;]+;/gi, 'max-width:100%;').replace(/width:[^;]+;/gi,
				'max-width:100%;');
			return match;
		});
		newContent = newContent.replace(/<br[^>]*\/>/gi, '');
		newContent = newContent.replace(/\<img/gi,
			'<img style="max-width:100%;height:auto;display:inline-block;margin:10rpx auto;"');
		return newContent;
	},

	clearHtml(html) {
		if (html) {
			let regex = /(<([^>]+)>)/ig
			return html.replace(regex, "");
		}
		return ''
	},
	/**
	 * 获取当天日期
	 **/
	getNowDay() {
		return this.formatDate(new Date());
	},
	/**
	 * 获取昨天日期
	 **/
	getYesterDay() {
		return this.formatDate(new Date(new Date().setDate(new Date().getDate() - 1)));
	},
	/*
	 * 获取本周第一天
	 */
	getStartWeekDay() {
		var weekday = new Date().getDay() || 7;
		return this.formatDate(new Date(new Date().setDate(new Date().getDate() - weekday + 1)));
	},
	/*
	 *获取本周最后一天
	 */
	getEndWeekDay() {
		var weekday = new Date().getDay() || 7;
		return this.formatDate(new Date(new Date().setDate(new Date().getDate() - weekday + 7)));
	},
	/*
	 *获取上周第一天
	 */
	getLastStartWeekDay() {
		var weekday = new Date().getDay() || 7;
		return this.formatDate(new Date(new Date().setDate(new Date().getDate() - weekday - 6)));
	},
	/*
	 *获取上周最后一天
	 */
	getLastEndWeekDay() {
		var weekday = new Date().getDay() || 7;
		return this.formatDate(new Date(new Date().setDate(new Date().getDate() - weekday)));
	},
	/**
	 * 获得本月的开始时间
	 *
	 */
	getStartDayOfMonth() {
		return this.formatDate(new Date(new Date().setDate(1)));
	},
	/**
	 * 获得本月的结束时间
	 *
	 * @returns
	 */
	getEndDayOfMonth() {
		return this.formatDate(new Date(new Date(new Date().setMonth(new Date().getMonth() + 1)).setDate(0)));
	},
	/**
	 * 获得本月天数
	 *
	 * @returns
	 */
	getMonthDays() {
		var now = new Date(); // 当前日期
		var nowMonth = now.getMonth(); // 当前月
		var nowYear = now.getYear(); // 当前年
		var monthStartDate = new Date(nowYear, nowMonth, 1);
		var monthEndDate = new Date(nowYear, nowMonth + 1, 1);
		var days = (monthEndDate - monthStartDate) / (1000 * 60 * 60 * 24);
		return days;
	},
	/*
	获取上月的开始时间
	*/
	getLastStartDayOfMonth() {
		return this.formatDate(new Date(new Date(new Date().setMonth(new Date().getMonth() - 1)).setDate(1)));
	},
	/**
	 * 获取上月的结束时间
	 */
	getLastEndDayOfMonth() {
		return this.formatDate(new Date(new Date().setDate(0)));
	},
	/**
	 * @param 日期格式化
	 * @returns {String}
	 */
	formatDate(date) {
		var myyear = date.getFullYear();
		var mymonth = date.getMonth() + 1;
		var myweekday = date.getDate();

		if (mymonth < 10) {
			mymonth = "0" + mymonth;
		}
		if (myweekday < 10) {
			myweekday = "0" + myweekday;
		}
		return (myyear + "-" + mymonth + "-" + myweekday);
	},
	/**
	 * 分转元
	 */
	regFenToYuan(fen) {
		var num = fen;
		num = fen * 0.01;
		num += '';
		var reg = num.indexOf('.') > -1 ? /(\d{1,3})(?=(?:\d{3})+\.)/g : /(\d{1,3})(?=(?:\d{3})+$)/g;
		num = num.replace(reg, '$1');
		num = toDecimal2(num);
		return num;
	},
	toDecimal2(x) {
		var f = parseFloat(x);
		if (isNaN(f)) {
			return false;
		}
		var f = Math.round(x * 100) / 100;
		var s = f.toString();
		var rs = s.indexOf('.');
		if (rs < 0) {
			rs = s.length;
			s += '.';
		}
		while (s.length <= rs + 2) {
			s += '0';
		}
		return s;
	},
	goSitePage(type, id, taskId = ''){
		let urls = {
			'article': 'pages/article_detail/article_detail',
			'file': 'pages/file_detail/file_detail',
			'product': 'pages/product_detail/product_detail',
			'poster': 'pages/poster_gen/poster_gen',
			'form': 'pages/form_detail/form_detail',
			'coupon': 'page_card/coupon_collect/coupon_collect',
			'card': 'page_card/receive_card/receive_card'
		}
		let url = urls[type]
		let userId = getApp().globalData.user.userId
		url = `${url}?c=${id}&u=${userId}&s=0&t=${taskId}`
		console.log(url)
		let appId = getApp().globalData.siteMa.appId
		uni.navigateToMiniProgram({
			appId: appId,
			path: url
		})
	},
	subscribeMsg(){
		if(process.env.NODE_ENV != 'development'){
			wx.requestSubscribeMessage({
				tmplIds: ['XXtyHVhotw0lGNnPaR-Dpod7n_XtrhtzlGbYIcdeCFE', '8xw1nX0meJJUVot-d5CPFpsDubJhhTH9F3bKgDfEV8M', '6_7libCNfhh4pdtszV1Ertk_uGC-euH9Ua6E2hMAUwg'],
				success: (res) => { 
					console.log(res)
				},
				fail: error => {
					console.log(error)
				}
			})
		}
	},
	emitSubscribeMsg(){
		uni.$emit('subscribeMsg')
	},
	showError(msg){
		uni.showToast({
			icon: 'none',
			title: msg
		})
	},
	getUserInfo(){
		return new Promise((resolve, reject) => {
			get('/app/user/info').then(res => {
				if(res.code == 0){
					let user = res.data
					uni.setStorageSync("user", user)
					resolve(user)
				}else{
					reject(res)
				}
			}).catch(e => {
				reject(e)
			})
		})
	},
	getOnlineService(id){
		return new Promise((resolve, reject) => {
			get('/app/account/online/service', {
				custId: id
			}).then(res => {
				if(res.code == 0){
					let onlineService = res.data
					resolve(onlineService)
				}else{
					reject(res)
				}
			}).catch(e => {
				reject(e)
			})
		})
	},
	getConfig(key){
		return new Promise((resolve, reject) => {
			get('/app/common/config', {
				key: key
			}).then(res => {
				if(res.code == 0){
					resolve(res.data)
				}else{
					reject(res)
				}
			}).catch(e => {
				reject(e)
			})
		})
	},
	getContent(code){
		return new Promise((resolve, reject) => {
			get('/app/content/info', {
				code: code
			}).then(res => {
				if(res.code == 0){
					resolve(res.data)
				}else{
					reject(res)
				}
			}).catch(e => {
				reject(e)
			})
		})
	},
	getContentById(articleId){
		return new Promise((resolve, reject) => {
			get('/app/content/infoById', {
				articleId: articleId
			}).then(res => {
				if(res.code == 0){
					resolve(res.data)
				}else{
					reject(res)
				}
			}).catch(e => {
				reject(e)
			})
		})
	}
};
