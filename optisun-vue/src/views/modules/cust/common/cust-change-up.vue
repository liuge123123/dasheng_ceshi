<template>
  <el-dialog :visible.sync="visible" title="调整上级" size="small" width="30%" class="container">
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="客户Id" prop="custId">
        {{dataForm.custId}}
      </el-form-item>
      <el-form-item label="客户名称" prop="custName">
        {{dataForm.custName}}
      </el-form-item>
      <el-form-item label="上级客户Id" prop="parentId">
          <el-input-number v-model="dataForm.parentId" :min="0" :precision="0"></el-input-number>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>
export default {
  name: "cust-change-up",
  data(){
    return{
      visible:false,
      loading:false,
      dataForm:{
        custId:'',
        custName:'',
        parentId:''
      },
      dataRule: {
      }
    }
  },
  methods:{
    init(row){
      this.visible=true
      this.$nextTick(() => {
        this.dataForm.custId= row.custId
        this.dataForm.custName =row.custName
        this.dataForm.parentId=row.parentId
      })
    },
    goBack(){
      this.visible=false
      this.$emit('back', {})
    },
    // 表单提交
    dataFormSubmit () {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/cust/cust/changeUp`),
            method: 'post',
            data: this.$http.adornData({
              'custId': this.dataForm.custId,
              'parentId': this.dataForm.parentId
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

<style scoped>

</style>
