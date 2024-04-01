<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="room-list">
				<view class="item" @click="goDetail(item, item.id > userInfo.level)" v-for="item in levelList"
					:key="item.id">
					<view class="left">
						<text class="text">{{item.gradeName}}</text>
					</view>
					<view class="right">
						<view class="wrap">
							<view class="title"><text>{{item.gradeName}}</text></view>
							<view class="desc" v-html="item.content"></view>
						</view>
						<view class="footer">
							<view class="btn" v-if="item.id > userInfo.level">{{$t('grabbing.unlock')}} <i
									class="first ri-arrow-right-s-line"></i><i class="second ri-arrow-right-s-line"></i>
							</view>
							<view class="btn" v-else>{{$t('grabbing.go')}} <i class="first ri-arrow-right-s-line"></i><i
									class="second ri-arrow-right-s-line"></i></view>
						</view>
					</view>
				</view>
			</view>
		</view>
		<toolbar :index="4"></toolbar>
	</container>
</template>

<script>
	export default {
		components: {

		},
		data() {
			return {
				levelList: [],
				userInfo: null
			}
		},
		computed: {

		},
		onLoad() {
			this.userInfo = uni.getStorageSync("user")
			this.getData()
		},
		methods: {
			getData() {
				this.showPageLoading()
				this.$get('/app/account/level/list').then(res => {
					this.serviceTime = res.timestamp
					if (res.code == 0) {
						this.levelList = res.data
						this.showPageContent()
					} else {
						this.showPageError(this.$t('error.' + res.code))
					}
				}).catch(e => {
					this.showPageError(this.$t('error.500'))
				})
			},
			getUserLevel(currlevelId) {
				for (var i = 0; i < this.levelList.length; i++) {
					if (currlevelId == this.levelList[i].id) {
						return this.levelList[i]
					}
				}
				return null
			},
			getMoney(currlevelId, level) {
				let curr = this.getUserLevel(currlevelId)
				return level.parameter1 - curr.parameter1
			},
			goDetail(item, lock) {
				if (lock) {
					let currLevel = this.getUserLevel(this.userInfo.level)
					let money = this.getMoney(this.userInfo.level, item)
					if (money > this.userInfo.leftRechargeMoney + this.userInfo.registerMoney) {
						uni.showModal({
							title: this.$t('grabbing.unlock.title'),
							confirmText: this.$t('grabbing.unlock.rechage'),
							content: this.$t('grabbing.unlock.tips1', {
								currLevel: currLevel.gradeName,
								nextLevel: item.gradeName,
								money: money / 10000
							}),
							success: res => {
								if (res.confirm) {
									uni.navigateTo({
										url: '/pages/recharge/recharge'
									})
								}
							}
						})

					} else {
						uni.showModal({
							title: this.$t('grabbing.unlock.title'),
							confirmText: this.$t('grabbing.unlock.confirm'),
							content: this.$t('grabbing.unlock.tips2', {
								currLevel: currLevel.gradeName,
								nextLevel: item.gradeName,
								money: money / 10000
							}),
							success: res => {
								if (res.confirm) {
									this.unlockLevel(item.id)
								}
							}
						})
					}
				} else {
					this.$goUrl('/pages/grabbing_detail/grabbing_detail?id=' + item.id)
				}

			},
			unlockLevel(level) {
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/account/unlockVip', {
					level: level,
				}).then(res => {
					if (res.code == 0) {
						uni.showToast({
							title: this.$t('grabbing.unlock.success')
						})
						this.$uni.getUserInfo().then(res => {
							uni.hideLoading()
							this.userInfo = uni.getStorageSync("user")
						})
					} else {
						uni.showToast({
							icon: 'none',
							title: this.$t('error.' + res.code)
						})
					}
				}).catch(e => {
					uni.hideLoading()
					uni.showToast({
						icon: 'error',
						title: this.$t('error.500')
					})
				})
			}
		}
	}
</script>

<style scoped lang="scss">
	.container {
		padding: 30rpx;
		margin-bottom: 100px;

		.room-list {
			.item {
				border-radius: 16rpx;
				margin-bottom: 30rpx;
				color: #666;
				background-color: #fff;
				border-radius: 20rpx;
				display: flex;
				height: 241rpx;
				background: #FFFFFF;
				box-shadow: 0rpx 10rpx 10rpx 0rpx rgba(162, 162, 162, 0.1);
				border-radius: 10rpx;

				.left {
					border-radius: 10rpx;
					width: 218rpx;
					background: linear-gradient(-45deg, #D9922C 0%, #FFF2D2 100%);
					line-height: 241rpx;
					text-align: center;
					font-size: 84rpx;
					font-weight: bold;
					color: rgba(255, 255, 255, 0.2);
				}

				.right {
					flex: 1;
					display: flex;
					flex-direction: column;

					.wrap {
						flex: 1;
						display: flex;
						flex-direction: column;
						justify-content: center;
						border-bottom: 1rpx solid #F5F5F5;
						padding-left: 50rpx;
						padding-right: 30rpx;

						.title {
							font-size: 32rpx;
							font-weight: 800;
							color: #333333;
						}

						.desc {
							font-size: 27rpx;
							font-weight: 500;
							color: #999999;
							margin-top: 25rpx;
						}
					}

					.footer {
						height: 90rpx;
						text-align: right;
						padding-right: 30rpx;
						font-size: 27rpx;
						color: #FF8B49;

						.btn {
							height: 90rpx;
							display: flex;
							align-items: center;
							justify-content: flex-end;

							.first {
								margin-right: -15rpx;
							}

							.first,
							.second {
								display: block;
								padding-top: 5rpx;
							}
						}
					}
				}

				&:nth-child(1) {
					.left {
						background: linear-gradient(-45deg, #2F84F4 0%, #A3E4FF 100%);
					}

					.right {
						.footer {
							color: #2F84F4;
						}
					}
				}

				&:nth-child(2) {
					.left {
						background: linear-gradient(-45deg, #19C48B 0%, #93E5CF 100%);
					}

					.right {
						.footer {
							color: #19C48B;
						}
					}
				}

				&:nth-child(3) {
					.left {
						background: linear-gradient(-45deg, #02C2C9 0%, #6AF6F1 100%);
					}

					.right {
						.footer {
							color: #02C2C9;
						}
					}
				}

				&:nth-child(4) {
					.left {

						background: linear-gradient(-45deg, #685EEF 0%, #C2CAFF 100%);
					}

					.right {
						.footer {
							color: #685EEF;
						}
					}
				}

				&:nth-child(5) {
					.left {
						background: linear-gradient(-45deg, #FC5957 0%, #FFC49B 100%);
					}

					.right {
						.footer {
							color: #FC5957;
						}
					}
				}

				&:nth-child(6) {
					.left {
						background: linear-gradient(-45deg, #FB9358 0%, #FFDA7F 100%);
					}

					.right {
						.footer {
							color: #FB9358;
						}
					}
				}

				&:nth-child(7) {
					.left {
						background: linear-gradient(-45deg, #E8B51A 0%, #F3EA86 100%);
					}

					.right {
						.footer {
							color: #E8B51A;
						}
					}
				}

				&:nth-child(8) {
					.left {
						background: linear-gradient(-45deg, #D9922C 0%, #FFF2D2 100%);
					}

					.right {
						.footer {
							color: #D9922C;
						}
					}
				}
			}
		}

	}
</style>
