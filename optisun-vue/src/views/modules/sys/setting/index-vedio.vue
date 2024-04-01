<template>
    <div class="mod-config">
      <el-card>
        <div slot="header">
          首页视频
        </div>
        <el-form :model="form"
                 :rules="formRule"
                 ref="form"
                 class="cpf"
                 v-loading="loading"
                 size="small"
                 label-width="140px">
          <el-form-item label="视频"  prop="avater">
             <!-- <img-upload v-model="form.avater"></img-upload> -->
             <file-upload  v-model="form.video"></file-upload>
          </el-form-item>
          <div style="text-align: center">
            <el-button icon="el-icon-check" type="primary" @click="setConfig">保存</el-button>
          </div>
        </el-form>
      </el-card>
    </div>
  </template>
  
  <script>
  import FileUpload from "../../../../components/upload/file-upload";
  export default {
    name: "setting",
    components: {FileUpload},
    data() {
      return {
        key: 'VEDIO_SETTING',
        loading: false,
        mailTo: '',
        form: {
          video:''
        },
        formRule: {
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
  