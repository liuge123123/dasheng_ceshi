<template>
  <div class="mod-config">
    <el-card>
      <div slot="header">
        首页弹屏设置
      </div>
      <el-form :model="form"
               :rules="formRule"
               ref="form"
               class="cpf"
               v-loading="loading"
               size="small"
               label-width="140px">
        <el-form-item label="开启弹屏：" prop="enable">
          <el-switch v-model="form.enable"></el-switch>
        </el-form-item>
        <el-form-item label="广告类型：" prop="type">
          <el-radio-group v-model="form.type">
            <el-radio :label="1">文本</el-radio>
            <el-radio :label="2">图片</el-radio>
            <el-radio :label="3">视频</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="form.type == 1" label="文本内容：" prop="content">
          <!-- <el-input type="textarea" v-model="form.content" placeholder="文本内容"></el-input> -->
          <rich-editor v-model="form.content"></rich-editor>
        </el-form-item>
        <el-form-item v-if="form.type == 2" label="广告图片：" prop="img">
          <img-upload v-model="form.img"></img-upload>
        </el-form-item>
        <el-form-item v-if="form.type == 3" label="广告视频：" prop="vedio">
          <file-upload  v-model="form.video"></file-upload>
        </el-form-item>
        <el-form-item label="广告链接：" prop="link">
          <el-input v-model="form.link" placeholder="请输入链接"></el-input>
        </el-form-item>
        <div style="text-align: center">
          <el-button icon="el-icon-check" type="primary" @click="setConfig">保存</el-button>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import ImgUpload from "@/components/upload/img-upload";
import FileUpload from "../../../../components/upload/file-upload";
export default {
  name: "setting",
  components: {ImgUpload,FileUpload},
  data() {
    return {
      key: 'AD_SETTING',
      loading: false,
      mailTo: '',
      form: {
        enable: false,
        type: 1,
        content: '',
        img: '',
        link: ''
      },
      formRule: {
        content: [{required: true, message: '请添加内容', tigger: 'change'}],
        img: [{required: true, message: '请上传图片', tigger: 'change'}]
      }
    }
  },
  filters: {},
  computed: {},
  activated() {
    this.getConfig()
  },
  methods: {
    getConfig() {
      this.$http({
        url: this.$http.adornUrl(`/sys/config/getVal`),
        method: 'get',
        params: this.$http.adornParams({
          key: this.key
        })
      }).then(({data}) => {
        if (data.code == 0) {
          if (data.data && data.data != '') {
            let config = JSON.parse(data.data)
            this.form = config
          }
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    setConfig() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.loading = true
          let params = {
            key: this.key,
            val: JSON.stringify(this.form)
          }
          this.$http({
            url: this.$http.adornUrl(`/sys/config/saveVal`),
            method: 'post',
            data: this.$http.adornData(params)
          }).then(({data}) => {
            this.loading = false
            if (data.code == 0) {
              this.$message.success('操作成功')
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
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
