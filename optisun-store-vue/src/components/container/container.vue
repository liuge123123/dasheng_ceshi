<template>
	<view class="root-container">
		<uni-nav-bar v-if="headerVisable" :title="title" :backgroundColor="headerBackGroudColor" :border="headerBorder" :rightText="rightText" :rightWidth="rightWidth" :color="color" @clickRight="clickRight"></uni-nav-bar>
		<view class="content" v-show="status == 'content'">
			<slot></slot>
		</view>
		<view v-show="status == 'empty'"  @click="refresh">
			<slot name="empty">
				<empty :text="empty" icon="ri-emotion-unhappy-line"></empty>
			</slot>
		</view>
		<view v-show="status == 'loading'">
			<slot name="loading">
				<loading></loading>
			</slot>
		</view>
		<view v-show="status == 'error'" @click="refresh">
			<slot name="error">
				<empty :text="error" icon="ri-emotion-unhappy-line"></empty>
			</slot>
		</view>
		<auth ref='auth'></auth>
	</view>
</template>

<script>
	import Auth from '../auth/auth.vue'
	export default {
		name:"container",
		components: {Auth},
		props: {
			title: {
				type: String,
				default: ''
			},
			status: {
				type: String,
				default: 'loading'
			},
			headerVisable:{
				type:Boolean,
				default:false
			},
			headerBackGroudColor:{
				type:String,
				default: '#252525'
			},
			headerBorder: {
				type: [Boolean, String],
				default: false
			},
			rightText: {
				type:String,
				default: ''
			},
			rightWidth: {
				type: [Number, String],
				default: 60
			},
			color: {
				type:String,
				default: '#fff'
			}
		},
		data() {
			return {
				error: this.$t('error.500'),
				empty: this.$t('error.empty')
			}
		},
		mounted() {
			let route = this.$Route
			if(route.isAuth && !getApp().globalData.userInfo){
				this.$refs.auth.show()
			}
		},
		methods: {
			showAuth(){
				this.$refs.auth.show()
			},
			refresh(){
				this.$emit('refresh')
			},
			showError(error){
				this.error = error
			},
			clickRight(){
				this.$emit('clickRight')
			}
		}
	}
</script>

<style scoped lang="scss">
	.root-container{
		height: 100%;
		display: flex;
		flex-direction: column;
		.content{
			position: relative;
			flex: 1;
			background-color: #fff;
		}
	}
</style>
