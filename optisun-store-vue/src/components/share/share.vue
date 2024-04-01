<template>
    <view class="share" v-if="shareState">
        <view :class="{'share-box': shareState}" @click="handleHiddenShare">
        </view>
        <view class="share-item" :class="{'share-show': shareState}">
            <view class="share-to">
                <text>{{headTitle}}</text>
            </view>
            <view class="content">
                <view class="item"  v-for="(item, index) in getList" :key="index" @click="itemClick(index)">
					<view class="image" :style="{'background-color': item.color}">
						<view class="iconfont icon" :class="item.image"></view>
					</view>
                    <text class="text">{{item.content}}</text>
                </view>
            </view>
            <view class="cancel" @click.stop="handleHiddenShare">
                <text>取消</text>
            </view>
        </view>
    </view>
</template>

<script>
    export default {
        props: {
            shareList: {
            	type: Array,
            	default: []
            },
			headTitle:{
				type:String,
				default:'分享到'
			}
        },
        data() {
            return {
                shareState: false,
            };
        },
		computed:{
			getList(){
				return this.shareList
			}
		},
        methods: {
            // 显示分享
            handleShowShare () {
                this.shareState = true;
            },
            // 隐藏分享
            handleHiddenShare () {
                this.shareState = false;
            },
			//单击
			itemClick(index){
			    this.shareState = false;
				console.log(index)
				this.$emit('itemClick',index)
			}
        }
    }
</script>

<style lang="less">
    .share {
        width: 100%;
        height: 100%;
    }
    .share-box {
        position: fixed;
        top: 0rpx; left: 0rpx; bottom: 0rpx; right: 0rpx;
        background-color: rgba(0, 0, 0, 0.4);
        transition: .3s;
        z-index: 999;
    }
    // 进入分享动画
    .share-show {
        transition: all 0.3s ease;
        transform: translateY(0%) !important;
    }
    // 离开分享动画
    .share-item {
        position: fixed;
        left: 0;
        bottom: 0;
        width: 100%;
        height: auto;
        background-color: rgba(#FFFFFF,0.95);
		border-radius: 20rpx 20rpx 0rpx 0rpx;
        transition: all 0.3s ease;
        transform: translateY(100%);
		padding-top: 20rpx;
        z-index: 1999;
        .share-to {
            width: 100%;
            height: 3rem;
            display: flex;
            justify-content: center;
            align-items: center;
            &::after {
                content: '';
                width: 240rpx;
                height: 0rpx;
                border-top: 1px solid #E4E7ED;
                -webkit-transform: scaleY(0.5);
                transform: scaleY(0.5);
                margin-left: 30rpx;
            }
            &::before {
                content: '';
                width: 240rpx;
                height: 0rpx;
                border-top: 1px solid #E4E7ED;
                -webkit-transform: scaleY(0.5);
                transform: scaleY(0.5);
                margin-right: 30rpx;
            }
        }
        .content {
            width: 100%;
            height: auto;
            display: flex;
            flex-wrap: wrap;
			padding-bottom: 20rpx;
            .item{
                width: 25%;
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                height: 180rpx;
				.image{
					width: 90rpx;
					height: 90rpx;
					border-radius: 45rpx;
					display: flex;
					justify-content: center;
					align-items: center;
					.icon{
						font-size: 40rpx;
						width: 40rpx;
						height: 40rpx;
						line-height: 40rpx;
						color: #FFFFFF;
					}
				}
                .text {
                    margin-top: 16rpx;
                    font-size: 28rpx;
                    color: #606266;
                }
            }
        }
        .cancel {
            width: 100%;
            height: 4rem;
            display: flex;
            justify-content: center;
            align-items: center;
            border-top: 0.5rpx solid #E4E7ED;
        }
    }
</style>