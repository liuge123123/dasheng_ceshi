<template>
  <div class="material-container">
    <div class="popover-container">
      <el-popover
        trigger="hover"
        placement="bottom">

        <div class="img-preview" style="margin-top: 5px" v-if="value.type == '1'">
          <el-image ref="img"
                    class="img"
                    fit="cover"
                    :src="showData.url"
                    :preview-src-list="[showData.url]"></el-image>
        </div>
        <div class="video-preview" v-if="value.type == '2'">
          <audio-full class="video" :theUrl="showData.url"></audio-full>
        </div>
        <div class="video-preview" v-if="value.type == '3'">
          <video class="video" controls :src="showData.url"></video>
        </div>
        <div class="video-preview" v-if="value.type == '4'">
          <a :href="value.url">点击下载</a>
        </div>
        <span slot="reference" class="type">[{{showData.typeText}}]</span>
      </el-popover>
    </div>
    <div class="show-text">
      {{showData.textContent}}
    </div>
  </div>
</template>

<script>
  export default {
    name: "material-show",
    props: {
      value: Object
    },
    computed: {
      showData() {
        if (this.value) {
          let typeItems = {
            1: '图片',
            2: '语音',
            3: '视频',
            4: '文件'
          }
          return {
            typeText: typeItems[this.value.type] || '文件',
            textContent: this.value.name,
            url: this.value.url
          }
        }
        return {}
      }
    },
    methods: {
      imgPreview() {
        this.$refs.img.clickHandler()
      }
    }
  }
</script>

<style scoped lang="scss">
  .material-container {
    display: flex;
    flex-direction: row;


    .show-text {
      white-space: nowrap;
      text-overflow: ellipsis;
      overflow: hidden;
      word-break: break-all;
      flex: 1;
    }
  }

  .popover-container {
    .type {
      color: $--color-primary;
      cursor: pointer;
    }
  }
  .text-preview {
    max-width: 500px;
  }

  .img-preview {
    margin-top: 5px;
    display: flex;

    .img {
      width: 80px;
      height: 80px;
      border: 1px solid #eeeeee;
      border-radius: 3px;
    }

    .img-count {
      width: 40px;
      height: 40px;
      border: 1px solid #eeeeee;
      border-radius: 3px;
      margin-left: 5px;
      line-height: 40px;
      text-align: center;
      color: #999999;
      background: #fbfdff;
      position: relative;
    }
  }


  .link-preview {
    display: flex;
    border: 1px solid #d9d9d9;
    border-radius: 6px;
    padding: 5px;
    position: relative;
    margin-top: 5px;
    width: 300px;

    .thumb {
      width: 60px;
      height: 60px;
      border: 1px solid #d9d9d9;
      border-radius: 6px;
      cursor: pointer;
      object-fit: cover;
    }

    .info {
      display: flex;
      flex-direction: column;
      margin-left: 5px;
      line-height: normal;
      text-align: justify;
      height: 60px;
      overflow: hidden;

      .title {
        white-space: nowrap;
        text-overflow: ellipsis;
        overflow: hidden;
        word-break: break-all;
      }

      .desc {
        font-size: 12px;
        color: #999999;
        margin-top: 5px;
        flex: 1;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }

  }

  .video-preview {
    .video {
      width: 400px;
    }
  }
</style>
