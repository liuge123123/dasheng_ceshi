<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="来源" prop="comeTo">
      <el-input v-model="dataForm.comeTo" placeholder="来源"></el-input>
    </el-form-item>
    <el-form-item label="短信内容" prop="content">
      <el-input v-model="dataForm.allContent" placeholder="短信内容"></el-input>
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
          comeTo: '',
          allContent: '',
          receiveTime: '',
          originalId: '',
          parseStatus: '',
          deviceId: '',
          transId: '',
          money: '',
          doneTime: '',
          isDone: '',
          remark: '',
          wallet: ''
        },
        dataRule: {
          comeTo: [
            { required: true, message: '来源不能为空', trigger: 'blur' }
          ],
          allContent: [
            { required: true, message: '短信内容不能为空', trigger: 'blur' }
          ],
          receiveTime: [
            { required: true, message: '接收时间不能为空', trigger: 'blur' }
          ],
          originalId: [
            { required: true, message: '原始id不能为空', trigger: 'blur' }
          ],
          parseStatus: [
            { required: true, message: '解析状态 0：未解析，1：解析失败，2：解析成功不能为空', trigger: 'blur' }
          ],
          deviceId: [
            { required: true, message: '设备ID不能为空', trigger: 'blur' }
          ],
          transId: [
            { required: true, message: '交易ID不能为空', trigger: 'blur' }
          ],
          money: [
            { required: true, message: '金额不能为空', trigger: 'blur' }
          ],
          doneTime: [
            { required: true, message: '处理时间不能为空', trigger: 'blur' }
          ],
          isDone: [
            { required: true, message: '是否处理 0-未处理，1-已上分不能为空', trigger: 'blur' }
          ],
          remark: [
            { required: true, message: '备注不能为空', trigger: 'blur' }
          ],
          wallet: [
            { required: true, message: '钱包名称不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/cust/devicessms/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.comeTo = data.devicesSms.comeTo
                this.dataForm.allContent = data.devicesSms.allContent
                this.dataForm.receiveTime = data.devicesSms.receiveTime
                this.dataForm.originalId = data.devicesSms.originalId
                this.dataForm.parseStatus = data.devicesSms.parseStatus
                this.dataForm.deviceId = data.devicesSms.deviceId
                this.dataForm.transId = data.devicesSms.transId
                this.dataForm.money = data.devicesSms.money
                this.dataForm.doneTime = data.devicesSms.doneTime
                this.dataForm.isDone = data.devicesSms.isDone
                this.dataForm.remark = data.devicesSms.remark
                this.dataForm.wallet = data.devicesSms.wallet
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
              url: this.$http.adornUrl(`/cust/devicessms/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'comeTo': this.dataForm.comeTo,
                'allContent': this.dataForm.allContent,
                'receiveTime': this.dataForm.receiveTime,
                'originalId': this.dataForm.originalId,
                'parseStatus': this.dataForm.parseStatus,
                'deviceId': this.dataForm.deviceId,
                'transId': this.dataForm.transId,
                'money': this.dataForm.money,
                'doneTime': this.dataForm.doneTime,
                'isDone': this.dataForm.isDone,
                'remark': this.dataForm.remark,
                'wallet': this.dataForm.wallet
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
