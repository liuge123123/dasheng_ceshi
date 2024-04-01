<template>
  <el-dialog :visible.sync="visible" :close-on-click-modal="false" width="500px" append-to-body>
    <div slot="title">修改手机号</div>
    <div>
      <el-form>
        <el-form-item label="手机号码">
          <el-input v-model="mobile" placeholder="请输入手机号码"></el-input>
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
      mobile: ''
    }
  },
  components: {},
  activated() {
  },
  computed: {

  },
  methods: {
    init(custId, mobile) {
      this.custId = custId
      this.visible = true
      this.mobile = mobile
    },
    confirmClick(){
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/changeMobile"),
        method: "post",
        data: this.$http.adornData({
          'custId': this.custId,
          'mobile': this.mobile
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
