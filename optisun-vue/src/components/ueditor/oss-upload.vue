<template>
  <el-dialog
    title="上传文件"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-upload
      class="my-drag-uploader"
      drag
      :action="url"
      :before-upload="beforeUploadHandle"
      :on-success="successHandle"

      :on-preview="handlePictureCardPreview"
      :on-remove="handleRemove"

      multiple
      :file-list="fileList"
      list-type="picture-card"
      style="text-align: center;">
      <i class="el-icon-upload"></i>
<!--      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>-->
<!--      <div class="el-upload__tip" slot="tip">只支持jpg、png、gif格式的图片！</div>-->
    </el-upload>
    <div slot="footer" class="dialog-footer">
      <el-button @click="closeHandle">取 消</el-button>
      <el-button type="primary" @click="confrim">确 定</el-button>
    </div>

    <el-dialog :visible.sync="dialogVisible">
      <img width="100%" :src="dialogImageUrl" alt="">
    </el-dialog>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        url: '',
        num: 0,
        successNum: 0,
        fileList: [],
        dialogImageUrl: '',
        dialogVisible: false
      }
    },
    methods: {
      init (id) {
        this.url = this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`)
        this.visible = true
      },
      // 上传之前
      beforeUploadHandle (file) {
        if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/gif') {
          this.$message.error('只支持jpg、png、gif格式的图片！')
          return false
        }
        this.num++
      },
      // 上传成功
      successHandle (response, file, fileList) {
        this.fileList = fileList
        this.successNum++
        if (response && response.code === 0) {
          // if (this.num === this.successNum) {
          //   this.$confirm('操作成功, 是否继续操作?', '提示', {
          //     confirmButtonText: '确定',
          //     cancelButtonText: '取消',
          //     type: 'warning'
          //   }).catch(() => {
          //     this.visible = false
          //   })
          // }
        } else {
          this.$message.error(response.msg)
        }
      },
      // 弹窗关闭时
      closeHandle () {
        this.fileList = []
        this.visible = false
      },
      confrim(){
        this.$emit('refreshDataList', this.fileList)
        this.visible = false
        this.fileList = []
      },
      handleRemove(file, fileList) {
        console.log(file, fileList);
      },
      handlePictureCardPreview(file) {
        this.dialogImageUrl = file.url;
        this.dialogVisible = true;
      }
    }
  }
</script>
<style lang="scss">
  .my-drag-uploader{
    .el-upload-dragger{
      width: 100%;
      height: 100%;
      border: none;
    }
  }
</style>
