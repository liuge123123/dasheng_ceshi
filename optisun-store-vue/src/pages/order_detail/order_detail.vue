<template>
	<container class="root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="status-panel">
			<image class="bg" mode="widthFix" src="../../static/images/shop_order_bg.png" style="width: 100%;"></image>
			<view class="content" v-if="order.orderStatus == 1">{{orderStatusText}}</view>
			<view class="content" v-if="order.orderStatus == 2">{{$t('order.status2')}}</view>
		</view>
		<view class="buyer">
			<view class="input-item">
				<i class="icon ri-user-2-line"></i>
				{{order.buyerName}}  {{order.buyderPhone}}
			</view>
			<view class="input-item area">
				<i class="icon ri-map-pin-user-fill"></i> {{order.buyerAddress}}
			</view>
		</view>
		<view class="goods-list">
			<view class="goods-item" v-for="(item, index) in order.goodsList" :key="index">
				<view class="goods">
					<image class="goods-img" :src="item.goodsImg" mode="aspectFit"></image>
					<view class="goods-info">
						<view class="goods-name">{{item.goodsName}}</view>
						<view class="goods-price">
							{{item.goodsPrice}} {{$t('app.unit')}}
							<label class="goods-num">*{{item.goodsNum}}</label>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="discount-panel">
			<view class="item" v-if="order.couponId == 0">
				<label>{{$t('order.discount.vip')}}：</label>
				<label>{{(10 - order.orderDiscount) * 10}}%</label>
			</view>
			<view class="item" v-else>
				<label>{{$t('order.discount.coupon')}}：</label>
				<label>{{(10 - order.orderDiscount) * 10}}%</label>
			</view>
			<view class="item">
				<label>{{$t('order.discount.money')}}：</label>
				<label>{{(order.orderMoney - order.orderPayMoney).toFixed(4)}} {{$t('app.unit')}}</label>
			</view>
			<view class="item">
				<label>{{$t('order.money.total')}}：</label>
				<label>{{order.orderMoney}} {{$t('app.unit')}}</label>
			</view>
			<view class="item">
				<label>{{$t('order.money.pay')}}：</label>
				<label>{{order.orderPayMoney}} {{$t('app.unit')}}</label>
			</view>
		</view>
	</container>
</template>

<script>
	export default {
		data() {
			return {
				order: {
					goodsList: []
				},
				checkAllFlag: false
			}
		},
		computed: {
			orderStatusText(){
				for (var i = 0; i < this.order.goodsList.length; i++) {
					let goods = this.order.goodsList[i]
					if(goods.goodsId == 10030076){
						return this.$t('order.status3')
					}
				}
				
				let now = new Date().getTime() / 1000;
				if(now - this.order.orderTime < 20 * 24 * 3600){
					let time = this.order.orderTime  + 20 * 24 * 3600;
					let timeStr = this.$uni.timeFormat(time, "YYYY-mm-dd")
					return this.$t('order.status1', {'sendTime': timeStr})
				}else{
					let time = this.order.orderTime  + 40 * 24 * 3600;
					let timeStr = this.$uni.timeFormat(time, "YYYY-mm-dd")
					return this.$t('order.status1_1', {'sendTime': timeStr})
				}
			}
		},
		onLoad(option) {
			this.order.id = option.id
			this.getData()
		},
		methods: {
			getData(){
				this.$get('/app/shop/order/info', {
					orderId: this.order.id
				}).then(res => {
					this.order = res.order
					this.showPageContent()
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.status-panel{
		position: relative;
		.content{
			position: absolute;
			top: 0;
			bottom: 0;
			left: 0;
			right: 0;
			padding: 30rpx;
			display: flex;
			flex-direction: column;
			justify-content: center;
			font-weight: bold;
			color: #70461c;
		}
	}
	.buyer{
		background: #ffffff;
		padding: 30rpx;
		.input-item{
			display: flex;
			font-size: 24rpx;
			font-weight: bold;
			align-items: center;
			&.area{
				font-weight: normal;
				margin-top: 20rpx;
				align-items: flex-start;
			}
			.icon{
				color: #70461c;
				font-weight: bold;
				margin-right: 20rpx;
				font-size: 28rpx;
			}
		}
	}
	.goods-list{
		margin-top: 30rpx;
		.goods-item{
			background: #ffffff;
			display: flex;
			align-items: center;
			padding: 30rpx;
			border-bottom: 1rpx solid #eee;
			.goods{
				margin-left: 20rpx;
				flex: 1;
				display: flex;
				align-items: center;
				.goods-img{
					width: 160rpx;
					height: 160rpx;
					border: 1px solid #eee;
					border-radius: 8rpx;
				}
				.goods-info{
					flex: 1;
					margin-left: 20rpx;
					font-size: 24rpx;
					display: flex;
					flex-direction: column;
					justify-content: space-between;
					position: relative;
					min-height: 160rpx;
					.goods-name{
						
					}
					.goods-price{
						color: #f00000;
						margin: 10rpx 0;
						.goods-num {
							color: #000000;
							margin-left: 20rpx;
						}
					}
				}
			}
		}
	}
	.discount-panel{
		background: #ffffff;
		margin-top: 30rpx;
		padding: 30rpx;
		.item{
			display: flex;
			justify-content: space-between;
			font-size: 24rpx;
			padding: 10rpx 0;
		}
	}
	.footer{
		background: #ffffff;
		position: fixed;
		left: 0;
		bottom: 0;
		right: 0;
		padding: 30rpx;
		display: flex;
		justify-content: space-between;
		.total-money{
			color: #f00000;
		}
		.buy-btn{
			background: linear-gradient(270deg, #FB2B36 0%, #FD614A 100%);
			border-radius: 25rpx;
			color: #ffffff;
			font-size: 26rpx;
			line-height: 50rpx;
			padding: 0 20rpx;
		}
	}
</style>
