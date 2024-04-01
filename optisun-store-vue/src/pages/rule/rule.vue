<template>
	<container class="root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<scroll-view :scroll-x="true" scroll-with-animation :scroll-into-view="'nav' + navIndex" ref="navs"
				class="navs">
				<view :id="'nav' + index" class="item" v-for="(item, index) in contentList" :key="item.articleId"
					:class="selectIndex == index ? 'active' : ''" @click="selectIndex = index">
					<text>{{item.title}}</text>
				</view>
			</scroll-view>
			<view class="content-container">
				<view class="content" v-html="contentList[selectIndex].content">
				</view>
			</view>
		</view>
	</container>
</template>
<script>
	export default {
		components: {},
		data() {
			return {
				navIndex: 0,
				selectIndex: 0,
				contentList: [{}]
			}
		},
		computed: {

		},
		onLoad(option) {
			this.getData()
		},
		onShow() {

		},
		methods: {
			getData() {
				this.$get('/app/content/list', {
					code: 'rule'
				}).then(res => {
					this.showPageContent()
					if (res.code == 0) {
						this.contentList = res.data
					}
				})
			},
			indexChange(index) {
				this.selectIndex = index
				if (index != 0) {
					this.navIndex = index - 1
				} else {
					this.navIndex = 0
				}
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
			background: #fff;
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
					color: #B4722D;

					&::after {
						content: '';
						position: absolute;
						bottom: 0;
						left: 32rpx;
						right: 32rpx;
						border-bottom: 2px solid #B4722D;
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
			}
		}

	}
</style>
