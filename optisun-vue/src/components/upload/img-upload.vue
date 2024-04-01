<template>
  <el-upload
    :action="url"
    :before-upload="beforeUploadHandle"
    :on-success="successHandle"
    :on-error="errorHandle"
    :show-file-list="false">
    <div class="upload-panel" :style="{width: width + 'px', height: height + 'px'}" v-loading="loading">
      <icon-svg v-if="imgUrl == ''" class="upload-icon" name="oss"></icon-svg>
      <img class="upload-img" v-if="imgUrl != ''" :src="imgUrl"/>
    </div>
    <div class="upload-tips">{{tips}}</div>
  </el-upload>
</template>

<script>
    export default {
        name: "img-upload",
        props: {
            value: {
                type: String,
                default: ''
            },
            width: {
                type: Number,
                default: 150
            },
            height: {
                type: Number,
                default: 150
            },
            tips: {
                type: String,
                default: '只支持jpg、png、gif格式的图片！'
            }
        },
        data() {
            return {
                loading: false,
                url: '',
                imgUrl: ''
            }
        },
        mounted() {
            this.imgUrl = this.value
            this.init()
        },
        methods: {
            init() {
                this.url = this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`)
            },
            // 上传之前
            beforeUploadHandle(file) {
                if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/gif') {
                    this.$message.error('只支持jpg、png、gif格式的图片！')
                    return false
                }
                this.loading = true
            },
            // 上传成功
            successHandle(response, file, fileList) {
                this.loading = false
                if (response && response.code === 0) {
                    this.imgUrl = response.url
                    this.$emit('input', response.url)
                    this.$emit('success', response.url)
                } else {
                    this.$message.error(response.msg)
                }
            },
            errorHandle(err, file, fileList){
                this.loading = false
                this.$message.error("文件上传失败")
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
      object-fit: contain;
    }
  }
  .upload-tips{
    font-size: 12px;
    color: #999;
  }
</style>
