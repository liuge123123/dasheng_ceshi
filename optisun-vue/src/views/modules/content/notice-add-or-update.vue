<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.noticeId ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="公告内容" prop="content">
      <el-input  type="textarea"
                 :autosize="{ minRows: 2, maxRows: 4}" v-model="dataForm.content" placeholder="公告内容"></el-input>
    </el-form-item>
    <el-form-item label="状态" prop="status">
      <el-radio-group v-model="dataForm.status">
        <el-radio label="1" v-model="dataForm.status">正常</el-radio>
        <el-radio label="0" v-model="dataForm.status">隐藏</el-radio>
      </el-radio-group>
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
          noticeId: 0,
          content: '',
          status: '0'
        },
        dataRule: {
          content: [
            { required: true, message: '公告内容不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '状态(0-开启;1-停用)不能为空', trigger: 'blur' }
          ],
          receiveId: [
            { required: true, message: '接收人(*-所有人；指定接收人Id)不能为空', trigger: 'blur' }
          ],
          createBy: [
            { required: true, message: '建立人不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '建立时间不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.noticeId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.noticeId) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/content/notice/info/${this.dataForm.noticeId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.content = data.notice.content
                this.dataForm.status = data.notice.status+''
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
              url: this.$http.adornUrl(`/content/notice/${!this.dataForm.noticeId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'noticeId': this.dataForm.noticeId || undefined,
                'content': this.dataForm.content,
                'status': this.dataForm.status,
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
