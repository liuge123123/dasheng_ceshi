<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="设备手机号码" prop="mobile">
        <el-input v-model="dataForm.mobile" placeholder="设备手机号码"></el-input>
      </el-form-item>
      <el-form-item label="钱包类型" prop="moneyType">
        <el-select v-model="dataForm.moneyType" clearable size="small" clearable placeholder="钱包类型">
          <el-option value="Airtel Money" label="Airtel Money"></el-option>
          <el-option value="Moov Money" label="Moov Money"></el-option>
          <el-option value="Zamani" label="Zamani"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="操作密码" prop="password">
        <el-input v-model="dataForm.password" placeholder="操作密码"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="isOpen">
        <el-select v-model="dataForm.isOpen" clearable size="small" clearable placeholder="状态">
          <el-option value="0" label="关闭"></el-option>
          <el-option value="1" label="开启"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item style="text-align: center">
        <el-button @click="goBack">取消</el-button>
        <el-button type="primary" @click="dataFormSubmit()" :loading="loading">确定</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      dataForm: {
        deviceId: 0,
        mobile: '',
        moneyType: '',
        password: '',
        isOpen: ''
      },
      dataRule: {
        mobile: [
          { required: true, message: '设备手机号码不能为空', trigger: 'blur' }
        ],
        moneyType: [
          { required: true, message: '钱包类型不能为空', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '操作密码不能为空', trigger: 'blur' }
        ],
        isOpen: [
          { required: true, message: '状态不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    goBack() {
      this.$emit('back', {})
    },
    init(id) {
      this.dataForm.deviceId = id || 0
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
        if (this.dataForm.deviceId) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/cust/devices/info/${this.dataForm.deviceId}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({ data }) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.mobile = data.devices.mobile
              this.dataForm.moneyType = data.devices.moneyType
              this.dataForm.password = data.devices.password
              this.dataForm.isOpen = String(data.devices.isOpen)
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
            url: this.$http.adornUrl(`/cust/devices/${!this.dataForm.deviceId ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'deviceId': this.dataForm.deviceId || undefined,
              'mobile': this.dataForm.mobile,
              'moneyType': this.dataForm.moneyType,
              'password': this.dataForm.password,
              'isOpen': this.dataForm.isOpen,
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
