<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        注册设置
      </div>
      <register-reward ref="registerReward"></register-reward>
      <register-ip ref="registerIp"></register-ip>
      <register-level ref="registerLevel"></register-level>
    </el-card>
    <el-card style="margin-top: 10px">
      <div slot="header">
        首充奖励设置
      </div>
      <register-first ref="registerFirst"></register-first>
    </el-card>
    <div style="text-align: center;margin-top: 10px">
      <el-button icon="el-icon-check" type="primary" @click="save">保存</el-button>
    </div>
  </div>
</template>

<script>
import RegisterReward from "./common/register-reward";
import RegisterIp from "./common/register-ip";
import RegisterLevel from "./common/register-level";
import RegisterFirst from "./common/register-first";
export default {
  name: "setting",
  components: {
    RegisterReward,
    RegisterIp,
    RegisterLevel,
    RegisterFirst
  },
  data() {
    return {
    }
  },
  filters: {},
  computed: {
  },
  activated() {
  },
  methods: {
    save(){
        this.$refs.registerReward.setConfig();
        this.$refs.registerIp.setConfig();
        this.$refs.registerLevel.setConfig();
        this.$refs.registerFirst.setConfig();
        this.$message.success('操作成功')
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
