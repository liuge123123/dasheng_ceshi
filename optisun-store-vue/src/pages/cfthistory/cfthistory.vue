<template>
    <container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
        <view class="container">
            <view class="navs-container">
                <!-- <view class="navs" v-for="(item, index) in roomList" :key="index" @click="indexChange(index)"
                    :class="{ active: selectIndex == index }">{{ item.name }}</view> -->
                <view class="navs"  @click="indexChange(1)" :class="{ active: selectIndex == 1 }">My Products</view>
                <view class="navs"  @click="indexChange(2)" :class="{ active: selectIndex == 2 }">Expired products</view>
            </view>
            <view class="goods-list">
                <!-- <div v-for="(m,index) in tabMain" v-show="selectIndex==index">{{m}}</div> -->
                <view class="goods-item" v-for="(item, index) in dataList" :key="index">
                    <view class="header">
                        <image class="goods-img" :src="item.goodsImg" mode="aspectFill"></image>
                        <view class="goods-name">
                            <view class="name1">{{ item.goodsName }}</view>
                            <!-- <view class="name2"> partagé {{ item.hours }} Heure </view> -->
                        </view>
                        <view class="goods-circle">{{ item.lastDays }}  jours restants</view>
                    </view>
                    <view class="center">
                        <view class="center1">
                            <view class="center1-text">{{ (item.goodsIncomeDay).toFixed(0) }}</view>
                            <view class="center1-text2">{{ $t('cft.goods.day.profit') }}</view>
                        </view>
                        <view class="center1">
                            <view class="center1-text">{{ (item.receiveProfit).toFixed(0) }}</view>
                            <!-- <view v-else class="center1-text">{{ (item.goodsPrice * item.goodsRate *item.goodsCycle/ 100).toFixed(2) }}</view> -->
                            <view class="center1-text2">{{ $t('cft.total.profit') }}</view>
                        </view>
                    </view>
                    <view class="center">
                        <view class="center1">
                            <!-- <view class="center1-text">{{ (item.totalProfit).toFixed(2) }}</view> -->
                            <view  class="center1-text">{{ (item.goodsIncomeDay * item.goodsCycle).toFixed(0) }}</view>
                            <view class="center1-text2">{{ $t('cft.goods.estimated') }}</view>
                        </view>
                        <view class="center1">
                            <view class="center1-text">{{ (item.goodsPrice).toFixed(0) }}</view>
                            <view class="center1-text2">{{ $t('cft.goods.price') }}</view>
                        </view>
                    </view>
                    <view class="bottom">
                        <button type="primary" v-if="item.iscanReceive" class="buy-btn" @click="receiveClick(item.id, 1,item)">{{
                            $t('cft.goods.receive') }}</button>
                        <button type="primary" v-else-if="item.orderStatus!=1" class="buy-btn" >{{
                            $t('cft.goods.receive3') }}</button>
                        <button type="primary" v-else class="buy-btn" @click="receiveClick(item.id, 0,item)">{{
                            $t('cft.goods.receive2') }}</button>
                    </view>
                </view>
            </view>
            <uni-load-more :status="loadingStatus"></uni-load-more>
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
            roomList: [],
            dataList: [],
            selectIndex: 1,
            loadingStatus: 'loading',
            localModelVis: getApp().globalData.home_model
        }
    },
    computed: {

    },
    onLoad() {
        this.getRoomList().then(() => {
            this.getData()

        })
    },
    methods: {
        receiveClick(id, type,item) {
            // type 0 不能领取 1正常领取
            if (type == 0) {
                //判断如果领取时间和 创建时间同一天   A partir de demain,cliquez dessus pour gagner des revenus.
                //和不是同一天提示 Vous avez gagné votre argent aujourd'hui, revenez demain.
                let title = this.$uni.timeFormat(item.receiveTime,'dd/mm/YYYY')==this.$uni.timeFormat(item.createTime,'dd/mm/YYYY')?
                this.$t('cft.goods.receive2tip'):this.$t('cft.goods.receive3tip')
                uni.showToast({
                    icon: 'none',
                    title: title
                })
                return
            }
            uni.showLoading({
                mask: true,
                title: 'loading...'
            })
            this.$post('/app/cft/order/receive', {
                orderId: id
            }).then(res => {
                uni.hideLoading()
                if (res.code == 0) {
                    uni.showToast({
                        title: this.$t('cft.goods.buy.success'),
                        icon: 'success'
                    })
                    this.indexChange(this.selectIndex)
                } else {
                    uni.showToast({
                        icon: 'none',
                        title: this.$t('error.' + res.code, { circle: res.msg })
                    })
                }
            }).catch(e => {
                uni.showToast({
                    icon: 'none',
                    title: this.$t('error.500')
                })
            })
        },
        getOrderList() {
            return this.$get('/app/cft/order/list', {
                //roomId: this.roomList[this.selectIndex].id,
                status:this.selectIndex
            })
        },
        getData() {
            this.showPageContent()
            this.loadingStatus = 'loading'
            this.dataList = []
            this.getOrderList().then(res => {
                if (res.code == 0) {
                    this.dataList = res.data
                    this.loadingStatus = 'noMore'
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

        getRoomList() {
            return new Promise((resove, reject) => {
                this.$get('/app/cft/room/list').then(res => {
                    if (res.code == 0) {
                        let data = res.data
                        this.roomList = data
                        resove()
                    } else {
                        reject()
                    }
                })
            })

        },
        indexChange(index) {
            this.selectIndex = index
            this.getData()
        },

    }
}
</script>

<style scoped lang="scss">
.container {
    margin-top: 20rpx;
    margin-bottom: 80rpx;

    .swiper {
        height: 400rpx;
        margin: 0 20rpx;
        background: rgba(#ffffff, 0.1);
        border-radius: 20rpx;
        box-shadow: 0 0 12px 5px rgba(0, 0, 0, 0.3);
        overflow: hidden;

        .img {
            width: 100%;
            height: 100%;
        }
    }

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
                color: #E2C795;
                font-size: 24rpx;
            }
        }
    }

    .navs-container {
        height: 80rpx;
        line-height: 80rpx;
        font-size: 26rpx;
        width: 95%;
        margin: auto;
        text-align: center;
        background: #000;
        color: #FFF;
        display: flex;
        border: 2px #C9C042 solid;
        border-radius: 20rpx;

        .navs {
            flex: 1;
        }

        .active {
            background: linear-gradient(90deg, #ff9700, #ff6300);
        }
    }

    .goods-list {
        width: 95%;
        margin: 20rpx auto;

        .goods-item {
            box-shadow: 0 0 15px #eee;
            border-radius: 20rpx;
            margin-top: 10rpx;

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
                    margin-left: 20rpx;
                    // color: #E2E2E2;

                    .name1 {
                        font-size: 24rpx;
                        line-height: 45rpx;
                        font-weight: bold;
                    }

                    .name2 {
                        color: #fc5f2e;
                        font-size: 20rpx;
                        // line-height: 50rpx;
                        background: #fdfaf4;
                        border-radius: 5rpx;
                        padding: 4rpx;
                        width: 220rpx;
                    }
                }

                .goods-circle {
                    background: #e9693f;
                    display: block;
                    height: 1.5rem;
                    line-height: 1.5rem;
                    padding: 0 1rem;
                    font-size: 12px;
                    color: #fff;
                    border-radius: 0 1rem 0 1rem;
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
    }
}
</style>
