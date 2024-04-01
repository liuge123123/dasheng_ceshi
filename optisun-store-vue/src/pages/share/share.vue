<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="top-text">
				<text>{{$t('share.remark')}}</text>
			</view>
			<image class="top-img" src="@/static/images/share_code.png"></image>
			<view class="code">
				<text>{{$t('share.code.title')}}：</text>
				<text>{{code}}</text>
			</view>
			<button class="copy-btn" @click="copyUrl">{{$t('share.copy.btn')}}</button>

			<view class="rule">
				<view class="title">{{$t('share.rule')}}</view>
				<rich code="share"></rich>
			</view>

		</view>
	</container>
</template>
<script>
	import {
		setClipboardData,
		getClipboardData
	} from '@/uni_modules/u-clipboard/js_sdk'
	export default {
		components: {},
		data() {
			return {
				code: ''
			}
		},
		computed: {

		},
		onLoad(option) {
			this.showPageContent()
		},
		onShow() {
			let user = uni.getStorageSync("user")
			this.code = user.custId
		},
		methods: {
			copyUrl() {
				let protocol = window.location.protocol
				let host = window.location.host
				let url = `${protocol}//${host}/#/pages/login/login?inviteCode=${this.code}&pageIndex=2`
				setClipboardData(url).then(res => {
					uni.showToast({
						icon: 'success',
						title: this.$t('share.copy.success')
					})
				}).catch(res => {

				})
			}
		}
	}
</script>


<style lang="scss" scoped>
	.container {
		padding: 30rpx 30rpx 0;
		text-align: center;

		.top-text {
			font-size: 36rpx;
			font-weight: 700;
			margin-top: 30rpx;
			color: #FA7B6E;
		}

		.top-img {
			// width: 500rpx;
			// height: 400rpx;
		}

		.code {
			margin-top: 30rpx;
			color: #FA7B6E;
			font-size: 36rpx;
			font-weight: 700;
		}

		.copy-btn {
			width: 340rpx;
			font-size: 32rpx;
			margin: 30rpx auto 0;
			color: #fff;
			background: #FA7B6E;
			border-radius: 27px;
		}

		.rule {
			.title {
				font-size: 30rpx;
				font-weight: bold;
				color: #1ec8db;
				line-height: 118rpx;
				text-align: center;
			}

			width: 678rpx;
			background: #FFFFFF;
			box-shadow: 0px 0px 27px 0px rgba(201, 201, 201, 0.35);
			border-radius: 88rpx 88rpx 0 0;
			margin: 0 auto;
			margin-top: 80rpx;
			text-align: left;
			color: #1ec8db;
			line-height: 54rpx;
			font-size: 24rpx;
		}
	}
</style>
