<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="卡名称" prop="name">
        <el-input v-model="dataForm.name"  placeholder="卡名称"></el-input>
      </el-form-item>
      <el-form-item label="卡号" prop="cardNo">
        <el-input v-model="dataForm.cardNo" placeholder="卡号"></el-input>
      </el-form-item>

      <el-form-item label="卡图片" prop="image">
        <img-upload v-model="dataForm.image"></img-upload>
      </el-form-item>
      <el-form-item label="字母标示" prop="letter">
        <el-input v-model="dataForm.letter" placeholder="字母标示"></el-input>
      </el-form-item>
      <el-form-item label="费率" prop="fee">
        <el-input-number v-model="dataForm.fee" :min="0" label="费率"></el-input-number>
      </el-form-item>
      <el-form-item label="最小接收金额" prop="fee">
        <el-input-number v-model="dataForm.minMoney" :min="0" label="最小接收金额"></el-input-number>
      </el-form-item>
      <el-form-item label="最大接收金额" prop="fee">
        <el-input-number v-model="dataForm.maxMoney" :min="0" label="最大接收金额"></el-input-number>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
import imgUpload from "@/components/upload/img-upload";
export default {
  data() {
    return {
      loading: false,
      dataForm: {
        id: 0,
        bankCateId: '',
        name: '',
        cardNo: '',
        image: '',
        letter: '',
        fee: '',
        minMoney: 0,
        maxMoney: 0,
      },
      dataRule: {
        name: [
          { required: true, message: '卡名称不能为空', trigger: 'blur' }
        ],
        cardNo: [
          { required: true, message: '卡号不能为空', trigger: 'blur' }
        ],
        image: [
          { required: true, message: '卡图片不能为空', trigger: 'blur' }
        ],
        letter: [
          { required: true, message: '字母标示不能为空', trigger: 'blur' }
        ],
        fee: [
          { required: true, message: '费率不能为空', trigger: 'blur' }
        ]

      }
    }
  },
  components: {
    imgUpload
  },
  mounted() {
    this.getBankCateList()
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
            url: this.$http.adornUrl(`/s/splatformbank/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({ data }) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.name = data.sPlatformBank.name
              this.dataForm.cardNo = data.sPlatformBank.cardNo
              this.dataForm.image = data.sPlatformBank.image
              this.dataForm.letter = data.sPlatformBank.letter
              this.dataForm.fee = data.sPlatformBank.fee
              this.dataForm.minMoney = data.sPlatformBank.minMoney
              this.dataForm.maxMoney = data.sPlatformBank.maxMoney
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
            url: this.$http.adornUrl(`/s/splatformbank/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'bankCateId': this.dataForm.bankCateId,
              'name': this.dataForm.name,
              'cardNo': this.dataForm.cardNo,
              'image': this.dataForm.image,
              'letter': this.dataForm.letter,
              'fee': this.dataForm.fee,
              'minMoney': this.dataForm.minMoney,
              'maxMoney': this.dataForm.maxMoney,
            })
          }).then(({ data }) => {
            this.loading = false
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  this.$emit('back', { refresh: true })
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
