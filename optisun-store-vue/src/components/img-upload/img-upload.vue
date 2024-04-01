<template>
	<view class="img-list">
		<text v-if="maxNum > imgList.length" 
			class="img-add iconfont icon-add-fill" 
			:style="{'width': size, 'height': size, 'line-height': size}" 
			@click="chooseImg"></text>
	  <view v-for="(img, index) in imgList" :key="index" class="img-item" :style="{'width': size, 'height': size}" >
	    <image :src="img.path" class="img" mode="aspectFill" @click="previwImg(img.path)"></image>
	    <view v-if="img.status == 'loading' || img.status == 'error'" class="img-cover">
	      <progress v-if="img.status == 'loading'" class="progress" percent="90" active stroke-width="3" style="width: 50%;" />
	      <text v-if="img.status == 'error'" class="iconfont icon-error-warning-fill"></text>
	    </view>
	    <text @click="delImg(index)" class="img-del iconfont icon-close"></text>
	  </view>
	</view>
</template>

<script>
	export default {
		name:"img-upload",
		props: {
			maxNum: {
				type: Number,
				default: 9
			},
			size: {
				type: String,
				default: "165rpx"
			}
		},
		data() {
			return {
				imgList: []
			};
		},
		methods: {
			chooseImg(){
			  uni.chooseImage({
			    count: this.maxNum - this.imgList.length,
			    success: res => {
			      res.tempFilePaths.forEach(path => {
			        this.imgList.push({
			          path: path,
					  status: ''
			        })
			      })
			    }
			  })
			},
			delImg(index){
				this.imgList.splice(index, 1)
			},
			previwImg(url){
				let urls = this.imgList.map(img => {
					return img.path
				})
				uni.previewImage({
					current: url,
					urls: urls
				})
			},
			uploadImg(){
				return new Promise((resolve, reject) => {
					if(this.imgList.length == 0){
						resolve([])
					}else{
						let completeCount = 0;
						let successList = []
						this.imgList.forEach(img => {
							if(img.status != 'success'){
								img.status = 'loading'
								this.$upload(img.path).then(res => {
									if(res.code == 0){
										img.status = 'success'
										img.url = res.url
										successList.push(res.url)
									}else{
										img.status = 'error'
									}
									completeCount ++
									if(completeCount == this.imgList.length){
										resolve(successList)
									}
								}).catch(e => {
									reject(e)
								})
							}else{
								completeCount ++
								successList.push(img.url)
								if(completeCount == this.imgList.length){
									resolve(successList)
								}
							}
						})
					}
				})
			}
		}
	}
</script>

<style scoped lang="scss">
.img-list {
	display: flex;
	flex-wrap: wrap;

	.img-item,
	.img-add {
		background-color: #f5f5f5;
		text-align: center;
		color: #999999;
		font-size: 48rpx;
		border-radius: 8rpx;
		margin-right: 10rpx;
		margin-bottom: 10rpx;
		position: relative;
		.img{
			width: 100%;
			height: 100%;
			border-radius: 8rpx;
		}
		
		.img-cover{
			position: absolute;
			left: 0;
			right: 0;
			top: 0;
			bottom: 0;
			border-radius: 8rpx;
			background-color: rgba($color: #000000, $alpha: 0.3);
			display: flex;
			align-items: center;
			justify-content: center;
			color: #F00;
			.progress{
				width: 80%;
			}
		}
		.img-del{
			position: absolute;
			padding: 20rpx;
			right: -30rpx;
			top: -30rpx;
			color: #000000;
			line-height: normal;
		}
	}
}
</style>
