<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="navs">
				<view class="item" :class="selectIndex == 0 ? 'active' : ''" @click="indexChange(0)">
					<text>{{$t('account.recharge')}}</text>
				</view>
				<view class="item" :class="selectIndex == 1 ? 'active' : ''" @click="indexChange(1)">
					<text>{{$t('account.cash')}}</text>
				</view>
			</view>
			<scroll-view scroll-y @scrolltolower="getData">
				<view class="list">
					<view class="item" v-for="item in dataList" :key="item.id">
						<view class="wrap">
							<view class="val">
								<text :class="{red: item.status == 2, blue: item.status == 1}">{{item.amount || item.money}} {{$t('app.unit')}}</text>
							</view>
							<view class="date">
								<text >{{item.status==2? item.remark:''}}</text>
							</view>
							<view class="date"><text>{{timeText(item.createTime)}}</text></view>
						</view>
						<view class="title">
							<text :class="{red: item.status == 2, blue: item.status == 1}">{{typeText(item.status)}}</text>
						</view>
					</view>
					<uni-load-more :status="loadingStatus"></uni-load-more>
				</view>
			</scroll-view>
		</view>
	</container>
</template>
<script>
 	export default {
		components:{
		},
		data() {
			return {
				selectIndex: 0,
				pageNo: 1,
				pageSize: 10,
				dataList: [],
				loadingStatus: 'more' //more/loading/noMore
			}
		},
		computed:{

		},
		onLoad(option){
			this.showPageContent()
		},
		onShow(){
			this.getData()
		},
		methods: {
			getData(){
				if(this.loadingStatus != 'more'){
					return
				}
				if(this.pageNo == 1){
					this.dataList = []
				}
				this.loadingStatus = 'loading'
				this.$get('/app/account/log/list', {
					pageNo: this.pageNo,
					pageSize: this.pageSize,
					type: this.selectIndex
				}).then(res => {
					if(res.code == 0){
						let current = res.data.currPage
						let total = res.data.totalPage
						if(current < total){
							this.pageNo ++
							this.loadingStatus = 'more'
						}else{
							this.loadingStatus = 'noMore'
						}
						res.data.list.forEach(item => {
							this.dataList.push(item)
						})
					}else{
						this.loadingStatus = 'more'
					}
				}).catch(e => {
					this.loadingStatus = 'more'
				})
			},
			indexChange(index){
				this.selectIndex = index
				this.pageNo = 1
				this.loadingStatus = 'more'
				this.getData()
			},
			typeText(val){
				return this.$t('account.status.' + val)
			},
			timeText(val){
				return this.$uni.timeFormat(val)
			}
		}
	}
</script>


<style lang="scss" scoped>
	.container{
		position: relative;
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
		overflow: hidden;
		.navs {
			white-space: nowrap;
			width: 100%;
			// background: #fff;
			box-shadow: 0px 4rpx 24rpx 0px rgba(176,176,176,0.28);
			text-align: center;
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
					color: #19d919;
		
					&::after {
						content: '';
						position: absolute;
						bottom: 0;
						left: 32rpx;
						right: 32rpx;
						border-bottom: 2px solid #19d919;
					}
				}
			}
		}
		.list{
			flex: 1;
			overflow-y: auto;
			padding: 30rpx;
			margin-left: 4rpx;
			max-height: calc(100vh - 260rpx);
			.item{
				background: #1ec8db;
				box-shadow: 0px 4rpx 24rpx 0px rgba(176,176,176,0.28);
				border-radius: 10rpx;
				padding: 30rpx 40rpx;
				display: flex;
				align-items: center;
				margin-bottom: 28rpx;
				.wrap{
					flex: 1;
					border-right: 1rpx solid #EFEFEF;
					.val{
						font-size: 37rpx;
						font-weight: bold;
						color: #282828;
						.blue{
							color: #4E88FF;
						}
						.red{
							color: #FF634E;
						}
					}
					.date{
						font-size: 24rpx;
						color: #FFF;
						margin-top: 27rpx;
					}
				}
				.title{
					font-size: 29rpx;
					font-weight: 500;
					color: #888888;
					width: 175rpx;
					text-align: center;
					.blue{
						color: #4E88FF;
					}
					.red{
						color: #FF634E;
					}
				}
				
			}
		}
		
	}
	
</style>
