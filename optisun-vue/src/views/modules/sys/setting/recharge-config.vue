<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
       充值套餐设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="150px">
        <div style="display:flex;align-items: center;justify-items: center" v-for="(item,index) in form.List">
          <el-form-item :label="'金额'">
            <div style="display: flex">
              <el-input-number style="width: 200px" :min="0" v-model="item.money"></el-input-number>
            </div>
          </el-form-item>
          <i style="margin-left: 15px" v-if="form.List.length >1 && index >0 " class="el-icon-delete" @click="deleteItem(item, index)"></i>
        </div>
        <el-form-item style="margin-top: 10px;text-align: center;" >
          <el-button  type="primary" icon="el-icon-circle-plus-outline" @click="addItem"
                      size="mini">新增
          </el-button>
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
      key: 'recharge.config',
      loading: false,
      form: {
        List:[{"money":0}]
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
            this.form.List = config
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
            val: JSON.stringify(this.form.List)
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
    addItem(){
      this.form.List.push({"money":0})
    },
    //删除标签值
    deleteItem(item, index) {
      this.form.List.splice(index, 1)
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
