<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        抽奖规则设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="180px">
        <el-form-item label="" prop="minLevel">
          <el-select v-model="form.minLevel" style="width: 150px">
            <el-option v-for="item in 8" :value="item" :label="'vip' + (item - 1)" :key="item"></el-option>
          </el-select>
           才可以获得抽奖机会
        </el-form-item>
        <el-form-item label="" prop="minRechargeMoney">
          <el-checkbox v-model="form.rechargeAble"></el-checkbox> 充值 <el-input-number v-model="form.minRechargeMoney" :min="0"  style="width: 150px"></el-input-number> CFA获得一次抽奖机会
        </el-form-item>
        <el-form-item label="" prop="shareAble">
          <el-checkbox v-model="form.shareAble"></el-checkbox> 邀请新人获得一次抽奖机会
        </el-form-item>
        <el-form-item label="" prop="shareRechargeFirstAble">
          <el-checkbox v-model="form.shareRechargeFirstAble"></el-checkbox> 邀请新人首充赠送一次抽奖机会
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
      key: 'luck.rule.config',
      loading: false,
      mailTo: '',
      form: {
        minLevel: 2,
        minRechargeMoney: 2000,
        rechargeAble:false,
        shareAble: false,
        shareRechargeFirstAble: false
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
