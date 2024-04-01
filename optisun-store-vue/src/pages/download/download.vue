<template>
	<container class="container" ref="container" :headerVisable="true" :status="pageStatus">
		<image mode="widthFix" class="header-img" src="../../static/images/login_header.png"></image>
		<view class="content">
			<view :style="{ height: topHeight }"></view>
			<view class="form">
				<view class="title">
					<image class="logo" src="../../static/images/login_logo.png"></image>
				</view>
				<view class="mt200 flex">
					<view class="submit-btn" @click="gotoUrl(download.andriod)">
						<image src="../../static/images/google-play.png" mode="aspectFit" style="width: 100%;height: 100%;">
						</image>
					</view>
					<view class="submit-btn" @click="gotoUrl(download.ios)">
						<image src="../../static/images/app-store.png" mode="aspectFit" style="width: 100%;height: 100%;">
						</image>
					</view>
				</view>
			</view>
		</view>
	</container>
</template>

<script>
export default {
	data() {
		return {
			download: null
		}
	},
	computed: {
		topHeight() {
			return uni.getSystemInfoSync().statusBarHeight + 44 + 'px'
		}
	},
	onLoad() {
		this.$get('/app/download').then(res => {
			if (res.code == 0) {
				this.download = res.data
			}
		})
		this.showPageContent()

	},
	methods: {
		gotoUrl(url) {
			window.open(url)
			// window.location.href =url
		}
	}
}
</script>

<style lang="scss" scoped>
.container {
	background: #FFFFFF;
	overflow-y: auto;
}

.header-img {
	position: absolute;
	left: 0;
	top: 0;
	width: 100%;
}

.mt200 {
	margin-top: 400rpx;
}

.content {
	height: 100%;
	z-index: 1;
	position: relative;
	width: 100%;
	background: url('../../static/images/login-bg.jpg');

	.form {
		padding: 32rpx 51rpx;

		.title {
			text-align: center;

			.logo {
				width: 200rpx;
				height: 200rpx;
				border-radius: 50%;
				border: 2px #fdb523 solid;
			}
		}

		.flex {
			display: flex;
		}

		.submit-btn {
			flex: 1;
			margin: 10rpx;
			height: 88rpx;
			line-height: 88rpx;
			text-align: center;
			// background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
			box-shadow: 0px 4rpx 13rpx 0px rgba(181, 135, 72, 0.47);
			border-radius: 44rpx;
			font-size: 32rpx;
			font-weight: bold;
			color: #FFFFFF;

			&::after {
				background: none;
				border: none;
			}
		}
	}
}</style>
