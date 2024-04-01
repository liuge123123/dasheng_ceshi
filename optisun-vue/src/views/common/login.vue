<template>
  <div class="wrap">
    <div class="form-data">
      <div class="form-title">Peace carrier pigeon</div>
      <el-form class="login-form" :model="dataForm" :rules="dataRule" size="small" ref="dataForm" @keyup.enter.native="dataFormSubmit()">
        <el-form-item prop="userName">
          <el-input v-model="dataForm.userName" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="dataForm.password" placeholder="请输入密码" type="password" autocomplete="new-password"></el-input>
        </el-form-item>
        <el-form-item prop="captcha">
          <el-input v-model="dataForm.captcha" placeholder="请输入验证码" autocomplete="new-password">
            <img slot="suffix" style="height: 30px" :src="captchaPath" @click="getCaptcha">
          </el-input>
        </el-form-item>
      </el-form>
      <el-button :loading="loading" class="lang-btn" type="primary" @click="dataFormSubmit()">登录</el-button>
      <p class="right">Powered by © 2020</p>
    </div>
  </div>

</template>

<script>
  import {getUUID} from '@/utils'

  export default {
    data() {
      return {
        loading: false,
        dataForm: {
          userName: '',
          password: '',
          uuid: '',
          captcha: ''
        },
        dataRule: {
          userName: [
            {required: true, message: '用户名不能为空', trigger: 'change'}
          ],
          password: [
            {required: true, message: '密码不能为空', trigger: 'change'}
          ],
          captcha: [
            {required: true, message: '验证码不能为空', trigger: 'change'}
          ]
        },
        captchaPath: ''
      }
    },
    created() {
      this.getCaptcha()
    },
    methods: {
      // 提交表单
      dataFormSubmit() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl('/sys/login'),
              method: 'post',
              data: this.$http.adornData({
                'username': this.dataForm.userName,
                'password': this.dataForm.password,
                'uuid': this.dataForm.uuid,
                'captcha': this.dataForm.captcha
              })
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.$cookie.set('token', data.token)
                this.$router.replace({name: 'home'})
              } else {
                this.getCaptcha()
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      // 获取验证码
      getCaptcha() {
        this.dataForm.uuid = getUUID()
        this.captchaPath = this.$http.adornUrl(`/captcha.jpg?uuid=${this.dataForm.uuid}`)
      }
    }
  }
</script>

<style lang="scss">
  .login-form{
    .el-form-item {
      margin-bottom: 0;
      .el-form-item__content {
        padding: 5px 0;
        height: 44px;
        box-sizing: border-box;
        width: 340px;
        margin: 0 auto 16px;
        line-height: 14px;
        display: block;
        .el-input__inner {
          border: none;
          border-radius: 0;
          border-bottom: 1px solid #e5e5e5;
          padding: 0;
        }
      }
    }
  }
</style>

<style scoped lang="scss">
  .wrap {
    box-sizing: border-box;
    height: 100vh;
    background: url("~@/assets/img/bj.jpg") no-repeat center;
    background-size: 100% 100vh;
    position: relative;
    .form-data {
      background-color: #ffffff;
      width: 460px;
      left: 50%;
      margin-left: -230px;
      border-radius: 5px;
      box-shadow: 0 0 30px rgba(0, 0, 0, .1);
      padding: 30px 0 30px 0;
      position: fixed;
      top: 18%;
      .form-title {
        text-align: center;
        font-size: 24px;
        letter-spacing: 2px;
        margin-bottom: 30px;
      }
      .lang-btn {
        width: 340px;
        margin: 20px auto;
        display: block;
        font-size: 18px;
        letter-spacing: 5px;
      }
    }
    .right {
      position: absolute;
      width: 1180px;
      bottom: -80px;
      text-align: center;
      line-height: 40px;
      left: 50%;
      margin-left: -590px;
      color: rgba(0, 0, 0, .3);
    }
  }
</style>
