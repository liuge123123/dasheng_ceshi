<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '详情'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="135px" size="small">
      <el-form-item label="订单号:" prop="orderNo">
        <div>{{dataForm.orderNo}}</div>
      </el-form-item>
    <el-form-item label="客户:" prop="userId">
      <cust-span v-model="dataForm.custId"></cust-span>
    </el-form-item>
    <el-form-item label="商品:" prop="goodsId">
      <t-good-span v-model="dataForm.goodsId"></t-good-span>
    </el-form-item>
    <el-form-item label="付款:" prop="money">
      <div>{{dataForm.money}}</div>
    </el-form-item>
    <el-form-item label="下单时候商品价格:" prop="goodsMoney">
      <div>{{dataForm.goodsMoney}}</div>
    </el-form-item>
    <el-form-item label="佣金比例(%):" prop="parameter1">
      <div>{{dataForm.parameter1}}</div>
    </el-form-item>
    <el-form-item label="佣金:" prop="parameter2">
      <div>{{dataForm.parameter2}}</div>
    </el-form-item>
<!--    <el-form-item label="参数3:" prop="parameter3">-->
<!--      <div>{{dataForm.parameter3}}</div>-->
<!--    </el-form-item>-->
<!--    <el-form-item label="审核状态:" prop="type">-->
<!--      <div v-if="dataForm.type == 0" class="yellow">待审</div>-->
<!--      <div v-if="dataForm.type == 1" class="green">同意</div>-->
<!--      <div v-if="dataForm.type == 2" class="red">驳回</div>-->
<!--    </el-form-item>-->

    <el-form-item label="销售状态:" prop="status">
      <div v-if="dataForm.type == 0">刚买入</div>
      <div v-if="dataForm.type == 1" class="yellow">出售中</div>
      <div v-if="dataForm.type == 2" class="green">已出售</div>
    </el-form-item>
<!--    <el-form-item label="订单状态:" prop="bigKind">-->
<!--      <div v-if="dataForm.type == 0">当前订单</div>-->
<!--      <div v-if="dataForm.type == 1" class="yellow">出售商品</div>-->
<!--      <div v-if="dataForm.type == 2" class="blue">定居</div>-->
<!--    </el-form-item>-->
    <el-form-item label="关联等级:" prop="gradeId">
      <grade-span v-model="dataForm.gradeId"></grade-span>
    </el-form-item>
    <el-form-item label="销售时间:" prop="sellTime">
      <div>{{dataForm.sellTime|timeStampDateTimeFormat}}</div>
    </el-form-item>
      <el-form-item label="建立时间:" prop="createTime">
        <div>{{dataForm.createTime|timeStampDateTimeFormat}}</div>
      </el-form-item>
    <el-form-item label="真实个人佣金:" prop="commission">
      <div>{{dataForm.commission}}</div>
    </el-form-item>
        <el-form-item style="text-align: center">
            <el-button @click="goBack">取消</el-button>
<!--            <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>-->
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
          // custId: [
          //   { required: true, message: '请选择客户', trigger: 'blur' }
          // ],
          // goodsId: [
          //   { required: true, message: '请选择商品', trigger: 'blur' }
          // ],
          // money: [
          //   { required: true, message: '付款不能为空', trigger: 'blur' }
          // ],
          // goodsMoney: [
          //   { required: true, message: '下单时候商品价格不能为空', trigger: 'blur' }
          // ],
          // parameter1: [
          //   { required: true, message: '参数1不能为空', trigger: 'blur' }
          // ],
          // parameter2: [
          //   { required: true, message: '参数2不能为空', trigger: 'blur' }
          // ],
          // parameter3: [
          //   { required: true, message: '参数3不能为空', trigger: 'blur' }
          // ],
          // type: [
          //   { required: true, message: '状态值', trigger: 'blur' }
          // ],
          // status: [
          //   { required: true, message: '状态值', trigger: 'blur' }
          // ],
          // bigKind: [
          //   { required: true, message: '状态值', trigger: 'blur' }
          // ],
          // gradeId: [
          //   { required: true, message: '关联等级', trigger: 'blur' }
          // ],
          // sellTime: [
          //   { required: true, message: '不能为空', trigger: 'blur' }
          // ],
          // sjId: [
          //   { required: true, message: '不能为空', trigger: 'blur' }
          // ],
          // orderNo: [
          //   { required: true, message: '订单号不能为空', trigger: 'blur' }
          // ],
          // commission: [
          //   { required: true, message: '真实个人佣金不能为空', trigger: 'blur' }
          // ]
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
              url: this.$http.adornUrl(`/t/torder/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                console.log(data)
                this.dataForm.custId = data.tOrder.custId
                this.dataForm.goodsId = data.tOrder.goodsId
                this.dataForm.money = data.tOrder.money
                this.dataForm.goodsMoney = data.tOrder.goodsMoney
                this.dataForm.parameter1 = data.tOrder.parameter1
                this.dataForm.parameter2 = data.tOrder.parameter2
                this.dataForm.parameter3 = data.tOrder.parameter3
                this.dataForm.type = data.tOrder.type
                this.dataForm.status = data.tOrder.status
                this.dataForm.bigKind = data.tOrder.bigKind
                this.dataForm.gradeId = data.tOrder.gradeId
                this.dataForm.sellTime = data.tOrder.sellTime
                this.dataForm.createTime = data.tOrder.createTime
                this.dataForm.sjId = data.tOrder.sjId
                this.dataForm.orderNo = data.tOrder.orderNo
                this.dataForm.commission = data.tOrder.commission
              }else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/t/torder/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'custId': this.dataForm.custId,
                'goodsId': this.dataForm.goodsId,
                'money': this.dataForm.money,
                'goodsMoney': this.dataForm.goodsMoney,
                'parameter1': this.dataForm.parameter1,
                'parameter2': this.dataForm.parameter2,
                'parameter3': this.dataForm.parameter3,
                'type': this.dataForm.type,
                'status': this.dataForm.status,
                'bigKind': this.dataForm.bigKind,
                'gradeId': this.dataForm.gradeId,
                'sellTime': this.dataForm.sellTime,
                'sjId': this.dataForm.sjId,
                'orderNo': this.dataForm.orderNo,
                'commission': this.dataForm.commission
              })
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('back', {refresh: true})
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
