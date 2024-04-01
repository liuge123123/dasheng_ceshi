<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="客户ID" prop="custId">
      <el-input v-model="dataForm.custId" placeholder="客户ID"></el-input>
    </el-form-item>
    <el-form-item label="客户名称" prop="custName">
      <el-input v-model="dataForm.custName" placeholder="客户名称"></el-input>
    </el-form-item>
    <el-form-item label="签到产品ID" prop="productId">
      <el-input v-model="dataForm.productId" placeholder="签到产品ID"></el-input>
    </el-form-item>
    <el-form-item label="签到产品图片" prop="productName">
      <el-input v-model="dataForm.productName" placeholder="签到产品图片"></el-input>
    </el-form-item>
    <el-form-item label="签到产品价格" prop="productPrice">
      <el-input v-model="dataForm.productPrice" placeholder="签到产品价格"></el-input>
    </el-form-item>
    <el-form-item label="已兑换次数" prop="signNum">
      <el-input v-model="dataForm.signNum" placeholder="已兑换次数"></el-input>
    </el-form-item>
    <el-form-item label="已兑换进度,总进度为1000000" prop="signProgress">
      <el-input v-model="dataForm.signProgress" placeholder="已兑换进度,总进度为1000000"></el-input>
    </el-form-item>
    <el-form-item label="最后一次兑换时间" prop="lastTime">
      <el-input v-model="dataForm.lastTime" placeholder="最后一次兑换时间"></el-input>
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
          id: 0,
          custId: '',
          custName: '',
          productId: '',
          productName: '',
          productPrice: '',
          signNum: '',
          signProgress: '',
          lastTime: ''
        },
        dataRule: {
          custId: [
            { required: true, message: '客户ID不能为空', trigger: 'blur' }
          ],
          custName: [
            { required: true, message: '客户名称不能为空', trigger: 'blur' }
          ],
          productId: [
            { required: true, message: '签到产品ID不能为空', trigger: 'blur' }
          ],
          productName: [
            { required: true, message: '签到产品图片不能为空', trigger: 'blur' }
          ],
          productPrice: [
            { required: true, message: '签到产品价格不能为空', trigger: 'blur' }
          ],
          signNum: [
            { required: true, message: '已兑换次数不能为空', trigger: 'blur' }
          ],
          signProgress: [
            { required: true, message: '已兑换进度,总进度为1000000不能为空', trigger: 'blur' }
          ],
          lastTime: [
            { required: true, message: '最后一次兑换时间不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/exercise/signprogress/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.custId = data.signProgress.custId
                this.dataForm.custName = data.signProgress.custName
                this.dataForm.productId = data.signProgress.productId
                this.dataForm.productName = data.signProgress.productName
                this.dataForm.productPrice = data.signProgress.productPrice
                this.dataForm.signNum = data.signProgress.signNum
                this.dataForm.signProgress = data.signProgress.signProgress
                this.dataForm.lastTime = data.signProgress.lastTime
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
              url: this.$http.adornUrl(`/exercise/signprogress/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'custId': this.dataForm.custId,
                'custName': this.dataForm.custName,
                'productId': this.dataForm.productId,
                'productName': this.dataForm.productName,
                'productPrice': this.dataForm.productPrice,
                'signNum': this.dataForm.signNum,
                'signProgress': this.dataForm.signProgress,
                'lastTime': this.dataForm.lastTime
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
