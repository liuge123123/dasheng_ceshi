<template>
	<view class="auth-container" v-if="vis">
		<view class="auth-panel1" v-if="authStep == 1">
			<view class="body">
				<image class="banner" src="../../static/images/auth.jpg" mode="widthFix"></image>
				<view class="title">微信授权</view>
				<view class="desc">我们需要获取您的公开信息（头像和昵称）</view>
				<button class="btn" @click="nicknameInput">
					<view class="icon iconfont icon-weixin"></view>
					授权登录
				</button>
			</view>
			<view class="close-btn">
				<view class="icon-btn iconfont icon-guanbi" @click="cancel"></view>
			</view>
		</view>
		
		<view class="auth-panel1" v-if="authStep == 2">
			<view class="body">
				<image class="banner" src="../../static/images/auth.jpg" mode="widthFix"></image>
				<view class="title">微信登录</view>
				<view class="desc">我们需要获取您的手机号码用于微信登录</view>
				<button class="btn" open-type="getPhoneNumber" @getphonenumber="getPhoneNumber">
					<view class="icon iconfont icon-weixin"></view>
					微信快速登录
				</button>
			</view>
			<view class="close-btn">
				<view class="icon-btn iconfont icon-guanbi" @click="cancel"></view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				avatarUrl: '',
				nickname: '',
				mobile: '',
				vis: false,
				authStep: 1
			}
		},
		methods: {
			show(){
				this.vis = true
				this.authStep = 1
			},
			hide(){
				this.vis = false
			},
      nicknameInput() {
				wx.getUserProfile({
					lang: 'zh_CN',
					desc: '获取您的公开信息（昵称和头像）',
					success: res => {
						this.nickname = res.userInfo.nickName
						this.avatarUrl = res.userInfo.avatarUrl
						this.authStep = 2
					}
				})
      },
			onChooseAvatar(res) {
				const {
					avatarUrl
				} = res.detail
				this.avatarUrl = avatarUrl
			},
			uploadAvatar(filePath) {
				return new Promise((resolve, reject) => {
					this.$upload(filePath).then(res => {
						if (res.code == 0) {
							resolve(res)
						} else {
							reject(res)
						}
					}).catch(error => {
						console.error(error)
						let res = {
							code: 500,
							msg: '未知错误'
						}
						reject(res)
					})
				})
			},
			getPhoneNumber(res) {
        const {
          cloudID,
          code,
          encryptedData,
          iv
        } = res.detail
        if(!code && !encryptedData){
          return
        }
				uni.showLoading({
					mask: true,
					title: '获取中...'
				})
				this.$post('/app/ma/decryptPhoneInfo', {
					code: code,
					encryptedData: encryptedData,
					iv: iv,
					sessionKey: getApp().globalData.maUserInfo.sessionKey
				}).then(res => {
					uni.hideLoading()
					console.log(res)
					if (res.code == 0) {
						this.mobile = res.data.phoneNumber
						this.login()
					} else {
						uni.showToast({
							icon: 'none',
							title: res.msg
						})
					}
				}).catch(e => {
					uni.hideLoading()
				})
			},
			login() {	
				uni.showLoading({
					mask: true,
					title: '登录中...'
				})
				if(!this.avatarUrl || this.avatarUrl == ''){
					uni.showToast({
						icon: 'none',
						title: '请选择头像'
					})
					return
				}
				if(!this.nickname || this.nickname == ''){
					uni.showToast({
						icon: 'none',
						title: '请输入昵称'
					})
					return
				}
				if(!this.mobile || this.mobile == ''){
					uni.showToast({
						icon: 'none',
						title: '请授权获取手机号'
					})
					return
				}
				
				this.uploadAvatar(this.avatarUrl).then(res => {
					let avatar = res.url
					this.$post('/app/ma/login', {
						openid: getApp().globalData.maUserInfo.openid,
						unionid: getApp().globalData.maUserInfo.unionid,
						nickname: this.nickname,
						avatar: avatar,
						mobile: this.mobile,
						clientType: 'mawx',
						orgId: getApp().globalData.siteInfo.orgId
					}).then(res => {
						uni.hideLoading()
						if(res.code == 0){
							getApp().globalData.userInfo = res.data
							uni.showToast({
								title: '授权成功'
							})
							this.hide()
							uni.redirectTo({
								url: this.$Route.fullPath
							})
						}else{
							uni.showToast({
								icon: 'none',
								title: res.msg
							})
						}
					})
				}).catch(res => {
					console.error(res)
					uni.showToast({
						icon: 'none',
						title: res.msg || '网络错误，请稍后重试！'
					})
				})
			},
			cancel(){
				uni.reLaunch({
					url: '/pages/index/index'
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.auth-container{
		position: fixed;
		left: 0;
		right: 0;
		top: 0;
		bottom: 0;
		background: rgba($color: #000000, $alpha: .5);
		display: flex;
		flex-direction: column;
		justify-content: center;
		padding: 0 36rpx;
	}
	.auth-panel {
		background: #FFFFFF;
		padding: 60rpx;
		border-radius: 8rpx;
		position: relative;

		.avatar-wrapper {
			background: none;
			border: none;
			position: absolute;
			padding: 0;
			margin: 0;
			top: -60rpx;
			left: calc(50% - 60rpx);
			&::after {
				border: none;
			}

			.avatar {
				width: 120rpx;
				height: 120rpx;
				line-height: 120rpx;
				border-radius: 60rpx;
				font-size: 60rpx;
				color: #cccccc;
				background: #FFFFFF;
			}

			.tips {
				font-size: 24rpx;
				color: #999999;
			}
		}

		.input-wrapper {
			margin-top: 60rpx;
			.input-item {
				border-bottom: 1px solid #E5E5E5;
				margin-bottom: 50rpx;

				.label {
					font-size: 24rpx;
					color: #999999;
				}

				.input {
					padding: 15rpx 0 10rpx;
					font-size: 26rpx;
					color: #333333;
				}

				.phone {
					background: none;
					border: none;

					&::after {
						border: none;
					}

					display: flex;
					padding: 0;
					margin: 0;
					line-height: normal;
					position: relative;

					.input.empty {
						color: #808080;
					}

					.btn {
						position: absolute;
						bottom: 10rpx;
						right: 0;
						font-size: 20rpx;
						background-color: $uni-color-primary;
						color: #FFFFFF;
						border-radius: 30rpx;
						padding: 4rpx 15rpx;
					}
				}
			}
			
			.btns{
				margin-top:60rpx;
				display: flex;
				.btn{
					border-radius: 50rpx;
					flex: 1;
					margin-left: 20rpx;
					&:first-child{
						margin-left: 0;
					}
				}
				.submit {
					background-color: $uni-color-primary;
					color: #FFFFFF;
					
				}
				.cancel{
					&::after{
						border: none;
					}
				}
			}
		}
	}

	.auth-panel1{
		.body{
			background: #FFFFFF;
			border-radius: 12rpx;
			overflow: hidden;
			.banner{
				width: 100%;
				height: 176rpx;
			}
			.title{
				font-size: 36rpx;
				font-weight: 500;
				color: #000000;
				line-height: 36rpx;
				text-align: center;
				margin-top: 55rpx;
			}
			.desc{
				font-size: 28rpx;
				font-weight: 500;
				color: #A7A7A7;
				line-height: 36px;
				text-align: center;
				margin-top: 32rpx;
			}
			.btn{
				display: flex;
				justify-content: center;
				align-items: center;
				margin: 45rpx 70rpx 58rpx;
				border-radius: 50rpx;
				background: $uni-color-primary;
				color: #FFFFFF;
				display: flex;
				justify-content: center;
				.icon{
					font-size: 36rpx;
					margin-right: 10rpx;
				}
			}
		}
		.close-btn{
			display: flex;
			justify-content: center;
			margin-top: 50rpx;
			.icon-btn{
				width: 30rpx;
				height: 30rpx;
				line-height: 30rpx;
				text-align: center;
				color: #FFFFFF;
				border: 1rpx solid #FFFFFF;
				border-radius: 30rpx;
				font-size: 12rpx;
				font-weight: bold;
			}
		}
	}

</style>
