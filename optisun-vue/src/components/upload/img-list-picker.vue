<template>
  <el-dialog :visible.sync="dialogVis" title="选择图片" width="800px" append-to-body modal-append-to-body>
    <div class="img-container">
      <image-manager ref="imgManager" class="img-list"></image-manager>
    </div>
    <div slot="footer">
      <el-button @click="dialogVis = false">取消</el-button>
      <el-button type="primary" @click="imgSelectChangeHandler">确定</el-button>
    </div>
  </el-dialog>
</template>

<script>
import ImageManager from './manager/image-manager'

export default {
  name: "img-list-picker",
  components: {ImageManager},
  props: {},
  data() {
    return {
      dialogVis: false,
      selectList: []
    }
  },
  computed: {},
  mounted() {
  },
  methods: {
    pickFileHandle() {
      this.dialogVis = true
      this.$nextTick(t => {
        this.$refs.imgManager.resetSelected()
      })
    },
    groupChangeHandler(group) {
      this.$refs.imgManager.groupChange(group)
    },
    imgSelectChangeHandler() {
      let selected = this.$refs.imgManager.getSelected()
      this.$emit("selected", selected)
      this.dialogVis = false
    }
  },
  watch: {}
}
</script>

<style scoped lang="scss">
.img-container {
  display: flex;
  height: 520px;

  .group-list {
    width: 200px;
    margin-right: 20px;
  }

  .img-list {
    flex: 1;
  }
}
</style>
