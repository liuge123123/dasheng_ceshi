<template>
	<container class="root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="top-panel">
			<view class="seach-panel">
				<input class="input" v-model="query.name" :placeholder="$t('shop.search.name')" confirm-type="search" @confirm="searchClick"/>
				<i class="icon iconfont icon-search" @click="searchClick"></i>
				<view class="cart-btn" @click="$goUrl('/pages/cart/cart')">
					<i class="iconfont icon-cart"></i>
					<view class="num">{{cardCount}}</view>
				</view>
			</view>
			<view class="nav-panel">
				<view class="nav-item">
					<picker @change="categoryChange" :range="categoryList" range-key="category1">
						<label>{{$t('shop.category')}}</label>
						<i class="iconfont icon-shaixuan"></i>
					</picker>
				</view>
				<view class="nav-item" @click="priceClick">
					<label>{{$t('shop.price')}}</label>
					<i class="iconfont icon-search"></i>
				</view>
				<view class="nav-item" @click="sortClick">
					<label>{{$t('shop.sort')}}</label>
					<i class="iconfont icon-sort"></i>
				</view>
			</view>
			<view class="search-key">
				<label class="item" v-if="query.name != ''">{{query.name}}</label>
				<label class="item" v-if="query.category1 != ''">{{query.category1}}</label>
				<label class="item" v-if="query.priceStart != '' && query.priceEnd != ''">{{query.priceStart}} - {{query.priceEnd}}</label>
			</view>
		</view>
		<view class="goods-list">
			<view class="goods-item" v-for="(item, index) in goodsList" :key="index" @click="$goUrl('/pages/goods/goods?goodsId=' + item.id)">
				<image mode="aspectFit" class="goods-img" :src="item.img"></image>
				<view class="goods-name">{{item.name}}</view>
				<view class="goods-price">{{item.price}} {{$t('app.unit')}}</view>
			</view>
		</view>
		<uni-popup animation type="bottom" ref="pricePopup">
			<view class="price-popup">
				<view class="header">
					<view class="cancel" @click="priceCancel">{{$t('common.cancel')}}</view>
					<view class="title">{{$t('shop.price.popup.title')}}</view>
					<view class="confirm" @click="priceConfirm">{{$t('common.confirm')}}</view>
				</view>
				
				<view class="input-item">
					<input type="digit" class="input" v-model="price.start" :placeholder="$t('shop.price.start')">
					-
					<input type="digit" class="input" v-model="price.end" :placeholder="$t('shop.price.end')">
				</view>
			</view>
		</uni-popup>
	</container>
</template>

<script>
	export default {
		data() {
			return {
				query: {
					page: 0,
					limit: 20,
					name: '',
					category1: '',
					priceStart: '',
					priceEnd: '',
					status: 1,
					sortField: 'sortNum',
					source: null
				},
				price: {
					start: '',
					end: ''
				},
				goodsList: [],
				categoryList: [],
				bottomFlag: false
			}
		},
		computed: {
			cardCount(){
				return this.$store.getters['cart/goodsNum'] 
			}
		},
		onLoad(option) {
			this.query.source = option.source || ''
			this.getData()
		},
		onReachBottom() {
			if(!this.bottomFlag){
				this.getGoodsList()
			}
		},
		methods: {
			getData(){
				this.getGoodsList().then(res => {
					this.showPageContent()
					return this.getCategory()
				})
			},
			getCategory(){
				return  new Promise((resolve, reject) => {
					this.$get('/app/shop/category/list', this.query).then(res => {
						if(res.code == 0){
							this.categoryList = res.list
						}
						resolve(res)
					})
				}) 
			},
			getGoodsList(){
				this.query.page = this.query.page + 1
				return  new Promise((resolve, reject) => {
					this.$get('/app/shop/goods/list', this.query).then(res => {
						if(res.code == 0){
							if(res.page.currPage == 1){
								this.goodsList = []
							}
							 res.page.list.forEach(item => {
								this.goodsList.push(item)
							})
							this.bottomFlag = (res.page.totalPage == res.page.currPage)
						}
						resolve(res)
					})
				}) 
			},
			searchClick(){
				this.query.page = 0
				this.bottomFlag = false;
				this.showPageLoading()
				this.getGoodsList().then(res => {
					this.showPageContent();
				})
			},
			categoryChange(e){
				const index = e.detail.value
				this.query.category1 = this.categoryList[index].category1
				this.query.page = 0;
				this.getGoodsList()
			},
			priceClick(){
				this.$refs.pricePopup.open()
			},
			priceCancel(){
				this.$refs.pricePopup.close()
			},
			priceConfirm(){
				this.$refs.pricePopup.close()
				this.query.priceStart = this.price.start
				this.query.priceEnd = this.price.end
				this.query.page = 0;
				this.getGoodsList()
			},
			sortClick(){
				uni.showActionSheet({
					itemList: [this.$t('shop.sort.sortNum'), this.$t('shop.sort.price_asc'), this.$t('shop.sort.price_desc')],
					success: res => {
						const index = res.tapIndex
						console.log(index)
						if(index == 0){
							this.query.sortField = 'sortNum'
						}else if(index == 1){
							this.query.sortField = 'price_asc'
						}else if(index == 2){
							this.query.sortField = 'price_desc'
						}
						this.query.page = 0
						this.getGoodsList()
					}
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.top-panel{
		background: #fff;
		.seach-panel{
			padding: 30rpx;
			position: relative;
			border-bottom: 1rpx solid #f5f5f5;
			display: flex;
			align-items: center;
			.input{
				background: #f5f5f5;
				padding: 10rpx 20rpx;
				border-radius: 40rpx;
				flex: 1;
			}
			.icon{
				position: absolute;
				top: 30rpx;
				right: 100rpx;
				line-height: 65rpx;
			}
			.cart-btn{
				padding-left: 20rpx;
				color: #B4722D;
				position: relative;
				.num{
					position: absolute;
					top: -20rpx;
					right: -20rpx;
					line-height: 30rpx;
					width: 30rpx;
					height: 30rpx;
					background: #f00000;
					color: #ffffff;
					font-size: 24rpx;
					border-radius: 50%;
					text-align: center;
				}
			}
		}
		.nav-panel{
			display: flex;
			padding: 20rpx 0;
			font-size: 2rpx;
			border-bottom: 1rpx solid #f5f5f5;
			.nav-item{
				flex: 1;
				text-align: center;
				display: flex;
				align-items: center;
				justify-content: center;
				.iconfont{
					font-size: 24rpx;
					padding-left: 10rpx;
				}
			}
		}
		.search-key{
			padding: 30rpx;
			background: #f8f8f8;
			.item{
				font-size: 24rpx;
				background: #B4722D;
				color: #fff;
				padding: 5rpx 10rpx;
				border-radius: 8rpx;
				margin-right: 10rpx;
			}
		}
	}
	.goods-list{
		display: flex;
		flex-wrap: wrap;
		padding: 0 30rpx 30rpx;
		.goods-item{
			background: #fff;
			width: calc(50% - 16rpx);
			margin-bottom: 30rpx;
			cursor: pointer;
			border-radius: 16rpx;
			&:nth-child(2n){
				margin-left: 30rpx;
			}
			.goods-img{
				width: 100%;
				height: 330rpx;
			}
			.goods-name{
				padding: 20rpx;
				font-size: 24rpx;
				overflow: hidden;
				text-overflow: ellipsis; 
				display: -webkit-box;
				-webkit-line-clamp: 2;
				-webkit-box-orient: vertical;
				height: 50rpx;
			}
			.goods-price{
				padding: 0 20rpx 20rpx;
				font-size: 36rpx;
				color: #f00000;
			}
		}
	}
	.price-popup{
		background-color: #ffffff;
		padding-bottom: 50rpx;
		.header{
			display: flex;
			padding: 30rpx;
			border-bottom: 1rpx solid #eee;
			font-size: 28rpx;
			.title{
				flex: 1;
				text-align: center
			}
			.cancel{
				width: 150rpx;
				color: #999999;
			}
			.confirm{
				width: 150rpx;
				text-align: right;
				color: #B4722D;
			}
		}
		.input-item{
			margin: 30rpx 30rpx 0;
			border: 1rpx solid #eee;
			display: flex;
			align-items: center;
			padding: 20rpx;
			.input{
				flex: 1;
				text-align: center;
			}
		}
	}
</style>
