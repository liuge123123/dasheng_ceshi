<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="订单号" prop="orderNo">
      <div>{{dataForm.orderNo}}</div>
    </el-form-item>
    <el-form-item label="客户" prop="custId">
      <cust-span v-model="dataForm.custId"></cust-span>
    </el-form-item>
    <el-form-item label="关联商品" prop="goodsId">
      <g-good-span v-model="dataForm.goodsId"></g-good-span>
    </el-form-item>
    <el-form-item label="关联房间" prop="roomId">
      <room-span v-model="dataForm.roomId"></room-span>
    </el-form-item>
    <el-form-item label="总价" prop="money">
      <div>{{dataForm.money}}</div>
    </el-form-item>
    <el-form-item label="单价" prop="unitPrice">
      <div>{{dataForm.unitPrice}}</div>
    </el-form-item>
    <el-form-item label="数量" prop="num">
      <div>{{dataForm.num}}</div>
    </el-form-item>
    <el-form-item label="佣金比例" prop="comRate">
      <div>{{dataForm.comRate}}</div>
    </el-form-item>
    <el-form-item label="佣金" prop="commission">
      <div>{{dataForm.commission}}</div>
    </el-form-item>
    <el-form-item label="状态" prop="status">
      <div v-if="dataForm.status == 1" class="yellow">待处理</div>
      <div v-if="dataForm.status == 2" class="green">已完成</div>
    </el-form-item>
      <el-form-item label="建立时间:" prop="createTime">
        <div>{{dataForm.createTime|timeStampDateTimeFormat}}</div>
      </el-form-item>
<!--        <el-form-item style="text-align: center">-->
<!--            <el-button @click="goBack">取消</el-button>-->
<!--&lt;!&ndash;            <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>&ndash;&gt;-->
<!--        </el-form-item>-->
    </el-form>
  </el-card>
</template>

<script>
import custSpan from '@/components/span/cust-span'
import gGoodSpan from '@/components/span/g-good-span'
import roomSpan from '@/components/span/room-span'
import {timeStampDateTimeFormat} from '@/utils/index.js'
  import imgUpLoad from '@/components/upload/img-upload'
  export default {
    components:{
      imgUpLoad,
      custSpan,
      gGoodSpan,
      roomSpan
    },
    data () {
      return {
        loading: false,
        dataForm: {
          id: 0,
          orderNo: '',
          custId: '',
          goodsId: '',
          roomId: '',
          money: '',
          unitPrice: '',
          num: '',
          comRate: '',
          commission: '',
          status: '',
        },
        dataRule: {
          orderNo: [
            { required: true, message: '订单号不能为空', trigger: 'blur' }
          ],
          custId: [
            { required: true, message: '请选择客户', trigger: 'blur' }
          ],
          goodsId: [
            { required: true, message: '请选择关联商品', trigger: 'blur' }
          ],
          roomId: [
            { required: true, message: '请选择关联房间', trigger: 'blur' }
          ],
          money: [
            { required: true, message: '总价不能为空', trigger: 'blur' }
          ],
          unitPrice: [
            { required: true, message: '单价不能为空', trigger: 'blur' }
          ],
          num: [
            { required: true, message: '数量不能为空', trigger: 'blur' }
          ],
          comRate: [
            { required: true, message: '佣金比例不能为空', trigger: 'blur' }
          ],
          commission: [
            { required: true, message: '佣金不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '状态', trigger: 'blur' }
          ]
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
              url: this.$http.adornUrl(`/g/gorder/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.orderNo = data.gOrder.orderNo
                this.dataForm.custId = data.gOrder.custId
                this.dataForm.goodsId = data.gOrder.goodsId
                this.dataForm.roomId = data.gOrder.roomId
                this.dataForm.money = data.gOrder.money
                this.dataForm.unitPrice = data.gOrder.unitPrice
                this.dataForm.num = data.gOrder.num
                this.dataForm.comRate = data.gOrder.comRate
                this.dataForm.commission = data.gOrder.commission
                this.dataForm.status = data.gOrder.status
                this.dataForm.createTime = data.gOrder.createTime
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
              url: this.$http.adornUrl(`/g/gorder/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'orderNo': this.dataForm.orderNo,
                'custId': this.dataForm.custId,
                'goodsId': this.dataForm.goodsId,
                'roomId': this.dataForm.roomId,
                'money': this.dataForm.money,
                'unitPrice': this.dataForm.unitPrice,
                'num': this.dataForm.num,
                'comRate': this.dataForm.comRate,
                'commission': this.dataForm.commission,
                'status': this.dataForm.status
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
