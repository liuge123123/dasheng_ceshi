<template>
  <el-dialog :visible.sync="visible" :close-on-click-modal="false" width="500px" append-to-body>
    <div slot="title">赠送抽奖次数</div>
    <div>
      <el-form>
        <el-form-item label="类型">
          <el-radio-group v-model="type">
            <el-radio-button :label="1">赠送</el-radio-button>
            <el-radio-button :label="-1">扣减</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="次数">
          <el-input-number :min="1" :step="1" precision="0" v-model="num"></el-input-number>
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
      num: 1,
      type: 1
    }
  },
  components: {},
  activated() {
  },
  computed: {

  },
  methods: {
    init(custId) {
      this.custId = custId
      this.visible = true
    },
    confirmClick(){
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/sendLuckNum"),
        method: "post",
        data: this.$http.adornData({
          'custId': this.custId,
          'num': this.num,
          'type': this.type
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
