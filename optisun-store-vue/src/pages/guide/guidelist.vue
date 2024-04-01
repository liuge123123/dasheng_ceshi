<template>
	<container class="root-container" ref="container" :title="$t('index.func.'+code)" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="content-container">
				<view class="content" v-for="(item, index) in contentList" :key="index" @click="$goUrl('/pages/guide/guidedetail?articleId='+item.articleId)">{{ item.title }}</view>
			</view>
		</view>
	</container>
</template>
<script>
	export default {
		components: {},
		data() {
			return {
				contentList: [{}],
				code:''
			}
		},
		computed: {

		},
		onLoad(option) {
			this.code = option.code
			
			uni.setNavigationBarTitle({
				title: this.$t('index.func.'+this.code) 
			});

			this.getData()
		},
		onShow() {

		},
		methods: {
			getData() {
				this.$get('/app/content/list', {
					code: this.code
				}).then(res => {
					this.showPageContent()
					if (res.code == 0) {
						this.contentList = res.data
					}
				})
			}
		}
	}
</script>


<style lang="scss" scoped>
	.root-container {
		background: #f8f8f8;
		overflow-y: hidden;
	}

	.container {
		position: relative;
		padding-bottom: constant(safe-area-inset-bottom);
		padding-bottom: env(safe-area-inset-bottom);

		.navs {
			white-space: nowrap;
			flex: 1;
			width: 100%;
			// background: #fff;
			box-shadow: 0px 4rpx 24rpx 0px rgba(176,176,176,0.28);

			.item {
				display: inline-block;
				font-size: 28rpx;
				padding: 20rpx 0;
				text-align: center;
				cursor: pointer;
				white-space: nowrap;
				color: #888888;
				padding: 32rpx;
				position: relative;

				&.active {
					color: #19d919;

					&::after {
						content: '';
						position: absolute;
						bottom: 0;
						left: 32rpx;
						right: 32rpx;
						border-bottom: 2px solid #19d919;
					}
				}
			}
		}

		.content-container {
			overflow-y: auto;
			padding: 30rpx;
			max-height: calc(100vh - 280rpx);
			overflow-y: auto;
			.content {
				font-size: 28rpx;
				background: #FFFFFF;
				box-shadow: 0px 4rpx 24rpx 0px rgba(176, 176, 176, 0.28);
				border-radius: 10rpx;
				padding: 30rpx;
				line-height: 53rpx;
				margin-top: 20rpx;
			}
		}

	}
</style>
