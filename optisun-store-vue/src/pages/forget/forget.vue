<template>
	<container class="container" ref="container" :headerVisable="false" :status="pageStatus">
		<image mode="widthFix" class="header-img" src="../../static/images/login_header.png"></image>
		<view class="content">
			<view :style="{ height: topHeight }"></view>
			<view class="form">
				<view class="title">
					<image class="logo" src="../../static/images/login_logo.png"></image>
				</view>
				<view class="nav">
					
					<view class="nav-item active" >
						{{ $t('login.forget') }}</view>
				</view>
				<view class="form-panel">
					<view class="input-panel">
						<view class="input-item">
							<view class="input-title">{{ $t('login.mobile') }}</view>
							<view class="input input-row">
								<view class="area-code" @click="pickArea"><text>{{ registerForm.mobileArea }}</text></view>
								<i class="ri-arrow-down-s-line"></i>
								<input class="row-input" v-model="registerForm.mobile" :placeholder="$t('login.mobile')"
									maxlength="8" />
								<view v-if="time == ''" class="area-button" @click="sendSMS2"><text>{{ $t('login.otp')
								}}</text></view>
								<view v-else class="area-button" style="opacity: 0.5;" ><text>{{time}}s</text></view>
							</view>
						</view>
						<view class="input-item">
							<view class="input-title">{{ $t('login.otp') }}</view>
							<input v-model="registerForm.otp" class="input"
								:placeholder="$t('login.otp')" />
						</view>
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
					</view>
					<button @click="forgetHandle" class="submit-btn" type="primary">{{
						$t('login.forgetbtn') }}</button>
					
					<view class="service">
						<text class="text" style="float: left;" @click="$goUrl('/pages/login/login')">Login</text>
						<text class="text" style="float: right;" @click="$goUrl('/pages/download/download')">Download APP</text>
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
			registerForm: {
				mobile: '',
				password: '',
				confirmPassword: '',
				mobileArea: '',
				otp:''
			},
			mobileAreas: [],
		}
	},
	computed: {
		topHeight() {
			return uni.getSystemInfoSync().statusBarHeight + 44 + 'px'
		}
	},
	onLoad() {
		this.showPageContent()
	},
	onShow() {
		this.getAreaList()
	
		uni.removeStorageSync("tokenExpireTime")
		uni.removeStorageSync("user")
		uni.removeStorageSync("token")
	},
	methods: {
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
				'type': 'FORGET_SMS_CODE'
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
		forgetHandle() {
			
			this.registerForm.mobile = this.registerForm.mobile.trim()
			this.registerForm.password = this.registerForm.password.trim()
			this.registerForm.confirmPassword = this.registerForm.confirmPassword.trim()
			this.registerForm.otp = this.registerForm.otp.trim()
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
			if (this.registerForm.otp == '') {
				uni.showToast({
					icon: 'none',
					title: this.$t('validate.cardadd.otp.blank')
				})
				return
			}
			let mobileReg = /\d{10}/
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
		
			this.$post('/app/changepwdbysms', this.registerForm).then(res => {
				uni.hideLoading()
				if (res.code == 0) {
					uni.showToast({
						icon: 'none',
						title: this.$t('login.register.success')
					})
					//注册信息赋值到登录页面
					this.$goUrl("/pages/login/login")
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
	position: absolute;
	left: 0;
	top: 0;
	width: 100%;
}

.content {
	height: 100%;
	z-index: 1;
	position: relative;
	width: 100%;
	background: url('../../static/images/login-bg.jpg');

	.form {
		padding: 32rpx 51rpx;

		.title {
			text-align: center;

			.logo {
				width: 200rpx;
				height: 200rpx;
				border-radius: 50%;
				border: 2px #fdb523 solid;
			}
		}

		.nav {
			display: flex;
			justify-content: center;
			// margin-top: 61rpx;

			.nav-item {
				font-size: 16px;
				margin: 0 5px;
				color: #fff;
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
						color: #fff;
						border-bottom: 2px solid #fff;
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
			margin-top: 46rpx;
			text-align: center;
			color: #C8995A;
			position: relative;

			.text {
				background: #FFFFFF;
				z-index: 1;
				position: relative;
				padding: 0 20rpx;
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
