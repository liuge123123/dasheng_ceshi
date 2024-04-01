<template>
  <el-dialog
    title="用户信息"
    :visible.sync="visible"
     width="40%"
    :append-to-body="true">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="用户ID">
        <span>{{ user.userId }}</span>
      </el-form-item>
      <el-form-item label="账号">
        <span>{{ user.username }}</span>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input  v-model="dataForm.name"></el-input>
      </el-form-item>
      <el-form-item label="头像" prop="avatar">
          <img-upload v-model="dataForm.avatar"></img-upload>
      </el-form-item>
      <el-form-item label="客服地址" prop="customerUrl">
        <el-input  v-model="dataForm.customerUrl"></el-input>
      </el-form-item>
      <el-form-item label="上级用户id">
        <span>{{ dataForm.relationId }}</span>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  import { clearLoginInfo } from '@/utils'
  import imgUpload from "../../../components/upload/img-upload";
  export default {
    components:{
      imgUpload
    },
    data () {
      return {
        visible: false,
        dataForm: {
          name: '',
          avatar: '',
          customerUrl: '',
          relationId:''
        },
        dataRule: {
        }
      }
    },
    computed: {
      user: {
        get () { return this.$store.state.user.userInfo }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      }
    },
    methods: {
      // 初始化
      init () {
        this.visible = true
        console.log(this.user)
        if(this.user.userId) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/sys/user/info/${this.user.userId}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.userName = data.user.username;
              this.dataForm.name =  data.user.name;
              this.dataForm.avatar =  data.user.avatar;
              this.dataForm.customerUrl = data.user.customerUrl
              this.dataForm.relationId = data.user.relationId
            }else{
              this.$message.error(data.msg)
            }
          })
        }
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl('/sys/user/updateInfo'),
              method: 'post',
              data: this.$http.adornData({
                'userId':this.user.userId,
                'name': this.dataForm.name,
                'avatar': this.dataForm.avatar,
                'customerUrl':this.dataForm.customerUrl
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
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

