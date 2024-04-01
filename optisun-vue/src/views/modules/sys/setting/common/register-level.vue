<!--注册用户初始等级-->
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
        <el-form-item label="注册初始等级" prop="value">
          <el-select v-model="form.value" placeholder="请选择">
            <el-option
              v-for="item in gradeList"
              :key="item.id"
              :label="item.gradeName"
              :value="item.id">
            </el-option>
          </el-select>
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
      key: 'register.level.config',
      loading: false,
      form: {
        value: 0
      },
      formRule: {
        value:  { required: true, message: '请选择等级', trigger: 'blur' }
      },
      gradeList:[]
    }
  },
  mounted() {
    this.getLevel()
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
            this.form.value = config
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
            val: JSON.stringify(this.form.value)
          }
          this.$http({
            url: this.$http.adornUrl(`/sys/config/saveVal`),
            method: 'post',
            data: this.$http.adornData(params)
          }).then(({data}) => {
            this.loading = false
            if (data.code == 0) {
             // this.$message.success('操作成功')
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    //获取等级
    getLevel(){
      this.$http({
        url: this.$http.adornUrl('/t/tgoodsgrade/getListAll'),
        method: 'get',
        params: this.$http.adornParams({
        })
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.gradeList = data.data
          this.getConfig()
        } else {
          this.gradeList = []
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
