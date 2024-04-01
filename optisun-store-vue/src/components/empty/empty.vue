<template>
	<view class="no-data">
		<view class="img" :class="icon"></view>
		<view class="text">{{text}}</view>
	</view>
</template>

<script>
	export default {
		name: "empty",
		props: {
			text: {
				type: String,
				default: 'no data'
			},
			icon: {
				type: String,
				default: 'ri-emotion-unhappy-line'
			}
		}
	}
</script>


<style lang="scss" scoped>
	.no-data {
		text-align: center;
		color: #8a8a8a;
		.img {
			margin-top: 150rpx;
			font-size: 156rpx;
		}

		.text {
			font-size: 26rpx;
			margin-top: 20rpx;
		}
	}
</style>
