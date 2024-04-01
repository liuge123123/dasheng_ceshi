<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="产品名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="产品名称"></el-input>
    </el-form-item>
    <el-form-item label="产品图片" prop="img">
      <img-upload  v-model="dataForm.img"></img-upload>
    </el-form-item>
    <el-form-item label="每次消耗积分" prop="price">
      <el-input-number :min="0" precision="0" controls-position="right" v-model="dataForm.price"
                       placeholder="每次消耗的积分"></el-input-number>
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
          name: '',
          img: '',
          price: ''
        },
        dataRule: {
          name: [
            { required: true, message: '产品名称不能为空', trigger: 'blur' }
          ],
          img: [
            { required: true, message: '产品图片不能为空', trigger: 'blur' }
          ],
          price: [
            { required: true, message: '产品价格不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/exercise/signproduct/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.name = data.signProduct.name
                this.dataForm.img = data.signProduct.img
                this.dataForm.price = data.signProduct.price
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
              url: this.$http.adornUrl(`/exercise/signproduct/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'img': this.dataForm.img,
                'price': this.dataForm.price
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
