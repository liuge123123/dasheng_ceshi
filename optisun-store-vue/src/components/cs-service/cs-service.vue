<template>
	<uni-popup ref="popup" type="bottom" style="z-index: 99999;">
		<view class="panel">
			<view class="title">
				{{$t('app.service')}}
			</view>
			<i @click="close" class="close-btn  ri-close-line"></i>
			<view class="list">
				<view class="item" v-if="csConfig.ws && csConfig.ws != ''" @click="goUrl('ws')">
					<image class="img" src="../../static/images/ws.png"></image>
					<view class="text">WhatsApp</view>
				</view>
				<view class="item" v-if="csConfig.wsGroup && csConfig.wsGroup != ''" @click="goUrl('wsGroup')">
					<image class="img" src="../../static/images/wsgroup.png"></image>
					<view class="text">WhatsApp Group</view>
				</view>
				<view class="item" v-if="csConfig.tgGroup && csConfig.tgGroup != ''" @click="goUrl('tgGroup')">
					<image class="img" src="../../static/images/tg.png"></image>
					<view class="text">Telegram Group</view>
				</view>
			</view>
		</view>
	</uni-popup>
</template>

<script>
	export default {
		name:"cs-service",
		data() {
			return {
				csConfig: {
					ws: '',
					wsGroup: '',
					tgGroup: ''
				}
			};
		},
		mounted() {
		},
		methods: {
			open(custId){
				uni.showLoading({
					title: 'loading...'
				})
				this.$uni.getOnlineService(custId).then(data => {
					uni.hideLoading()
					this.csConfig = data
					this.$refs.popup.open()
				})
				
			},
			goUrl(type){
				let url = this.csConfig[type]
				if(url && url != ''){
					window.location.href = url
				}
			},
			close(){
				this.$refs.popup.close()
			}
		}
	}
</script>

<style scoped lang="scss">
	.panel{
		background: #fff;
		width: 750rpx;
		position: relative;
		border-top-left-radius: 30rpx;
		border-top-right-radius: 30rpx;
		.title{
			font-weight: bold;
			padding: 30rpx;
			color: #3B5074;
			text-align: center;
			font-size: 45rpx;
		}
		.close-btn{
			position: absolute;
			top: 0rpx;
			right: 0rpx;
			padding: 30rpx;
			cursor: pointer;
		}
		.list{
			display: flex;
			padding: 30rpx;
			.item{
				flex: 1;
				text-align: center;
				cursor: pointer;
				.img{
					width: 150rpx;
					height: 150rpx;
				}
				.text{
					font-size: 30rpx;
					font-weight: 600;
				}
			}
		}
	}
	
</style>