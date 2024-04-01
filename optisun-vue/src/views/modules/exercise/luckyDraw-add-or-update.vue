<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="活动名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="活动名称"></el-input>
    </el-form-item>
      <el-form-item label="每天参与次数" prop="maxNum">
        <el-input-number controls-position="right" v-model="dataForm.maxNum" placeholder="每天参与次数，0不限制"></el-input-number>
      </el-form-item>
     <el-form-item label="开始时间" prop="startTime">
      <el-date-picker v-model="dataForm.startTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="Pick startTime"/>
    </el-form-item>
    <el-form-item label="结束时间" prop="endTime">
      <el-date-picker v-model="dataForm.endTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="Pick endTime" />
    </el-form-item>
     <el-form-item label="活动规则" prop="description">
       <rich-editor v-model="dataForm.description"></rich-editor>
    </el-form-item>
        <el-form-item style="text-align: center">
            <el-button @click="goBack">取消</el-button>
            <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
        </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import imgUpLoad from '@/components/upload/img-upload'

  export default {
    components:{
      imgUpLoad,
    },
    data () {
      return {
        loading: false,
        startTime: '',
        endTime: '',
        dataForm: {
          id: 0,
          maxNum: 0,
          name: '',
          startTime: '',
          endTime: '',
          description: ''
        },
        options:[],
        dataRule: {
          name: [
            { required: true, message: '抽奖信息名称不能为空', trigger: 'blur' }
          ],
          maxNum: [
            { required: true, message: '抽奖次数不能为空', trigger: 'blur' }
          ],
          startTime: [
            { required: true, message: '开始时间不能为空', trigger: 'blur' }
          ],
          endTime: [
            { required: true, message: '结束时间不能为空', trigger: 'blur' }
          ],
          description: [
            { required: true, message: '描述不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    mounted() {
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/exercise/luckyDraw/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.name = data.luckyDraw.name
                this.dataForm.startTime = data.luckyDraw.startTime
                this.dataForm.endTime = data.luckyDraw.endTime
                this.dataForm.description = data.luckyDraw.description
                this.dataForm.maxNum = data.luckyDraw.maxNum
              }else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/exercise/luckyDraw/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'name': this.dataForm.name,
                'startTime': this.dataForm.startTime,
                'endTime': this.dataForm.endTime,
                'description': this.dataForm.description,
                'maxNum': this.dataForm.maxNum
              })
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('back', {refresh: true})
                  }
                })
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
