<template>
    <container ref="container" :headerVisable="true" :status="pageStatus" @refresh="getData()">
        <view class="container1" v-if="page == 1">
            <view class="top">
                <view class="text">{{ $t('recharge.left') }}</view>
                <view class="val" v-if="userInfo">{{ userInfo.leftRechargeMoney || 0 }}{{ $t('app.unit') }}</view>
            </view>
            <view class="input">
                <input class="input" :placeholder="$t('treasure.input.value')" v-model="code" />
            </view>

            <button class="comfirm" @click="comfirm">{{ $t('treasure.go') }}</button>

            <view class="desc">
                <view class="title">{{ $t('treasure.tips') }}</view>
                <view class="content">
                    <rich code="treasure"></rich>
                </view>
            </view>
        </view>
        <uni-popup ref="popup" type="center">
            <view class="model">
                <view class="model-wrap">
                    <view class="model-content">congratulations</view>
                    <view class="model-content2">{{ getMoney }}Rs</view>
                    <view class="model-content3" @click="closeModel">Get it now</view>
                    
                </view>
            </view>
        </uni-popup>
    </container>
</template>

<script>

export default {
    components: {},
    data() {
        return {
            page: 1,
            code: '',
            userInfo: null,
            getMoney: 0
        }
    },
    onLoad() {
        this.showPageContent()

    },
    onShow() {
        this.getuserinfo()
    },
    methods: {
        closeModel() {
			this.$refs.popup.close()
		},
        getuserinfo() {
            this.$uni.getUserInfo().then(user => {
                this.userInfo = user
            })
        },
        comfirm() {
            this.code = (this.code + '').trim()

            if (this.code == '') {
                uni.showToast({
                    icon: 'none',
                    title: this.$t('treasure.validate.code.blank')
                })
                return
            }

            uni.showLoading({
                mask: true,
                title: 'loading...'
            })
            this.$post('/app/account/treasure/receive', {
                code: this.code,
            }).then(res => {
                uni.hideLoading()
                if (res.code == 0) {
                    uni.showToast({
                        icon: "none",
                        title: this.$t('treasure.confirm.success')
                    })
                    this.getMoney = res.data
                    this.$refs.popup.open() 
                    this.getuserinfo()
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

<style lang="scss" scoped>
.root-container {
    background: #fff;
    overflow-y: auto;
}

.container1 {
    padding: 30rpx;
    background: #FFF;
    height: 100%;

    .top {
        height: 214rpx;
        background: url('../../static/images/recharge_top_bg.jpg');
        background-size: cover;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;

        .text {
            font-size: 29rpx;
            font-weight: 500;
            color: #000;
            opacity: 0.8;
        }

        .val {
            font-size: 61rpx;
            font-weight: bold;
            color: #FFFFFF;
            line-height: 72rpx;
            margin-top: 44rpx;
            // background: linear-gradient(0deg, #D19C69 0%, #EAD2AF 100%);
            background: #000;
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }
    }

    .input {
        display: flex;
        height: 117rpx;
        line-height: 117rpx;
        border-bottom: 1rpx solid #e6e6e6;
        font-size: 32rpx;

        .unit {}

        .input {
            flex: 1;
            text-align: center;
            padding-left: 20rpx;
        }
    }

    .money {
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        margin-top: 40rpx;

        .item {
            width: 210rpx;
            height: 100rpx;
            line-height: 100rpx;
            text-align: center;
            background: #FFFFFF;
            border: 2px solid #E6E6E6;
            margin-bottom: 20rpx;
            font-size: 30rpx;
            font-weight: 500;
            color: #666666;

            &.active {
                background: #F8F1E6;
                border: 2px solid #E8CEAA;
                color: #1ec8db;
            }
        }
    }

    .way {
        .item {
            display: flex;
            align-items: center;
            margin-top: 34rpx;

            .icon {
                width: 42rpx;
                height: 42rpx;
                object-fit: cover;
            }

            .text {
                flex: 1;
                margin: 0 20rpx;
                font-size: 30rpx;
                color: #333333;
            }

            .check {
                width: 32rpx;
                height: 32rpx;
            }
        }
    }

    .comfirm {
        height: 88rpx;
        background: linear-gradient(0deg, #ff9700 0%, #ff6300 100%);
        border-radius: 44rpx;
        font-size: 34rpx;
        color: #FFFFFF;
        margin-top: 42rpx;

        &::after {
            border: none;
        }
    }

    .desc {
        margin-top: 65rpx;

        .title {
            font-size: 35rpx;
            font-weight: bold;
            color: #282828;
        }

        .content {
            font-size: 29rpx;
            color: #666666;
            line-height: 59rpx;
            margin-top: 33rpx;
        }
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
        width: 100%;
        display: flex;
        flex-direction: column;
        height: 626rpx;
        // line-height: 126rpx;
        background: url('../../static/images/alert.png');
        background-size: cover;
        position: relative;

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
            width: 100%;
            position: absolute;
            top: 29%;
            text-align: center;
            font-size: 16px;
            color: #DE8235;
        }

        .model-content2 {
            width: 100%;
            position: absolute;
            top: 44%;
            text-align: center;
            font-size: 25px;
            font-weight: 600;
            color: #D43030;
        }

        .model-content3 {
            cursor: pointer;
            text-align: center;
            display: block;
            width: 100%;
            position: absolute;
            bottom: 12%;
            font-size: 18px;
            color: #fff;
        }

    }
}
</style>
