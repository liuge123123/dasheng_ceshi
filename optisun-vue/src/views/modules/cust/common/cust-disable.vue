<template>
  <el-dialog :visible.sync="visible" :close-on-click-modal="false" width="500px" append-to-body>
    <div slot="title">{{status == 0 ? '封禁' : '解封'}}用户：{{custId}}</div>
    <div v-if="status == 0">
      <el-radio-group v-model="type">
        <el-radio :label="1">只封禁该用户</el-radio>
        <el-radio :label="2">封禁该用户及所有下级</el-radio>
      </el-radio-group>
    </div>
    <div v-if="status == 1">
      <el-radio-group v-model="type">
        <el-radio :label="1">只解封该用户</el-radio>
        <el-radio :label="2">解封该用户及所有下级</el-radio>
      </el-radio-group>
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
      type: 1,
      status: 1
    }
  },
  components: {},
  activated() {
  },
  computed: {

  },
  methods: {
    init(custId, status) {
      this.custId = custId
      this.type = 1
      this.status = (status == 1 ? 0 : 1)
      this.visible = true
    },
    confirmClick(){
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/statusChage"),
        method: "post",
        data: this.$http.adornData({
          'custId': this.custId,
          'status': this.status,
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
