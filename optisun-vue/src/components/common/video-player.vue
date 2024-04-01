<template>
  <div class="container">
    <video class="video" :src="src"></video>
    <div class="play" @click="previewVis = true">
      <div class="icon"></div>
    </div>
    <el-dialog width="50%" :visible.sync="previewVis" append-to-body @close="previewClose" @opened="previewOpen">
      <video ref="video" controls :src="src" style="width: 100%"></video>
    </el-dialog>
  </div>
</template>
<script>
export default {
  name: "video-player",
  props: {
    src: String
  },
  data: () => ({
    previewVis: false
  }),
  mounted() {

  },
  methods: {
    previewClose(){
      this.$refs.video.pause()
    },
    previewOpen(){
      this.$refs.video.play()
    }
  }
}
</script>
<style lang="scss" scoped>
.container {
  background: #000000;
  border-radius: 4px;
  position: relative;

  .video {
    width: 100%;
    height: 100%;
  }


  .play {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 0;
    height: 0;
    border: 15px solid rgba(0, 0, 0, 0.3);
    border-radius: 50%;

    .icon {
      position: absolute;
      left: -2px;
      top: -6px;
      height: 0;
      width: 0;
      border-top: 6px solid transparent;
      border-bottom: 6px solid transparent;
      border-left: 10px solid #fff;
      border-right: 0 solid transparent;
    }
  }
}
</style>

