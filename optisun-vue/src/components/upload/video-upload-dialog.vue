<template>
  <div>
    <el-dialog :visible.sync="dialogVis" title="选择视频" width="800px" append-to-body>
      <div class="img-container">
<!--        <group-manager class="group-list" group-type="video" @change="groupChangeHandler"></group-manager>-->
        <video-manager ref="imgManager" class="img-list" @change="imgSelectChangeHandler"></video-manager>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import VideoManager from './manager/video-manager'
export default {
  name: "video-upload",
  components: {VideoManager},
  props: {
    value: {
      type: String,
      default: ''
    },
    tips: {
      type: String,
      default: '只支持MP4格式的视频！'
    }
  },
  data() {
    return {
      loading: false,
      imgUrl: '',
      dialogVis: false
    }
  },
  computed: {
  },
  mounted() {
    this.imgUrl = this.value
  },
  methods: {
    init(){
      this.pickFileHandle();
    },
    pickFileHandle(){
      this.dialogVis = true
      this.$nextTick(t => {
        this.$refs.imgManager.resetSelected()
      })
    },
    groupChangeHandler(group){
      this.$refs.imgManager.groupChange(group)
    },
    imgSelectChangeHandler(selectedList){
      if(selectedList.length > 0){
        this.$emit('input', selectedList[0])
        this.$emit('success', selectedList[0])
        this.dialogVis = false
      }
    }
  },
  watch: {
    value(newVal, oldVal) {
      this.imgUrl = newVal
    }
  }
}
</script>

<style scoped lang="scss">
.upload-panel {
  border: 1px solid #DCDFE6;
  border-radius: 5px;
  display: flex;
  flex-direction: column;
  justify-content: center;

  &:hover {
    border-color: #C0C4CC;
  }

  .upload-icon {
    color: #DCDFE6;
    font-size: 56px;
    margin: 0 auto;
  }

  .upload-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}

.upload-tips {
  font-size: 12px;
  color: #999;
}

.img-container {
  display: flex;
  height: 520px;
  .group-list{
    width: 200px;
    margin-right: 20px;
  }
  .img-list {
    flex: 1;
  }
}
</style>
