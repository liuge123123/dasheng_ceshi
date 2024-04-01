<template>
	<container class="goods-container root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<image class="img" mode="aspectFit" :src="goods.img"></image>
		<view class="info">
			<view class="name">{{goods.name}}</view>
			<view class="price">{{goods.price}} {{$t('app.unit')}}</view>
		</view>
		<view class="desc">
			<view class="desc-title">{{$t('goods.desc.title')}}</view>
			<view class="desc-content">
				<rich-text id="goods-desc-content" v-html="goods.description"></rich-text>
			</view>
		</view>
		<view class="footer">
			<view class="icon" @click="goOnlineService"><i class="iconfont icon-kefu"></i></view>
			<view class="icon" @click="$goUrl('/pages/cart/cart')">
				<i class="iconfont icon-cart"></i>
				<view class="num" v-if="cardCount > 0">{{cardCount}}</view>
			</view>
			<view class="card-btn" @click="addToCart">{{$t('goods.addtocart')}}</view>
			<view class="buy-btn" @click="buyNow">{{$t('goods.buynow')}}</view>
		</view>
	</container>
</template>

<script>
	export default {
		data() {
			return {
				goodsId: null,
				goods: {},
				onlineService: {},
				userInfo: {}
			}
		},
		computed: {
			cardCount(){
				return this.$store.getters['cart/goodsNum'] 
			}
		},
		onLoad(option) {
			this.userInfo = uni.getStorageSync("user")
			this.goodsId = option.goodsId || 27130
			this.getData()
			
			this.$uni.getOnlineService(this.userInfo.salesmanId).then(data => {
				this.onlineService = data
			})
		},
		methods: {
			goOnlineService() {
				window.location.href = this.onlineService.url
			},
			getData(){
				this.$get('/app/shop/goods/info', {
					goodsId: this.goodsId
				}).then(res => {
					this.showPageContent()
					this.goods = res.goods
					setTimeout(() => {
						this.loadImg()
					}, 100)
				})
			},
			addToCart(){
				this.$store.commit('cart/addGoods', {
					num: 1,
					id: this.goodsId,
					name: this.goods.name,
					price: this.goods.price,
					img: this.goods.img
				})
			},
			buyNow(){
				getApp().globalData.shopCheckedGoodsList = [{
					id: this.goods.id,
					num: 1
				}]
				this.$goUrl('/pages/order_preview/order_preview')
			},
			loadImg(){
				let imgs = document.getElementById('goods-desc-content').getElementsByTagName('img');
				if(imgs && imgs.length > 0){
					for (let i = 0; i < imgs.length; i++) {
						let img = imgs[i]
						if(img.dataset && img.dataset.lazy == '' && img.dataset.src && img.dataset.src != ''){
							img.setAttribute('src', img.dataset.src)
						}
					}
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.img{
		width: 100%;
		height: 750rpx;
	}
	.info{
		background: #ffffff;
		padding: 30rpx;
		.name{
			font-size: 28rpx;
		}
		.price{
			font-size: 36rpx;
			color: #f00000;
			margin-top: 20rpx;
			font-weight: bold;
		}
	}
	.desc{
		background-color: #ffffff;
		margin-top: 30rpx;
		margin-bottom: 100rpx;
		.desc-title{
			padding: 20rpx;
			text-align: center;
			font-weight: bold;
			border-bottom: 1rpx solid #eeeeee;
		}
		.desc-content{
			padding: 30rpx;
			overflow-x: hidden;
			position: relative;
			/deep/ *{
				font-size: 28rpx;
				color: #666666;
				max-width: 100%;
				word-break: break-all;
				white-space: break-spaces;
			}
			/deep/ img{
				width: 100% !important;
				height: auto !important;
			}
		}
	}
	.footer{
		position: fixed;
		left: 0;
		bottom: 0;
		display: flex;
		align-items: center;
		width: 100%;
		height: 88rpx;
		line-height: 88rpx;
		background: #ffffff;
		border-top: 1rpx solid #eeeeee;
		text-align: center;
		.icon{
			width: 100rpx;
			border-right: 1rpx solid #eeeeee;
			position: relative;
			.iconfont{
				color: #999999;
				font-size: 36rpx;
			}
			.num{
				position: absolute;
				top: 10rpx;
				right: 10rpx;
				line-height: 30rpx;
				width: 30rpx;
				height: 30rpx;
				background: #f00000;
				color: #ffffff;
				font-size: 24rpx;
				border-radius: 50%;
			}
		}
		.card-btn{
			flex: 1;
			background: linear-gradient(270deg, #FFB729 0%, #FFC544 100%);
			color: #ffffff;
			font-size: 32rpx;
		}
		.buy-btn{
			flex: 1;
			background: linear-gradient(270deg, #FE1C30 0%, #FD614A 100%);
			color: #ffffff;
			font-size: 32rpx;
		}
	}
</style>
