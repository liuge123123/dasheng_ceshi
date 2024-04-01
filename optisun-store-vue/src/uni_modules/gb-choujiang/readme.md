##简单粗暴的抽奖系统，可前端控制中奖概率 也可以后端控制概率，非常简单使用

##步骤

1.导入

2.使用示例
```html
<template>
	<view class="">
		<gb-choujiang nImg='' :AwardList="AwardList" :sjNum="sjNum"
			@updateMoney="updateMoney"></gb-choujiang>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				// 背景图，可网络地址，可本地
				nImg:'../../static/luck.png',
				// 中奖的索引，也就是AwardList数组的索引，sjNum不能等于4，4是立即抽奖
				sjNum: 0,
				// false前端，true前端
				isQd:false,
				// 可自己后端获取，也可以前端
				AwardList: [{
						image: '../../static/smdj.png',
						name: '神秘大奖'
					},
					{
						image: '../../static/fhb.png',
						name: '0.5个分红币'
					},
					{
						image: '../../static/fhb.png',
						name: '0.1个分红币'
					},
					{
						image: '../../static/fhb.png',
						name: '1.00个分红币'
					},
					{
						image: '../../static/begin.png',
						name: '1.00元/次'
					},
					{
						image: '../../static/fhb.png',
						name: '0.50个分红币'
					},
					{
						image: '../../static/fhb.png',
						name: '1.00个分红币'
					},
					{
						image: '../../static/fhb.png',
						name: '5.00个分红币'
					},
					{
						image: '../../static/dehb.png',
						name: '大额红包'
					}
				]
			}
		},
		onLoad() {
           this.getData()
		},
		methods: {
			// 中奖后的逻辑
			updateMoney(money) {
				uni.showToast({
					title: '恭喜获得-' + this.AwardList[money].name,
					icon: "none",
					position: 'bottom'
				})
				// 中奖后获取最新状态
				this.getData()
			},
			// 获取本次中奖的
			getData(){
				// 概率指定，如果是前端控制，如果后端控制下面步骤判断步骤可以省略  直接后端获取sjNum值即可
				if(this.isQd){
					// ------------------------------这里写获取后端中奖索引
					// 后端赋值
					this.sjNum = this.sjNum || 0
				}else{
					// 概率控制  概率100%
					let sj = Math.round(Math.random() * 100)
					// 前端控制概率
					if (sj < 20) {
						this.sjNum = 0
					} else if (sj >= 20) {
						this.sjNum = 1
					} else if (sj >= 40) {
						this.sjNum = 2
					} else if (sj >= 50) {
						this.sjNum = 3
					} else if (sj >= 60) {
						this.sjNum = 5
					} else if (sj >= 80) {
						this.sjNum = 6
					} else if (sj >= 90) {
						this.sjNum = 7
					} else {
						this.sjNum = 8
					}
				}
			}
			
		}
	}
</script>

<style>
	
</style>


```

###温馨提示：前端处理判定的话 容易被篡改，最好是后端处理判定本次中奖概率以及根据你设定的百分比去处理相关逻辑，然后搞个字段去存储这次指定的索引，然后返回给前端这次抽奖他中奖的索引，然后前端他会根据你提供的索引随机转动，最终到指定索引的奖品，然后抽奖结束之后后端执行逻辑，核对下存储的索引是否和执行中奖后传过来的值是否一致 等一些逻辑