<template>
	<container class="root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="game">
				<view class="game-wrap">
					<view class="game-area">
						<!-- <HM-slotMachine ref="HMslotMachine"></HM-slotMachine> -->
						<slot-machine ref="HMslotMachine"></slot-machine>
					</view>
				</view>

			</view>

			<view class="game-tips">
				<text class="title">{{$t('grabbing.data.done')}}：</text>
				<text class="val">{{userData.completeCount}}/{{userData.totalCount}}</text>
			</view>

			<view class="game-action">
				<button class="game-btn" type="primary" @click="start">{{$t('grabbing.game.start')}}</button>
			</view>
			
			<view class="game-source">
				<text class="title">{{$t('grabbing.source.title')}}</text>
					<view class="body">
					<view class="item">
						<image mode="aspectFit" class="img" :src="$config.imgUrl+'/fileupload/brandlogo/' + levelId + '_1.png'"></image>
					</view>
					<view class="item">
						<image mode="aspectFit" class="img" :src="$config.imgUrl+'/fileupload/brandlogo/' + levelId + '_2.png'"></image>
					</view>
				</view>
			</view>

			<view class="game-desc">
				<view class="title">
					{{$t('grabbing.rule')}}
				</view>
				<view class="content">
					<rich code="task"></rich>
				</view>
			</view>

		</view>

		<uni-popup ref="resultPop" type="center">
			<view class="panel-wrap">
				<image class="panel-header" mode="widthFix" src="@/static/images/grabsuccess.png"></image>
				<view class="order-wrap">
					<view class="order-goods">
						<image class="goods-img" mode="aspectFill" :src="order.goodsImg"></image>
						<view class="goods-wrap">
							<view class="goods-name">
								<text>{{order.goodsName}}</text>
							</view>
							<view class="goods-price">
								<text class="title">{{$t('grabbing.order.money')}}：</text>
								<text class="val">{{$moneyFormat(order.orderMoney)}} {{$t('app.unit')}}</text>
							</view>
							<view class="goods-price">
								<text class="title">{{$t('grabbing.order.profit')}}：</text>
								<text class="val">{{$moneyFormat(order.commission)}} {{$t('app.unit')}}</text>
							</view>
						</view>
					</view>
					<view class="action-wrap">
						<button class="btn primary" type="primary"
							@click="comfirmClick">{{$t('grabbing.order.confirm')}}</button>
					</view>
					
					<view class="source">{{$t('grabbing.source')}}：<image style="height: 20rpx;" mode="heightFix" :src="'http://localhost/fileupload/brandlogo/' + levelId + '_1.png'"></image></view>
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
				levelId: null,
				userInfo: {},
				userData: {},
				prizeList: [],
				rollStop: true,
				order: {
					goodsName: '',
					goodsImg: '',
					commission: 0,
					orderMoney: 0
				},
				resultGoodsIndex: 0
			}
		},
		computed: {

		},
		onLoad() {
			this.levelId = this.$Route.query.id
			this.userInfo = uni.getStorageSync("user")
			console.log(this.$config.imgUrl)
		},
		onShow() {
			this.getData()
		},
		onReady() {
			// this.$refs.resultPop.open()
		},
		methods: {
			initMachine(goods){
				console.log(goods)
				this.$refs.HMslotMachine.init({
					prizeList: this.prizeList,
					defaultResults: [goods, goods, goods],
					duration: 3000,
					direction: 'up'
				})
			},
			getData() {
				this.showPageLoading()
				this.$uni.getUserInfo().then(user => {
					this.userInfo = user
				}).then(res => {
					return this.getTotalData()
				}).then(res => {
					return this.getGoodsList()
				}).then(res => {
					if(this.userData.level != this.levelId){
						uni.navigateTo({
							url: '/pages/grabbing/grabbing'
						})
					}
					this.showPageContent()
				}).catch(e => {
					let code = e.code || 500
					this.showPageError(this.$t('error.' + code))
				})
			},
			getTotalData() {
				
				return new Promise((resolve, reject) => {
					this.$get('/app/account/task/count').then(res => {
						if (res.code == 0) {
							this.userData = res.data
							resolve()
						} else {
							reject(res)
						}
					}).catch(e => {
						reject({code : 500})
					})
				})
			},
			getGoodsList(){
				return new Promise((resolve, reject) => {
					this.$get('/app/order/g/goods/list', {
						levelId: this.levelId
					}).then(res => {
						if (res.code == 0) {
							this.prizeList = res.data.map(item => {
								return {
									name: item.goodsName,
									value: item.id,
									img: item.image
								}
							})
							if(this.prizeList && this.prizeList.length > 0){
								this.initMachine(this.prizeList[0].value)
							}
							resolve()
						} else {
							reject(res)
						}
					}).catch(e => {
						reject({code : 500})
					})
					
				})
			},
			start() {
				if(!this.rollStop){
					return
				}
				if (this.userData.complete) {
					uni.showToast({
						icon: 'none',
						title: this.$t('grabbing.game.done')
					})
					return
				}
				this.rollStop = false
				let index = this.getResultIndex()
				let goodsId = this.prizeList[index].value
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.getOrder(goodsId).then(res => {
					uni.hideLoading()
					this.$refs.HMslotMachine.roll(index, () => {
						this.rollStop = true
						this.$refs.resultPop.open()
					})
				}).catch(error => {
					this.rollStop = true
					uni.showToast({
						title: this.$t('error.' + error.code)
					})
				})
			},
			getResultIndex() {
				let max = this.prizeList.length - 1;
				return Math.floor(Math.random() * (max - 1 + 1) + 1);
			},
			getOrder(goodsId) {
				console.log(1111)
				return new Promise((resolve, reject) => {
					this.$post('/app/account/taskDeal', {
						level: this.levelId,
						goodsId: goodsId
					}).then(res => {
						if (res.code == 0) {
							this.order = res.data
							this.userData.completeCount ++
							if(this.userData.completeCount >= this.userData.totalCount){
								this.userData.complete = true
							}
							resolve()
						} else {
							reject(res)
						}
					}).catch(e => {
						reject({code: 500})
					})
				})
			},
			comfirmClick() {
				this.$refs.resultPop.close()
			}
		}
	}
</script>

<style scoped lang="scss">
	.root-container {
		background-color: #fff;
		overflow-y: auto;
	}

	.container {
		.game {
			padding-top: 20rpx;
			padding-bottom: 20rpx;

			.game-wrap {
				width: 690rpx;
				height: 366rpx;
				margin: 0 auto;
				background: url('@/static/images/qdz.png') no-repeat;
				background-size: 100% 100%;
				position: relative;

				.game-area {
					position: relative;
					width: 642rpx;
					height: 285rpx;
					left: 0rpx;
					top: 45rpx;
					margin: 0 auto;
					overflow: hidden;
					border-radius: 8rpx;
					background-color: rgba(255, 255, 255, 1);
				}
			}
		}

		.game-tips {
			font-size: 28rpx;
			color: #666;
			text-align: center;
			margin-top: 30rpx;

			.val {
				color: #CF8B32;
			}
		}

		.game-action {
			padding: 30rpx;
			display: flex;

			.game-btn {
				flex: 1;
				height: 82rpx;
				background: linear-gradient(183deg, #E9D4A5 0%, #C49050 100%);
				border-radius: 41rpx;

				&::after {
					border: 0;
				}
			}
		}

		.game-source{
			padding: 30rpx;
			.title{
				font-size: 35rpx;
				font-weight: bold;
			}
			.body{
				display: flex;
				flex-wrap: wrap;
				justify-content: space-between;
				margin-top: 30rpx;
				.item{
					width: calc(50% - 50rpx);
					margin-bottom: 20rpx;
					background: #f8f8f8;
					padding: 20rpx;
					display: flex;
					flex-direction: column;
					justify-content: center;
					align-items: center;
					.img{
						width: 100%;
						height: 60rpx;
					}
				}
			}
		}

		.game-desc {
			padding: 30rpx;

			.title {
				font-size: 35rpx;
				font-weight: bold;
			}

			.content {
				margin-top: 30rpx;
				font-size: 29rpx;
				font-weight: 500;
				line-height: 59rpx;
				opacity: 0.8;
			}
		}

		.info-panel {
			padding: 30rpx;

			.data {
				.data-title {
					font-size: 44rpx;
					font-weight: 700;
				}

				.data-wrap {
					margin-top: 20rpx;
					background: #fff;
					padding: 20rpx;
					display: flex;
					flex-wrap: wrap;

					.data-item {
						width: 50%;
						display: flex;
						flex-direction: column;
						font-size: 24rpx;
						color: #666;
						margin: 10rpx 0;

						.val {
							color: #caa160;
							font-size: 36rpx;
							margin-top: 10rpx;
						}
					}
				}
			}

			.next {
				padding: 20rpx;
				margin-top: 20rpx;
				display: flex;
				align-items: center;
				background: #fff;

				.next-btn {
					background: #000;
				}

				.next-tips {
					flex: 1;
					margin-left: 20rpx;
					font-size: 28rpx;
					color: #666;
				}
			}

			.task {
				margin: 40rpx 0;

				.task-title {
					font-size: 36rpx;
					font-weight: 700;
					padding-left: 20rpx;
					margin: 10rpx 0;
					position: relative;

					&::after {
						content: "";
						position: absolute;
						left: 0;
						top: 0;
						height: 100%;
						width: 10rpx;
						background: #caa160;
					}
				}

				.task-tips {
					font-size: 24rpx;
					font-weight: bold;
				}
			}
		}
	}

	.panel-wrap {
		position: relative;
		width: 640rpx;
		.panel-header{
			width: 100%;
			display: block;
			margin: 0;
			padding: 0;
			border-top-left-radius: 30rpx;
			border-top-right-radius: 30rpx;
		}
		.order-wrap {
			background: #fff;
			border-bottom-left-radius: 30rpx;
			border-bottom-right-radius: 30rpx;
			padding: 0 30rpx 30rpx;
			.order-goods {
				font-size: 28rpx;
				display: flex;
				.goods-img {
					width: 200rpx;
					height: 200rpx;
					object-fit: contain;
					border: 1rpx solid #eee
				}
				.goods-wrap{
					flex: 1;
					margin-left: 20rpx;
					.goods-name{
						font-weight: bold;
						padding-bottom: 10rpx;
						word-break: break-all;
						word-wrap: break-word;
						white-space: break-spaces;
					}
					.goods-price{
						display: flex;
						flex-direction: column;
						.title{
							color: #666;
							font-size: 24rpx;
						}
						.val{
							font-size: 32rpx;
							font-weight: bold;
							color: #CF8B32;
						}
					}
				}
			}
			.action-wrap{
				.btn{
					height: 78rpx;
					line-height: 78rpx;
					background: linear-gradient(183deg, #E9D4A5 0%, #C49050 100%);
					border-radius: 39rpx;
					margin-top: 50rpx;
					font-size: 35rpx;
					font-weight: bold;
					color: #fff;
					&::after{
						border: none;
					}
				}
			}
			
			.source{
				padding-top: 50rpx;
				font-size: 24rpx;
				display: flex;
				align-items: center;
				justify-content: center;
				color: #999;
			}
		}
	}
</style>
