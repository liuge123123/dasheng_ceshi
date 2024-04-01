<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="room-list">
				<view class="item" @click="goDetail(item)" v-for="(item, index) in levelList" :key="item.id">
					<view class="item-body">
						<!-- <image :src="'https://www.openai.bar/upload/vipbanner/' + item.id + '.jpg'" style="width: 100%;" mode="widthFix"></image> -->
						<image :src="item.vipBanner" style="width: 100%;" mode="widthFix"></image>
					</view>
					<view class="item-footer">
						<view class="wrap">
							<view class="title">
								<text>{{item.gradeName}}</text>
								<text style="margin-left: 20rpx;">*{{item.rate}}%</text>
							</view>
							<text class="money">{{$t('grabbing.unlock.money')}}：<text style="font-size: 28rpx;font-weight: bold;"> {{item.money}} - {{item.parameter1}} {{$t('app.unit')}}</text></text>
							<view class="desc" v-html="item.content"></view>
						</view>
						<view class="btn locked" v-if="item.id != userData.level">{{$t('grabbing.unlock')}}</view>
						<view class="btn" v-else>{{$t('grabbing.go')}}</view>
					</view>
				</view>
			</view>
		</view>
		<toolbar :index="3"></toolbar>
	</container>
</template>

<script>
	export default {
		components: {

		},
		data() {
			return {
				levelList: [],
				userData: {}
			}
		},
		computed: {

		},
		onShow() {
			this.$uni.getUserInfo().then(res => {
				this.getData()
			})
		},
		onLoad() {
			// this.getData()
		},
		methods: {
			getData(){
				this.showPageLoading()
				this.getLevelList().then(res => {
					return this.getTotalData()
				}).then(res => {
					this.showPageContent()
				}).catch(error => {
					this.showPageError(this.$t('error.' + error.code))
				})
			},
			getLevelList(){
				return new Promise((resolve, reject) => {
					this.$get('/app/account/level/list').then(res => {
						this.serviceTime = res.timestamp
						if(res.code == 0){
							this.levelList = res.data
							resolve()
						}else{
							reject(res)
						}
					}).catch(e => {
						reject({code : 500})
					})
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
			goDetail(item){
				let itemLevel = item.id
				let userLevel = this.userData.level
				if(itemLevel != userLevel){
					uni.showToast({
						icon: 'none',
						title: this.$t('grabbing.not.access')
					})
				} else{
					this.$goUrl('/pages/grabbing_detail/grabbing_detail?id=' + item.id)
				}				
			}
		}
	}
</script>

<style scoped lang="scss">
	.container{
		padding: 30rpx;
		margin-bottom: 100px;
		.room-list{
			.item{
				border-radius: 16rpx;
				margin-bottom: 30rpx;
				color: #666;
				background-color: #fff;
				border-radius: 20rpx;
				overflow: hidden;
				.item-body{
					width: 690rpx;
					height: 234rpx;
					display: flex;
					.icon{
						width: 120rpx;
						height: 120rpx;
						background-color: #FFFFFF;
						border-radius: 50%;
						text-align: center;
						line-height: 120rpx;
						font-size: 84rpx;
						box-shadow: 0 2px 12px 2px rgba(233,214,164,0.5);
						&.color0{
							color: #2F84F4;
						}
						&.color1{
							color: #19C48B;
						}
						&.color2{
							color: #02C2C9;
						}
						&.color3{
							color: #685EEF;
						}
						&.color4{
							color: #FC5957;
						}
						&.color5{
							color: #FB9358;
						}
						&.color6{
							color: #E8B51A;
						}
						&.color7{
							color: #D9922C;
						}
					}
				}
				.item-footer{
					padding: 20rpx;
					display: flex;
					align-items: flex-start;
					position: relative;
					.wrap{
						flex: 1;
						display: flex;
						flex-direction: column;
						justify-content: center;
						.title{
							font-size: 32rpx;
							font-weight: bold;
							color: #191514;
						}
						.money{
							font-size: 24rpx;
							margin-top: 10rpx;
							color: #6F4935;
						}
						.desc{
							font-size: 24rpx;
							color: #6F4935;
							margin-top: 10rpx;
						}
					}
					.btn{
						font-size: 28rpx;
						font-weight: bold;
						color: #FFFFFF;
						height: 50rpx;
						line-height: 50rpx;
						background: linear-gradient(183deg, #E9D4A5 0%, #C49050 100%);
						border-radius: 31rpx;
						text-align: center;
						padding: 0 40rpx;
						position: absolute;
						right: 15rpx;
						top: 15rpx;
						&.locked{
							opacity: 0.5;
						}
					}
				}
			}
		}
		
	}
</style>
