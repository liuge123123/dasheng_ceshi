<template>
	<container class="order-container root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="buyer">
			<view class="input-item">
				<label class="label">{{$t('order.buyername')}}：</label>
				<input class="input" v-model="order.buyerName" :placeholder="$t('order.buyerplaceholder')" />
			</view>
			<view class="input-item">
				<label class="label">{{$t('order.buyerphone')}}：</label>
				<input class="input" v-model="order.buyderPhone" :placeholder="$t('order.buyerplaceholder')" />
			</view>
			<view class="input-item area">
				<label class="label">{{$t('order.buyeraddress')}}：</label>
				<textarea class="input" v-model="order.buyerAddress" :placeholder="$t('order.buyerplaceholder')"></textarea>
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
		<view class="footer">
			<view class="total-money">
				<label style="color: #000000;margin-right: 10rpx;font-size: 24rpx">{{$t('order.total')}}:</label>
				{{order.orderPayMoney}} {{$t('app.unit')}}
			</view>
			<view class="buy-btn" @click="payNow">{{$t('order.payNow')}}</view>
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

		},
		onLoad(option) {
			this.getData()
		},
		methods: {
			getData(){
				let goodsList = getApp().globalData.shopCheckedGoodsList
				if(goodsList && goodsList.length > 0){
					getApp().globalData.shopCheckedGoodsList = []
					this.$post('/app/shop/order/preview', {
						goodsList: goodsList
					}).then(res => {
						this.order = res.order
						this.setBuyer()
						this.showPageContent()
					})
				}else{
					this.showPageEmpty()
				}
			},
			setBuyer(){
				let cacheBuyer = uni.getStorageSync("shop_buyer")
				this.order.buyerName = cacheBuyer.buyerName
				this.order.buyderPhone = cacheBuyer.buyderPhone
				this.order.buyerAddress = cacheBuyer.buyerAddress
			},
			payNow(){
				if(this.order.buyerName == '' 
				|| this.order.buyderPhone == '' 
				|| this.order.buyerAddress == ''
				|| !this.order.buyerName
				|| !this.order.buyderPhone
				|| !this.order.buyerAddress){
					uni.showToast({
						icon: 'none',
						title: this.$t('order.addressnull')
					})
					return;
				}
				uni.setStorageSync("shop_buyer", {
					buyerName: this.order.buyerName,
					buyderPhone: this.order.buyderPhone,
					buyerAddress: this.order.buyerAddress
				})
				uni.showLoading({
					title: 'loading...',
					mask: true
				})
				this.$post('/app/shop/order/buy', this.order).then(res => {
					uni.hideLoading()
					if(res.code == 0){
						this.order = res.order
						let goodsIds = this.order.goodsList.map(item => {
							return item.goodsId
						})
						this.$store.commit('cart/removeGoodsList', goodsIds)
						uni.redirectTo({
							url: '/pages/order_success/order_success?id=' + this.order.id
						})
					}else{
						uni.showToast({
							icon: 'none',
							title: this.$t('error.' + res.code)
						})
					}
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.buyer{
		background: #ffffff;
		padding: 30rpx;
		.input-item{
			display: flex;
			border-bottom: 1rpx solid #eee;
			line-height: 80rpx;
			align-items: center;
			font-size: 24rpx;
			&.area{
				align-items: flex-start;
				.input{
					padding: 25rpx 0;
					height: 100rpx;
				}
			}
			.label{
				color: #666;
			}
			.input{
				flex: 1;
				font-size: 24rpx;
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
		margin-bottom: 150rpx;
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
