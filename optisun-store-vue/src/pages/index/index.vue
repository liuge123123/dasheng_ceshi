<template>
	<container class="root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<swiper autoplay class="swiper">
				<swiper-item v-for="(item, index) in swiper" :key="index">
					<image class="img" :src="item.imgUrl" mode="aspectFill"></image>
				</swiper-item>
			</swiper>
			
			<view class="notice-panel">
				<image class="icon" src="../../static/images/notice.png"></image>
				<view class="text">
					<swiper class="notice-swiper" autoplay vertical circular :interval="3000" :duration="300">
						<swiper-item class="notice-swiper-item" v-for="(item, index) in noticeList" :key="index">
							{{ item }}
						</swiper-item>
					</swiper>
				</view>
			</view>
			
			<view class="account-actions">
				<view class="btn" @click="gotoUrl(download.andriod)">
					<image src="../../static/images/google-play.png" mode="aspectFit" style="width: 100%;height: 100%;">
					</image>
				</view>
				<view class="btn" @click="gotoUrl(download.ios)">
					<image src="../../static/images/app-store.png" mode="aspectFit" style="width: 100%;height: 100%;">
					</image>
				</view>
			</view>
			<view class="func-panel">
				<view class="func-item" @click="$goUrl('/pages/guide/guidelist?code='+item.linkUrl)" v-for="(item, index) in swiper" :key="index">
					<image class="icon" :src="item.imgUrl" ></image>
					<text class="text">{{item.advertiseName}}</text>
				</view>
				<view class="func-item" style="background: transparent;">
					<view v-if="model.type==1" class="model-content" v-html="model.content"></view>
					<image v-else-if="model.type==2" class="img" :src="model.img" ></image>
					<video v-else-if="model.type==3" class="video" :controls="false" :enable-progress-gesture="false" :show-center-play-btn="disable" :src="model.video"></video>
				</view>
				
			</view>

			<view class="kefu" @click="goOnlineService">
				<image src="../../static/images/index_kf.png" mode="aspectFit" style="width: 100%;height: 100%;"></image>
			</view>
			
		</view>

		<uni-popup ref="popup" type="center">
			<view class="model">
				<view class="model-wrap">
					<view class="model-title">
						<text>{{ $t('index.model.title') }}</text>
					</view>
					<view class="model-content" v-html="model.content"></view>
					<view class="model-footer" @click="closeModel">
						<button class="btn">{{ $t('index.model.close') }}</button>
						<button class="btn">{{ $t('index.model.confirm') }}</button>
					</view>
				</view>
			</view>
		</uni-popup>
		<cs-service ref="csService"></cs-service>
		<toolbar></toolbar>
	</container>
</template>
<script>
export default {
	components: {},
	data() {
		return {
			noticeList: [],
			swiper: [],
			onlineService: {},
			video: '',
			userInfo: {},
			model: {
				enable: false,
				content: '',
				img:'',
				link:'',
				type:0,
				video:''

			},
			download:null,
			localModelVis: getApp().globalData.home_model
		}
	},
	computed: {

	},
	onLoad(option) {
		getApp().globalData.home_model = true
		this.userInfo = uni.getStorageSync("user")
		this.$uni.getOnlineService(this.userInfo.custId).then(data => {
			console.log(data)
			this.onlineService = data
		})
		if (!this.localModelVis) {
			this.$uni.getConfig('AD_SETTING').then(res => {
				if (res && res != '') {
					let data = JSON.parse(res)
					this.model = data
					console.log(this.model)
					if (!this.localModelVis && this.model.enable) {
						this.$refs.popup.open()
					}
				}
			})
		}
		this.getAds()
		this.getNotice()
		this.getVideo()
		this.getdownload()
	},
	onShow() {
		this.$uni.getConfig('AD_SETTING').then(res => {
			if (res && res != '') {
				let data = JSON.parse(res)
				this.model = data
				console.log(this.model)
			}
		})
		this.getData()
	},
	methods: {
		getdownload() {
			this.$get('/app/download').then(res => {
				if (res.code == 0) {
					this.download = res.data
				}
			})
		},
		getData() {
			this.showPageContent()
		},
		goOnlineService() {
			window.location.href = this.onlineService.ws;
			// this.$refs['csService'].open(this.userInfo.custId)
		},
		closeModel() {
			this.localModelVis = true
			getApp().globalData.home_model = true
			this.$refs.popup.close()
		},
		getAds() {
			this.$get('/app/content/ad').then(res => {
				if (res.code == 0) {
					this.swiper = res.data
				}
			})
		},
		getNotice() {
			this.$get('/app/content/notice').then(res => {
				if (res.code == 0) {
					this.noticeList = res.data
				}
			})
		},
		getVideo() {
			this.$get('/app/content/video').then(res => {
				if (res.code == 0) {
					this.video = res.data
					console.log(this.video)
				}
			})
		},
		gotoUrl(url){
			window.open(url)
		}
	}
}
</script>


<style lang="scss" scoped>
.root-container {
	overflow-y: auto;
}

.container {
	// background: url("../../static/images/index_top.png") top no-repeat;
	// background-size: 100% auto;
	padding-bottom: constant(safe-area-inset-bottom);
	padding-bottom: env(safe-area-inset-bottom);
	margin-bottom: 100px;
	background: #ffffff;
	.notice-panel {
		display: flex;
		align-items: center;
		padding: 20rpx;
		height: 72rpx;

		.icon {
			width: 31rpx;
			height: 32rpx;
		}

		.text {
			flex: 1;
			font-size: 28rpx;

			.notice-swiper {
				color: #fff;
				height: 32rpx;
				line-height: 32rpx;
				margin-left: 20rpx;
				color: #fe9901;
				font-size: 24rpx;
			}
		}
	}

	.video {
		width: 100%;
	}

	.account-actions {
		width: 90%;
		margin: auto;
		position: relative;
		display: flex;
		justify-content: space-between;
		// margin-top: 33rpx;
		margin-bottom: 35rpx;

		.btn {
			flex: 1;
			height: 80rpx;
			line-height: 80rpx;
			text-align: center;
			// background: linear-gradient(-26deg, #7C521E 23%, #CD9856 100%);
			// background: linear-gradient(90deg, #ff9700, #ff6300);
			border-radius: 19px;
			font-size: 32rpx;
			font-weight: 500;
			color: #FFFFFE;

			&:first-child {
				margin-right: 30rpx;
			}
		}
	}

	.swiper {
		height: 400rpx;
		padding-top: 20rpx;
		margin: 0 30rpx 30rpx;
		background: rgba(#ffffff, 0.1);
		border-radius: 20rpx;
		//box-shadow: 0 0 12px 5px rgba(0, 0, 0, 0.3);
		overflow: hidden;

		.img {
			width: 100%;
			height: 100%;
		}
	}

	.func-panel {
		display: flex;
		flex-wrap: wrap;
		justify-content: space-between;
		padding: 0 40rpx;
		//padding-bottom: 400rpx;
		background-color: #ffffff;
		.func-item {
			display: flex;
			// flex-direction: column;
			align-items: center;
			width: 100%;
			margin-bottom: 20rpx;
			background: #FBBFCB;
			padding: 10rpx 0;
			border-radius: 10rpx;

			.icon {
				margin-left: 20rpx;
				width: 150rpx;
				height: 110rpx;
			}

			.text {
				font-size: 28rpx;
				// color: #1ec8db;
				// margin-top: 25rpx;
				margin-left: 20rpx;
				text-align: center;
			}
			.img{
				border-radius: 20rpx;
				width: 100%;
			}
		}
	}

	.kefu {
		position: fixed;
		right: 10px;
		bottom: 100px;
		width: 160rpx;
		height: 160rpx;
		border-radius: 50%;
		text-align: center;
		font-size: 42rpx;
		z-index: 100;
	}
}

.model {
	display: flex;
	position: fixed;
	z-index: 999;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.3);
	display: flex;
	align-items: center;
	justify-content: center;

	.model-wrap {
		width: 536rpx;
		display: flex;
		flex-direction: column;

		.model-title {
			text-align: center;
			color: #caa160;
			font-size: 32rpx;
			height: 126rpx;
			line-height: 126rpx;
			//background: url('../../static/images/index_notice_header.jpg');
			//background-size: cover;
			background-color: #ffffff;
			text {
				display: block;
				font-size: 37rpx;
				font-weight: 800;
				color: #7C551D;
				// padding-top: 20rpx;
			}
		}

		.model-content {
			background: #fff;
			line-height: 1.5em;
			flex: 1;
			overflow-y: auto;
			padding: 30rpx;
			min-height: 300rpx;
			max-height: 500rpx;
		}

		.model-footer {
			display: flex;
			background-color: #ffffff;
			border-radius: 0px 0px 10px 10px;
			border-top: 1rpx solid #F2F2F2;
			height: 100rpx;

			.btn {
				flex: 1;
				font-size: 32rpx;
				line-height: 100rpx;
				background: none;
				color: #AE7735;

				&::after {
					border: none;
				}

				&:first-child {
					color: #888888;
					border-right: 1px solid #F2F2F2;
				}
			}
		}
	}
}</style>
