<template>
	<container class="root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="way-container">
				<!-- <view class="way-panel" @click="wayPick">
					<view class="type-item">
						<image class="img" :src="way[wayIndex].image"></image>
						<text class="text">{{way[wayIndex].name}}</text>
					</view>
					<view class="num-item" :class="{'small': way[wayIndex].name == 'USDT'}">
						<text class="text">{{way[wayIndex].cardNo}}</text>
					</view>
					<view class="account-name">{{way[wayIndex].cardName}}</view>
					<i class="arrow ri-arrow-right-s-line"></i>
				</view> -->
				<view class="way-panel">
					<view class="num-item pt30">
						<view class="text">compte en ligne:</view>
						<view class="val">{{way[wayIndex].cardNo}}</view>
					</view>
					<view class="num-item">
						<view class="text">nom:</view>
						<view class="val">{{way[wayIndex].cardName}}</view>
					</view>
					<view class="num-item">
						<view class="text">solde du compte:</view>
						<view class="val">{{userInfo.leftCommissionMoney || 0 }}</view>
					</view>
				</view>
			</view>
			<view class="input-panel panel">
				<view class="input">
					<input class="val" @input="fpNumInput" v-model="money" :placeholder="$t('withdraw.value',{min:minVal})" />
					<text class="unit">{{$t('app.unit')}}</text>
				</view>
				<view class="input">
					<input class="val" type="password" v-model="pwd" :placeholder="$t('withdraw.pwd.placeholder')" />
				</view>
			</view>

			<button class="confirm-btn" type="primary" @click="confirmClick">{{$t('withdraw.confirm')}}</button>
			
			<view class="remark-panel">
				<view class="title">{{$t('withdraw.tips.title')}}</view>
				<rich class="content" code="withdraw"></rich>
			</view>
		</view>
	</container>
</template>

<script>
	export default {
		components: {},
		data() {
			return {
				minVal: 1000,
				maxVal: 500000,
				wayIndex: 0,
				way: [{
					id: 0,
					name: '',
					cardName: '',
					cardNo: '',
					image: ''
				}],
				money: '',
				pwd: '',
				userInfo: {}
			}
		},
		onLoad() {
			this.showPageContent()
		},
		onShow() {
			this.getData()
			this.$uni.getConfig('CASH_SETTING').then(res => {
				let config = JSON.parse(res)
				this.minVal = config.minCash
			})
			this.userInfo = uni.getStorageSync("user") || {}
			console.log(this.userInfo.pwd1)
			if(!this.userInfo.pwd1){
				uni.showToast({
					icon: 'none',
					title: this.$t('withdraw.pwd1')
				})
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/cashpwd/cashpwd'
					})
				}, 1000)
			}
		},
		methods: {
			fpNumInput(e) {
				const o = e.target;
				const inputRule = /[^\d]/g		
				this.$nextTick(function() {
					this.money = o.value.replace(inputRule , '');
				})
			},
			getData() {
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$get('/app/account/my/bank/list').then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						this.way = res.data.map(item => {
							return {
								id: item.id,
								name: item.cateName,
								cardName: item.name,
								cardNo: item.account,
								image: item.image
							}
						})
						if(this.way.length == 0){
							uni.showToast({
								icon: 'none',
								title: this.$t('withdraw.no.card')
							})
							setTimeout(() => {
								this.$goUrl('/pages/cardlist/cardlist')
							}, 3000)
							
						}
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
			wayPick() {
				uni.showActionSheet({
					itemList: this.way.map(item => {
						return item.name + '-' + item.cardName + '-' + item.cardNo
					}),
					success: res => {
						this.wayIndex = res.tapIndex
					}
				})
			},
			confirmClick() {
				if(this.way.length == 0){
					uni.showToast({
						icon: 'none',
						title: this.$t('withdraw.no.card')
					})
				}
				if (this.money == '') {
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.withdraw.money.blank')
					})
					return
				}
				if (isNaN(this.money)) {
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.withdraw.money.notANum')
					})
					return
				}
				if ((this.userInfo.level > 1 && this.money < this.minVal) || (this.userInfo.level > 1 && this.money > this.maxVal)) {
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.withdraw.money.min1000',{
								num: this.minVal
						})
					})
					return
				}
				if (this.pwd == '') {
					uni.showToast({
						icon: 'none',
						title: this.$t('withdraw.pwd.placeholder')
					})
					return
				}
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/account/withdraw', {
					cardId: this.way[this.wayIndex].id,
					money: this.money,
					pwd: this.pwd
				}).then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						uni.showToast({
							icon: "none",
							title: this.$t('withdraw.success')
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

<style lang="scss" scoped>
	.root-container {
		background-color: #fff;
		overflow-y: auto;
	}

	.container {
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);

		.way-container {
			padding: 0 10rpx;
		}

		.way-panel {
			position: relative;
			height: 360rpx;
			background: url('../../static/images/recharge_card.jpg') no-repeat center;
			background-size: 100% 100%;
			color: #fff;
			border-radius: 20rpx;
			margin-top: 30rpx;
			.type-item {
				display: flex;
				align-items: center;
				padding-top: 30rpx;
				padding-left: 85rpx;

				.img {
					width: 70rpx;
					height: 70rpx;
					border-radius: 100%;
					border: 4rpx solid #fff;
					background: #F8F1E6;
				}

				.text {
					margin-left: 20rpx;
					width: 600rpx;
					flex: 1;
					font-size: 32rpx;
				}
			}
			.pt30{
				padding-top: 110rpx;
			}
			.num-item {
				width: 580rpx;
				font-size: 36rpx;
				font-weight: 500;
				color: #FFFFFF;
				height: 60rpx;
				margin-left: 85rpx;
				word-wrap: break-word;
				display: flex;
				.text{
					width: 300rpx;
				}
				.val{
					height: 60rpx;
					line-height: 60rpx;
					color: #ff6300;
				}
				&.small {
					font-size: 28rpx;
				}
			}

			.account-name {
				padding-left: 85rpx;
				font-size: 28rpx;
				margin-top: 10rpx;
			}

			.arrow {
				position: absolute;
				top: 38rpx;
				right: 50rpx;
				font-size: 64rpx;
				color: rgba(255, 255, 255, .6);
			}
		}

		.input-panel {
			// background: #fff;
			padding: 0 30rpx 30rpx;

			.input {
				margin-bottom: 20rpx;
				display: flex;
				align-items: center;
				border-bottom: 1rpx solid rgba(0, 0, 0, .1);
				height: 88rpx;
				padding-bottom: 1rpx;
				font-size: 32rpx;
				font-weight: 700;
				background-color: #FFF;
				border-radius: 20rpx;
				padding-left: 10rpx;
				.val {
					font-size: 32rpx;
					flex: 1;
				}
			}
		}

		.confirm-btn {
			margin-top: 20rpx;
			width: 689rpx;
			background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
			border-radius: 41px;
			&::after{
				border: none;
			}
		}

		.remark-panel {
			margin-top: 20rpx;
			padding: 30rpx;
			font-size: 29rpx;
			color: #666666;
			line-height: 59rpx;
			.title{
				font-size: 35rpx;
				font-weight: bold;
				color: #282828;
			}
		}


	}
</style>
