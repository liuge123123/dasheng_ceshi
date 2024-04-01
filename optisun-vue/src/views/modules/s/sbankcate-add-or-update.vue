<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="前端显示名称" prop="showName">
        <el-input v-model="dataForm.showName" placeholder="前端显示名称"></el-input>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="字母标示" prop="letter">
        <el-input v-model="dataForm.letter" placeholder="字母标示"></el-input>
      </el-form-item>
      <el-form-item label="汇率" prop="fee">
        <el-input-number controls-position="right" v-model="dataForm.fee" :min="0" label="汇率"></el-input-number>
      </el-form-item>
      <el-form-item label="排序" prop="letter">
        <el-input v-model="dataForm.sort" placeholder="排序"></el-input>
      </el-form-item>
      <el-form-item label="开启充值" prop="rechargeOpen">
        <el-radio-group v-model="dataForm.rechargeOpen">
          <el-radio-button :label="1">是</el-radio-button>
          <el-radio-button :label="0">否</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="开启提现" prop="cashoutOpen">
        <el-radio-group v-model="dataForm.cashoutOpen">
          <el-radio-button :label="1">是</el-radio-button>
          <el-radio-button :label="0">否</el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="卡类型图标" prop="image">
        <img-upload v-model="dataForm.logo"></img-upload>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import imgUpload from "../../../components/upload/img-upload";

export default {
  data() {
    return {
      loading: false,
      dataForm: {
        id: 0,
        showName:'',
        name: '',
        letter: '',
        fee: 1,
        createBy: '',
        updateBy: '',
        delFlag: '',
        orgId: '',
        updateTime: '',
        createTime: '',
        rechargeOpen: 1,
        cashoutOpen: 1,
        sort:0
      },
      dataRule: {
        showName: [
          {required: true, message: '前端名称不能为空', trigger: 'blur'}
        ],
        name: [
          {required: true, message: '名称不能为空', trigger: 'blur'}
        ],
        letter: [
          {required: true, message: '字母标示不能为空', trigger: 'blur'}
        ],
        fee: [
          {required: true, message: '费率不能为空', trigger: 'blur'}
        ]
      }
    }
  },
  components: {
    imgUpload
  },
  methods: {
    goBack() {
      this.$emit('back', {})
    },
    init(id) {
      this.dataForm.id = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.id) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/s/sbankcate/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.name = data.sBankCate.name
              this.dataForm.letter = data.sBankCate.letter
              this.dataForm.fee = data.sBankCate.fee
              this.dataForm.logo = data.sBankCate.logo
              this.dataForm.rechargeOpen = data.sBankCate.rechargeOpen
              this.dataForm.cashoutOpen = data.sBankCate.cashoutOpen
              this.dataForm.showName = data.sBankCate.showName
              this.dataForm.sort = data.sBankCate.sort
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/s/sbankcate/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'name': this.dataForm.name,
              'letter': this.dataForm.letter,
              'fee': this.dataForm.fee,
              'logo': this.dataForm.logo,
              'rechargeOpen': this.dataForm.rechargeOpen,
              'cashoutOpen': this.dataForm.cashoutOpen,
              'showName':this.dataForm.showName,
              'sort':this.dataForm.sort
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
