<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="交易ID" prop="transId">
      <el-input v-model="dataForm.transId" placeholder="交易ID"></el-input>
    </el-form-item>
    <el-form-item label="充值金额" prop="money">
      <el-input v-model="dataForm.money" placeholder="充值金额"></el-input>
    </el-form-item>
    <el-form-item label="短信全文" prop="allContent">
      <el-input v-model="dataForm.allContent" placeholder="短信全文"></el-input>
    </el-form-item>
    <el-form-item label="发件人" prop="comeTo">
      <el-input v-model="dataForm.comeTo" placeholder="发件人"></el-input>
    </el-form-item>
    <el-form-item label="接收时间" prop="receiveTime">
      <el-input v-model="dataForm.receiveTime" placeholder="接收时间"></el-input>
    </el-form-item>
    <el-form-item label="处理时间" prop="doneTime">
      <el-input v-model="dataForm.doneTime" placeholder="处理时间"></el-input>
    </el-form-item>
    <el-form-item label="是否处理" prop="isDone">
      <el-input v-model="dataForm.isDone" placeholder="是否处理"></el-input>
    </el-form-item>
    <el-form-item label="关联的短信id" prop="relId">
      <el-input v-model="dataForm.relId" placeholder="关联的短信id"></el-input>
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
          transId: '',
          money: '',
          allContent: '',
          comeTo: '',
          receiveTime: '',
          doneTime: '',
          isDone: '',
          relId: ''
        },
        dataRule: {
          transId: [
            { required: true, message: '交易ID不能为空', trigger: 'blur' }
          ],
          money: [
            { required: true, message: '充值金额不能为空', trigger: 'blur' }
          ],
          allContent: [
            { required: true, message: '短信全文不能为空', trigger: 'blur' }
          ],
          comeTo: [
            { required: true, message: '发件人不能为空', trigger: 'blur' }
          ],
          receiveTime: [
            { required: true, message: '接收时间不能为空', trigger: 'blur' }
          ],
          doneTime: [
            { required: true, message: '处理时间不能为空', trigger: 'blur' }
          ],
          isDone: [
            { required: true, message: '是否处理不能为空', trigger: 'blur' }
          ],
          relId: [
            { required: true, message: '关联的短信id不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/s/ssms/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.transId = data.sSms.transId
                this.dataForm.money = data.sSms.money
                this.dataForm.allContent = data.sSms.allContent
                this.dataForm.comeTo = data.sSms.comeTo
                this.dataForm.receiveTime = data.sSms.receiveTime
                this.dataForm.doneTime = data.sSms.doneTime
                this.dataForm.isDone = data.sSms.isDone
                this.dataForm.relId = data.sSms.relId
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
              url: this.$http.adornUrl(`/s/ssms/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'transId': this.dataForm.transId,
                'money': this.dataForm.money,
                'allContent': this.dataForm.allContent,
                'comeTo': this.dataForm.comeTo,
                'receiveTime': this.dataForm.receiveTime,
                'doneTime': this.dataForm.doneTime,
                'isDone': this.dataForm.isDone,
                'relId': this.dataForm.relId
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
