<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="info-container">
				<view class="data">
					<view class="item">
						<text class="text">{{ $t('team.total.day') }}</text>
						<text class="val">{{ userData.todayMoney || 0}}</text>
					</view>
					<view class="item">
						<text class="text">{{ $t('team.total.commission') }}</text>
						<text class="val">{{ userData.totalMoney || 0 }} </text>
					</view>
				</view>
				
			</view>
			<view class="info-container mt20">
				
				<view class="info">
					<view class="info-wrap">
						<view class="item">
							<text>{{ $t('me.invite.code') }}：</text>
							<text class="green">{{ userInfo.custId }}</text>
						</view>
						<view class="item" style="display: block;">
							<text>{{ $t('me.invite.link') }}：</text>
							<text class="green">{{ url }}</text>
							<text class="copy" @click="copyLink">{{ $t('me.copy') }}</text>
						</view>
					</view>
				</view>
			</view>
			<view class="info-container mt20">
				<view class="data">
					<view class="item">
						<text class="text">{{ $t('team.total.num') }}</text>
						<text class="val">{{ userData.totalNum || 0 }}</text>
					</view>
					<view class="item">
						<text class="text">{{ $t('team.total.memberNum') }}</text>
						<text class="val">{{ userData.memberNum || 0 }} </text>
					</view>
				</view>
				
			</view>
			<view class="table">
				<view class="head">
					<view class="cell"><text>{{ $t('team.agent.level') }}</text></view>
					<view class="cell"><text>{{ $t('team.agent.num') }}</text></view>
					<view class="cell"><text>{{ $t('team.agent.rechargeNum') }}</text></view>
				</view>
				<view class="body">
					<view class="row" v-for="(item, index) in userData.list" :key="index">
						<view class="cell"><text>{{ item.level }}</text></view>
						<view class="cell" @click="$goUrl('/pages/team/team_list?position=' + item.level)"><text>{{ item.num }}</text></view>
						<view class="cell" @click="$goUrl('/pages/team/team_list_recharge?position=' + item.level)"><text>{{ item.numRecharge }}</text></view>
					</view>
				</view>
			</view>
			<view class="content">
				<view class="title">{{ $t('team.rule') }}</view>
				<view v-html="contentList[0].content"></view>
			</view>
		</view>
		<toolbar :index="2"></toolbar>
	</container>
</template>

<script>
import { setClipboardData, getClipboardData } from '@/uni_modules/u-clipboard/js_sdk'
export default {
	components: {

	},
	data() {
		return {
			url: '',
			contentList: [{}],
			userInfo: {},
			userData: {},
			pageNo: 1,
			pageSize: 10,
			dataList: [
				{ level: '1级', num: 100, commission: 10000 },
				{ level: '2级', num: 100, commission: 10000 },
				{ level: '3级', num: 100, commission: 10000 },
				{ level: '4级', num: 100, commission: 10000 },
				{ level: '5级', num: 100, commission: 10000 },
				{ level: '6级', num: 100, commission: 10000 },
				{ level: '7级', num: 100, commission: 10000 }
			],
			loadingStatus: 'more' //more/loading/noMore
		}
	},
	computed: {

	},
	onLoad() {
		this.showPageContent()
		this.getData()
	},
	onShow() {
		this.userInfo = uni.getStorageSync("user") || {}
		this.$uni.getUserInfo().then(user => {
			this.userInfo = user
		})
		let protocol = window.location.protocol
		let host = window.location.host
		this.url = `${protocol}//${host}/#/pages/login/login?inviteCode=${this.userInfo.custId}&pageIndex=2`
	},
	methods: {
		getData() {
			this.getTotalData()
			this.getRuleData()
		},
		getTotalData() {
			uni.showLoading({
				mask: true,
				title: 'loading...'
			})
			this.$get('/app/account/team/getTeamTotal').then(res => {
				uni.hideLoading()
				if (res.code == 0) {
					this.userData = res.data
				} else {
					uni.showToast({
						icon: 'none',
						title: this.$t('error.' + res.code)
					})
				}
			}).catch(e => {
				uni.showToast({
					icon: 'error',
					title: this.$t('error.500')
				})
			})
		},
		getRuleData() {
			this.$get('/app/content/list', {
				code: 'rule'
			}).then(res => {
				if (res.code == 0) {
					this.contentList = res.data
				}
			})
		},
		copyLink() {

			setClipboardData(this.url).then(res => {
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

<style scoped lang="scss">
.mt20 {
	margin-top: 20rpx;
}
.container {
	padding: 30rpx;
	margin-bottom: 100px;
	position: relative;

	&::before {
		content: '';
		position: absolute;
		top: 0;
		right: 0;
		left: 0;
		height: 300rpx;
		// background: linear-gradient(0deg, #1B191C 0%, #3D3C3F 100%);
		border-radius: 0px 0px 30px 30px;
	}

	.info-container {
		
		background: #fe9901;
		position: relative;
		padding-bottom: 20rpx;
		border-radius: 20rpx;
		.info {
			display: flex;
			padding: 20rpx 50rpx;

			.head {
				width: 100rpx;
				height: 100rpx;
				// border-radius: 50%;
				background: #fff;
				border: 1px solid #fff;
				padding: 20rpx;
			}

			.info-wrap {
				// padding-left: 20rpx;
				flex: 1;

				.item {
					display: flex;
					align-items: center;
					margin-top: 18rpx;
					font-size: 24rpx;

					&:first-child {
						margin-top: 0;
					}

					.name {
						font-size: 29rpx;
						font-weight: bold;
					}

					.level {
						width: 60rpx;
						height: 40rpx;
						margin-left: 10rpx;
					}

					.help {
						color: rgb(202, 161, 96);
						font-size: 48rpx;
						margin-left: 10rpx;
					}

					.green {
						width: 67%;
						word-wrap: break-word;
						word-break: normal;
						color: #f5f8f6;
					}

					.copy {
						margin-left: 10px;
						text-decoration: underline;
					}
				}
			}
		}

		.data {
			display: flex;
			text-align: center;
			padding-top: 20rpx;
			.item {
				flex: 1;
				display: flex;
				flex-direction: column;

				.val {
					font-size: 40rpx;
					font-weight: bold;
				}

				.text {
					font-size: 24rpx;
					font-weight: bold;
					// color: #A56D0D;
					margin-top: 10rpx;
				}
			}
		}
	}

	.table {
		background: #fe9901;
		margin-top: 20rpx;
		border-radius: 20rpx;
		padding: 30rpx;
		position: relative;

		// margin-top: 20rpx;
		.data {
			display: flex;
			text-align: center;

			.item {
				flex: 1;

				.val {
					font-size: 40rpx;
					font-weight: bold;
				}

				.text {
					font-size: 24rpx;
					font-weight: bold;
					// color: #A56D0D;
					margin-top: 10rpx;
				}
			}
		}

		.head {
			display: flex;
			font-weight: bold;
			height: 66rpx;
		}

		.row {
			display: flex;
			height: 66rpx;

			&:nth-child(2n-1) {
				background: #FAF6EE;
			}
		}

		.cell {
			flex: 1;
			padding: 12rpx;
			display: flex;
			align-items: center;
			justify-content: center;
			word-wrap: break-word;
			word-break: break-all;
			font-size: 27rpx;
		}
	}

	.content {
		box-shadow: 0px 2px 13px 0px rgba(204, 126, 31, 0.48);
		background: #fff;
		border-radius: 20rpx;
		margin-top: 30rpx;
		padding: 30rpx;

		.title {
			text-align: center;
			font-weight: 600;
		}
	}
}</style>
