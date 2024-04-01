<template>
	<container class="root-container" ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="item" v-for="(item, index) in levelList" :key="index" v-if="item.gradeLevel">
				<view class="vip">{{item.gradeName}}</view>
				<view class="vipmoney">{{item.money}}-{{item.parameter1}}</view>
				<view class="viptext">decerner {{item.rate}}%</view>
			</view>
		</view>
	</container>
</template>
<script>
export default {
	components: {},
	data() {
		return {
			levelList: [],
		}
	},
	computed: {

	},
	onLoad(option) {

	},
	onShow() {
		this.getData()
	},
	methods: {
		getData() {
			this.showPageLoading()
			this.getLevelList().then(res => {
				this.showPageContent()
			}).catch(error => {
				this.showPageError(this.$t('error.' + error.code))
			})
		},
		getLevelList() {
			return new Promise((resolve, reject) => {
				this.$get('/app/account/level/list').then(res => {
					this.serviceTime = res.timestamp
					if (res.code == 0) {
						this.levelList = res.data
						resolve()
					} else {
						reject(res)
					}
				}).catch(e => {
					reject({ code: 500 })
				})
			})
		},
	}
}
</script>


<style lang="scss" scoped>
.container {
	width: 95%;
	margin: auto;
	display: flex;
	justify-content: space-between;
	flex-wrap: wrap;

	.item {
		position: relative;
		width: 48%;
		height: 200rpx;
		margin: 0 5px 5px 0;
		background: url('../../static/images/vip-bg.jpg') center no-repeat;
		background-size: 100% 100%;

		.vip {
			color: #FFF2C1;
			font-size: 40rpx;
			padding: 6rpx;
		}

		.vipmoney {
			margin-top: 40rpx;
			text-align: center;
			color: #F14751;
			font-size: 30rpx;
		}

		.viptext {
			float: right;
			font-size: 24rpx;
			background: #FF9902;
			color: #E2E2E2;
			padding: 2px;
			position: absolute;
			bottom: 0;
			right: 0;
		}
	}
}
</style>
