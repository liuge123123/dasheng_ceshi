<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        客服设置
      </div>
      <el-form :model="form" :rules="formRule" ref="form" class="cpf" v-loading="loading" size="small"
        label-width="140px">
        <el-form-item label="开启客服：" prop="enable">
          <el-switch v-model="form.enable"></el-switch>
        </el-form-item>
        <!--        <el-form-item label="Tel客服：" prop="tel">-->
        <!--          <el-input v-model="form.tel" placeholder="Tel客服"></el-input>-->
        <!--        </el-form-item>-->
        <el-form-item label="Whats APP：" prop="whatsapp">
          <el-input v-model="form.ws" placeholder="Whats APP"></el-input>
        </el-form-item>
        <el-form-item label="Whats APP群组：" prop="whatsapp">
          <el-input v-model="form.wsGroup" placeholder="Whats APP群组"></el-input>
        </el-form-item>
        <el-form-item label="飞机群组：" prop="whatsapp">
          <el-input v-model="form.tgGroup" placeholder="飞机群组"></el-input>
        </el-form-item>
        <el-form-item label="客服名字" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="头像" prop="avater">
          <img-upload v-model="form.avater"></img-upload>
        </el-form-item>
        <!--        <el-form-item label="网页客服：" prop="web">-->
        <!--          <el-input v-model="form.web" placeholder="网页客服"></el-input>-->
        <!--        </el-form-item>-->
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
      key: 'KEFU_SETTING',
      loading: false,
      mailTo: '',
      form: {
        tel: '',
        whatsapp: '',
        wsGroup: '',
        tgGroup: '',
        web: '',
        name: '',
        avater: ''
      },
      formRule: {
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
      }).then(({ data }) => {
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
          }).then(({ data }) => {
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
