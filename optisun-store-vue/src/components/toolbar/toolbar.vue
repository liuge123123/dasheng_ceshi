<template>
  <view class="toolbar">
    <view class="toolbar-item" :class="{active: index == 1}" @click="goItem(1)">
      <image mode="aspectFit" v-if="index == 1" class="icon" src="../../static/images/icon/home2.png"></image>
      <image mode="aspectFit" v-else class="icon" src="../../static/images/icon/home.png"></image>
      <text class="text">{{ $t('toolbar.index') }}</text>
    </view>
    <view class="toolbar-item" :class="{active: index == 4}" @click="goItem(4)">
      <image mode="aspectFit" v-if="index == 4" class="icon" src="../../static/images/icon/order2.png"></image>
      <image mode="aspectFit" v-else class="icon" src="../../static/images/icon/order.png"></image>
      <text class="text">{{ $t('toolbar.task') }}</text>
    </view>
    <view class="toolbar-item" :class="{active: index == 3}" @click="goItem(3)">
      <image mode="aspectFit" class="big" src="../../static/images/icon/big.png"></image>
      <view class="icon"></view>
    </view>
    <view class="toolbar-item" :class="{active: index == 2}" @click="goItem(2)">
      <image mode="aspectFit" v-if="index == 2" class="icon" src="../../static/images/icon/team2.png"></image>
      <image mode="aspectFit" v-else class="icon" src="../../static/images/icon/team.png"></image>
      <text class="text">{{ $t('toolbar.team') }}</text>
    </view>
    <view class="toolbar-item" :class="{active: index == 5}" @click="goItem(5)">
      <image mode="aspectFit" v-if="index == 5" class="icon" src="../../static/images/icon/me2.png"></image>
      <image mode="aspectFit" v-else class="icon" src="../../static/images/icon/me.png"></image>
      <text class="text">{{ $t('toolbar.me') }}</text>
    </view>
  </view>
</template>

<script>
import { th } from '@dcloudio/vue-cli-plugin-uni/packages/postcss/tags';

export default {
  name: "toolbar",
  props: {
    index: {
      type: Number,
      default: 1,
      
    }
  },
  mounted(){
    let userInfo = uni.getStorageSync("user")
		this.$uni.getOnlineService(userInfo.custId).then(data => {
			this.onlineService = data
		})
    // this.showPageContent()
  },
  methods: {
    goItem(index) {
      let url = ''
      switch (index) {
        case 1:
          url = '/pages/index/index'
          break
        case 2:
          url = '/pages/team/team'
          break
        case 3:
          window.location.href = this.onlineService.wsGroup;
          return
        case 4:
          url = '/pages/cftnew/cftnew'
          break
        case 5:
          url = '/pages/me/me'
          break
      }
      this.$goUrl(url)
    }
  }
}
</script>

<style scoped lang="scss">
.toolbar {
  // background: url('@/static/images/icon/tabbg.png');
  background: #fff;
  z-index: 9999;
  position: fixed;
  left: 0;
  bottom: 0;
  padding-bottom: constant(safe-area-inset-bottom);
  padding-bottom: env(safe-area-inset-bottom);
  box-sizing: initial !important;
  background-size: 100% 100% !important;
  background-repeat: no-repeat !important;
  width: 100%;
  height: 130rpx;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;

  .toolbar-item {
    width: 150rpx;
    display: flex;
    text-align: center;
    align-items: center;
    flex-direction: column;
    justify-content: space-between;
    position: relative;
    padding: 20rpx 0;
    box-sizing: border-box;
    z-index: 5;
    color: #333;

    &.active {
      color: #fe9901;
    }

    .big {
      width: 90rpx;
      height: 90rpx;
      position: absolute;
      left: 50%;
      transform: translateX(-50%) rotate(0deg);
      top: -13px;
      transition: all .2s linear;
      border-radius: 50%;
      z-index: 5;
    }

    .icon {
      position: relative;
      padding-top: 7px;
      width: 40rpx;
      height: 40rpx;
    }

    .text {
      font-weight: 700;
      transform: scale(.8);
      font-size: 12px;
	    // color: #C1B5A7;
      line-height: 14px;
      transform-origin: center 100%;
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
	  margin-top: 10rpx;
    }
  }
}
</style>