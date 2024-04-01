<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="来源" prop="comeTo">
        <el-input v-model="dataForm.comeTo" placeholder="来源"></el-input>
      </el-form-item>
      <el-form-item label="短信内容" prop="allContent">
        <el-input type="textarea" :rows="5" v-model="dataForm.allContent" placeholder="短信内容"></el-input>
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
        comeTo: '手动',
        allContent: '',
        receiveTime: '',
        originalId: '',
        parseStatus: ''
      },
      dataRule: {
        comeTo: [
          {required: true, message: '来源不能为空', trigger: 'blur'}
        ],
        allContent: [
          {required: true, message: '短信内容不能为空', trigger: 'blur'}
        ],
        receiveTime: [
          {required: true, message: '接收时间不能为空', trigger: 'blur'}
        ],
        originalId: [
          {required: true, message: '原始id不能为空', trigger: 'blur'}
        ],
        parseStatus: [
          {required: true, message: '解析状态 0：未解析，1：解析失败，2：解析成功不能为空', trigger: 'blur'}
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
            url: this.$http.adornUrl(`/s/ssmsoriginal/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.comeTo = data.sSmsOriginal.comeTo
              this.dataForm.allContent = data.sSmsOriginal.allContent
              this.dataForm.receiveTime = data.sSmsOriginal.receiveTime
              this.dataForm.originalId = data.sSmsOriginal.originalId
              this.dataForm.parseStatus = data.sSmsOriginal.parseStatus
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
            url: this.$http.adornUrl(`/s/ssmsoriginal/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'comeTo': this.dataForm.comeTo,
              'allContent': this.dataForm.allContent,
              'receiveTime': this.dataForm.receiveTime,
              'originalId': this.dataForm.originalId,
              'parseStatus': this.dataForm.parseStatus
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
