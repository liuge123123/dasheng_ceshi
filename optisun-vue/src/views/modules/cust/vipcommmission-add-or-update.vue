<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="" prop="orderTime">
      <el-input v-model="dataForm.orderTime" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="orderCust">
      <el-input v-model="dataForm.orderCust" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="rewardCust">
      <el-input v-model="dataForm.rewardCust" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="" prop="rewardMoney">
      <el-input v-model="dataForm.rewardMoney" placeholder=""></el-input>
    </el-form-item>
        <el-form-item style="text-align: center">
            <el-button @click="goBack">取消</el-button>
            <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
        </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
  export default {
    data () {
      return {
        loading: false,
        dataForm: {
          orderId: 0,
          orderTime: '',
          orderCust: '',
          rewardCust: '',
          rewardMoney: ''
        },
        dataRule: {
          orderTime: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          orderCust: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          rewardCust: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          rewardMoney: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.orderId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.orderId) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/cust/vipcommmission/info/${this.dataForm.orderId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.orderTime = data.vipCommmission.orderTime
                this.dataForm.orderCust = data.vipCommmission.orderCust
                this.dataForm.rewardCust = data.vipCommmission.rewardCust
                this.dataForm.rewardMoney = data.vipCommmission.rewardMoney
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
              url: this.$http.adornUrl(`/cust/vipcommmission/${!this.dataForm.orderId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'orderId': this.dataForm.orderId || undefined,
                'orderTime': this.dataForm.orderTime,
                'orderCust': this.dataForm.orderCust,
                'rewardCust': this.dataForm.rewardCust,
                'rewardMoney': this.dataForm.rewardMoney
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
