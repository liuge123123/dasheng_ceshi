<template>
  <el-dialog :visible.sync="visible" title="系统赠送、扣减体验金" size="small" width="500px" class="container">
    <div v-loading="loading">
      <el-form  :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
        <el-form-item label="客户Id" prop="custId">
            {{dataForm.custId}}
        </el-form-item>
        <el-form-item label="客户名称" prop="custId">
            {{dataForm.custName}}
        </el-form-item>
        <el-form-item label="当前余额" prop="custId">
          {{dataForm.registerMoney}}
        </el-form-item>
        <el-form-item label="方式">
          <el-radio-group v-model="dataForm.type">
            <el-radio :label="1">增加</el-radio>
            <el-radio :label="2">扣减</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="dataForm.type==2" label="扣减金额" prop="amount">
          <el-input-number v-model="dataForm.amount" :min="1"  ></el-input-number>
        </el-form-item>
        <el-form-item v-else label="赠送金额" prop="amount">
          <el-input-number v-model="dataForm.amount" :min="1"  ></el-input-number>
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
          custId: '',
          custName:'',
          balance:0,
          amount: 0,
          type:1
        },
        dataRule: {}
      }
    },
    methods: {
      goBack(){
        this.visible=false
        this.$emit('back', {})
      },
      init (row) {
        this.$nextTick(() => {
          this.dataForm.custId= row.custId
          this.dataForm.custName =row.custName
          this.dataForm.registerMoney =row.registerMoney
        })
        this.visible = true
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/cust/cust/registerMoney`),
              method: 'post',
              data: this.$http.adornData({
                'custId': this.dataForm.custId,
                'money': this.dataForm.amount,
                'type':this.dataForm.type
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
