<template>
  <el-dialog :visible.sync="visible" :close-on-click-modal="false" width="500px" append-to-body>
    <div slot="title">修改会员等级 ID:{{ custId }}</div>
    <div>
      <el-form>
        <el-form-item label="会员等级">
          <el-select v-model="level" style="margin-left:5px;margin-right:5px;width:100px" size="small" clearable
            placeholder="会员等级">
            <el-option value="0" label="游客"></el-option>
            <el-option value="1" label="入门级"></el-option>
            <el-option value="2" label="初学者"></el-option>
            <el-option value="3" label="进阶级"></el-option>
            <el-option value="4" label="大亨"></el-option>
            <el-option value="5" label="伯爵"></el-option>
          </el-select>
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
      level: ""
    }
  },
  components: {},
  activated() {
  },
  computed: {

  },
  methods: {
    init(custId, level) {
      this.custId = custId
      this.visible = true
      this.level = level.toString()
    },
    confirmClick() {
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/changeVip"),
        method: "post",
        data: this.$http.adornData({
          'custId': this.custId,
          'level': this.level
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
              this.$emit('back', { refresh: true })
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
<style lang="scss" scoped></style>
