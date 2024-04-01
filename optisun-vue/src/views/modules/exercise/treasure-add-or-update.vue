<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="类型" prop="type">
        <el-select v-model="dataForm.type" placeholder="类型">
          <el-option value="1" label="固定金额"></el-option>
          <el-option value="0" label="随机金额"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="总金额" prop="amount">
        <el-input v-model="dataForm.amount" placeholder="总金额"></el-input>
      </el-form-item>
      <el-form-item label="数量" prop="number">
        <el-input v-model="dataForm.number" placeholder="数量"></el-input>
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
  data() {
    return {
      loading: false,
      dataForm: {
        id: 0,
        uid: '',
        type: '',
        amount: '',
        number: '',
        code: '',
        status: '',
        createTime: ''
      },
      dataRule: {
        uid: [
          { required: true, message: '用户ID不能为空', trigger: 'blur' }
        ],
        type: [
          { required: true, message: '类型：1-固定金额，2-随机金额不能为空', trigger: 'blur' }
        ],
        amount: [
          { required: true, message: '总金额不能为空', trigger: 'blur' }
        ],
        number: [
          { required: true, message: '数量不能为空', trigger: 'blur' }
        ],
        code: [
          { required: true, message: '红包码不能为空', trigger: 'blur' }
        ],
        status: [
          { required: true, message: '状态：0-未抢完，1-已抢完不能为空', trigger: 'blur' }
        ],
        createTime: [
          { required: true, message: '创建时间不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    goBack() {
      this.$emit('back', {})
    },
    init(id) {
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/exercise/treasure/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({ data }) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.uid = data.treasure.uid
              this.dataForm.type = data.treasure.type
              this.dataForm.amount = data.treasure.amount
              this.dataForm.number = data.treasure.number
              this.dataForm.code = data.treasure.code
              this.dataForm.status = data.treasure.status
              this.dataForm.createTime = data.treasure.createTime
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/exercise/treasure/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'uid': this.dataForm.uid,
              'type': this.dataForm.type,
              'amount': this.dataForm.amount,
              'number': this.dataForm.number,
              'code': this.dataForm.code,
              'status': this.dataForm.status,
              'createTime': this.dataForm.createTime
            })
          }).then(({ data }) => {
            this.loading = false
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('back', { refresh: true })
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
