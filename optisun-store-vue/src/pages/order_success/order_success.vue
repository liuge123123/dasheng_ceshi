<template>
	<container class="order-container root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="success-panel">
			<i class="icon ri-emotion-happy-fill"></i>
			<view class="text">{{$t('order.buy.success')}}</view>
			<button class="btn1" type="primary" @click="goDetail">{{$t('order.godetail')}}</button>
			<button class="btn2" @click="goShop">{{$t('order.goshop')}}</button>
		</view>
	</container>
</template>

<script>
	export default {
		data() {
			return {
				orderId: null
			}
		},
		onLoad(option) {
			this.orderId = option.id
			this.showPageContent()
			this.$uni.getUserInfo()
		},
		methods: {
			goDetail(){
				this.$goUrl('redirect://pages/order_detail/order_detail?id=' + this.orderId)
			},
			goShop(){
				this.$goUrl('redirect://pages/shop/shop')
			}
		}
	}
</script>

<style scoped lang="scss">
	.success-panel{
		display: flex;
		flex-direction: column;
		justify-content: center;
		height: 100%;
		margin-top: -100rpx;
		align-items: center;
		.icon{
			font-size: 256rpx;
			color: #B4722D;
		}
		.text{
			font-size: 32rpx;
			margin-top: 50rpx;
			max-width: 600rpx;
		}
		.btn1{
			background: #B4722D;
			margin-top: 50rpx;
			width: 600rpx;
		}
		.btn2{
			margin-top: 50rpx;
			width: 600rpx;
		}
	}
</style>
