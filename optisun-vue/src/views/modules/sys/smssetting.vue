<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        短信设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="140px">
        <el-form-item label="短信服务商：">
          <el-select v-model="form.type">
            <el-option value="aliyun" label="阿里云"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="应用ID：" prop="appId">
          <el-input v-model="form.appId" placeholder="请输入应用ID"></el-input>
          <div class="form-tips">从短信服务商获取</div>
        </el-form-item>
        <el-form-item label="应用密钥：" prop="appSecret">
          <el-input v-model="form.appSecret" placeholder="请输入应用密钥"></el-input>
          <div class="form-tips">从短信服务商获取</div>
        </el-form-item>
        <el-form-item label="短信签名：" prop="sign">
          <el-input v-model="form.sign" placeholder="请输入短信签名"></el-input>
          <div class="form-tips">短信签名需要在短信服务商申请备案，然后将备案后的短信签名填写到此处。</div>
        </el-form-item>
        <el-form-item label="测试短信模版：" prop="tplCode">
          <el-input v-model="form.tplCode" placeholder="请输入测试模版的编号"></el-input>
          <div class="form-tips">短信模版需要在短信服务商申请备案，然后将备案后的短信编码填写到此处，模版中使用变量：平台名称${customer}。</div>
        </el-form-item>
        <el-form-item label="发送测试短信：">
          <el-input v-model="smsTo" placeholder="请输入接受测试短信的手机号">
            <el-button slot="append" @click="sendTest">发送</el-button>
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
      key: 'SMS_SETTING',
      loading: false,
      smsTo: '',
      form: {
        type: 'aliyun',
        appId: '',
        appSecret: '',
        sign: '',
        tplCode: ''
      },
      formRule: {
        appId: [{required: true, message: '请输入应用ID', tigger: 'change'}],
        appSecret: [{required: true, message: '请输入应用密钥', tigger: 'change'}],
        sign: [{required: true, message: '请输入短信签名', tigger: 'change'}],
        tplCode: [{required: true, message: '请输入测试短信模版编号', tigger: 'change'}]
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
    sendTest() {
      let params = this.form
      params.smsTo = this.smsTo
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/sys/config/sendTestSms`),
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
