<template>
  <el-dialog :visible.sync="visible" :close-on-click-modal="false" width="500px" append-to-body>
    <div slot="title">修改提现费率</div>
    <div>
      <el-form>
        <el-form-item label="提现手续费">
          <el-input v-model="withdrawRate" placeholder="请输入提现手续费"><el-button style="padding-right:10px" slot="suffix" type="text" >%</el-button></el-input> 
          -1 代表使用全局配置 
        </el-form-item>
      </el-form>
    </div>
    <div slot="footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button :loading="loading" type="primary" @click="confirmClick">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  data() {
    return {
      custId: '',
      loading: false,
      visible: false,
      withdrawRate: 0
    }
  },
  components: {},
  activated() {
  },
  computed: {

  },
  methods: {
    init(custId, withdrawRate) {
      this.custId = custId
      this.visible = true
      this.withdrawRate = withdrawRate
    },
    confirmClick(){
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/withdrawRateChange"),
        method: "post",
        data: this.$http.adornData({
          'custId': this.custId,
          'withdrawRate': this.withdrawRate
        })
      }).then(({ data }) => {
        this.loading = false
        if (data && data.code === 0) {
          this.visible = false
          this.$message({
            message: "操作成功",
            type: "success",
            duration: 1000,
            onClose: () => {
              this.$emit('back', {refresh: true})
            },
          });
        } else {
          this.$message.error(data.msg);
        }
      })
    }

  }
}
</script>
<style lang="scss" scoped>

</style>
