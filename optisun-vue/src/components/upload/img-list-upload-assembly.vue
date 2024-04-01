<template>
  <div>
    <el-upload ref="upload"
               action=""
               :limit="limitNum"
               :http-request="uploadFile"
               list-type="picture-card"
               :before-upload="beforeUploadHandle"
               :on-success="successHandle"
               :file-list="fileList"
               :on-remove="handleRemove"
               :on-exceed="exceedHandle">
      <i class="el-icon-plus"></i>
    </el-upload>
  </div>
</template>

<script>
  export default {
    name: "img-list-upload-assembly",
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
      limitNum:{
        type:Number,
        default:9
      },
      tips: {
        type: String,
        default: ''
      }
    },
    data() {
      return {
        loading: false,
        url: '',
        fileList: []
      }
    },
    mounted() {
      this.init()
      this.$nextTick(() => {
        this.valueToFileList(this.value)
      });
    },
    methods: {
      init() {
        this.url = this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`)
      },
      uploadFile: function (param) { // 封面图片上传
        let fileObj = param.file
        let form = new FormData()
        form.append('file', fileObj)
        let headers = {headers: {'Content-Type': 'multipart/form-data'}}
        this.$http({
          url: this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`),
          method: 'post',
          data: form,
          headers: headers
        }).then(({data}) => {
          param.onSuccess(data)
        })
      },
      valueToFileList(val){
        if(this.limitNum == 1){
          if(val && val != ''){
            let name = val.substr(val.lastIndexOf("/") + 1)
            this.fileList = [
              {name: name, url: val}
            ]
          }
        }else if(this.limitNum > 1){
          if(val && val != ''){
            let arr = JSON.parse(val)
            for (let i = 0; i < arr.length; i++) {
              let url = arr[i]
              let name =  url.substr(val.lastIndexOf("/") + 1)
              this.fileList.push({name:name,url:url})
            }
          }
        }
      },
      exceedHandle(files, fileList){
        if(this.fileList.length >= this.limitNum){
          this.$message.warning(`只能上传${this.limitNum}个文件，请删除后再上传`);
        }
      },
      // 上传之前
      beforeUploadHandle(file) {
        // if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png' && file.type !== 'image/gif') {
        //     this.$message.error('只支持jpg、png、gif格式的图片！')
        //     return false
        // }
        this.loading = true
      },
      // 上传成功
      successHandle(response, file, fileList) {
        this.loading = false
        console.log(response)
        if (response && response.code === 0) {
          let name = response.url.substr(response.url.lastIndexOf("/") + 1)
          console.log(this.fileList)
          console.log("this.fileList")
          this.fileList.push({name: name, url: response.url})
          let val = ''
          if(this.limitNum == 1) {
            val = response.url
          }
          else{
            let list = this.fileList.map(item => {
              return item.url
            });
            let array = []
            if (list && list.length > 0) {
              for (let i = 0; i < list.length; i++) {
                let arr = list[i]
                array.push(arr)
              }
              val = JSON.stringify(list)
            }
          }
          this.$emit('input', val)
          this.$emit('success', response.url)
        } else {
          this.$message.error(response.msg)
        }
      },
      handleRemove (file, fileList) {
        var index = -1
        for (var i = 0; i < this.fileList.length; i++) {
          var item = this.fileList[i]
          if (item.url == file.url) {
            index = i
            break
          }
        }
        this.fileList.splice(index, 1)
      },
      errorHandle(err, file, fileList){
        this.loading = false
        this.$message.error("文件上传失败")
      }
    },
    watch: {
      // value(newVal, oldVal) {
      //   this.valueToFileList(newVal)
      // }
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
