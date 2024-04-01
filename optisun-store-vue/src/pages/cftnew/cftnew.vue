<template>
    <container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
        <view class="container">
            <view class="navs-container">
                <view class="navs" v-for="(item, index) in roomList" :key="index" @click="indexChange(index, item)"
                    :class="{ active: selectIndex == index }">{{ item.name }}</view>
                <view class="navs"  @click="$goUrl('pages/cfthistory/cfthistory')" >
                    {{$t('toolbar.order')}}
                    <view v-if="badge>0" class="badge">{{badge}}</view>
                </view>
            </view>
            <view class="goods-list">
                <view class="goods-item" v-for="(item, index) in dataList" :key="index" :class="{ active: isFund == 1 }"
                    :style="{
                        'background-color': item.backColor
                    }">
                    <view class="header">
                        <image class="goods-img" :src="item.img" mode="aspectFill"></image>
                        <view class="goods-name">
                            <view class="name1">{{ item.name }}</view>
                            <view class="name1">Price:{{ (item.price).toFixed(0) }}</view>
                        </view>
                        <!-- <view class="goods-circle">{{ $t('cft.goods.cycle') }}: {{ item.days }}</view> -->
                    </view>
                    <view class="center">
                        <view class="center1" >
                            <view class="center1-text">{{ (item.incomeDay ).toFixed(0) }}</view>
                            <view class="center1-text2">{{ $t('cft.goods.day.profit') }}</view>
                        </view>
                        <view class="center1">
                            <view class="center1-text">{{ item.days }}</view>
                            <view class="center1-text2">{{ $t('cft.goods.cycle') }}</view>
                        </view>
                        <view class="center1">
                            <view class="center1-text">{{ (item.incomeDay * item.days).toFixed(0) }}</view>
                            <view class="center1-text2">{{ $t('cft.goods.total.profit') }}</view>
                        </view>
                        
                    </view>
                    <view class="bottom">
                        <button v-if="item.maxNum == -1" type="primary" class="buy-btn" style="opacity: 0.5;">{{
                            $t('cft.goods.sellout') }}</button>
                        <button v-else-if="item.isGive == 1" type="primary" class="buy-btn" style="opacity: 0.5;" @click="goOnlineService()">{{ $t('cft.goods.receive')
                        }}</button>
                        <button v-else type="primary" class="buy-btn" @click="buyClick(item.id)">{{ $t('cft.goods.buy')
                        }}</button>
                    </view>
                </view>
            </view>
            <uni-load-more :status="loadingStatus"></uni-load-more>
            <view class="remark">
                <view>{{ $t('cft.goods.product') }}</view>
                <view>{{ roomList[selectIndex].name }} : {{ roomList[selectIndex].remark }}</view>
            </view>
        </view>
        <uni-popup ref="popup" type="center">
            <view class="model">
                <view class="model-wrap">
                    <view class="model-title">
                        <text>{{ $t('index.model.title') }}</text>
                    </view>
                    <view class="model-content" v-html="model.content"></view>
                    <view class="model-footer" @click="closeModel">
                        <button class="btn">{{ $t('index.model.close') }}</button>
                        <button class="btn">{{ $t('index.model.confirm') }}</button>
                    </view>
                </view>
            </view>
        </uni-popup>
        <toolbar :index="4"></toolbar>
    </container>
</template>

<script>
export default {
    components: {

    },
    data() {
        return {
            userInfo: {},
            onlineService: {},
            swiper: [],
            noticeList: [],
            roomList: [
                {
                    name:''
                }
            ],
            dataList: [],
            badge:0,
            selectIndex: 0,
            isFund: 0,
            loadingStatus: 'loading',
            model: {
                enable: false,
                content: ''
            },
            localModelVis: getApp().globalData.home_model
        }
    },
    computed: {

    },
    onPullDownRefresh() {
        this.getData()
        uni.stopPullDownRefresh()
    },
    onLoad() {
        this.userInfo = uni.getStorageSync("user")
        this.$uni.getOnlineService(this.userInfo.custId).then(data => {
			console.log(data)
			this.onlineService = data
		})
        // getApp().globalData.home_model = true

        // if (!this.localModelVis) {
        //     this.$uni.getConfig('AD_SETTING').then(res => {
        //         if (res && res != '') {
        //             let data = JSON.parse(res)
        //             this.model = data
        //             if (!this.localModelVis && this.model.enable) {
        //                 this.$refs.popup.open()
        //             }
        //         }
        //     })
        // }

        this.getAds()
        this.getNotice()
        this.getRoomList().then(() => {
            this.getData()

        })
    },
    methods: {
        goOnlineService() {
			window.location.href = this.onlineService.ws;
		},
        closeModel() {
            this.localModelVis = true
            getApp().globalData.home_model = true
            this.$refs.popup.close()
        },
        getGoodsList() {
            return this.$get('/app/cft/goods/list', {
                roomId: this.roomList[this.selectIndex].id
            })
        },
        getData() {
            this.showPageContent()
            this.loadingStatus = 'loading'
            this.dataList = []
            this.getGoodsList().then(res => {
                if (res.code == 0) {
                    this.dataList = res.data
                    this.loadingStatus = 'noMore'
                    this.badge = res.num
                } else {
                    uni.showToast({
                        icon: 'none',
                        title: this.$t('error.' + res.code)
                    })
                }
            }).catch(e => {
                uni.showToast({
                    icon: 'none',
                    title: this.$t('error.500')
                })
            })
        },
        getAds() {
            this.$get('/app/content/ad').then(res => {
                if (res.code == 0) {
                    this.swiper = res.data
                }
            })
        },
        getNotice() {
            this.$get('/app/content/notice').then(res => {
                if (res.code == 0) {
                    this.noticeList = res.data
                }
            })
        },
        getRoomList() {
            return new Promise((resove, reject) => {
                this.$get('/app/cft/room/list').then(res => {
                    if (res.code == 0) {
                        this.roomList = res.data
                        this.isFund = this.roomList[this.selectIndex]['isFund']
                        resove()
                    } else {
                        reject()
                    }
                })
            })

        },
        indexChange(index, item) {
            this.selectIndex = index
            this.getData()
            this.isFund = item.isFund
        },
        buyClick(id) {
            uni.showLoading({
                mask: true,
                title: 'loading...'
            })
            this.$post('/app/cft/goods/buy', {
                goodsId: id
            }).then(res => {
                uni.hideLoading()
                if (res.code == 0) {
                    uni.showToast({
                        title: this.$t('cft.goods.buy.success'),
                        icon: 'success'
                    })
                    //跳转页面
                } else {
                    uni.showToast({
                        icon: 'none',
                        title: this.$t('error.' + res.code)
                    })
                }
            }).catch(e => {
                uni.showToast({
                    icon: 'none',
                    title: this.$t('error.500')
                })
            })
        }
    }
}
</script>

<style scoped lang="scss">
.container {
    .notice-panel {
        display: flex;
        align-items: center;
        padding: 20rpx;
        height: 72rpx;
        width: 95%;
        margin: auto;

        .icon {
            width: 31rpx;
            height: 37rpx;
        }

        .text {
            flex: 1;
            color: rgb(51, 51, 51);
            font-size: 28rpx;

            .notice-swiper {
                color: #fff;
                height: 32rpx;
                line-height: 32rpx;
                margin-left: 20rpx;
                color: #1ec8db;
                font-size: 24rpx;
            }
        }
    }

    .navs-container {
        height: 80rpx;
        line-height: 80rpx;
        font-size: 26rpx;
        width: 95%;
        margin:10rpx auto;
        text-align: center;
        background: #000;
        color: #FFF;
        display: flex;
        border: 2px #C9C042 solid;
        border-radius: 20rpx;

        .navs {
            flex: 1;

            &:first-child {
                border-radius: 10rpx 0 0 10rpx;
            }

            &:last-child {
                border-radius: 0 10rpx 10rpx 0;
            }
            .badge{
                position: absolute;
                top: 0px;
                right: 0px;
                background-color: red;
                color: white;
                border-radius: 50%;
                width: 40rpx;
                height: 40rpx;
                text-align: center;
                line-height: 40rpx;
                font-size: 24rpx;
                z-index: 1;

            }
        }

        .active {
            background: linear-gradient(90deg, #ff9700, #ff6300);
        }
    }

    .goods-list {
        width: 95%;
        margin: 20rpx auto;
        // margin-bottom: 150rpx;

        .goods-item {
            box-shadow: 0 0 15px #eee;
            border-radius: 20rpx;
            margin-top: 20rpx;

            .header {
                display: flex;
                padding: 20rpx;

                .goods-img {
                    width: 84rpx;
                    height: 84rpx;
                    background: #f8f8f8;
                    border-radius: 8rpx;
                }

                .goods-name {
                    flex: 1;
                    margin-left: 10rpx;
                    color: #fc5f2e;

                    .name1 {
                        font-size: 24rpx;
                        line-height: 45rpx;
                        font-weight: bold;
                    }

                    .name2 {
                        color: #fc5f2e;
                        font-size: 20rpx;
                        // line-height: 50rpx;
                        // background: #fdfaf4;
                        border-radius: 5rpx;
                        padding: 4rpx;
                    }
                }

                .goods-circle {
                    margin: 10rpx;
                    // background: #fdfaf4;
                    border-radius: 5rpx;
                    color: #fc5f2e;
                    font-size: 24rpx;
                    height: 40rpx;
                    line-height: 40rpx;
                    padding: 5rpx;
                }
            }

            .center {
                text-align: center;
                display: flex;

                .center1 {
                    flex: 1;

                    .center1-text {
                        color: #fc5f2e;
                    }

                    .center1-text2 {
                        white-space: nowrap;
                        color: #979797;
                        font-size: 24rpx;
                    }
                }
            }

            .bottom {
                padding: 20rpx;

                .buy-btn {
                    background: linear-gradient(90deg, #ff9700, #ff6300);
                    font-size: 28rpx;
                    border-radius: 40rpx;

                    &::after {
                        border: none;
                    }
                }
            }
        }

        .active {
            background: #FFF;
        }
    }
    .remark{
        width: 90%;
        margin: auto;
        border-radius: 50rpx;
        text-align: center;
        background: linear-gradient(90deg, #ff9700, #ff6300);
        line-height: 50rpx;
        font-size: 28rpx;
        color: #fff;
        margin-bottom: 200rpx;
    }
}

.model {
    display: flex;
    position: fixed;
    z-index: 999;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;

    .model-wrap {
        width: 536rpx;
        display: flex;
        flex-direction: column;

        .model-title {
            text-align: center;
            color: #caa160;
            font-size: 32rpx;
            height: 126rpx;
            line-height: 126rpx;
            background: url('../../static/images/index_notice_header.jpg');
            background-size: cover;

            text {
                display: block;
                font-size: 37rpx;
                font-weight: 800;
                color: #7C551D;
                // padding-top: 20rpx;
            }
        }

        .model-content {
            background: #fff;
            line-height: 1.5em;
            flex: 1;
            overflow-y: auto;
            padding: 30rpx;
            min-height: 300rpx;
            max-height: 500rpx;
        }

        .model-footer {
            display: flex;
            background-color: #ffffff;
            border-radius: 0px 0px 10px 10px;
            border-top: 1rpx solid #F2F2F2;
            height: 100rpx;

            .btn {
                flex: 1;
                font-size: 32rpx;
                line-height: 100rpx;
                background: none;
                color: #AE7735;

                &::after {
                    border: none;
                }

                &:first-child {
                    color: #888888;
                    border-right: 1px solid #F2F2F2;
                }
            }
        }
    }
}</style>
