<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" :headerBorder="false" @refresh="getData()">
		<view class="container">
			<scroll-view scroll-x class="navs" scroll-with-animation :scroll-into-view="'nav' + (selectIndex - 1)">
				<!-- <view id="nav0" class="item" :class="selectIndex == 0 ? 'active' : ''" @click="indexChange(0)">
					<text>{{$t('title.grabbing')}}</text>
				</view> -->
				<view id="nav3" class="item" :class="selectIndex == 3 ? 'active' : ''" @click="indexChange(3)">
					<text>{{$t('title.account')}}</text>
				</view>
				<!-- <view id="nav1" class="item" :class="selectIndex == 1 ? 'active' : ''" @click="indexChange(1)">
					<text>{{$t('title.luckdraw')}}</text>
				</view> -->
				<!-- <view id="nav2" class="item" :class="selectIndex == 2 ? 'active' : ''" @click="indexChange(2)">
					<text>{{$t('title.vip')}}</text>
				</view> -->
				
				<!-- <view id="nav4" class="item" :class="selectIndex == 4 ? 'active' : ''" @click="indexChange(4)">
					<text>{{$t('order.nav.title')}}</text>
				</view> -->
				<view id="nav5" class="item" :class="selectIndex == 5 ? 'active' : ''" @click="indexChange(5)">
					<text>Récompenses d'équipe</text>
				</view>
			</scroll-view>
			
			<scroll-view scroll-y @scrolltolower="getData">
				<view class="list">
					<view class="item" v-if="selectIndex == 0" v-for="item in dataList" :key="item.id">
						<view class="item-body">
							<image class="img" mode="aspectFit" :src="item.goodsImg"></image>
							<view class="wrap">
								<view class="name"><text>{{item.goodsName}}</text></view>
								<view class="tips">{{$t('order.no')}}：{{item.orderNo}}</view>
								<view class="tips">{{$t('order.date')}}：{{timeText(item.createTime)}}</view>
							</view>
						</view>
						<view class="item-footer">
							<text>{{$t('order.commission')}}：</text>
							<text>{{item.commission}} {{$t('app.unit')}}</text>
						</view>
					</view>
					
					<view class="item" v-if="selectIndex == 1" v-for="item in dataList" :key="item.id">
						<view class="item-body">
							<image class="img" mode="aspectFit" :src="item.hitPrizeImg"></image>
							<view class="wrap">
								<view class="name"><text>{{item.hitPrize}}</text></view>
								<view class="tips">{{timeText(item.createTime)}}</view>
							</view>
						</view>
					</view>
					
					<view class="item" v-if="selectIndex == 2" v-for="item in dataList" :key="item.id">
						<view class="item-body">
							<view class="wrap">
								<view class="name"><text>{{item.score}} {{$t('app.unit')}}</text></view>
								<view class="tips">{{timeText(item.createTime)}}</view>
							</view>
						</view>
					</view>
					
					<view class="item" v-if="selectIndex == 3" v-for="item in dataList" :key="item.id">
						<view class="item-body">
							<view class="wrap">
								<view class="name"><text>{{item.score}} {{$t('app.unit')}}</text></view>
								<view class="tips" v-if="item.type==12">
									{{$t('account.log.type' + item.type,{name:formatRemark(item.remark)})}}
								</view>
								<view class="tips" v-else-if="item.type==13">
									{{ item.score>0?$t('account.log.type' + item.type,{name:formatRemark(item.remark)}):$t('account.log.type' + item.type+'_',{name:formatRemark(item.remark)}) }}
									
								</view>
								<view class="tips" v-else>{{$t('account.log.type' + item.type)}}</view>
								<view class="tips">{{timeText(item.createTime)}}</view>
							</view>
						</view>
					</view>
					
					<view class="item" v-if="selectIndex == 4" v-for="item in dataList" :key="item.id">
						<view class="item-body" @click="$goUrl('/pages/order_detail/order_detail?id=' + item.id)">
							<view class="wrap">
								<view class="name">{{item.orderPayMoney}} {{$t('app.unit')}}</view>
								<view class="tips"><text>{{$t('order.no')}}：{{item.orderNo}}</text></view>
								<view class="tips">{{$t('order.date')}}：{{timeText(item.orderTime)}}</view>
							</view>
						</view>
					</view>
					<view class="item" v-if="selectIndex == 5" v-for="item in dataList" :key="item.id">
						<view class="item-body">
							<view class="wrap">
								<view class="name"><text>{{item.score}} {{$t('app.unit')}}</text></view>
								<view class="tips" >{{$t('account.log.type' + item.type)}}</view>
								<view class="tips">{{timeText(item.createTime)}}</view>
							</view>
						</view>
					</view>
					
					<uni-load-more :status="loadingStatus"></uni-load-more>
				</view>
			</scroll-view>
		</view>
		<!-- <toolbar :index="4"></toolbar> -->
	</container>
</template>

<script>
	export default {
		components: {

		},
		data() {
			return {
				selectIndex: 3,
				pageNo: 1,
				pageSize: 10,
				dataList: [],
				loadingStatus: 'more' //more/loading/noMore
			}
		},
		computed: {

		},
		onLoad(options) {
			this.selectIndex = options.index || 3
			this.showPageContent()
		},

		onShow() {
			this.getData()
		},
		onReachBottom() {
			this.getData()
		},
		methods: {
			formatRemark(remark) {
				var m = remark.split(",");
				if (m.length > 1) {
					return m[1]
				}else{
					return ''
				}
			},
			indexChange(index) {
				this.selectIndex = index
				this.pageNo = 1
				this.loadingStatus = 'more'
				this.getData()
			},
			getData() {
				if (this.loadingStatus != 'more') {
					return
				}
				if (this.pageNo == 1) {
					this.dataList = []
				}
				this.loadingStatus = 'loading'
				let promise = null
				if(this.selectIndex == 0){
					promise = this.getOrderList()
				}else if(this.selectIndex == 1){
					promise = this.getLuckRecord()
				}else if(this.selectIndex == 2){
					promise = this.getVipRecord()
				}else if(this.selectIndex == 3){
					promise = this.getAccountList()
				}else if(this.selectIndex == 4){
					promise = this.getShopOrderList()
				}else if(this.selectIndex == 5){
					promise = this.getTeamList()
				}
				
				promise.then(res => {
					this.loadingStatus = 'more'
					if (res.code == 0) {
						let current = res.data.currPage
						let total = res.data.totalPage
						if (current < total) {
							this.pageNo++
						} else {
							this.loadingStatus = 'noMore'
						}
						res.data.list.forEach(item => {
							this.dataList.push(item)
						})
					}
				})
			},
			getOrderList(){
				return this.$get('/app/order/g/list', {
					pageNo: this.pageNo,
					pageSize: this.pageSize
				})
			},
			getLuckRecord(){
				return this.$get('/app/act/luckList', {
					pageNo: this.pageNo,
					pageSize: this.pageSize
				})
			},
			getVipRecord(){
				return this.$get('/app/account/vipCommmission', {
					pageNo: this.pageNo,
					pageSize: this.pageSize
				})
			},
			getAccountList(){
				return this.$get('/app/account/detail', {
					pageNo: this.pageNo,
					pageSize: this.pageSize
				})
			},
			getShopOrderList(){
				return this.$get('/app/shop/order/list', {
					pageNo: this.pageNo,
					pageSize: this.pageSize
				})
			},
			getTeamList(){
				return this.$get('/app/account/detailTeam', {
					pageNo: this.pageNo,
					pageSize: this.pageSize
				})
			},
			timeText(val) {
				return this.$uni.timeFormat(val)
			}
		}
	}
</script>

<style scoped lang="scss">
	.container{
		position: relative;
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
		overflow: hidden;
		margin-bottom: 100rpx;
		.navs {
			white-space: nowrap;
			width: 100%;
			// background: #fff;
			box-shadow: 0px 4rpx 24rpx 0px rgba(176,176,176,0.28);
			text-align: center;
			border-radius: 20rpx;
			.item {
				display: inline-block;
				font-size: 28rpx;
				padding: 20rpx 0;
				text-align: center;
				cursor: pointer;
				white-space: nowrap;
				color: #888888;
				padding: 32rpx;
				position: relative;
		
				&.active {
					color: #FFF;
					background: linear-gradient(90deg, #ff9700, #ff6300);
					&::after {
						content: '';
						position: absolute;
						bottom: 0;
						left: 32rpx;
						right: 32rpx;
						// border-bottom: 2px solid #19d919;
					}
				}
			}
		}
		.list{
			flex: 1;
			overflow-y: auto;
			padding: 30rpx;
			margin-left: 4rpx;
			max-height: calc(100vh - 360rpx);
			.item {
				width: 690rpx;
				background: linear-gradient(90deg, #ff9700, #ff6300);
				box-shadow: 0px 4rpx 24rpx 0px rgba(176, 176, 176, 0.28);
				border-radius: 10rpx;
				margin-bottom: 30rpx;
			
				.item-body {
					padding: 32rpx;
					display: flex;
					border-bottom: 1px solid #EAE7E3;
			
					.img {
						width: 120rpx;
						height: 120rpx;
						background-color: #f8f8f8;
						object-fit: contain;
					}
			
					.wrap {
						flex: 1;
						margin-left: 28rpx;
			
						.name {
							font-size: 28rpx;
							font-weight: 800;
							color: #FFF;
							margin-bottom: 10rpx;
						}
			
						.tips {
							font-size: 21rpx;
							font-weight: 500;
							color: #FFF;
							margin-top: 10rpx;
						}
					}
				}
			
				.item-footer {
					height: 75rpx;
					line-height: 75rpx;
					text-align: right;
					padding: 0 38rpx;
					font-weight: bold;
					color: #464646;
					font-size: 32rpx;
				}
			}
		}
		
	}
	
</style>
