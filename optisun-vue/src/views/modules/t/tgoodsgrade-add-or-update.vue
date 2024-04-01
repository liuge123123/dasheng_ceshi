<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="等级名称" prop="gradeName">
      <el-input v-model="dataForm.gradeName" placeholder="等级名称"></el-input>
    </el-form-item>
    <!-- <el-form-item label="起始金额" prop="money">
      <el-input-number v-model="dataForm.money" style="width: 30%" :step="0.1"></el-input-number>
    </el-form-item>
    <el-form-item label="结束金额" prop="parameter1">
      <el-input-number v-model="dataForm.parameter1" style="width: 30%" :step="0.1"></el-input-number>
    </el-form-item>
      <el-form-item label="任务单数要求" prop="gmNums">
        <el-input-number v-model="dataForm.gmNums" style="width: 30%" :min="0" :precision="0"></el-input-number>
      </el-form-item>
      <el-form-item label="佣金比例%">
        <el-input-number v-model="dataForm.rate"  :min="0" label=""></el-input-number>
      </el-form-item> -->
      <el-form-item label="会员级别" prop="gradeLevel">
        <el-input-number v-model="dataForm.gradeLevel" style="width: 30%" :min="0" :precision="0"></el-input-number>
      </el-form-item>
    <el-form-item label="等级说明" prop="content">
      <el-input v-model="dataForm.content"
                type="textarea"
                :rows="4"
                maxlength="125"
                show-word-limit
                placeholder="等级说明"></el-input>
    </el-form-item>
    <el-form-item label="图片" prop="image">
      <img-up-load v-model="dataForm.image"></img-up-load>
    </el-form-item>
    <el-form-item label="等级banner" prop="vipBanner">
      <img-up-load v-model="dataForm.vipBanner"></img-up-load>
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
      imgUpLoad
    },
    data () {
      return {
        loading: false,
        dataForm: {
          id: 0,
          gradeName: '',
          money: '',
          parameter1: '',
          content:'',
          image: '',
          vipBanner:'',
          gmNums:0,
          gradeLevel:0,
          rate:0
        },
        dataRule: {
          gradeName: [
            { required: true, message: '等级名称不能为空', trigger: 'blur' }
          ],
          // money: [
          //   { required: true, message: '金额不能为空', trigger: 'blur' }
          // ],
          // parameter1: [
          //   { required: true, message: '不能为空', trigger: 'blur' }
          // ],
          // gmNums: [
          //   { required: true, message: '购买次数不能为空', trigger: 'blur' }
          // ],
          gradeLevel: [
            { required: true, message: '会员级别不能为空', trigger: 'blur' }
          ]
        }
      }
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
              url: this.$http.adornUrl(`/t/tgoodsgrade/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.gradeName = data.tGoodsGrade.gradeName
                this.dataForm.money = data.tGoodsGrade.money
                this.dataForm.parameter1 = data.tGoodsGrade.parameter1
                this.dataForm.content = data.tGoodsGrade.content
                this.dataForm.image = data.tGoodsGrade.image
                this.dataForm.gmNums =data.tGoodsGrade.gmNums
                this.dataForm.gradeLevel = data.tGoodsGrade.gradeLevel
                this.dataForm.rate = data.tGoodsGrade.rate
                this.dataForm.vipBanner = data.tGoodsGrade.vipBanner
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
              url: this.$http.adornUrl(`/t/tgoodsgrade/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'gradeName': this.dataForm.gradeName,
                'money': this.dataForm.money,
                'parameter1': this.dataForm.parameter1,
                'content': this.dataForm.content,
                'image': this.dataForm.image,
                'gmNums': this.dataForm.gmNums,
                'gradeLevel':this.dataForm.gradeLevel,
                'rate':this.dataForm.rate,
                'vipBanner':this.dataForm.vipBanner
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
