<!--注册奖励-->
<template>
  <div class="mod-config">
    <div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="140px">
        <el-form-item label="注册体验金：" prop="reward">
          <el-input-number v-model="form.reward"  :min="0" label=""></el-input-number>
          <div class="form-tips"></div>
        </el-form-item>
        <el-form-item label="体验金有效期：" prop="reward">
          <el-input-number v-model="form.expire"  :min="0" label=""></el-input-number>
          <div class="form-tips"></div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "register-reward",
  data(){
    return{
      key: 'register.reward.config',
      loading: false,
      form: {
        reward: 0,
        expire: 4
      },
      formRule: {
      }
    }
  },
  mounted() {
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
              //this.$message.success('操作成功')
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
