<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '详情'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="135px" size="small">
      <el-form-item label="活动时间:" prop="name">
        <div>{{dataForm.name}}</div>
      </el-form-item>
    <el-form-item label="活动开始时间:" prop="startTime">
      <cust-span v-model="dataForm.startTime"></cust-span>
    </el-form-item>
    <el-form-item label="活动结束时间:" prop="endTime">
      <t-good-span v-model="dataForm.endTime"></t-good-span>
    </el-form-item>
    <el-form-item label="活动描述" prop="description">
      <div>{{dataForm.description}}</div>
    </el-form-item>
    <el-form-item label="奖品类型" prop="prizeType">
      <div>{{dataForm.prizeType}}</div>
    </el-form-item>
    <el-form-item label="奖品名称" prop="prizeName">
      <div>{{dataForm.prizeName}}</div>
    </el-form-item>
    <el-form-item label="奖品值" prop="prizeValue">
      <div>{{dataForm.prizeValue}}</div>
    </el-form-item>
<el-form-item label="中奖几率(%)" prop="ratio">
      <div>{{dataForm.ratio}}</div>
    </el-form-item>
        <el-form-item style="text-align: center">
            <el-button @click="goBack">取消</el-button>
        </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
  import custSpan from '@/components/span/cust-span'
  import tGoodSpan from '@/components/span/t-good-span'
  import gradeSpan from '@/components/span/grade-span'
  import {timeStampDateTimeFormat} from '@/utils/index.js'
  export default {
    components:{
      custSpan,
      tGoodSpan,
      gradeSpan
    },
    filters:{
      timeStampDateTimeFormat
    },
    data () {
      return {
        loading: false,
        dataForm: {
          id: 0,
          userId: '',
          goodsId: '',
          money: '',
          goodsMoney: '',
          parameter1: '',
          parameter2: '',
          parameter3: '',
          type: '',
          createTime: '',
          updateTime: '',
          deleteTime: '',
          delFlag: '',
          status: '',
          bigKind: '',
          gradeId: '',
          sellTime: '',
          sjId: '',
          orderNo: '',
          commission: ''
        },
        dataRule: {
        }
      }
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/exercise/record/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                console.log(data)
                this.dataForm.name = data.record.name
                this.dataForm.startTime = data.record.startTime
                this.dataForm.endTime = data.record.endTime
                this.dataForm.description = data.record.description
                this.dataForm.prizeType = data.record.prizeType
                this.dataForm.prizeName = data.record.prizeName
                this.dataForm.prizeValue = data.record.prizeValue
                this.dataForm.ratio = data.record.ratio
              }else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
    }
  }
</script>
