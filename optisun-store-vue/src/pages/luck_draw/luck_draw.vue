<template>
	<container ref="container" :headerVisable="true" :status="pageStatus">
		<image class="bg" src="../../static/images/black_bg.png"></image>
		<view class="container">
			<view class="luck-container">
				<view class="luck-wrap">
					<template v-for="(item, index) in dataList">
						<view class="luck-item" :class="{'active': selectIndex == index}" >
							<image class="img" :src="item.prizeImg" mode="aspectFit"></image>
							<view class="text">{{item.prizeName}}</view>
						</view>
						<view class="luck-item luck-start" v-if="index == 3" @click="startHandle">
							<view class="text">{{$t('luckdraw.start')}}</view>
						</view>
					</template>
				</view>
			</view>
			
			<view class="tips">
				<view class="item">
					<text>{{$t('luckdraw.total.num')}}：</text>
					<text class="val">{{userInfo.luckLeftNum}}</text>
				</view>
				<view class="item">
					<text>{{$t('luckdraw.day.num')}}：</text>
					<text class="val">{{act.todayNum}}/{{act.maxNum}}</text>
				</view>
			</view>
			
			<swiper autoplay class="result" circular vertical :display-multiple-items="5" :interval="1000">
				<swiper-item class="item" v-for="(item, index) in recordList" :key="index">{{item}}</swiper-item>
			</swiper>
			
			<view class="rule">
				<view class="title">{{$t('luckdraw.rule')}}</view>
				<rich-text class="content" :nodes="act.description"></rich-text>
			</view>
		
		</view>
		
		<uni-popup ref="popup" type="center" :is-mask-click="false">
			<view class="prize-bg" v-if="resultIndex != -1">
				<view class="prize-title">{{$t("luckdraw.receive")}}</view>
				<image class="prize-img" :src="dataList[resultIndex].prizeImg" mode="aspectFill"></image>
				<view class="prize-name">{{dataList[resultIndex].prizeName}}</view>
				<button class="prize-close" @click="$refs.popup.close()">{{$t('luckdraw.close')}}</button>
			</view>
		</uni-popup>
	</container>
</template>

<script>
	export default {
		data() {
			return {
				actId: 12,
				selectIndex: -1,
				dataList: [],
				act: {},
				resultIndex: -1,
				timer: null,
				loading: false,
				userInfo: {},
				recordList: []
			}
		},
		beforeCreate() {
			
		},
		onShow() {
			this.$uni.getUserInfo().then(user => {
				this.userInfo = user
			})
		},
		onLoad() {
			this.showPageContent()
			this.getRecordList()
			this.getAct()
		},
		methods: {
			getRecordList(){
				this.$get('/app/common/config', {
					key: 'luck.record.config'
				}).then(res => {
					if(res.code == 0){
						this.recordList = res.data.split('\n')
					}
				})
			},
			getAct(){
				this.$get('/app/act/info',{
					actId: this.actId
				}).then( res =>{
					if(res.code == 0){
						this.act = res.data
						this.dataList = res.data.prizeList
					}
				})
			},
			showResult(){
				this.userInfo.luckLeftNum --
				this.act.todayNum ++
				this.$refs.popup.open()
			},
			startHandle(){
				if(this.act.todayNum >= this.act.maxNum){
					uni.showToast({
						icon: 'none',
						title: this.$t('luckdraw.notimes.today')
					})
					return
				}else if(this.userInfo.luckLeftNum <= 0){
					uni.showToast({
						icon: 'none',
						title: this.$t('luckdraw.notimes')
					})
					return
				}
				if(this.loading){
					return
				}
				this.loading = true
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.getResult().then(res => {
					uni.hideLoading()
					return this.startLuck()
				}).then(res => {
					this.loading = false
					this.showResult()
				}).catch(e => {
					uni.hideLoading()
					this.loading = false
					if(e && e.code){
						uni.showToast({
							icon: 'none',
							title: this.$t('error.' + e.code)
						})
					}else{
						uni.showToast({
							icon: 'none',
							title: this.$t('error.500')
						})
					}
				})
				
			},
			startLuck(){
				return new Promise((resolve, reject) => {
					let indexList = [0, 1, 2, 4, 7, 6, 5, 3 ]
					let index = -1
					let sleep = 100
					let times = 0
					if(this.timer){
						clearTimeout(this.timer)
						this.timer = null
					}
					let loop = () => {
						times ++
						if(index < 7){
							index ++
							this.selectIndex ++
						}else{
							index = 0
						}
						this.selectIndex = indexList[index]
						if(times < 24){
							sleep = 100
						}else if(times < 32){
							sleep = sleep + 30
						}
						if(times > (40 - this.resultIndex) && this.selectIndex == this.resultIndex){
							resolve()
							return
						}else{
							this.timer = setTimeout(() => {
								loop()
							}, sleep)
						}
					}
					loop()
				})
				
			},
			getResult(){
				return new Promise((resolve, reject) => {
					this.$post('/app/act/start',{
						actId: this.actId
					}).then(res => {
						if(res.code == 0){
							const star = res.data
							for(let i = 0; i < this.dataList.length; i++){
								if(star.id == this.dataList[i].id){
									this.resultIndex = i
									resolve()
									return
								}
							}
						}
						reject(res)
					})
				})
			}
		}
	}
</script>
<style scoped lang="scss">
	.bg{
		position: fixed;
		left: 0;
		top: 0;
		width: 100%;
		height: 100%;
		object-fit: cover;
	}
	.container{
		padding: 30rpx;
		position: relative;
		color: #fff;
		.luck-container{
			height: 519rpx;
			width: 690rpx;
			background: url('../../static/images/luck_bg.jpg');
			background-size: cover;
			.luck-wrap{
				display: flex;
				flex-wrap: wrap;
				padding: 25rpx;
				.luck-item{
					width: 213rpx;
					height: 158rpx;
					background: url('../../static/images/luck_item.png');
					background-size: cover;
					display: flex;
					flex-direction: column;
					justify-content: center;
					align-items: center;
					&.active{
						position: relative;
						&::before{
							content: '';
							position: absolute;
							top: 10rpx;
							bottom: 20rpx;
							left: 12rpx;
							right: 12rpx;
							background: rgba(#FFD07F, 1);
							border-radius: 15rpx;
						}
					}
					.img{
						width: 60rpx;
						height: 60rpx;
						object-fit: contain;
						margin-top: -5rpx;
					}
					.text{
						font-size: 19rpx;
						font-weight: 500;
						color: #666666;
						position: relative;
					}
					&.luck-start{
						background: url('../../static/images/luck_start.png');
						background-size: cover;
						color: #fff;
						font-weight: bold;
						.text{
							margin-top: -20rpx;
							font-size: 32rpx;
							font-weight: bold;
							color: #fff;
						}
					}
				}
			}
		}
		.tips{
			display: flex;
			justify-content: space-between;
			padding: 30rpx 0;
			font-size: 27rpx;
			font-weight: 500;
			color: #FFFFFF;
			.val{
				color: #FFD07F;
			}
		}
		.result{
			background: rgba(#F9F0E6, 0.12) ;
			border-radius: 20rpx;
			padding: 30rpx;
			font-size: 24rpx;
			color: #1ec8db;
		}
		.rule{
			margin-top: 50rpx;
			.title{
				font-size: 35rpx;
				font-weight: bold;
				color: #F6F6F6;
				text-align: center;
				margin-bottom: 30rpx;
			}
			.content{
				font-size: 29rpx;
				font-weight: 500;
				color: #F6F6F6;
				line-height: 48rpx;
				opacity: 0.8;
			}
		}
	}
	.prize-bg{
		background: #fff;
		text-align: center;
		padding: 30rpx;
		border-radius: 10rpx;
		width: 400rpx;
		.prize-title{
			text-align: center;
			font-weight: bold;
			font-size: 32rpx;
		}
		.prize-img{
			width: 100rpx;
			height: 100rpx;
			object-fit: contain;
			margin-top: 100rpx;
		}
		.prize-name{
			text-align: center;
			color: #AE7735;
			font-size: 28rpx;
			margin-bottom: 100rpx;
			margin-top: 30rpx;
		}
		.prize-close{
			border-radius: 40rpx;
			height: 82rpx;
			font-size: 32rpx;
			color: #666666;
			border: 1rpx solid #ccc;
			background: #fff;
			&::after{
				border: none;
			}
		}
	}
</style>
