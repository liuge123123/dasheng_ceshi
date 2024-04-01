<template>
	<container ref="container" :headerVisable="true"  :status="pageStatus"   @refresh="getData()">
		<view class="container">
			<input class="input" v-model="accountName" :placeholder="$t('cardadd.name')" />
			<input class="input" type="number" maxlength="8"  v-model="accountNo" :placeholder="$t('cardadd.no')" />
			<view class="input" @click="typePick">
				<image v-if="typeIndex == -1" class="img" src="../../static/images/cz.jpg"></image>
				<image v-else class="img" :src="type[typeIndex].logo"></image>
				<text class="text" v-if="typeIndex == -1">{{$t('cardadd.type')}}</text>
				<text class="text" v-else>{{type[typeIndex].name}}</text>
			</view>
			<button class="btn" type="primary" @click="confirmClick">{{$t('cardadd.confirm')}}</button>
		</view>
	</container>
</template>

<script>
	export default {
		data() {
			return {
				accountName: '',
				accountNo: '',
				typeIndex: -1,
				type: [],
			}
		},
		onLoad() {
			this.getData()
		},
		methods: {
			getData(){
				this.$get('/app/account/bank/cate/list', {
					cashoutOpen: 1
				}).then(res => {
					this.showPageContent()
					if(res.code == 0){
						this.type = res.data.map(item => {
							return {
								id: item.id,
								name: item.name,
								logo: item.logo
							}
						})
					}else{
						this.showPageError(this.$t('error.' + res.code));
					}
				}).catch(e => {
					this.showPageError(this.$t('error.500'));
				})
			},
			typePick(){
				uni.showActionSheet({
					itemList: this.type.map(item => {
						return item.name
					}),
					success: res => {
						this.typeIndex = res.tapIndex 
					}
				})
			},
			confirmClick(){
				if(this.accountNo == ''){
					uni.showToast({
						icon:"none",
						title: this.$t('validate.cardadd.no.blank')
					})
					return
				}
				if(this.accountNo.length !=8){
					uni.showToast({
						icon:"none",
						title: 'Veuillez saisir le numéro de carte bancaire à 8 chiffres'
					})
					return
				}
				if(this.accountName == ''){
					uni.showToast({
						icon:"none",
						title: this.$t('validate.cardadd.name.blank')
					})
					return
				}
				if(this.typeIndex == -1){
					uni.showToast({
						icon: 'none',
						title: this.$t('validate.cardadd.type.blank')
					})
					return
				}
				uni.showLoading({
					mask: true,
					title: 'loading...'
				})
				this.$post('/app/account/my/bank/save', {
					cateId: this.type[this.typeIndex].id,
					cardNo: this.accountNo,
					cardName: this.accountName
				}).then(res => {
					uni.hideLoading()
					if(res.code == 0){
						uni.showToast({
							title: this.$t('cardadd.success'),
						})
						setTimeout(r => {
							uni.navigateBack()
						}, 1000)
					}else{
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
	.container{
		padding: 20rpx;
		.input{
			background-color: #FFF;
			border: 1rpx solid #ddd;
			padding: 20rpx;
			margin-bottom: 30rpx;
			border-radius: 16rpx;
			display: flex;
			align-items: center;
			.img{
				width: 40rpx;
				height: 40rpx;
				margin-right: 10rpx;
			}
			.text{
				font-size: 28rpx;
				color: grey;
			}
		}
		.btn{
			background: #caa160;
			font-size: 32rpx;
			height: 88rpx;
			// background: linear-gradient(0deg, #FFFFFF 0%, #000000 0%, #C39155 0%, #E9D1A0 100%);
			background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
			border-radius: 40rpx;
		}
	}
</style>
