<template>
	<container ref="container" headerBackGroudColor="#3D3C3F" color="#ffffff" :headerBorder="false" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="header">
				<view class="wrap">
					<view class="val">{{userInfo.pointLeft || 0}}</view>
					<view class="text">{{$t('sign.pointleft')}}</view>
				</view>
			</view>
			<view class="sign-list">
				<view class="title" style="display: flex;justify-content: space-between;">
					<view v-html="$t('sign.totalnum', {num: userInfo.totalSignIn || 0})"></view>
				</view>
				<view class="list">
					<view class="item" v-for="(item, index) in signList" :key="index">
						<view class="val">
							<i v-if="item.pointNum == 0" class="ri-check-fill"></i>
							<text v-else>+{{item.pointNum}}</text>
						</view>
						<view class="day">{{item.time}}</view>
					</view>
				</view>
				<view class="footer">
					<view class="rule" @click="openPopup"><i class="ri-information-line" style="font-size: 24rpx; margin-right: 5rpx;"></i> {{$t('sign.rule')}}</view>
					<view class="btn" @click="signinNow" v-if="signBtnVis">
						{{$t('sign.now')}}
					</view>
				</view>
			</view>
			
			<view class="gift-list" v-if="rewardList && rewardList.length > 0">
				<view class="title">{{$t('sign.gift.title')}}</view>
				<view class="list">
					<view class="item" v-for="(item, index) in rewardList" :key="index">
						<view class="day">{{item.days}} {{$t('sign.gift.day')}}</view>
						<view class="icon">
							<!-- <i class="ri-gift-fill"></i> -->
							<image class="banner" src="../../static/images/sign_gift.png" style="width: 60rpx;" mode="widthFix"></image>
						</view>
						<view class="val">{{item.num}} {{$t('app.unit')}}</view>
					</view>
				</view>
			</view>
			
			<view class="product-list">
				<view class="title">{{$t('sign.point.title')}}</view>
				<view class="list">
					<view class="item" v-for="(item, index) in productList" :key="index">
						<image class="img" :src="item.productImg" mode="aspectFit"></image>
						<view class="wrap">
							<view class="name">{{item.productName}}</view>
							<view class="progress">
								<progress class="bar" :percent="item.progress" active :border-radius="20" activeColor="#CD9856" :stroke-width="15"></progress>
								<view class="val">{{item.progress}}%</view>
								<view class="btn" v-if="item.progress < 100" @click="pointExchange(item)">{{$t('sign.point.btn.exchange', {point: item.productPrice})}}</view>
								<view class="btn" v-if="item.progress == 100" @click="pointExchange(item)">{{$t('sign.point.btn.success')}}</view>
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<uni-popup ref="rulePopup">
			<view class="rule-popup">
				<view class="title">{{$t('sign.rule')}}</view>
				<view class="content">
					<rich code="sign"></rich>
				</view>
				<view class="footer" @click="closePopup">
					{{$t('sign.close')}}
				</view>
			</view>
		</uni-popup>
		
	</container>
</template>

<script>
	export default {
		components: {
	
		},
		data() {
			return {
				userInfo: {},
				signList: [],
				rewardList: [],
				productList: []
			}
		},
		computed: {
			today(){
				return this.$uni.timeFormat(new Date().getTime(), 'mm.dd')
			},
			signBtnVis(){
				let today = this.today
				if(this.signList && this.signList.length > 0){
					return this.signList[this.signList.length - 1].time != today
				}
				return true
			}
		},
		onLoad() {
			this.showPageContent()
			this.userInfo = uni.getStorageSync("user") || {}
			this.getSignRecords()
			this.getSignReward()
			this.getPointProduct()
		},
		onShow() {
			this.getUserInfo()
		},
		methods: {
			openPopup(){
				this.$refs.rulePopup.open()
			},
			closePopup(){
				this.$refs.rulePopup.close()
			},
			getUserInfo(){
				this.$uni.getUserInfo().then(user => {
					this.userInfo = user
				})
			},
			getSignRecords() {
				this.$get('/app/act/signList').then(res => {
					if (res.code == 0) {
						this.signList = res.data
					}
				})
			},
			getSignReward() {
				this.$get('/app/act/signReward').then(res => {
					if (res.code == 0) {
						this.rewardList = res.data
					}
				})
			},
			getPointProduct() {
				this.$get('/app/act/pointProduct').then(res => {
					if (res.code == 0) {
						this.productList = res.data
					}
				})
			},
			signinNow() {
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/act/sign').then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						this.getUserInfo()
						this.getSignRecords()
						uni.showToast({
							icon: "none",
							title: this.$t('sign.success')
						});
					} else {
						uni.showToast({
							icon: "none",
							title: this.$t('error.' + res.code),
						});
					}
				})
			},
			pointExchange(progress){
				if(progress.progress == 100){
					uni.showToast({
						icon: "none",
						title: this.$t('sign.point.success')
					})
					return
				}
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/act/pointExchange', {
					productId: progress.productId
				}).then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						this.getUserInfo()
						this.getPointProduct()
						uni.showToast({
							title: this.$t('sign.success')
						});
					} else {
						uni.showToast({
							icon: "none",
							title: this.$t('error.' + res.code),
						});
					}
				})
			}
		}
	}
</script>

<style lang="scss" scoped>
	.container{
		.header{
			height: 257rpx;
			background: linear-gradient(0deg, #1B191C 0%, #3D3C3F 100%);
			border-radius: 0px 0px 30px 30px;
			display: flex;
			text-align: center;
			padding: 30rpx;
			color: #fff;
			.wrap{
				flex: 1;
				.val{
					font-size: 61rpx;
					font-weight: bold;
					color: #FFFFFF;
					background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
					-webkit-background-clip: text;
					-webkit-text-fill-color: transparent;
				}
				.text{
					font-size: 27rpx;
					color: #E7C19F;
					opacity: 0.6;
				}
			}
		}
		.sign-list{
			margin-left: 30rpx;
			margin-right: 30rpx;
			margin-top: -100rpx;
			background: #FFFFFF;
			border-radius: 20rpx;
			padding: 30rpx;
			box-shadow: 0 2px 12px 2px rgba(233,214,164,0.5);
			.title{
				font-size: 24rpx;
				padding-bottom: 30rpx;
				border-bottom: 1rpx dashed #ccc;
			}
			.list{
				display: flex;
				padding-top: 50rpx;
				padding-bottom: 20rpx;
				.item{
					font-size: 24rpx;
					margin-right: 55rpx;
					text-align: center;
					&:last-child{
						margin-right: 0;
					}
					.val{
						width: 80rpx;
						height: 80rpx;
						line-height: 80rpx;
						border-radius: 50%;
						border: 1rpx solid #1ec8db;
						color: #1ec8db;
						// background: rgba(233,214,164,0.2);
						text-align: center;
						display: flex;
						align-items: center;
						justify-content: center;
						&.btn{
							line-height: 30rpx;
							background: #D19C69;
							color: #FFFFFF;
						}
					}
					.day{
						color: #999;
						margin-top: 10rpx;
					}
				}
			}
			.footer{
				text-align: center;
				border-top: 1rpx solid #eee;
				padding-top: 20rpx;
				.rule{
					font-size: 24rpx;
					color: #C49050;
				}
				.btn{
					height: 80rpx;
					line-height: 80rpx;
					background: #1ec8db;
					border-radius: 40rpx;
					color: #fff;
					margin-top: 30rpx;
				}
			}
		}
		.gift-list{
			margin: 30rpx 30rpx 0;
			padding: 30rpx;
			background: linear-gradient(180deg, rgba(#E9D1A0, 0.5) 0%, #fff 50%, #fff 10%);
			box-shadow: 0 2px 12px 2px rgba(233,214,164,0.5);
			border-radius: 20rpx;
			.title{
				font-size: 28rpx;
				font-weight: bold;
				margin-bottom: 30rpx;
			}
			.list{
				display: flex;
				text-align: center;
				font-size: 24rpx;
				.item{
					flex: 1;
					position: relative;
					&::before{
						content: '';
						position: absolute;
						left: 60%;
						top: 48%;
						width: 100%;
						height: 4rpx;
						background-color: #EAD2AF;
					}
					&:last-child{
						&::before{
							height: 0 !important;
						}
					}
					.icon{
						font-size: 64rpx;
						color: #AE7735;
						position: relative;
					}
					.day{
						color: #999;
					}
					.val{
						color: #AE7735;
					}
				}
			}
		}
		.product-list{
			margin: 30rpx;
			padding: 30rpx;
			background: #FFFFFF;
			box-shadow: 0 2px 12px 2px rgba(233,214,164,0.5);
			border-radius: 20rpx;
			.title{
				font-size: 28rpx;
				font-weight: bold;
				margin-bottom: 30rpx;
			}
			.list{
				.item{
					display: flex;
					margin-bottom: 30rpx;
					border-bottom: 1rpx solid #f8f8f8;
					padding-bottom: 30rpx;
					.img{
						width: 140rpx;
						height: 140rpx;
						object-fit: contain;
						background: #f8f8f8;
						border-radius: 8rpx;
					}
					.wrap{
						margin-left: 20rpx;
						flex: 1;
						display: flex;
						flex-direction: column;
						justify-content: space-between;
						.name{
							font-size: 28rpx;
							font-weight: bold;
						}
						.progress{
							display: flex;
							align-items: center;
							.bar{
								flex: 1;
								border-radius: 20rpx;
								overflow: auto;
							}
							.val{
								font-size: 24rpx;
								margin-left: 10rpx;
								position: absolute;
								color: #fff;
								transform: scale(0.8);
							}
							.btn{
								background: #1ec8db;
								border-radius: 40rpx;
								color: #fff;
								font-size: 24rpx;
								padding: 10rpx 0;
								margin-left: 20rpx;
								width: 150rpx;
								text-align: center;
							}
						}
					}
				}
			}
		}
	}
	.rule-popup{
		width: 630rpx;
		background: #fff;
		padding: 30rpx;
		border-radius: 15rpx;
		.title{
			font-size: 28rpx;
			text-align: center;
			font-weight: bold;
			padding-bottom: 30rpx;
			border-bottom: 1rpx solid #eee;
			color: #AE7735;
		}
		.content{
			height: 800rpx;
			padding: 30rpx 0;
			overflow-y: auto;
			font-size: 24rpx;
			line-height: 50rpx;
		}
		.footer{
			border-top: 1rpx solid #eee;
			padding-top: 30rpx;
			text-align: center;
			color: #C49050;
			font-size: 28rpx;
		}
	}
</style>
