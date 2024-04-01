<template>
  <div id="radar" :class="className" :style="{height:height,width:width}"> </div>
</template>

<script>
  // 引入基本模板
  import echarts from 'echarts/lib/echarts'
  // 引入雷达图组件
  import 'echarts/lib/chart/radar'
  // 引入提示框和图例组件
  import 'echarts/lib/component/tooltip'
  import 'echarts/lib/component/legend'
  const animationDuration = 3000
  export default {
    components: {
    },
    name: 'radar-chart',
    props: {
      className: {
        type: String,
        default: 'chart'
      },
      width: {
        type: String,
        default: '100%'
      },
      height: {
        type: String,
        default: '300px'
      }
    },
    data () {
      return {
        chart: null
      }
    },
    mounted () {
      this.$nextTick(() => {
        this.initChart()
      })
    },
    beforeDestroy () {
      if (!this.chart) {
        return
      }
      this.chart.dispose()
      this.chart = null
    },
    methods: {
      initChart () {
        const option = { // 创建图表配置数据
          tooltip: {
            trigger: 'axis',
            axisPointer: { // 坐标轴指示器，坐标轴触发有效
              type: 'line' // 默认为直线，可选为：'line' | 'shadow'
            }
          },
          radar: {
            radius: '66%',
            center: ['50%', '42%'],
            splitNumber: 8,
            splitArea: {
              areaStyle: {
                color: 'rgba(127,95,132,.3)',
                opacity: 1,
                shadowBlur: 45,
                shadowColor: 'rgba(0,0,0,.5)',
                shadowOffsetX: 0,
                shadowOffsetY: 15
              }
            },
            indicator: [
              { name: '销售额', max: 10000 },
              { name: '管理', max: 20000 },
              { name: '信息技术', max: 20000 },
              { name: '客户支持', max: 20000 },
              { name: '发展', max: 20000 },
              { name: '营销', max: 20000 }
            ]
          },
          legend: {
            left: 'center',
            bottom: '10',
            data: ['预算', '支出', '实际支出']
          },
          series: [{
            type: 'radar',
            symbolSize: 0,
            areaStyle: {
              normal: {
                shadowBlur: 13,
                shadowColor: 'rgba(0,0,0,.2)',
                shadowOffsetX: 0,
                shadowOffsetY: 10,
                opacity: 1
              }
            },
            data: [
              {
                value: [5000, 7000, 12000, 11000, 15000, 14000],
                name: '预算'
              },
              {
                value: [4000, 9000, 15000, 15000, 13000, 11000],
                name: '支出'
              },
              {
                value: [5500, 11000, 12000, 15000, 12000, 12000],
                name: '实际支出'
              }
            ],
            animationDuration: animationDuration
          }]
        }
        // 初始化图表
        const chartObj = echarts.init(document.getElementById('radar'))
        chartObj.setOption(option)
      }
    }
  }
</script>

<style scoped>
  .container{width: 500px;height: 400px;}
</style>
