<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        佣金设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="140px">
        <el-form-item label="服务地址：" prop="host">
          <el-input v-model="form.host" placeholder="请输入邮件服务器地址"></el-input>
          <div class="form-tips">本系统只支持smtp协议，格式一般为smtp.xxx.com，例如：163邮箱smtp.163.com，QQ邮箱smtp.qq.com等等。</div>
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
  name: "setting",
  data() {
    return {
      key: 'AGENT_SETTING',
      loading: false,
      mailTo: '',
      form: {
        host: ''
      },
      formRule: {
        host: [{required: true, message: '请输入邮件服务器地址', tigger: 'change'}]
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
