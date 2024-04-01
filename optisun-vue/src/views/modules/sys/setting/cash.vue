<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        提现设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="180px">
        <el-form-item label="最低提现：" prop="minCash">
          <el-input-number v-model="form.minCash" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="提现次数：" prop="cashCount">
          <el-input-number v-model="form.cashCount" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="提现手续费比例%：" prop="rate">
          <el-input-number v-model="form.rate" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="周六、周日是否允许提现" prop="rate">
            <el-checkbox v-model="form.ifCash"></el-checkbox>
        </el-form-item>
        <el-form-item label="非充值用户不能提款" prop="rate">
            <el-checkbox v-model="form.ifRecharge"></el-checkbox>
        </el-form-item>
        <el-form-item label="提现时间" prop="rate">
          <el-time-picker
            is-range
            v-model="form.timeRang"
            value-format="HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            placeholder="选择时间范围">
          </el-time-picker>
        </el-form-item>

<!--        <el-form-item label="VIP0最低提款金额" prop="v0MinCash">-->
<!--          <el-input-number v-model="form.v0MinCash" :min="0"></el-input-number>-->
<!--        </el-form-item>-->

<!--        <el-form-item label="VIP0可提款次数" prop="v0Count">-->
<!--          <el-input-number v-model="form.v0Count" :min="0"></el-input-number>-->
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
      key: 'CASH_SETTING',
      loading: false,
      mailTo: '',
      form: {
        minCash: 0,
        rate:0.1,
        ifCash:false,
        v0MinCash: 0,
        v0Count: 0,
        cashCount:1,
        timeRang:[]
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
