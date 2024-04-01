<template>
	<container class="cart-container root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="goods-list">
			<view class="goods-item" v-for="(item, index) in goodsList" :key="index">
				<view class="checkbox"><checkbox :checked="isChecked(item.id) != -1" @click="checkItem(item)"></checkbox></view>
				<view class="goods">
					<image class="goods-img" :src="item.img" mode="aspectFit"></image>
					<view class="goods-info">
						<view class="goods-name">{{item.name}}</view>
						<view class="goods-price">{{item.price}} {{$t('app.unit')}}</view>
						<view class="goods-num">
							<i class="btn ri-subtract-line" @click="changeNum(item, -1)"></i>
							<label class="num">{{item.num}}</label>
							<i class="btn ri-add-line" @click="changeNum(item, 1)"></i>
						</view>
						<view class="del-btn" @click="delGoods(item)"><i class="ri-delete-bin-5-line"></i></view>
					</view>
				</view>
			</view>
		</view>
		<view class="footer">
			<view class="check-all">
				<checkbox class="checkbox" :checked="checkAllFlag" @click="checkAll"></checkbox>{{$t('cart.checkall')}}
			</view>
			<view class="total-money">{{totalMoney}} {{$t('app.unit')}}</view>
			<view class="buy-btn" @click="buyNow">{{$t('cart.gobuy')}}</view>
		</view>
	</container>
</template>

<script>
	export default {
		data() {
			return {
				checkList: [],
				checkAllFlag: false
			}
		},
		computed: {
			cardCount(){
				return this.$store.getters['cart/goodsNum'] 
			},
			goodsList(){
				return this.$store.getters['cart/goodsList']
			},
			totalMoney(){
				let money = 0
				this.checkGoodsList.forEach(item => {
					money += (item.price * item.num)
				})
				return money
			},
			checkGoodsList(){
				let checkGoodsList = []
				for(let i = 0; i < this.checkList.length; i++){
					let id = this.checkList[i]
					for (let j = 0; j < this.goodsList.length; j++) {
						let goods = this.goodsList[j]
						if(id == goods.id){
							checkGoodsList.push(goods)
							break
						}
					}
				}
				return checkGoodsList
			}
		},
		onLoad(option) {
			this.showPageContent()
			this.checkAll()
		},
		methods: {
			isChecked(id){
				for (var i = 0; i < this.checkList.length; i++) {
					if(id == this.checkList[i]){
						return i;
					}
				}
				return -1;
			},
			checkItem(item){
				let index = this.isChecked(item.id);
				if(index == -1){
					this.checkList.push(item.id)
				}else{
					this.checkList.splice(index , 1)
				}
			},
			checkAll(){
				this.checkList = []
				this.checkAllFlag = !this.checkAllFlag
				if(this.checkAllFlag){
					for (var i = 0; i < this.goodsList.length; i++) {
						let item = this.goodsList[i]
						this.checkList.push(item.id)
					}
				}
			},
			changeNum(goods, opt){
				if(opt == 1){
					this.$store.commit('cart/addGoods', goods);
				}else{
					if(goods.num > 1){
						this.$store.commit('cart/subGoods', goods);
					}
				}
			},
			delGoods(goods){
				this.$store.commit('cart/removeGoods', goods.id);
				let checkedIndex = this.checkList.indexOf(goods.id);
				if(checkedIndex != -1){
					this.checkList.splice(checkedIndex, 1);
				}
			},
			buyNow(){
				if(this.checkGoodsList.length == 0){
					uni.showToast({
						icon:'none',
						title: this.$t('cart.checkedgoods.null')
					})
				}else{
					getApp().globalData.shopCheckedGoodsList = this.checkGoodsList
					this.$goUrl('/pages/order_preview/order_preview')
				}
			}
		}
	}
</script>

<style scoped lang="scss">
	.checkbox{
		/deep/ .uni-checkbox-input{
			border-radius: 50%;
			transform: scale(0.7);
			&.uni-checkbox-input-checked{
				background-color: #f00000;
				color: #ffffff !important;
				border: 1px solid #f00000;
			}
		}
	}
	.goods-list{
		margin-bottom: 120rpx;
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
					}
					.goods-num{
						display: flex;
						line-height: 40rpx;
						border: 1rpx solid #eee;
						width: 150rpx;
						.btn{
							background: #eee;
							width: 40rpx;
							text-align: center;
						}
						.num{
							width: 70rpx;
							text-align: center;
						}
					}
					.del-btn{
						position: absolute;
						bottom: 0;
						right: 0;
						color: #999999;
						padding: 5rpx;
					}
				}
			}
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
