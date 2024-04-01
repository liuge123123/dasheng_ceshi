<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="card-item" v-for="(item, index) in cardList" :key="index">
				<!-- <view class="account-no">BeneName:{{item.name}}</view>
				<view class="account-no">BankAccNo:{{item.account}}</view>
				<view class="account-no">BeneMobile:{{item.parameter1}}</view>
				<view class="account-no">BeneIFSC:{{item.code}}</view> -->

				<view class="account-no">{{ item.name }}</view> <!-- 姓名-->
				<view class="account-no">{{ item.account }}</view><!-- 卡号-->
				<view class="account-no">{{ item.parameter1 }}</view><!-- 手机号-->
				<view class="account-no">{{ item.code }}</view><!-- IFSC-->
			</view>

			<button v-if="cardList.length < minVal" class="add-btn" type="primary"
				@click="$goUrl('/pages/cardadd/cardadd')">{{ $t('cardlist.add') }}</button>
			<button v-else class="add-btn" type="primary" @click="goOnlineService">{{ $t('cardlist.edit') }}</button>
		</view>
	</container>
</template>

<script>
export default {
	data() {
		return {
			onlineService: {},
			minVal: 2,
			cardList: []
		}
	},
	onShow() {
		this.getData()
		this.$uni.getConfig('CUST_CARD_SETTING').then(res => {
			this.minVal = parseInt(res)
		})
		let userInfo = uni.getStorageSync("user")
		this.$uni.getOnlineService(userInfo.custId).then(data => {
			this.onlineService = data
		})
	},
	methods: {
		goOnlineService() {
			// this.showPageError('Une seule carte peut être connectée. Si vous souhaitez changer de carte, veuillez contacter votre gestionnaire attitré.');
			uni.showToast({
				icon: "none",
				title: 'Une seule carte peut être connectée. Si vous souhaitez changer de carte, veuillez contacter votre gestionnaire attitré.'
			})
			// window.location.href = this.onlineService.ws;
		},
		getData() {
			this.$get('/app/account/my/bank/list').then(res => {
				this.showPageContent()
				if (res.code == 0) {
					this.cardList = res.data
				} else {
					this.showPageError(this.$t('error.' + res.code));
				}
			}).catch(e => {
				this.showPageError(this.$t('error.500'));
			})
		}
	}
}
</script>

<style scoped lang="scss">
.container {
	padding: 30rpx;

	.card-item {
		background: url('../../static/images/card.png');
		background-size: cover;
		margin-left: -14rpx;
		margin-right: -14rpx;
		height: 368rpx;
		color: #FFFFFF;

		.type {
			display: flex;
			align-items: center;
			padding-left: 78rpx;
			padding-top: 49rpx;

			.type-icon {
				width: 70rpx;
				height: 70rpx;
				margin-right: 10rpx;
				border: 2rpx solid #FFFFFF;
				border-radius: 50%;
				background-color: #f8f8f8;
			}

			.type-name {
				flex: 1;
				margin-left: 18rpx;
				font-size: 32rpx;
				font-weight: bold;
			}
		}

		.account-no {
			height: 80rpx;
			line-height: 80rpx;
			font-size: 36rpx;
			font-weight: bold;
			color: #FFFFFF;
			// margin-top: 40rpx;
			padding-left: 78rpx;
			width: 580rpx;
			word-wrap: break-word;

			&.small {
				font-size: 28rpx;
			}
		}

		.account-name {
			padding-left: 78rpx;
			font-size: 28rpx;
			margin-top: 10rpx;
		}

	}

	.add-btn {
		margin-top: 30rpx;
		height: 88rpx;
		// background: linear-gradient(0deg, #FFFFFF 0%, #000000 0%, #C39155 0%, #E9D1A0 100%);
		background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
		border-radius: 40rpx;
	}
}
</style>
