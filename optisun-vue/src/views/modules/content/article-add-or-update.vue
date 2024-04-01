<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.articleId ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="文章标题" prop="title">
        <el-input v-model="dataForm.title" placeholder="文章标题"></el-input>
      </el-form-item>
      <el-form-item label="文章内容" prop="content">
        <rich-editor v-model="dataForm.content"></rich-editor>
      </el-form-item>
      <el-form-item label="排序(越大越靠前)" prop="goodsSort">
        <el-input-number
          v-model="dataForm.sort"
          style="width: 30%;"
          controls-position="right"
          label="排序"
        ></el-input-number>
      </el-form-item>
      <el-form-item label="位置编码" prop="positionCode">
        <el-input v-model="dataForm.positionCode"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="dataForm.status">
          <el-radio label="1" v-model="dataForm.status">上架</el-radio>
          <el-radio label="0" v-model="dataForm.status">下架</el-radio>
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
  data() {
    return {
      loading: false,
      dataForm: {
        articleId: 0,
        title: '',
        content: '',
        status:'1',
        sort:0,
        positionCode:''
      },
      dataRule: {
        title: [
          {required: true, message: '文章标题不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '文章内容不能为空', trigger: 'blur'}
        ],
        positionCode: [
          {required: true, message: '位置编码不能为空', trigger: 'blur'}
        ],
      }
    }
  },
  methods: {
    goBack() {
      this.$emit('back', {})
    },
    init(id) {
      this.dataForm.articleId = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.articleId) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/content/article/info/${this.dataForm.articleId}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.title = data.article.title
              this.dataForm.content = data.article.content
              this.dataForm.status = data.article.status+''
              this.dataForm.sort =data.article.sort
              this.dataForm.positionCode= data.article.positionCode

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
            url: this.$http.adornUrl(`/content/article/${!this.dataForm.articleId ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'articleId': this.dataForm.articleId || undefined,
              'title': this.dataForm.title,
              'content': this.dataForm.content,
              'status': this.dataForm.status,
              'sort': this.dataForm.sort,
              'positionCode': this.dataForm.positionCode,
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
