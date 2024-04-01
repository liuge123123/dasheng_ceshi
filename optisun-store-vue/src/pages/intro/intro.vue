<template>
	<container class="root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<image class="logo-icon" src="../../static/images/logo_icon.png"></image>
		<view class="container">
			<!-- <image class="logo-text" src="../../static/images/logo_text.png"></image> -->
			<rich class="content" code="intro"></rich>
		</view>
	</container>
</template>
<script>
	export default {
		components: {},
		data() {
			return {
				content: ''
			}
		},
		computed: {

		},
		onLoad(option) {
			this.showPageContent()
		},
		onShow() {

		},
		methods: {

		}
	}
</script>


<style lang="scss" scoped>
	.root-container{
		overflow: auto;
		background: linear-gradient(45deg, rgba(233,212,183,1) 0%, rgba(233,212,183,0.1) 0%, rgba(235,216,190,0.1) 0%, rgba(233,212,183,0.5) 100%);
	}
	.logo-icon {
		width: 129rpx;
		height: 129rpx;
		padding: 33rpx;
		background: #FFFFFF;
		box-shadow: 0px 6px 16px 0px rgba(0, 0, 0, 0.08);
		border-radius: 50%;
		object-fit: contain;
		display: block;
		margin: 42rpx auto 0;
	}

	.container {
		margin: 30rpx;
		margin-top: -67rpx;
		padding: 30rpx;
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);
		border-radius: 8px;
		background: #fff;
		color: #282828;

		.logo-text {
			width: 186rpx;
			height: 31rpx;
			display: block;
			margin: 87rpx auto 30rpx;
		}

		.content {
			font-size: 28rpx;
			line-height: 53rpx;
			text-align: justify;

			/deep/ image {
				max-width: 100%;
				object-fit: cover;
			}

			/deep/ img {
				max-width: 100%;
				object-fit: cover;
			}
		}

	}
</style>
