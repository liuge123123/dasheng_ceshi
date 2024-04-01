<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        签到设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="140px">
        <el-form-item label="首充奖励：" prop="firstAward">
          <el-input v-model="form.firstAward"></el-input>
          <div class="form-tips">首次充值奖励积分</div>
        </el-form-item>
        <el-form-item label="下级充值奖励：" prop="parentAward">
          <el-input v-model="form.parentAward"></el-input>
          <div class="form-tips">下级充值奖励积分</div>
        </el-form-item>
        <el-form-item label="每累计签到" prop="usualAward">
          <el-input v-model="form.usualDays" style="width: 200px">
            <span slot="suffix">天</span>
          </el-input>
          奖励
          <el-input v-model="form.usualAward" style="width: 200px">
            <span slot="suffix">积分</span>
          </el-input>
        </el-form-item>
        <el-form-item v-for="(domain, index) in form.domains" label="每累计签到" :key="domain.key"
                      :prop="'domains.' + index + '.value'">
          <el-input v-model="domain.days" placeholder="天数" style="width: 200px" clearable>
            <span slot="suffix">天</span>
          </el-input>
          奖励
          <el-input v-model="domain.num" placeholder="奖励金额" style="width: 200px" clearable>
            <span slot="suffix">CFA</span>
          </el-input>
          <el-button @click.prevent="removeDomain(domain)">删除</el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="addDomain">新增条件</el-button>
          <el-button @click="resetForm('form')">重置</el-button>
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
      key: 'SIGN_IN_SETTING',
      loading: false,
      mailTo: '',
      form: {
        firstAward: 0,
        parentAward: 0,
        usualDays: 0,
        usualAward: "",
        domains: []
      },
      formRule: {},
      values: [], //匹配内容
      ranges: [], //匹配
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
      var _this = this
      console.log(_this.form, '测试111')
      _this.$refs["form"].validate(valid => {
        if (valid) {
          console.log(_this.form, '测试222111')
          _this.loading = true
          let params = {
            key: this.key,
            val: JSON.stringify(_this.form)
          }
          _this.$http({
            url: _this.$http.adornUrl(`/sys/config/saveVal`),
            method: 'post',
            data: _this.$http.adornData(params)
          }).then(({data}) => {
            _this.loading = false
            if (data.code == 0) {
              _this.$message.success('操作成功')
            } else {
              _this.$message.error(data.msg)
            }
          })
        }
      })
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
    removeDomain(item) {
      var index = this.form.domains.indexOf(item);
      if (index !== -1) {
        this.form.domains.splice(index, 1);
      }
    },
    addDomain() {
      debugger
      this.form.domains.push({});
    },
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
