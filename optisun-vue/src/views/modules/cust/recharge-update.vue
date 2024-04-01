<template>
  <el-dialog :visible.sync="visible" title="修改订单" size="small" width="45%" class="container">
    <div v-loading="loading">
      <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
        <el-form-item label="客户Id" prop="custId">
            {{dataForm.custId}}
        </el-form-item>
        <el-form-item label="客户名称" prop="custName">
            {{dataForm.custName}}
        </el-form-item>
        <el-form-item label="换算前充值金额" prop="moneyFront">
          <el-input v-model="dataForm.moneyFront" @input="moneyChange"></el-input>
        </el-form-item>
        <el-form-item label="充值金额" prop="amount">
          {{dataForm.amount}}
        </el-form-item>
        <el-form-item  label="交易id" prop="transid">
           <el-input v-model="dataForm.transid"></el-input>
        </el-form-item>
        <el-form-item style="text-align: center">
          <el-button @click="goBack">取消</el-button>
          <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible:false,
        loading: false,
        dataForm: {
          rechargeId:0,
          custId: '',
          custName:'',
          moneyFront:0,
          amount: 0,
          transid:0,
          fee: 1
        },
        dataRule: {}
      }
    },
    methods: {
      moneyChange(){
        this.dataForm.amount = this.dataForm.moneyFront * this.dataForm.fee
      },
      goBack(){
        this.visible=false
        this.$emit('back', {})
      },
      init (row) {
        this.$nextTick(() => {
          this.dataForm.rechargeId = row.rechargeId
          this.dataForm.custId= row.custId
          this.dataForm.custName =row.custName
          this.dataForm.moneyFront =row.moneyFront
          this.dataForm.amount=row.amount
          this.dataForm.transid=row.transid
          this.dataForm.fee=row.fee
        })
        this.visible = true
      },
      // 表单提交
      dataFormSubmit () {
        if(isNaN(this.dataForm.moneyFront) || this.dataForm.moneyFront == 0){
          this.$message.error('充值金额必须大于0且必须为数字')
          return
        }
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/cust/recharge/updateRecharge`),
              method: 'post',
              data: this.$http.adornData({
                'rechargeId': this.dataForm.rechargeId,
                'moneyFront': this.dataForm.moneyFront,
                'amount':this.dataForm.amount,
                'transid':this.dataForm.transid
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
<style lang="scss" scoped>

</style>
