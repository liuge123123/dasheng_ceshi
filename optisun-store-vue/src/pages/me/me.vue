<template>
	<container class="root-container" ref="container" :headerVisable="false" :status="pageStatus"
		:rightText="$t('me.changepwd')" :rightWidth="140" @clickRight="$goUrl('/pages/changepwd/changepwd')"
		@refresh="getData()">
		<view class="container">
			<view class="info">
				<image mode="aspectFit" class="head" src="../../static/images/logo.png"></image>
				<view class="info-wrap">
					<view class="item">
						<text class="color">{{ userInfo.grade.gradeName }}</text>
						
					</view>
					<view class="item">
						<text>Nom d'utilisateur:{{ userInfo.mobile }}</text>
					</view>
					<view class="item">
						<text>{{ $t('me.invite.code') }}：</text>
						<text class="green">{{ userInfo.custId }}</text>
						<text class="copy" @click="copyLink">{{ $t('me.copy') }}</text>
					</view>
				</view>
				<view class="upgrade-btn"><image class="level" :src="userInfo.grade ? userInfo.grade.image : ''" mode="aspectFit"></image></view>
			</view>
			<view class="account">
				<view class="item" style="width: 100%;">
					<view class="text">{{ $t('me.recharge.left') }}</view>
					<view class="val">{{ $t('app.unit') }} {{ userInfo.leftCommissionMoney ||0}} </view>
				</view>
				<view class="item">
					<view class="btn" @click="$goUrl('/pages/recharge/recharge')">{{ $t('me.recharge') }}</view>
				</view>
				<view class="item">
					<view class="btn"  @click="$goUrl('/pages/withdraw/withdraw')">{{ $t('me.withdraw') }}</view>
				</view>
				<!-- <view class="item">
					<view class="text">{{ $t('me.recharge.left') }}</view>
					<view class="val">{{ $t('app.unit') }}{{ userInfo.leftRechargeMoney }} </view>
					<view class="btn" @click="$goUrl('/pages/recharge/recharge')">{{ $t('me.recharge') }}</view>
				</view>
				<view class="item">
					<view class="text">{{ $t('me.total.withdraw') }}</view>
					<view class="val">{{ $t('app.unit') }}{{ userInfo.leftCommissionMoney }} </view>
					<view class="btn"  @click="$goUrl('/pages/withdraw/withdraw')">{{ $t('me.withdraw') }}</view>
				</view> -->
			</view>

			<!-- <view class="account-actions">
				<view class="btn" @click="$goUrl('/pages/recharge/recharge')">{{ $t('me.recharge') }}</view>
				<view class="btn" style="background: #61be49;" @click="$goUrl('/pages/withdraw/withdraw')">
					{{ $t('me.withdraw') }}</view>
			</view> -->
			<!-- <view class="progress">
				<view class="text">{{ $t('me.level.change') }}: {{ userInfo.personCftMoney }}/{{ userInfo.grade.parameter1 }}</view>
				<progress :percent="userInfo.personCftMoney/userInfo.grade.parameter1*100" stroke-width="10" borderRadius="30"></progress>
			</view> -->

			<!-- 			<view class="total">
				<view class="item">
					<image mode="aspectFit" class="icon" src="../../static/images/me_profit.png"></image>
					<view class="val">{{(userInfo.totalCommissionMoney).toFixed(2) || 0}} {{$t('app.unit')}}</view>
					<view class="text">{{$t('me.total.profit')}}</view>
				</view>
				<view class="item">
					<image mode="aspectFit" class="icon" src="../../static/images/me_withdraw.png"></image>
					<view class="val">{{commmissionMoney || 0}} {{$t('app.unit')}}</view>
					<view class="text">{{$t('me.total.withdraw')}}</view>
				</view>
				<view class="item">
					<image mode="aspectFit" class="icon" src="../../static/images/me_team.png"></image>
					<view class="val">{{userInfo.teamCommissionMoney.toFixed(2) || 0}} {{$t('app.unit')}}</view>
					<view class="text">{{$t('me.total.team')}}</view>
				</view>
				<view class="item">
					<image mode="aspectFit" class="icon" src="../../static/images/me_vip.png"></image>
					<view class="val">{{((userInfo.totalCommissionMoney - userInfo.teamCommissionMoney)).toFixed(2) || 0}} {{$t('app.unit')}}</view>
					<view class="text">{{$t('me.total.reward')}}</view>
				</view>
			</view> -->

			<view class="func-list">
				<view class="func-item first" @click="$goUrl('/pages/cardlist/cardlist')">
					<image class="icon" src="../../static/images/me_func_bank.png"></image>
					<text class="text">{{ $t('me.func.addcard') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view>
				<view class="func-item" @click="$goUrl('/pages/account/account')">
					<image class="icon" src="../../static/images/me_func_account.png"></image>
					<text class="text">{{ $t('me.func.account') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view>
				<view class="func-item last" @click="$goUrl('/pages/order/order')">
					<image class="icon" src="../../static/images/me_func_order.png"></image>
					<text class="text">{{ $t('me.func.history') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view>
				<!-- 	<view class="func-item" @click="$goUrl('/pages/cft/cft')">
					<image class="icon" src="../../static/images/me_func_cft.png"></image>
					<text class="text">{{$t('me.func.cft')}}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view> -->
			</view>
			<view class="func-list">
				<!-- <view class="func-item" @click="$goUrl('/pages/level/level')">
					<image class="icon" src="../../static/images/me_func_level.png"></image>
					<text class="text">{{ $t('me.func.level') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view> -->
				<view class="func-item" @click="$goUrl('/pages/share/share')">
					<image class="icon" src="../../static/images/me_func_share.png"></image>
					<text class="text">{{ $t('me.func.share') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view>
				<!-- <view class="func-item" @click="$goUrl('/pages/guide/guide')">
					<image class="icon" src="../../static/images/me_func_guide.png"></image>
					<text class="text">{{ $t('me.func.guide') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view> -->

			</view>
			<view class="func-list">
				
				<!-- <view class="func-item" @click="$goUrl('/pages/download/download')">
					<image class="icon" src="../../static/images/me_func_download.png"></image>
					<text class="text">{{ $t('me.func.download') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view> -->
				<view class="func-item" @click="$goUrl('/pages/intro/intro')">
					<image class="icon" src="../../static/images/me_func_about.png"></image>
					<text class="text">{{ $t('me.func.about') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view>
				<view class="func-item" @click="$goUrl('/pages/changepwd/changepwd')">
					<image class="icon" src="../../static/images/me_func_changepwd.png"></image>
					<text class="text">{{ $t('me.changepwd') }}</text>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view>
			</view>
			<view class="actions">
				<button class="logout-btn" type="primary" @click="logout">{{ $t('me.logout') }}</button>
			</view>

		</view>
		<toolbar :index="5"></toolbar>
	</container>
</template>

<script>
import { setClipboardData, getClipboardData } from '@/uni_modules/u-clipboard/js_sdk'
export default {
	components: {
	},
	data() {
		return {
			userInfo: {
				grade:{
					gradeName:''
				}
			},
			onlineService: {},
			commmissionMoney: 0,
			rewardMoney: 0
		}
	},
	onShow() {
		this.getData()
		// this.getCommmissionMoney()
		this.getRewardMoney()
		this.$uni.getUserInfo().then(user => {
			this.userInfo = user
		})
	},
	onLoad() {
		// this.userInfo = uni.getStorageSync("user") || {}
		// this.$uni.getOnlineService(this.userInfo.custId).then(data => {
		// 	this.onlineService = data
		// })
	},
	methods: {
		formatMoney(val) {
			if (!val || val == '') {
				return 0
			}

			return Math.floor(val)
		},
		getCommmissionMoney() {
			this.$get('/app/account/commmissionMoney').then(res => {
				this.commmissionMoney = res.data
			})
		},
		getRewardMoney() {
			this.$get('/app/account/rewardMoney').then(res => {
				this.rewardMoney = res.data
			})
		},
		getData() {
			this.showPageContent()
		},
		downloadClick() {
			let platform = uni.getSystemInfoSync().platform
			if (platform == 'ios') {
				console.log('我是iOS')
				window.location.href = 'https://apps.apple.com/cn/app/openai/id6443512141'
			} else {
				console.log('我是android')
				window.location.href = 'https://www.openai.bar/OpenAI.apk'
				// uni.showToast({
				// 	title: 'Téléchargement Android non pris en charge pour le moment',
				// 	icon: 'none'
				// })
			}
		},
		logout() {
			uni.showLoading({
				mask: true,
				title: 'loading...'
			})
			this.$post('/app/logout').then(res => {
				uni.hideLoading()
				if (res.code == 0) {
					this.$goUrl('/pages/login/login')
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
		copyLink() {
			let protocol = window.location.protocol
			let host = window.location.host
			let url = `${protocol}//${host}/#/pages/login/login?inviteCode=${this.userInfo.custId}&pageIndex=2`
			setClipboardData(url).then(res => {
				uni.showToast({
					icon: 'success',
					title: this.$t('share.copy.success')
				})
			}).catch(res => {

			})
		}
	}
}
</script>

<style lang="scss" scoped>
/deep/ .uni-progress-bar {
	border-radius: 20rpx;
}

/deep/ .uni-progress-inner-bar {
	border-radius: 20rpx;
}

.container {
	padding: 30rpx;
	margin-bottom: 100px;
	position: relative;

	&::before {
		content: '';
		position: absolute;
		top: 0;
		right: 0;
		left: 0;
		height: 360rpx;
		background: linear-gradient(0deg, #1B191C 0%, #3D3C3F 100%);
		border-radius: 0px 0px 30px 30px;
	}

	.info {
		padding: 70rpx 30rpx 0;
		border-radius: 16rpx;
		display: flex;
		position: relative;
		color: #fff;

		.upgrade-btn {
			position: absolute;
			// background: linear-gradient(-26deg, #7C521E 23%, #CD9856 100%);
			border-radius: 30rpx;
			right: 0;
			top: 50rpx;
			font-size: 24rpx;
			padding: 8rpx 20rpx;
			.level {
				width: 120rpx;
				height: 120rpx;
			}
		}

		.head {
			width: 100rpx;
			height: 100rpx;
			border-radius: 50%;
			background: #fff;
			border: 1px solid #fff;
			padding: 20rpx;
		}

		.info-wrap {
			padding-left: 20rpx;
			flex: 1;

			.item {
				display: flex;
				align-items: center;
				margin-top: 18rpx;
				font-size: 24rpx;
				&:first-child {
					margin-top: 0;
				}
				.color{
					color: #fe9901;
				}

				.help {
					color: rgb(202, 161, 96);
					font-size: 48rpx;
					margin-left: 10rpx;
				}

				.green {
					color: #11FF5A;

				}

				.copy {
					margin-left: 10px;
					text-decoration: underline;
				}
			}
		}
	}

	.account {
		background: linear-gradient(90deg, #ff9700, #ff6300);
		margin-top: 40rpx;
		display: flex;
		justify-content: space-between;
		position: relative;
		border-radius: 12rpx;
		overflow: hidden;
		flex-wrap: wrap;
		height: 200rpx;
		margin-bottom: 20rpx;

		.item {
			// flex: 1;
			width: 40%;
			margin: auto;
			// height: 100rpx;
			// background: url('@/static/images/me_panel1.jpg') center;
			// background-size: cover;
			display: flex;
			flex-direction: column;
			justify-content: center;
			position: relative;
			text-align: center;
			margin-top: 10rpx;

			&:last-child {
				// background: url('@/static/images/me_panel2.png') center;
				// background-size: cover;
			}

			.val {
				margin-top: 10rpx;
				font-size: 28rpx;
				font-weight: bold;
				color: #1c1a1ad1;
			}

			.val1 {
				font-size: 24rpx;
				font-weight: bold;
				color: #1c1a1ad1;
			}

			.text {
				font-size: 24rpx;
				font-weight: 500;
				color: #1c1a1ad1;
				margin-top: 5rpx;
			}

			.btn {
				margin-top: 20rpx;
				border: 1px solid #1c1a1ad1;
				line-height: 50rpx;
				border-radius: 20px;
			}
		}
	}

	.progress {
		width: 90%;
		margin: auto;
		height: 100rpx;

		.text {
			color: #FFF;
			font-size: 24rpx;
			margin-bottom: 10rpx;
		}
	}

	.account-actions {
		position: relative;
		display: flex;
		justify-content: space-between;
		margin-top: 33rpx;
		margin-bottom: 35rpx;

		.btn {
			flex: 1;
			height: 80rpx;
			line-height: 80rpx;
			text-align: center;
			// background: linear-gradient(-26deg, #7C521E 23%, #CD9856 100%);
			background: linear-gradient(90deg, #ff9700, #ff6300);
			border-radius: 19px;
			font-size: 32rpx;
			font-weight: 500;
			color: #FFFFFE;

			&:first-child {
				margin-right: 30rpx;
			}
		}
	}

	.total {
		position: relative;
		display: flex;
		justify-content: space-between;
		margin-top: 33rpx;
		margin-bottom: 35rpx;

		.item {
			width: 160rpx;
			height: 160rpx;
			background: linear-gradient(0deg, #2C2A32 0%, #35333F 100%);
			box-shadow: 0px 3px 10px 0px rgba(110, 109, 123, 0.5);
			border-radius: 16px;
			display: flex;
			flex-direction: column;
			justify-content: center;
			align-items: center;

			.icon {
				width: 64rpx;
				height: 64rpx;
				object-fit: fill;
			}

			.val {
				font-size: 24rpx;
				font-weight: 500;
				color: #FFD087;
				white-space: nowrap;
				transform: scale(0.9);
			}

			.text {
				font-size: 24rpx;
				transform: scale(0.8);
				color: #A8A7AD;
				white-space: nowrap
			}
		}
	}

	.func-list {
		margin-bottom: 16rpx;
		// background: #FFFFFF;
		border-radius: 20rpx;

		.first{
			border-top-left-radius: 20rpx;
			border-top-right-radius: 20rpx;
		}
		.last{
			border-bottom-left-radius: 20rpx;
			border-bottom-right-radius: 20rpx;
		}

		.func-item {
			
			background-color: #333333;
			font-size: 28rpx;
			color: rgb(51, 51, 51);
			padding: 26rpx 30rpx;
			display: flex;
			align-items: center;
			position: relative;

			.icon {
				width: 50rpx;
				height: 50rpx;
			}

			.text {
				flex: 1;
				margin: 0 10rpx;
				font-size: 24rpx;
				font-weight: 500;
				color: #fe9901;
			}

			.arrow {
				font-size: 42rpx;
				color: #A58D75;
			}
		}
	}

	.actions {
		margin-top: 60rpx;
		display: flex;

		.logout-btn {
			padding: 0;
			line-height: 80rpx;
			background: linear-gradient(90deg, #ff9700, #ff6300);
			// background: #fe9901;
			font-size: 24rpx;
			flex: 1;
			border-radius: 20px;

			&:last-child {
				margin-left: 20rpx;
				font-size: 32rpx;
			}
		}
	}

}
</style>
