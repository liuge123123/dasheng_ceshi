<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="total-panel">
				<view class="total-header">
					<text>{{total.jr}}</text><text v-html="$t('cft.today.profit')"></text>
				</view>
				<view class="total-body">
					<view class="total-item"><text v-html="$t('cft.total.investment')"></text><text>{{total.tr}}
							{{$t('app.unit')}}</text>
					</view>
					<view class="total-item"><text v-html="$t('cft.total.profit')"></text><text>{{total.sy}}
							{{$t('app.unit')}}</text>
					</view>
					<view class="total-item"><text
							v-html="$t('cft.fund.profit')"></text><text>{{userInfo.fundCommissionMoney}}
							{{$t('app.unit')}}</text></view>
				</view>
			</view>
			<view class="navs-container">
				<scroll-view :scroll-x="true" scroll-with-animation :scroll-into-view="'nav' + navIndex" ref="navs"
					class="navs" show-scrollbar>
					
					<view id="nav-1" class="btn" :class="selectIndex == -1 ? 'active' : ''" @click="indexChange(-1)">
						{{$t('cft.goods.me')}}
					</view>
					<view v-for="(item, index) in roomList" :key="index" :id="'nav' + index" class="btn"
						:class="selectIndex == index ? 'active' : ''" @click="indexChange(index)">{{item.name}}</view>
				</scroll-view>
				<view class="scoll-tips" v-if="roomList.length>4">
					{{$t('cft.goods.scoll')}}
				</view>
				<image class="left" src="@/static/images/left.png"></image>
				<image class="right" src="@/static/images/right.png"></image>
			</view>

			<view class="goods-list" :class="roomList.length>4?'':'notip'" v-if="selectIndex != -1">
				<view class="goods-item" v-for="(item, index) in dataList" :key="index">
					<view class="goods-item-body">
						<image class="goods-img" :src="item.img" mode="aspectFill"></image>
						<view class="goods-wrap">
							<view class="goods-name" style="margin-bottom: 20rpx;">
								{{item.name}}
								<label
									style="color: #fc5f2e; margin-left: 10rpx;">{{ item.rate }}%</label>
							</view>
							<view class="goods-text">{{$t('cft.goods.price')}}：<span>{{item.price}} {{$t('app.unit')}}</span></view>
							<view class="goods-text">
								{{$t('cft.goods.total.profit')}}：<span>{{item.price * item.rate * item.days / 100}}
								{{$t('app.unit')}}</span>
							</view>
							<view class="goods-text">{{$t('cft.goods.cycle')}}：<span>{{item.days}}</span></view>
							<view class="goods-text">
								{{$t('cft.goods.day.profit')}}：<span>{{item.price * item.rate / 100}}
								{{$t('app.unit')}}</span>
							</view>
							<view class="goods-text impotant" v-if="item.type == 1">{{$t('cft.goods.experience.tips')}}
							</view>
						</view>
					</view>
					<view class="goods-item-footer">
						<button type="primary" class="buy-btn"
							@click="buyClick(item.id)">{{$t('cft.goods.buy')}}</button>
					</view>
				</view>
			</view>
			<view class="goods-list" :class="roomList.length>4?'':'notip'" v-if="selectIndex == -1">
				<view class="goods-item" v-for="(item, index) in dataList" :key="index">

					<view class="goods-item-body">
						<image class="goods-img" :src="item.goodsImg" mode="aspectFill"></image>
						<view class="goods-wrap">
							<view style="display: flex;align-items: center;margin-bottom: 20rpx;">
								<view class="goods-name" style="flex: 1; margin-right: 10rpx;">
									{{item.goodsName}}
									<label style="color: #fc5f2e; margin-left: 10rpx;">{{item.goodsRate}}%</label>
								</view>
								<view class="goods-status" :class="{blue: item.orderStatus == 1}">
									{{$t('cft.order.status.' + item.orderStatus)}}
								</view>
							</view>
							<view class="goods-text">{{$t('cft.goods.price')}}：<span>{{item.goodsPrice}} {{$t('app.unit')}}</span> </view>
							<view class="goods-text">
								{{$t('cft.goods.total.profit')}}：<span>{{item.goodsPrice * item.goodsRate * item.goodsCycle / 100}}{{$t('app.unit')}}</span>
							</view>
							<view class="goods-text">{{$t('cft.goods.cycle')}}：<span>{{item.goodsCycle}}</span></view>
							<view class="goods-text">{{$t('cft.goods.buy.time')}}：{{timeText(item.createTime)}}</view>
							<view class="goods-text" v-if="item.orderStatus == 4 || item.orderStatus == 5">
								{{$t('cft.goods.lock.time')}}：{{timeText(item.lockTime)}}
							</view>
							<view class="goods-text" v-else>{{$t('cft.goods.expire.time')}}：{{timeText(item.expireTime)}}</view>
						</view>
					</view>
					<view class="goods-item-footer" v-if="item.orderStatus != 4 && item.orderStatus != 5">
						<view class="goods-total">
							<view class="goods-total-item">
								<text>{{item.goodsPrice * item.goodsRate / 100}} {{$t('app.unit')}}</text>
								<text>{{$t('cft.goods.day.profit')}}</text>
							</view>
							<view class="goods-total-item">
								<text>{{item.receiveProfit}} {{$t('app.unit')}}</text>
								<text>{{$t('cft.goods.cumulative.profit')}}</text>
							</view>
						</view>
					</view>
					<view class="goods-item-footer">
						<button type="primary" v-if="item.iscanReceive" class="buy-btn" @click="receiveClick(item.id,1)">{{ $t('cft.goods.receive') }}</button>
						<button type="primary" v-else class="buy-btn" @click="receiveClick(item.id,0)">{{ $t('cft.goods.receive2') }}</button>
					</view>
				</view>
			</view>
			<uni-load-more :status="loadingStatus"></uni-load-more>
		</view>
		<!-- 新手引导 -->
		<guide :show="showGuide" :width="cWidth" :height="cHeight" :left="cLeft" :top="cTop" :showMessage='cShowMsg'
			:currentIndex="currentIndex" :noticeArray="noticeArray" @click="clicktoNext"></guide>
		
		<toolbar :index="3"></toolbar>
	</container>
</template>

<script>
	export default {
		components: {

		},
		data() {
			return {
				selectIndex: 0,
				navIndex: -1,
				dataList: [],
				day: 5,
				type: 1,
				loadingStatus: 'loading',
				total: {
					tr: 0,
					sy: 0,
					jr: 0
				},
				userInfo: {},
				roomList: [],


				showGuide: false, //引导是否显示 
				cShowMsg: '', // 展示的解释语
				cWidth: '',
				cHeight: '',
				cLeft: '',
				cTop: '',
				currentIndex: 0,
				//配置需要显示引导的view以及引导显示的msg
				noticeArray: [{
						"showID": "test1", // 对应的id
						"showMessage": this.$t('cft.guide.tip1'), // 对应的解释文本
						"type": "bottom", // 解释框的气泡类型
						"radius": "8rpx"
					},
					{
						"showID": "test2",
						"showMessage": this.$t('cft.guide.tip2'),
						"type": "bottom",
						"radius": "8rpx"
					}
				]
			}
		},
		computed: {

		},
		onShow() {
			this.getRoomList().then(() => {
				this.getData()
				this.getTotalData()
				this.$uni.getUserInfo().then(user => {
					this.userInfo = user
				})
			})
		},
		onLoad() {
			this.userInfo = uni.getStorageSync("user") || {}
			this.selectIndex = this.$route.query.index || -1
			this.showPageContent()

			// setInterval(() => {
			// 	this.getData()
			// 	this.getTotalData()
			// }, 60000)

			// this.showGuide = !uni.getStorageSync("cft.guide")

			// if (this.showGuide) {
			// 	let _this = this;
			// 	this.$nextTick(function() {
			// 		if (_this.currentIndex >= _this.noticeArray.length) {
			// 			_this.showGuide = false;
			// 			return;
			// 		}
			// 		_this.showGuide = true;
			// 		_this.cShowMsg = _this.noticeArray[_this.currentIndex].showMessage;
			// 		var idS = '#' + _this.noticeArray[_this.currentIndex].showID;
			// 		//根据布局信息显示引导框位置
			// 		const query = uni.createSelectorQuery().in(_this);
			// 		query.select(idS).boundingClientRect(data => {
			// 			_this.cWidth = data.width;
			// 			_this.cHeight = data.height;
			// 			_this.cLeft = data.left;
			// 			_this.cTop = data.top;
			// 		}).exec();
			// 	});
			// }
		},
		methods: {
			receiveClick(id,type){
				// type 0 不能领取 1正常领取
				if(type==0){
					uni.showToast({
						icon: 'none',
						title: this.$t('cft.goods.receive2tip')
					})
					return
				}
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/cft/order/receive', {
					orderId: id
				}).then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						uni.showToast({
							title: this.$t('cft.goods.buy.success'),
							icon: 'success'
						})
						this.indexChange(-1)
					} else {
						uni.showToast({
							icon: 'none',
							title: this.$t('error.' + res.code,{circle: res.msg})
						})
					}
				}).catch(e => {
					uni.showToast({
						icon: 'none',
						title: this.$t('error.500')
					})
				})
			},
			getRoomList() {
				return new Promise((resove, reject) => {
					this.$get('/app/cft/room/list').then(res => {
						if (res.code == 0) {
							let data = res.data
							this.roomList = data
							resove()
						}else{
							reject()
						}
					})
				})
				
			},
			clicktoNext() {
				if (this.currentIndex >= this.noticeArray.length) {
					this.showGuide = false;
					return;
				}
				this.noticeArray[this.currentIndex].zindex = 0;
				this.cShowMsg = '';
				if (this.currentIndex + 1 >= this.noticeArray.length) {
					this.showGuide = false;
					uni.setStorageSync("cft.guide", true)
					return;
				}
				this.currentIndex++;
				this.cShowMsg = this.noticeArray[this.currentIndex].showMessage;
				var idS = '#' + this.noticeArray[this.currentIndex].showID;
				console.log(idS)
				const query = uni.createSelectorQuery().in(this);
				query.select(idS).boundingClientRect(data => {
					this.cWidth = data.width;
					this.cHeight = data.height;
					this.cLeft = data.left;
					this.cTop = data.top;
				}).exec();

			},
			getTotalData() {
				this.$get('/app/cft/total').then(res => {
					if (res.code == 0) {
						let data = res.data
						this.total = data
					}
				})
			},
			getData() {
				this.loadingStatus = 'loading'
				this.dataList = []
				let callback;
				if (this.selectIndex == -1) {
					callback = this.getOrderList()
				} else {
					callback = this.getGoodsList()
				}
				callback.then(res => {
					if (res.code == 0) {
						this.dataList = res.data
						this.loadingStatus = 'noMore'
					} else {
						uni.showToast({
							icon: 'none',
							title: this.$t('error.' + res.code)
						})
					}
				}).catch(e => {
					uni.showToast({
						icon: 'none',
						title: this.$t('error.500')
					})
				})
			},
			getGoodsList() {
				return this.$get('/app/cft/goods/list', {
					roomId: this.roomList[this.selectIndex].id
				})
			},
			getOrderList() {
				return this.$get('/app/cft/order/list')
			},
			indexChange(index) {
				this.selectIndex = index
				this.navIndex = index > -1 ? index - 1 : -1
				this.getData()
			},
			timeText(val) {
				return this.$uni.timeFormat(val)
			},
			progress(val) {

			},
			buyClick(id) {
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/cft/goods/buy', {
					goodsId: id
				}).then(res => {
					uni.hideLoading()
					if (res.code == 0) {
						uni.showToast({
							title: this.$t('cft.goods.buy.success'),
							icon: 'success'
						})
						this.indexChange(-1)
					} else {
						uni.showToast({
							icon: 'none',
							title: this.$t('error.' + res.code)
						})
					}
				}).catch(e => {
					uni.showToast({
						icon: 'none',
						title: this.$t('error.500')
					})
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.container {
		.total-panel {
			background: url('../../static/images/ctf_top_bg.png');
			background-size: cover;
			height: 335rpx;
			color: #E7C19F;
			display: flex;
			flex-direction: column;
			justify-content: center;

			.total-header {
				text-align: center;
				display: flex;
				flex-direction: column;
				align-items: center;
				font-size: 61rpx;
				font-weight: bold;
				color: #FFFFFF;
				background: linear-gradient(0deg, #D19C69 0%, #EAD2AF 100%);
				-webkit-background-clip: text;
				-webkit-text-fill-color: transparent;

				text:last-child {
					font-size: 27rpx;
					font-weight: 500;
					color: #E7C19F;
					margin-top: 16rpx;
				}
			}

			.total-body {
				display: flex;
				justify-content: space-between;
				margin-top: 54rpx;

				.total-item {
					flex: 1;
					margin-top: 10rpx;
					font-size: 24rpx;
					text-align: center;
					display: flex;
					flex-direction: column;
					align-items: center;
					color: #E7C19F;

					text:last-child {
						margin-top: 20rpx;
						font-size: 32rpx;
						font-weight: bold;
						color: #E7C19F;
					}
				}
			}
		}

		.navs-container {
			display: flex;
			background: #000;
			align-items: center;
			box-shadow: 0px 4rpx 24rpx 0px rgba(176, 176, 176, 0.28);
			@keyframes fadeleft {
				100% {
					opacity: 0.3;
					transform: translateX(-20rpx);
				}
			}
			
			@keyframes faderight {
				100% {
					opacity: 0.3;
					transform: translateX(20rpx);
				}
			}
			
			.left {
				height: 50rpx;
				width: 50rpx;
				left: 0rpx;
				top: 28rpx;
				animation: fadeleft 1s ease-in-out infinite;
				position: absolute;
			}
			
			.right {
				height: 50rpx;
				width: 50rpx;
				right: 0rpx;
				top: 28rpx;
				animation: faderight 1s ease-in-out infinite;
				position: absolute;
			}

			.scoll-tips {
				text-align: center;
				font-size: 24rpx;
				color: #F00;
				position: absolute;
				left: 0;
				right: 0;
				bottom: -50rpx;
				background: #fff;
				line-height: 50rpx;
			}

			position: relative;

			.icon {
				width: 50rpx;
				text-align: center;
				color: #B4722D;
				font-size: 42rpx;
			}


			.navs {
				white-space: nowrap;
				flex: 1;
				position: relative;
				z-index: 1;
				&::after {
					content: '';
					position: absolute;
					bottom:2rpx;
					left: 0;
					right: 0;
					height: 2rpx;
					background: #eee;
				}
				
				.btn {
					display: inline-block;
					font-size: 28rpx;
					padding: 20rpx 0;
					text-align: center;
					cursor: pointer;
					white-space: nowrap;
					padding: 32rpx;
					position: relative;
					font-weight: bold;
					color: #fff;
					// width: 24%;
					&.active {
						// color: #fff;
						// background: linear-gradient(180deg, #DDBD85 0%, #C38748 100%);
						background: linear-gradient(90deg,#ff9700,#ff6300);
						&::after {
							content: '';
							position: absolute;
							bottom: 0;
							left: 32rpx;
							right: 32rpx;
							border-bottom: 3px solid #B4722D;
							z-index: 1;
						}
					}
				}
			}
		}
		.notip{
			padding-top: 40rpx !important;
		}
		.goods-list {
			padding: 30rpx;
			padding-top: 80rpx;

			.goods-item {
				border-radius: 20rpx;
				overflow: hidden;
				position: relative;
				box-shadow: 0px 4rpx 24rpx 0px rgba(176, 176, 176, 0.28);
				margin-bottom: 20rpx;

				.goods-item-body {
					display: flex;
					// background: #fff;
					padding: 30rpx;

					.goods-img {
						width: 200rpx;
						height: 200rpx;
						background: #f8f8f8;
						border-radius: 8rpx;
					}

					.goods-wrap {
						margin-left: 20rpx;
						flex: 1;

						.goods-name {
							font-weight: bold;
							color: #E2E2E2;
							font-size: 32rpx;
						}

						.goods-status {
							color: #fff;
							background: linear-gradient(90deg, #DDBD85 0%, #C38748 100%);
							border-radius: 19px;
							font-size: 24rpx;
							padding: 5rpx 15rpx;
						}

						.goods-text {
							font-size: 24rpx;
							color: #888888;
							margin-top: 5rpx;
							span{
								color: #fc5f2e;
							}
							&.impotant {
								color: #F00;
							}
						}
					}
				}

				.goods-item-footer {
					padding: 20rpx;
					// background-color: #fff;
					.buy-btn {
						background: linear-gradient(0deg, #C79758 0%, #EAD6A6 100%);
						font-size: 28rpx;
						border-radius: 40rpx;

						&::after {
							border: none;
						}
					}

					.goods-total {
						background: #f8f8f8;
						border-radius: 8rpx;

						display: flex;
						font-size: 28rpx;

						.goods-total-item {
							display: flex;
							flex: 1;
							flex-direction: column;
							text-align: center;
							color: rgba(#191514, 0.6);
							font-size: 24rpx;

							&:first-child {
								border-right: 2rpx solid #fff;
							}

							text:first-child {
								font-size: 35rpx;
								font-weight: bold;
								color: #B4722D;
								margin-bottom: 10rpx;
							}
						}
					}
				}
			}
		}
	}
</style>
