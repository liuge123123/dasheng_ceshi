<!--首充奖励-->
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
           <div style="display:flex;align-items: center;justify-items: center" v-for="(item,index) in form.firstList">
                 <el-form-item :label="'金额区间'+(index+1)">
                    <div style="display: flex">
                      <el-input-number style="width: 200px" :min="0" v-model="item.start"></el-input-number>
                      <div style="margin-left: 10px;margin-right: 10px">至</div>
                      <el-input-number  style="width: 200px" :min="0" v-model="item.end"></el-input-number>
                    </div>
                 </el-form-item>
                <el-form-item label="奖励金额">
                  <el-input  v-model="item.value"></el-input>
                </el-form-item>
                <i style="margin-left: 15px" class="el-icon-delete" @click="deleteItem(item, index)"></i>
           </div>
        <el-form-item style="margin-top: 10px;text-align: center;" >
          <el-button  type="primary" icon="el-icon-circle-plus-outline" @click="addItem"
                     size="mini">新增
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>
export default {
  name: "register-first",
  data(){
    return{
      key: 'first.recharge.reward.config',
      loading: false,
      form: {
        firstList:[
          {"start":0,"end":1000,"value":150}
        ]
      },
      formRule: {
        value:  { required: true, message: '请选择等级', trigger: 'blur' }
      },
      gradeList:[],
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
             // this.$message.success('操作成功')
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    addItem(){
      this.form.firstList.push(
        {"start":0,"end":0,"value":0}
      )
    },
    //删除标签值
    deleteItem(item, index) {
      this.form.firstList.splice(index, 1)
    },
  }
}
</script>

<style scoped>

</style>
