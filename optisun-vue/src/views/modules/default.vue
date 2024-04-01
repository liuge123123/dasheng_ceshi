<template>
  <el-card class="default-container" :style="{height: documentClientHeight + 'px'}">
    <icon-svg class="icon" name="building" ></icon-svg>
    <div class="text">该功能正在建设中，敬请期待</div>
  </el-card>
</template>

<script>
export default {
  name: "default",
  computed: {
    documentClientHeight: {
      get() {
        return this.$store.state.common.documentClientHeight - 70
      }
    }
  }
}
</script>

<style scoped lang="scss">
  .default-container{
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: center;
    .icon{
      width: 400px;
      height: 400px;
    }
    .text{
      font-size: 24px;
      color: #aaaaaa;
      padding-bottom: 150px;
    }
  }
</style>
