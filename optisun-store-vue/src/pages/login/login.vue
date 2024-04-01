<template>
	<container class="container" ref="container" :headerVisable="false" :status="pageStatus">
		<view style="text-align: center;">
			<image  class="header-img" src="../../static/images/logo.png"></image>
		</view>
		<view class="content">
			<!-- <view :style="{ height: topHeight }"></view> -->
			<view class="form">
				<view class="title">
					{{ $t('login.welcome') }}
				</view>
				<view class="nav">
					<view class="nav-item" :class="activeIndex == 1 ? 'active' : ''" @click="activeIndex = 1">
						{{ $t('login.login') }}</view>
					<view class="nav-item" :class="activeIndex == 2 ? 'active' : ''" @click="activeIndex = 2">
						{{ $t('login.register') }}</view>
				</view>
				<view class="form-panel"> 
					<view class="input-panel" v-if="activeIndex == 1">
						<view class="input-item">
							<view class="input-title">{{ $t('login.username') }}</view>
							<!-- <input v-model="loginForm.username" class="input" :placeholder="$t('login.username')" /> -->
							<view class="input input-row">
								<view class="area-code" @click="pickArea"><text>{{ registerForm.mobileArea }}</text></view>
								<i class="ri-arrow-down-s-line"></i>
								<input class="row-input" v-model="loginForm.username" :placeholder="$t('login.username')"
									maxlength="9" />
								
							</view>
						</view>
						<view class="input-item">
							<view class="input-title">{{ $t('login.password') }}</view>
							<input v-model="loginForm.password" class="input" type="password"
								:placeholder="$t('login.password')" />
						</view>
					</view>
					<view class="input-panel" v-if="activeIndex == 2">
						<view class="input-item">
							<view class="input-title">{{ $t('login.mobile') }}</view>
							<view class="input input-row">
								<view class="area-code" @click="pickArea"><text>{{ registerForm.mobileArea }}</text></view>
								<i class="ri-arrow-down-s-line"></i>
								<input class="row-input" v-model="registerForm.mobile" :placeholder="$t('login.mobile')"
									maxlength="8" />
								<!-- <view v-if="time == ''" class="area-button" @click="sendSMS2"><text>{{ $t('login.otp')
								}}</text></view>
								<view v-else class="area-button" style="opacity: 0.5;" ><text>{{time}}s</text></view> -->
							</view>
						</view>
						<!-- <view class="input-item">
							<view class="input-title">{{ $t('login.otp') }}</view>
							<input v-model="registerForm.otp" class="input"
								:placeholder="$t('login.otp')" />
						</view> -->
						<view class="input-item">
							<view class="input-title">{{ $t('login.password') }}</view>
							<input v-model="registerForm.password" class="input" type="password"
								:placeholder="$t('login.password')" />
						</view>
						<view class="input-item">
							<view class="input-title">{{ $t('login.confirmPassword') }}</view>
							<input v-model="registerForm.confirmPassword" class="input" type="password"
								:placeholder="$t('login.confirmPassword')" />
						</view>
						<view class="input-item">
							<view class="input-title">{{ $t('login.inviteCode') }}</view>
							<input disabled v-model="registerForm.inviteCode" class="input"
								:placeholder="$t('login.inviteCode')" />
						</view>
					</view>

					<button v-if="activeIndex == 1" @click="submitClick" class="submit-btn" type="primary">{{
						$t('login.login') }}</button>
					<button v-if="activeIndex == 2" @click="registerHandle" class="submit-btn" type="primary">{{
						$t('login.register') }}</button>
					<!-- <view class="service" @click="goOnlineService">
						<text class="text">{{ $t('app.service') }}</text>
					</view> -->
					<view class="service">
						<text class="text" style="float: left;" @click="goOnlineService()">mot de passe oublié</text> <!--@click="$goUrl('/pages/forget/forget')"-->
						<text class="text" style="float: right;" @click="gotoUrl(download.andriod)">téléchargement APP</text> <!--@click="$goUrl('/pages/download/download')"-->
					</view>
				</view>
			</view>
		</view>
	</container>
</template>

<script>
export default {
	data() {
		return {
			time: '',
			activeIndex: 1,
			loginForm: {
				username: '',
				password: ''
			},
			registerForm: {
				mobile: '',
				password: '',
				confirmPassword: '',
				inviteCode: '',
				mobileArea: '',
				// otp:''
			},
			mobileAreas: [],
			onlineService: {},
			authCode: '',
			download:null,
		}
	},
	computed: {
		topHeight() {
			return uni.getSystemInfoSync().statusBarHeight + 340 + 'rpx'
		}
	},
	onLoad() {
		this.registerForm.inviteCode = this.$route.query.inviteCode || ''
		this.activeIndex = this.$route.query.pageIndex || 1
		this.authCode = this.$route.query.code || ''
		this.showPageContent()
		this.$uni.getOnlineService(0).then(data => {
			this.onlineService = data
		})

		this.authCodeLogin()
		this.getdownload()
	},
	onShow() {
		this.getAreaList()
		this.loginForm.username = uni.getStorageSync("username") || ''
		this.loginForm.password = uni.getStorageSync("password") || ''
		uni.removeStorageSync("tokenExpireTime")
		uni.removeStorageSync("user")
		uni.removeStorageSync("token")
	},
	methods: {
		gotoUrl(url){
			window.open(url)
		},
		getdownload() {
			this.$get('/app/download').then(res => {
				if (res.code == 0) {
					this.download = res.data
				}
			})
		},
		authCodeLogin() {
			if (this.authCode && this.authCode != '') {
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				let result = this.$post(`/app/authCodeLogin`, {
					code: this.authCode
				})
				this.loginResultHandle(result)
			}
		},
		getAreaList() {
			this.$get('/app/common/config', {
				key: 'register.mobileareas.config'
			}).then(res => {
				if (res.code == 0) {
					this.mobileAreas = res.data.split(',')
					if (this.mobileAreas.length > 0) {
						this.registerForm.mobileArea = this.mobileAreas[0]
					}
				}
			})
		},
		pickArea() {
			uni.showActionSheet({
				itemList: this.mobileAreas,
				success: res => {
					this.registerForm.mobileArea = this.mobileAreas[res.tapIndex]
				}
			})
		},
		goOnlineService() {
			// console.log(this.onlineService.ws)
			window.location.href =this.onlineService.ws
		},
		submitClick() {
			this.loginForm.username = this.loginForm.username.trim()
			this.loginForm.password = this.loginForm.password.trim()
			if (this.loginForm.username == '') {
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.username.blank')
				})
				return
			}
			if (this.loginForm.password == '') {
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.password.blank')
				})
				return
			}
			uni.showLoading({
				mask: true,
				title: 'loading...'
			})
			let result = this.$post(`/app/login?username=${this.loginForm.username}`, this.loginForm)
			this.loginResultHandle(result)
		},
		loginResultHandle(result) {
			result.then(res => {
				uni.hideLoading()
				if (res.code != 0) {
					uni.showToast({
						icon: 'none',
						title: this.$t('error.' + res.code)
					})
					return
				}
				uni.setStorageSync("username", this.loginForm.username)
				uni.setStorageSync("password", this.loginForm.password)
				uni.setStorageSync("tokenExpireTime", tokenExpireTime)
				uni.setStorageSync("user", user)
				let token = res.data.token.token
				let expire = res.data.token.tokenExpireTime
				let tokenExpireTime = parseInt(new Date().getTime() / 1000) + expire
				let user = res.data.user
				getApp().globalData.user = user
				uni.setStorageSync("token", token)
				uni.setStorageSync("tokenExpireTime", tokenExpireTime)
				uni.setStorageSync("user", user)
				this.$goUrl('/pages/index/index')
				// this.$goUrl('/pages/cftnew/cftnew')
			}).catch(e => {
				uni.hideLoading()
				uni.showToast({
					icon: 'error',
					title: this.$t('error.500')
				})
			})
		},
		sendSMS2() {
			this.registerForm.mobile = this.registerForm.mobile.trim()

			uni.showLoading({
				mask: true,
				title: 'loading...'
			})
			if (this.registerForm.mobile == '') {
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.username.blank')
				})
				return
			}
			let mobileReg = /\d{8}/
			
			if (!mobileReg.test(this.registerForm.mobile)) {
				
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.mobile.error')
				})
				return
			}
			this.$post('/app/sendSms', {
				'tel': this.registerForm.mobile,
				'type': 'REIGSTER_SMS_CODE'
			}).then(res => {
				uni.hideLoading()
				if (res.code == 0) {
					uni.showToast({
						icon: 'none',
						title: this.$t('login.sendSuccess')
					})
					this.time = 60
					this.setInt = setInterval(() => {
						if (this.time > 0) {
							this.time--
						} else {
							this.time = ''
							clearInterval(this.setInt)
							this.setInt = null
						}
					}, 1000)
				} else {
					uni.showToast({
						icon: 'none',
						title: this.$t('error.' + res.code)
					})
				}
			}).catch(e => {
				uni.hideLoading()
				uni.showToast({
					icon: 'error',
					title: this.$t('error.500')
				})
			})
		},
		registerHandle() {
			// let registerFlag = uni.getStorageSync("registerFlag")
			// if(!registerFlag){
			// 	uni.showToast({
			// 		icon: 'none',
			// 		title: this.$t('非法的请求')
			// 	})
			// 	return
			// }else if(registerFlag != 'unregister'){
			// 	uni.showToast({
			// 		icon: 'none',
			// 		title: this.$t('本设备已经注册')
			// 	})
			// 	return
			// }
			this.registerForm.mobile = this.registerForm.mobile.trim()
			this.registerForm.password = this.registerForm.password.trim()
			this.registerForm.confirmPassword = this.registerForm.confirmPassword.trim()
			this.registerForm.inviteCode = this.registerForm.inviteCode.trim()
			// this.registerForm.otp = this.registerForm.otp.trim()
			uni.showLoading({
				mask: true,
				title: 'loading...'
			})
			if (this.registerForm.mobile == '') {
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.username.blank')
				})
				return
			}
			// if (this.registerForm.otp == '') {
			// 	uni.showToast({
			// 		icon: 'none',
			// 		title: this.$t('validate.cardadd.otp.blank')
			// 	})
			// 	return
			// }
			let mobileReg = /\d{8}/
			if (!mobileReg.test(this.registerForm.mobile)) {
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.mobile.error')
				})
				return
			}
			if (this.registerForm.password == '') {
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.password.blank')
				})
				return
			}
			if (this.registerForm.password != this.registerForm.confirmPassword) {
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.confirmPassword.error')
				})
				return
			}

			//手机验证码-搁置
			// if (this.registerForm.inviteCode == '') {
			// 	uni.showToast({
			// 		icon: 'none',
			// 		title: this.$t('validate.inviteCode.error')
			// 	})
			// 	return
			// }
			
			this.$post('/app/register', this.registerForm).then(res => {
				uni.hideLoading()
				if (res.code == 0) {
					uni.setStorageSync("registerFlag", "regiter")
					uni.showToast({
						icon: 'none',
						title: this.$t('login.register.success')
					})
					//注册信息赋值到登录页面
					this.loginForm.username = this.registerForm.mobile
					this.loginForm.password = this.registerForm.password
					this.activeIndex = 1
				} else {
					uni.showToast({
						icon: 'none',
						title: this.$t('error.' + res.code)
					})
				}
			}).catch(e => {
				uni.hideLoading()
				uni.showToast({
					icon: 'error',
					title: this.$t('error.500')
				})
			})
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	background: #FFFFFF;
	overflow-y: auto;
}

.header-img {
	margin-top: 50rpx;
	height: 200rpx;
	// position: absolute;
	// left: 0;
	// top: 0;
	width: 200rpx;
	text-align: center;
	
}

.content {
	height: 100%;
	z-index: 1;
	position: relative;
	width: 100%;
	.form {
		padding: 0rpx 51rpx;

		.title {
			font-size: 36rpx;
			font-weight: 800;
			color: #C8995A;
		}

		.nav {
			display: flex;
			justify-content: center;
			margin-top: 60rpx;

			.nav-item {
				font-size: 16px;
				margin: 0 5px;
				color: #C8995A;
				position: relative;
				margin: 0 65rpx;
				padding-bottom: 13rpx;

				&.active {
					&::after {
						content: '';
						bottom: 0;
						left: 20rpx;
						right: 20rpx;
						position: absolute;
						color: #C8995A;
						border-bottom: 2px solid #C8995A;
						border-radius: 8rpx;
					}
				}
			}
		}

		.form-panel {
			width: 540rpx;
			background: #FFFFFF;
			border-radius: 30rpx;
			padding: 55rpx;
			margin: 0 auto;
			margin-top: 30rpx;
			box-shadow: 0 2px 12px 2px rgba(233, 214, 164, 0.5);
		}

		.input-panel {
			position: relative;
			.input-item {
				margin-bottom: 35rpx;
				.title {
					font-size: 27rpx;
					color: #555555;
				}
				.input {
					height: 74rpx;
					line-height: 74rpx;
					background: #F9F9F9;
					border: 1px solid #E9E9E9;
					border-radius: 37rpx;
					margin-top: 22rpx;
					padding: 0 33rpx;
					font-size: 26rpx;

					&.input-row {
						display: flex;
						align-items: center;

						.row-input {
							flex: 1;
							margin-left: 10rpx;
						}
					}

					.area-button {
						text-align: center;
						width: 100rpx;
						background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
						box-shadow: 0px 4rpx 13rpx 0px rgba(181, 135, 72, 0.47);
						position: absolute;
						right: 0;
						border-radius: 0 30rpx 30rpx 0;
						color: #fff;
					}

				}
			}

		}
		.submit-btn {
			width: 65%;
			margin-top: 47rpx;
			height: 88rpx;
			line-height: 88rpx;
			text-align: center;
			background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
			box-shadow: 0px 4rpx 13rpx 0px rgba(181, 135, 72, 0.47);
			border-radius: 44rpx;
			font-size: 32rpx;
			font-weight: bold;
			color: #FFFFFF;

			&::after {
				background: none;
				border: none;
			}
		}

		.service {
			font-size: 24rpx;
			color: #2953ff;
			margin-top: 20rpx;
			text-align: center;
			color: #C8995A;
			position: relative;

			.text {
				background: #FFFFFF;
				z-index: 1;
				position: relative;
				// padding: 0 20rpx;
			}

			&::after {
				content: '';
				position: absolute;
				width: 364rpx;
				height: 1px;
				background: linear-gradient(90deg, #FFFFFF 0%, #E0E0E0 50%, #FFFFFF 100%);
				left: 96rpx;
				top: 50%;
			}
		}
	}
}
</style>
