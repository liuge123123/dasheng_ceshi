<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        三级分销设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="140px">
        <el-form-item label="是否开启分销：" prop="open">
          <el-switch
            v-model="form.open"
            active-color="#13ce66"
            inactive-color="#ff4949">
          </el-switch>
          <div class="form-tips"></div>
        </el-form-item>
        <el-form-item label="VIP0是否给上级返佣">
          <el-switch
            v-model="form.vip0IsReturn"
            active-color="#13ce66"
            inactive-color="#ff4949">
          </el-switch>
        </el-form-item>
        <el-form-item label="一级分销比例%">
           <el-input-number v-model="form.first"  :min="0"  label=""></el-input-number>
        </el-form-item>
        <el-form-item label="二级分销比例%">
          <el-input-number v-model="form.second"  :min="0" label=""></el-input-number>
        </el-form-item>
        <el-form-item label="三级分销比例%">
          <el-input-number v-model="form.third"  :min="0"  label=""></el-input-number>
        </el-form-item>
        <el-form-item label="四级分销比例%">
          <el-input-number v-model="form.four"  :min="0"  label=""></el-input-number>
        </el-form-item>
        <el-form-item label="五级分销比例%">
          <el-input-number v-model="form.five"  :min="0"  label=""></el-input-number>
        </el-form-item>
        <el-form-item label="六级分销比例%">
          <el-input-number v-model="form.six"  :min="0"  label=""></el-input-number>
        </el-form-item>
        <el-form-item label="七级分销比例%">
          <el-input-number v-model="form.seven"  :min="0"  label=""></el-input-number>
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
  name: "distribution",
  data() {
    return {
      key: 'rebate.config',
      loading: false,
      form: {
        open: 1,
        first:0,
        second:0,
        third:0,
        four:0,
        five:0,
        six:0,
        seven:0,
        vip0IsReturn:false
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
