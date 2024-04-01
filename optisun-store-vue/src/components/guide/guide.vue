<!-- guide.vue -->
<template>
	<view v-show="show" class="main" @touchmove.stop>
		<view class="rect-shadow" :style="{'width':width+'px','height':height+'px','left':left+'px',top:top+'px','border-radius': noticeArray[currentIndex].radius?noticeArray[currentIndex].radius+'rpx':'' }"></view>
		<template v-if="noticeArray[currentIndex].type == 'left' ">
			<view  class="show-message_left" :style="{'top': top +'px', 'right': winWidth-right+width+20+'px' }">
				<view style="padding: 12rpx 18rpx;">{{showMessage}}</view>
			</view>			
		</template>
		<template v-if="noticeArray[currentIndex].type == 'top' ">
			<view :class="left<(winWidth/2)?'show-message_top_left':'show-message_top_right'" :style="{'top': (top-height-topMsgHeght) +'px', 'left': left<(winWidth/2)?left+'px':'' , 'right': left>(winWidth/2)?(winWidth-right)+'px':''  }">
				<view style="padding: 12rpx 18rpx;">{{showMessage}}</view>
			</view>			
		</template>
		<template v-if="noticeArray[currentIndex].type == 'right' ">
			<view  class="show-message_right" :style="{'top': top +'px', 'left': (left+width+20) +'px' }">
				<view style="padding: 12rpx 18rpx;">{{showMessage}}</view>
			</view>			
		</template>
		<template v-if="noticeArray[currentIndex].type == 'bottom' ">
			<view :class="left<(winWidth/2)?'show-message_bottom_left':'show-message_bottom_right'" :style="{'top': (top+height+15) +'px', 'left': left<(winWidth/2)?left+'px':'' , 'right': left>(winWidth/2)?(winWidth-right) +'px':'' }">
				<view style="padding: 12rpx 18rpx;">{{showMessage}}</view>
			</view>			
		</template>
		<view class="cover-wrap" >
			<view class="guide-next" @tap="click" :style="{color: currentIndex==noticeArray.length-1? '#EA3E4F':'#000000'}">{{nextText}}</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "guide",
		props: {
			show: {
				type: Boolean,
				default: false
			},
			left: {
				type: [Number, String],
				default: ''
			},
			right: {
				type: [Number, String],
				default: ''
			},
			top: {
				type: [Number, String],
				default: ''
			},
			width: {
				type: [Number, String],
				default: ''
			},
			height: {
				type: [Number, String],
				default: ''
			},
			showMessage: {
				type: String,
				default: ''
			},
			currentIndex: {
				type: Number,
				default: 0
			},
			noticeArray: {
				type: Array,
				default: []
			}
		},
		data() {
			return {
				topMsgHeght: 0,
				winWidth: 0,
				nextText: this.$t('guide.next') // 知道了，关闭说明
			}
		},
		watch: {
			show(n) {},
			currentIndex(value) {
				if (value != this.noticeArray.length&&this.noticeArray[value].type == 'top') {
					const className = this.left<(this.winWidth/2) ? '.show-message_top_left' : '.show-message_top_right'
					const query = uni.createSelectorQuery().in(this);
					query.select(className).boundingClientRect(data => {
						// 29px 为一行语句的高度
						this.topMsgHeght = data.height - 29
					}).exec();
				}
				if (value == this.noticeArray.length-1) {
					this.nextText = this.$t('guide.close') 
				}
			}
		},
		methods: {
			click() {
				this.$emit('click');
			},
			// 清除页面所有定时器
			clearTimeAll(){
				clearTimeout(this.stt01);
			}
		},
		created() {
			this.stt01 = setTimeout(() => {
				uni.getSystemInfo({
					success: (res) => {
						this.winWidth = res.windowWidth;
					}
				});	
			}, 60)
		}
	}
</script>

<style lang="scss">
	.main {
		position: fixed;
		top: 0;
		left: 0;
		right: 0;
		bottom: 0;
		z-index: 99999;
	}
	.rect-shadow {
		position: absolute;
		border-radius: 8rpx;
		box-shadow: 0 0 0 3000px rgba(0, 0, 0, 0.5);
		pointer-events: none;
	}
	.show-message_left,
	.show-message_top_left,
	.show-message_top_right,
	.show-message_right,
	.show-message_bottom_left,
	.show-message_bottom_right {
		position: absolute;
		border-radius: 12rpx;
		background-color: rgba(0,0,0,.5);
		text-align: center;
		pointer-events: none;
		color: #fff;
		font-size: 24rpx;
		max-width: 80%;
		text-align: left;
	}
	.show-message_left::after {
		content: '';
		border-left: 12rpx solid rgba(0,0,0,.5);
		border-top: 12rpx solid transparent;
		border-right: 12rpx solid transparent;
		border-bottom: 12rpx solid transparent;
		width: 0;
		height: 0;
		position: absolute;
		top: 25%;
		right: -22rpx;
	}
	
	.show-message_top_left::after,.show-message_top_right::after {
		content: '';
		border-left: 12rpx solid transparent;
		border-top: 12rpx solid rgba(0,0,0,.5);
		border-right: 12rpx solid transparent;
		border-bottom: 12rpx solid transparent;
		width: 0;
		height: 0;
		position: absolute;
		top: 100%;
	}
	.show-message_top_left::after {
		left: 20%;
	}
	.show-message_top_right::after {
		right: 20%;
	}
	
	.show-message_right::after {
		content: '';
		border-left: 12rpx solid transparent;
		border-top: 12rpx solid transparent;
		border-right: 12rpx solid rgba(0,0,0,.5);
		border-bottom: 12rpx solid transparent;
		width: 0;
		height: 0;
		position: absolute;
		top: 25%;
		left: -24rpx;
	}
	.show-message_bottom_left::after, .show-message_bottom_right::after {
		content: '';
		border-left: 12rpx solid transparent;
		border-top: 12rpx solid transparent;
		border-right: 12rpx solid transparent;
		border-bottom: 12rpx solid rgba(0,0,0,.5);
		width: 0;
		height: 0;
		position: absolute;
		top: -22rpx;
	}
	
	.show-message_bottom_left::after {
		left: 20%;
	} 
	.show-message_bottom_right::after {
		right: 20%;
	}
	
	.cover-wrap {
		width: 100vw;
		height: 100vh;
		top: 0;
		left: 0;
		position: absolute;
		
		.guide-next {
			// padding: 0 40rpx;
			padding: 16rpx 32rpx;
			height: 44rpx;
			line-height: 44rpx;
			background-color: #FFFFFF;
			text-align: center;
			font-size: 28rpx;
			border-radius: 38rpx;
			position: absolute;
			bottom: 372rpx;
			left: 50%;
			transform: translate(-50%,0);
		}
	}
</style>


