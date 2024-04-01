<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container1" v-if="page == 1">
			<view class="top">
				<view class="top1">
					<view class="text">{{$t('recharge.name')}} :</view>
					<view class="val">{{userInfo.custName}}</view>
				</view>
				<view class="top1">
					<view class="text">{{$t('recharge.left')}} :</view>
					<view class="val">{{userInfo.leftCommissionMoney || 0}}</view>
				</view>
			</view>
			<view class="input">
				<view class="unit">{{way[wayIndex].letter}}</view>
				<input class="input" :placeholder="$t('recharge.money.value')" v-model="money" />
			</view>
			<view class="money">
				<view class="item" :class="{'active': money == item.money}" v-for="(item, index) in moneyList" :key="index"
					@click="money = item.money">{{item.money}}</view>
			</view>

			<view class="way">
				<view class="item" v-for="(item, index) in way" :key="index" @click="wayIndex = index">
					<image class="icon" :src="item.image"></image>
					<view class="text">{{item.name}}</view>
					<image v-if="index == wayIndex" class="check" src="../../static/images/check_1.png"></image>
					<image v-else class="check" src="../../static/images/check_0.png"></image>
				</view>
			</view>

			<button class="comfirm" @click="getCard">{{$t('recharge.go')}}</button>

			<view class="desc">
				<view class="title">{{$t('recharge.tips')}}</view>
				<view class="content">
					<rich code="recharge"></rich>
				</view>
			</view>

		</view>
		<view class="container2" v-if="page == 2">
			<view class="account-panel">
				<view class="type-item">
					<image class="img" :src="bank.image"></image>
					<text class="text">{{bank.showName}}</text>
					<text class="copy" @click="copyCardNo(bank.cardNo)">{{$t('recharge.account.copy')}}</text>
				</view>
				<view class="num-item" :class="{'small': bank.letter == 'USDT'}" @click="copyCardNo(bank.cardNo)">
					<text class="text">{{bank.cardNo}}</text>
				</view>
			</view>
			<view class="input-panel">
				<view class="item">
					<text class="title">{{$t('recharge.money.value')}}：</text>
					<text class="val">{{money}} {{bank.letter}}</text>
				</view>
				<view class="item">
					<text class="title">{{$t('recharge.money.real')}}：</text>
					<text class="val">{{money * bank.fee}} {{bank.letter}}</text>
				</view>
			</view>
			<view class="id-panel">
				<view class="title" v-if="this.way[this.wayIndex].name == 'Zamani'">
					<text>Veuillez saisir votre numéro de transfert</text>
				</view>
				<view class="title" v-else>
					<text>{{$t('recharge.trans.id')}}</text>
				</view>
				<view class="input" v-if="this.way[this.wayIndex].name == 'Zamani'">
					<input class="val" type="number" v-model="transCode" placeholder="Numéro de transfert" />
				</view>
				<view class="input" v-else>
					<input class="val" v-model="transCode" :placeholder="$t('recharge.trans.placeholder')" />
				</view>
				<!-- <view class="title mt20" v-if="this.way[this.wayIndex].name == 'EVCPlus'">
					<text>{{$t('recharge.trans.date')}}</text>
				</view>
				<view class="input" v-if="this.way[this.wayIndex].name == 'EVCPlus'">
					<input class="val" v-model="transDate" :placeholder="$t('recharge.trans.dateplaceholder')" />
				</view> -->
			</view>
			<button class="confirm-btn" type="primary" @click="confirmClick">{{$t('recharge.confirm')}}</button>
			<view class="desc">
				<view class="title">{{$t('recharge.tips')}}</view>
				<view class="content">
					<rich code="recharge"></rich>
				</view>
			</view>
		</view>
	</container>
</template>

<script>
	import {
		setClipboardData,
		getClipboardData
	} from '@/uni_modules/u-clipboard/js_sdk'

	export default {
		components: {},
		data() {
			return {
				page: 1,
				minVal: 10000,
				wayIndex: 0,
				way: [{
					id: 0,
					name: '',
					letter: '',
					cardNo: '',
					image: '',
					fee: 1
				}],
				money: 10000,
				transCode: '',
				transDate:'',
				remark: "",
				moneyList: [],
				userInfo: null,
				bank: {}
			}
		},
		onLoad() {
			this.showPageContent()
			this.$uni.getConfig('RECHARGE_SETTING').then(data => {
				this.minVal = parseFloat(data)
			})
			this.getRemark()
			this.getMoneyList()
		},
		onShow() {
			this.getData()
			this.userInfo = uni.getStorageSync("user") || {}
			console.log(this.userInfo)
		},
		methods: {
			getMoneyList(){
				this.$get('/app/common/config', {
					key: 'recharge.config'
				}).then(res => {
					if(res.code == 0){
						this.moneyList = JSON.parse(res.data)
					}
				})
			},
			getCard(){
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$get('/app/account/bank/listByMoney', {
					bankCode: this.way[this.wayIndex].name,
					money: this.money
				}).then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						this.bank = res.data
						this.page = 2
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
			copyCardNo(cardNo) {
				setClipboardData(cardNo).then(res => {
					uni.showToast({
						icon: 'success',
						title: this.$t('share.copy.success')
					})
				}).catch(res => {

				})
			},
			getRemark() {
				this.$uni.getContent('recharge').then(res => {
					this.remark = res.content
				})
			},

			getData() {
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$get('/app/account/bank/cate/list', {
					rechargeOpen: 1
				}).then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						this.way = res.data.map(item => {
							return {
								id: item.id,
								name: item.name,
								letter: item.letter,
								cardNo: item.cardNo,
								image: item.logo,
								fee: item.fee
							}
						})
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
						return item.name
					}),
					success: res => {
						this.wayIndex = res.tapIndex
					}
				})
			},
			confirmClick() {
				this.money = (this.money + '').trim()
				this.transCode = this.transCode.trim()
				if (this.money == '') {
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.rechare.money.blank')
					})
					return
				}
				if (isNaN(this.money)) {
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.rechare.money.notANum')
					})
					return
				}
				if (this.money * this.way[this.wayIndex].fee < this.minVal) {
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.rechare.money.min1000')
					})
					return
				}
				if (this.transCode == '') {
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.rechare.transcode.blank')
					})
					return
				}
				if (this.way[this.wayIndex].name == 'EVCPlus' && this.transDate == '') {
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.rechare.transdate.blank')
					})
					return
				}
				// if (this.way[this.wayIndex].name == 'Orange' && this.transCode.length != 20) {
				// 	uni.showToast({
				// 		icon: 'none',
				// 		title: this.$t('error.100038')
				// 	})
				// 	return
				// }
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/account/recharge', {
					bankId: this.bank.id,
					money: this.money,
					transCode: this.transCode,
					transDate: this.transDate
				}).then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						uni.showToast({
							icon: "none",
							title: this.$t('recharge.confirm.success')
						})
						setTimeout(e => {
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
		background: #fff;
		overflow-y: auto;
	}
	.mt20{
		margin-top: 20rpx;
	}
	.container1 {
		padding: 30rpx;
		background: #FFF;
		.top {
			height: 320rpx;
			background: url('../../static/images/recharge_top_bg.jpg');
			background-size: cover;
			display: flex;
			flex-direction: column;
			justify-content: center;
			// align-items: center;
			border-radius: 20rpx;
			.mt30{
				margin-top: 30rpx;
			}
			.top1{
				display: flex;
				height: 50rpx;
				line-height: 50rpx;
			}
			.text {
				width: 260rpx;
				margin-left: 50rpx;
				font-size: 29rpx;
				font-weight: 500;
				color: #FFFFFF;
				opacity: 0.8;
			}

			.val {
				flex:1;
				font-weight: bold;
				color: #FFFFFF;
				background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
				-webkit-background-clip: text;
				-webkit-text-fill-color: transparent;
			}
		}

		.input {
			display: flex;
			height: 117rpx;
			line-height: 117rpx;
			border-bottom: 1rpx solid #e6e6e6;
			font-size: 32rpx;

			.unit {}

			.input {
				flex: 1;
				text-align: right;
				padding-left: 20rpx;
			}
		}

		.money {
			display: flex;
			flex-wrap: wrap;
			justify-content: space-between;
			margin-top: 40rpx;

			.item {
				width: 210rpx;
				height: 100rpx;
				line-height: 100rpx;
				text-align: center;
				background: #FFFFFF;
				border: 2px solid #E6E6E6;
				margin-bottom: 20rpx;
				font-size: 30rpx;
				font-weight: 500;
				color: #666666;

				&.active {
					background: #F8F1E6;
					border: 2px solid #E8CEAA;
					color: #1ec8db;
				}
			}
		}

		.way {
			.item {
				display: flex;
				align-items: center;
				margin-top: 34rpx;

				.icon {
					width: 42rpx;
					height: 42rpx;
					object-fit: cover;
				}

				.text {
					flex: 1;
					margin: 0 20rpx;
					font-size: 30rpx;
					color: #333333;
				}

				.check {
					width: 32rpx;
					height: 32rpx;
				}
			}
		}

		.comfirm {
			height: 88rpx;
			background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
			border-radius: 44rpx;
			font-size: 34rpx;
			color: #FFFFFF;
			margin-top: 42rpx;

			&::after {
				border: none;
			}
		}

		.desc {
			margin-top: 65rpx;

			.title {
				font-size: 35rpx;
				font-weight: bold;
				color: #282828;
			}

			.content {
				font-size: 29rpx;
				color: #666666;
				line-height: 59rpx;
				margin-top: 33rpx;
			}
		}
	}



	.container2 {
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);

		.account-panel {
			height: 373rpx;
			background: url('../../static/images/recharge_card.png');
			background-size: 100% 100%;
			color: #fff;
			margin: 30rpx 10rpx;
			
			.type-item {
				display: flex;
				align-items: center;
				padding-top: 52rpx;
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
				.copy{
					color: #FFC36D;
					padding-right: 85rpx;
				}
			}
			.num-item{
				text-align: center;
				width: 580rpx;
				font-size: 60rpx;
				font-weight: 600;
				color: #FFFFFF;
				margin-top: 60rpx;
				margin-left: 85rpx;
				word-wrap: break-word;
				&.small{
					font-size: 28rpx;
				}
			}
		}

		.input-panel {
			
			padding: 30rpx;
			background: url('../../static/images/recharge_card.png');
			background-size: 100% 100%;
			border-radius: 20rpx;
			.item{
				display: flex;
				align-items: center;
				justify-content: space-between;
				font-size: 32rpx;
				font-weight: 500;
				color: #FFF;
				border-bottom: 1px solid #E6E6E6;
				line-height: 80rpx;
				margin-top: 20rpx;
				.val{
					// color: #454545;
				}
			}
		}

		.id-panel {
			padding: 30rpx;
			.title{
				font-size: 32rpx;
				color: grey;
			}
			.input {
				margin-top: 20rpx;
				height: 102rpx;
				background: #FBFBFB;
				border: 1rpx solid #E6E6E6;
				border-radius: 6rpx;
				.val {
					font-size: 32rpx;
					line-height: 102rpx;
					height: 102rpx;
					padding: 0 40rpx;
				}
			}
		}

		.confirm-btn {
			margin: 30rpx;
			// background: linear-gradient(0deg, #FFFFFF 0%, #000000 0%, #C39155 0%, #E9D1A0 100%);
			background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
			border-radius: 44px;
			&::after{
				border: none;
			}
		}
		
		.desc {
			margin-top: 65rpx;
			padding: 0 30rpx 30rpx;
		
			.title {
				font-size: 35rpx;
				font-weight: bold;
				color: #282828;
			}
		
			.content {
				font-size: 29rpx;
				color: #666666;
				line-height: 59rpx;
				margin-top: 33rpx;
			}
		}
	}
</style>
