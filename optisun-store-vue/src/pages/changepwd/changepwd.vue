<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="nav">
			<view class="item active">{{$t('changepwd.login')}}</view>
			<view class="item" @click="goCashPwd">{{$t('changepwd.withdraw')}}</view>
		</view>
		<view class="container">
			<input class="input" v-model="oldPwd" type="passowd" :placeholder="$t('changepwd.oldpwd')" />
			<input class="input" v-model="newPwd" type="passowd" :placeholder="$t('changepwd.newpwd')" />
			<input class="input" v-model="confirmPwd" type="passowd" :placeholder="$t('changepwd.confirmpwd')" />
			<button class="btn" type="primary" @click="confirmClick">{{$t('changepwd.confirm')}}</button>
		</view>
	</container>
</template>

<script>
	export default {
		data() {
			return {
				newPwd: '',
				confirmPwd: '',
				oldPwd: ''
			}
		},
		onLoad() {
			this.getData()
		},
		methods: {
			goCashPwd(){
				uni.redirectTo({
					url: '/pages/cashpwd/cashpwd'
				})
			},
			getData() {
				this.showPageContent()
			},
			confirmClick() {
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/changepwd', {
					newPwd: this.newPwd,
					confirmPwd: this.confirmPwd,
					oldPwd: this.oldPwd
				}).then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						uni.showToast({
							icon: 'none',
							title: this.$t('changepwd.success')
						})
						setTimeout(res => {
							uni.navigateBack()
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
			}
		}
	}
</script>

<style scoped lang="scss">
	.nav {
		display: flex;
		justify-content: center;
		padding: 21rpx;
	
		.item {
			height: 70rpx;
			line-height: 70rpx;
			padding: 0 25rpx;
			font-size: 24rpx;
			background: #FFFFFF;
			border-radius: 6rpx 0px 0px 6rpx;
			&:last-child{
				border-radius: 0 6px 6px 0;
			}
			&.active {
				background: #C9975A;
				color: #FFFFFF;
			}
		}
	}

	.container {
		padding: 30rpx;

		.input {
			color: #000;
			border: 1rpx solid #ddd;
			padding: 20rpx;
			margin-bottom: 30rpx;
			border-radius: 16rpx;
		}

		.btn {
			background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
			border-radius: 50rpx;
			font-size: 32rpx;
			&::after{
				border: 0;
			}
		}
	}
</style>
