<template>
	<div class="roll">
		<scroll-view class="roll-container" :scroll-x="true" :enable-flex="true" :scroll-with-animation="animation"
			:scroll-left="left">
			<view class="roll-body">
				<view class="item" v-for="(item, index) in itemList" :key="index">
					<image mode="aspectFit" class="img" :src="item.img"></image>
				</view>
			</view>
		</scroll-view>
		<view class="masker"></view>
	</div>
</template>

<script>
	export default {
		name: "roll",
		data() {
			return {
				animation: false,
				left: 0,
				prizeList: [],
				duration: 0
			};
		},
		computed: {
			itemList() {
				let list = []
				for (let i = 0; i < 3; i++) {
					this.prizeList.forEach(item => {
						list.push(item)
					})
				}
				return list
			}
		},
		methods: {
			init(opts) {
				this.prizeList = opts.prizeList
				this.duration = opts.duration
			},
			roll(index, callback) {
				let itemWidth = uni.upx2px(214)
				let max = this.prizeList.length * itemWidth
				let duration = 0
				let timer = null
				let result = 0
				if(index > 0){
					result = itemWidth * (index - 1)
				}else{
					result = max + itemWidth
				}
				let func = () => {
					let offset = 50
					// if(duration > 1000){
					// 	offset = 20
					// }else if(duration > 2000){
					// 	offset = 1
					// }
					this.left += offset
					if (this.left >= max) {
						this.left = max - this.left + offset
					}
					duration += 50
					if(duration >= this.duration){
						this.left = result
						callback()
					}else {
						setTimeout(res => {
							func()
						}, 50)
					}
				}
				func()
			}
		},
		mounted() {

		},
		beforeCreate() {

		},
		created() {

		},
	};
</script>

<style scoped lang="scss">
	.roll {
		width: 642rpx;
		height: 100%;
		overflow: hidden;

		.masker {
			position: absolute;
			left: 214rpx;
			width: 214rpx;
			bottom: 0;
			top: 0;
			border-left: 2rpx solid #E9D4A5;
			border-right: 2rpx solid #E9D4A5;
			background: rgba(#E9D4A5, 0.1);
		}

		.roll-container {
			height: 105%;
			width: 642rpx;

			.roll-body {
				display: flex;
				height: 100%;

				.item {
					height: 100%;
					width: 214rpx;
					.img {
						padding: 0 40rpx;
						width: 134rpx;
						height: 100%;
					}
				}
			}

		}
	}
</style>
