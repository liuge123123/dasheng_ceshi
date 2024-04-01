<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        邮件设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="140px">
        <el-form-item label="邮件服务器地址：" prop="host">
          <el-input v-model="form.host" placeholder="请输入邮件服务器地址"></el-input>
          <div class="form-tips">本系统只支持smtp协议，格式一般为smtp.xxx.com，例如：163邮箱smtp.163.com，QQ邮箱smtp.qq.com等等。</div>
        </el-form-item>
        <el-form-item label="SSL连接服务器：" prop="sslEnable">
          <el-switch v-model="form.sslEnable"></el-switch>
        </el-form-item>
        <el-form-item label="邮件服务器端口：" prop="port">
          <el-input v-model="form.port" placeholder="请输入发送邮件服务器端口号"></el-input>
          <div class="form-tips">一般使用SSL端口号465，不使用SSL端口号为25</div>
        </el-form-item>
        <el-form-item label="发送者邮箱账号：" prop="from">
          <el-input v-model="form.from" placeholder="请输入发送者邮箱账号"></el-input>
        </el-form-item>
        <el-form-item label="发送者邮箱密码：" prop="pass">
          <el-input v-model="form.pass" type="password" placeholder="请输入发送者邮箱密码"></el-input>
          <div class="form-tips">QQ邮箱等部分邮件服务必须使用平台的授权码，从邮件服务商获取授权码填写到密码输入框即可。</div>
        </el-form-item>
        <el-form-item label="发送测试邮件：">
          <el-input v-model="mailTo" placeholder="请输入测试邮件地址">
            <el-button slot="append" @click="sendTestMail">发送</el-button>
          </el-input>
        </el-form-item>
        <div style="text-align: center">
          <el-button icon="el-icon-check" type="primary" @click="setConfig">保存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
export default {
  name: "remindsetting",
  data() {
    return {
      key: 'MAIL_SETTING',
      loading: false,
      mailTo: '',
      form: {
        host: '',
        port: '',
        from: '',
        pass: '',
        sslEnable: true
      },
      formRule: {
        host: [{required: true, message: '请输入邮件服务器地址', tigger: 'change'}],
        port: [{required: true, message: '请输入邮件服务器端口', tigger: 'change'}],
        from: [{required: true, message: '请输入发送者邮箱账号', tigger: 'change'}],
        pass: [{required: true, message: '请输入发送者邮箱密码', tigger: 'change'}]
      }
    }
  },
  filters: {},
  computed: {},
  activated() {
    this.getConfig()
  },
  methods: {
    getConfig() {
      this.$http({
        url: this.$http.adornUrl(`/sys/config/getVal`),
        method: 'get',
        params: this.$http.adornParams({
          key: this.key
        })
      }).then(({data}) => {
        if (data.code == 0) {
          if (data.data && data.data != '') {
            let config = JSON.parse(data.data)
            this.form = config
          }
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    setConfig() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading = true
          let params = {
            key: this.key,
            val: JSON.stringify(this.form)
          }
          this.$http({
            url: this.$http.adornUrl(`/sys/config/saveVal`),
            method: 'post',
            data: this.$http.adornData(params)
          }).then(({data}) => {
            this.loading = false
            if (data.code == 0) {
              this.$message.success('操作成功')
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    sendTestMail() {
      let params = this.form
      params.mailTo = this.mailTo
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/sys/config/sendTestMail`),
            method: 'post',
            data: this.$http.adornData(params)
          }).then(({data}) => {
            this.loading = false
            if (data.code == 0) {
              this.$message.success('发送成功')
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

<style scoped lang="scss">
.border-line {
  /deep/ .el-form-item__content {
    border-bottom: 1px solid #eeeeee;

  }
}
</style>
