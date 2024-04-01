<template>
  <el-dialog :visible.sync="dialogVisible" :title="title">
    <el-form v-loading="loading" :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()"
             label-width="80px">
      <el-form-item label="编码" prop="code">
        <el-input v-model="dataForm.code" placeholder="编码" :disabled="codeVisble"></el-input>
      </el-form-item>
      <el-form-item label="名称" prop="value">
        <el-input v-model="dataForm.value" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="排序" prop="sort">
        <el-input-number v-model="dataForm.sort" size="medium" controls-position="right" :min="1" :max="998"
                         style="width: 100%"></el-input-number>
      </el-form-item>
      <el-form-item label="备注" prop="comments">
        <el-input v-model="dataForm.comments" placeholder="备注"></el-input>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<script>

  export default {
    data() {
      return {
        dialogVisible: false,
        loading: false,
        dataForm: {
          id: 0,
          code: '',
          value: '',
          parentCode: '',
          parentName: '',
          comments: '',
          status: '',
          createUserId: '',
          createTime: '',
          sort: '1',
          dicType: 10,
          orgId: '',
          isEdit:1
        },
        dataRule: {
          code: [
            {required: true, message: '编码不能为空', trigger: 'blur'}
          ],
          value: [
            {required: true, message: '值不能为空', trigger: 'blur'}
          ]
        },
        codeVisble:false
      }
    },
    computed: {
      title(){
        let title = ''
        if(this.dataForm.id == 0){
          title = '新增'
        }else{
          title = '修改'
        }
        if(this.dataForm.parentCode == '-1'){
          title = title + '数据字典'
        }else{
          title = title + '字典选项值'
        }
        return title
      }
    },
    created() {
    },
    methods: {
      init(id, parentCode) {
        this.dialogVisible = true
        this.dataForm.id = id || 0
        this.dataForm.parentCode = parentCode
        this.$nextTick(() => {
          this.dataForm.parentName = ''
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.loading = true
            // 修改
            this.$http({
              url: this.$http.adornUrl(`/sys/dictionary/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.code = data.dictionary.code
                this.dataForm.value = data.dictionary.value
                this.dataForm.parentCode = data.dictionary.parentCode
                this.dataForm.comments = data.dictionary.comments
                this.dataForm.status = data.dictionary.status
                this.dataForm.createUserId = data.dictionary.createUserId
                this.dataForm.createTime = data.dictionary.createTime
                this.dataForm.sort = data.dictionary.sort
                this.dataForm.orgId = data.dictionary.orgId
                this.dataForm.dicType = data.dictionary.dicType
                this.dataForm.isEdit = data.dictionary.isEdit
                console.log(this.dataForm.isEdit)
                if(this.dataForm.isEdit==0){
                  this.codeVisble = true
                }
                this.codeListTreeSetCurrentNode()
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit() {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/sys/dictionary/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'code': this.dataForm.code,
                'value': this.dataForm.value,
                'parentCode': this.dataForm.parentCode,
                'comments': this.dataForm.comments,
                'status': this.dataForm.status,
                'createUserId': this.dataForm.createUserId,
                'createTime': this.dataForm.createTime,
                'sort': this.dataForm.sort,
                'dicType': this.dataForm.dicType,
                'orgId': this.dataForm.orgId,
                'isEdit':this.dataForm.isEdit
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dialogVisible = false
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.$emit('back', {refresh: true, type: this.dataForm.parentCode == '-1' ? 1: 2})
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      goBack() {
        this.dialogVisible = false
        this.$emit('back', {
          type: this.dataForm.parentCode == '-1' ? 1: 2
        })
      }
    }
  }
</script>
