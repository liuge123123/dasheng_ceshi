<template>
  <el-upload
    :action="url"
    :before-upload="beforeUploadHandle"
    :on-success="successHandle"
    :on-error="errorHandle"
    :on-exceed="exceedHandle"
    :limit="num"
    multiple
    :file-list="fileList">
    <el-button type="primary" icon="el-icon-upload" size="small">立即上传</el-button>
    <div class="upload-tips">{{tips}}</div>
  </el-upload>
</template>

<script>
    export default {
        name: "file-upload",
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
                default: ''
            },
            num: {
                type: Number,
                default: 1
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
            this.valueToFileList(this.value)
            this.init()
        },
        methods: {
            init() {
                this.url = this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`)
            },
            valueToFileList(val){
                if(this.num == 1){
                    if(val && val != ''){
                        let name = val.substr(val.lastIndexOf("/") + 1)
                        this.fileList = [
                            {name: name, url: val}
                        ]
                    }
                }else if(num > 1){
                    if(val && val != ''){
                        this.fileList = JSON.parse(val)
                    }
                }
            },
            exceedHandle(files, fileList){
                if(this.fileList.length >= this.num){
                    this.$message.warning(`只能上传${this.num}个文件，请删除后再上传`);
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
                if (response && response.code === 0) {
                    this.fileList = fileList
                    let val = JSON.stringify(this.fileList)
                    if(this.num == 1) {
                        val = response.url
                    }
                    this.$emit('input', val)
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
                this.valueToFileList(newVal)
            }
        }
    }
</script>

<style scoped lang="scss">
  .upload-tips{
    font-size: 12px;
    color: #999;
  }
</style>
