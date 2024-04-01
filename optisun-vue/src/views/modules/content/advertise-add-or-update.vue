<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="图片名称" prop="advertiseName">
        <el-input v-model="dataForm.advertiseName" placeholder="图片名称"></el-input>
      </el-form-item>
     <el-form-item label="图片地址" prop="imgUrl">
       <img-upload  v-model="dataForm.imgUrl"></img-upload>
     </el-form-item>
    <el-form-item label="图片链接" prop="linkUrl">
      <el-input v-model="dataForm.linkUrl" placeholder="图片链接"></el-input>
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
          id: 0,
          imgUrl: '',
          linkUrl: '',
          advertiseName: '',
          status: '1'
        },
        dataRule: {
          imgUrl: [
            { required: true, message: '图片地址不能为空', trigger: 'blur' }
          ],
          linkUrl: [
            { required: true, message: '图片链接不能为空', trigger: 'blur' }
          ],
          advertiseName: [
            { required: true, message: '图片名称不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '启用-1；禁用-0不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '建立时间不能为空', trigger: 'blur' }
          ],
          createBy: [
            { required: true, message: '建立人不能为空', trigger: 'blur' }
          ],
          updateTime: [
            { required: true, message: '修改时间不能为空', trigger: 'blur' }
          ],
          updateBy: [
            { required: true, message: '修改人不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/content/advertise/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.imgUrl = data.advertise.imgUrl
                this.dataForm.linkUrl = data.advertise.linkUrl
                this.dataForm.advertiseName = data.advertise.advertiseName
                this.dataForm.status = data.advertise.status+''
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
              url: this.$http.adornUrl(`/content/advertise/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'imgUrl': this.dataForm.imgUrl,
                'linkUrl': this.dataForm.linkUrl,
                'advertiseName': this.dataForm.advertiseName,
                'status': this.dataForm.status
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
