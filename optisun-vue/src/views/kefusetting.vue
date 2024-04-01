<template>
  <el-card style="margin: 15px">
    <div slot="header">个人客服设置</div>
    <div v-loading="loading">
      <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="200px">
        <el-form-item label="客服名称" prop="password">
          <el-input v-model="dataForm.name"></el-input>
        </el-form-item>
        <el-form-item label="客服头像" prop="newPassword">
          <img-upload v-model="dataForm.avatar"></img-upload>
        </el-form-item>
        <el-form-item label="whatsapp链接" >
          <el-input v-model="dataForm.customerUrlConfig.ws"></el-input>
        </el-form-item>
        <el-form-item label="whatsapp群组" >
          <el-input v-model="dataForm.customerUrlConfig.wsGroup"></el-input>
        </el-form-item>
        <el-form-item label="飞机群组" >
          <el-input v-model="dataForm.customerUrlConfig.tgGroup"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="dataFormSubmit()">提交</el-button>
        </el-form-item>
      </el-form>
    </div>
  </el-card>
</template>

<script>
export default {
  name: "kefusetting",
  data(){
    return {
      loading: false,
      dataForm: {
        name: '',
        avatar: '',
        customerUrlConfig: {
          ws: "",
          tgGroup: "",
          wsGroup: ""
        }
      },
      dataRule: {}
    }
  },
  computed:{
    userInfo(){
      return this.$store.state.user.userInfo;
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init(){
      this.loading = true
      this.$http({
        url: this.$http.adornUrl(`/sys/user/info/${this.userInfo.userId}`),
        method: 'get',
        params: this.$http.adornParams()
      }).then(({data}) => {
        this.loading = false
        if (data && data.code === 0) {
          this.dataForm.name =  data.user.name
          this.dataForm.avatar =  data.user.avatar
          this.dataForm.customerUrl = data.user.customerUrl
          if(data.user.customerUrl && data.user.customerUrl != ''){
            this.dataForm.customerUrlConfig = JSON.parse(data.user.customerUrl)
          }
        }else{
          this.$message.error(data.msg)
        }
      })
    },
    // 表单提交
    dataFormSubmit () {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/sys/user/setkefu`),
            method: 'post',
            data: this.$http.adornData({
              'name':this.dataForm.name,
              'avatar':this.dataForm.avatar,
              'customerUrl': JSON.stringify(this.dataForm.customerUrlConfig)
            })
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500
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
