<template>
	<container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
		<view class="container">
			<view class="table">
				<view class="head">
					<view class="cell"><text>{{$t('team.agent.id')}}</text></view>
					<view class="cell"><text>{{$t('team.agent.name')}}</text></view>
					<view class="cell"><text>{{$t('team.agent.status')}}</text></view>
				</view>
				<view class="body">
					<view class="row" v-for="(item, index) in dataList" :key="index">
						<view class="cell"><text>{{item.custId}}</text></view>
						<view class="cell"><text>{{item.custName}}</text></view>
						<!-- <view class="cell"><text>{{item.gradeName}}</text></view> -->
						<!-- <view class="cell"><text>{{item.totalRechargeMoney>0?'recharged':'Not recharged'}}</text></view> -->
						<view v-if="item.num > 0" class="cell" style="color: orange;"><text>non</text></view>
						<view v-if="item.num == 0" class="cell"><text>Acquis</text></view>
					</view>
					<uni-load-more :status="loadingStatus"></uni-load-more>
				</view>
			</view>
		</view>
	</container>
</template>

<script>
	import { setClipboardData, getClipboardData } from '@/uni_modules/u-clipboard/js_sdk'
	export default {
		components: {

		},
		data() {
			return {
				position: 0,
				pageNo: 1,
				pageSize: 20,
				dataList: [
				],
				loadingStatus: 'more' //more/loading/noMore
			}
		},
		computed: {

		},
		onLoad() {
			this.position = this.$route.query.position || 1
			this.showPageContent()
		},
		onShow() {
			this.getData()
		},
		onReachBottom() {
			this.getData()
		},
		methods: {
			getData(){
				if(this.loadingStatus != 'more'){
					return
				}
				if(this.pageNo == 1){
					this.dataList = []
				}
				this.loadingStatus = 'loading'
				this.$get('/app/account/team/getTeamPersonList', {
					pageNo: this.pageNo,
					pageSize: this.pageSize,
					position: this.position
				}).then(res => {
					this.loadingStatus = 'more'
					if(res.code == 0){
						let current = res.data.currPage
						let total = res.data.totalPage
						if(current < total){
							this.pageNo ++
						}else{
							this.loadingStatus = 'noMore'
						}
						res.data.list.forEach(item => {
							this.dataList.push(item)
						})
					}
				})
			},
		}
	}
</script>

<style scoped lang="scss">
	.container{
		padding: 30rpx;
		margin-bottom: 100px;
		position: relative;
		&::before{
			content: '';
			position: absolute;
			top: 0;
			right: 0;
			left: 0;
			height: 300rpx;
			background: linear-gradient(0deg, #1B191C 0%, #3D3C3F 100%);
			border-radius: 0px 0px 30px 30px;
		}
		.table{
			background: #FFFFFF;
			box-shadow: 0px 4px 13px 0px rgba(210,205,199,0.48);
			border-radius: 20rpx;
			padding: 30rpx;
			position: relative;
			.head{
				display: flex;
				font-weight: bold;
				height: 66rpx;
			}
			.row{
				display: flex;
				height: 66rpx;
				&:nth-child(2n-1){
					background: #1ec8db;
				}
			}
			.cell{
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
	}
</style>
