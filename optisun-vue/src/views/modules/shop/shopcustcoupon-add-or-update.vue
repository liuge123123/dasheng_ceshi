<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="客户ID" prop="custId">
        <el-input v-model="dataForm.custId" placeholder="客户ID" :disabled="dataForm.id"></el-input>
      </el-form-item>
      <el-form-item label="折扣" prop="discount">
        <el-input-number :min="0" :max="10" controls-position="right" v-model="dataForm.discount" placeholder="折扣"></el-input-number>折
        <div class="form-tips">折扣为0-10之间的数字，最终价格为：订单总价*折扣/10</div>
      </el-form-item>
      <el-form-item label="有效期" prop="expireTime">
        <el-date-picker type="datetime" value-format="timestamp" v-model="dataForm.expireTime" placeholder="有效期"></el-date-picker>
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
        id: 0,
        custId: '',
        discount: '',
        expireTime: '',
        useStatus: '',
        salesmanId: '',
        isNb: '',
        createTime: ''
      },
      dataRule: {
        custId: [
          {required: true, message: '客户ID不能为空', trigger: 'blur'}
        ],
        discount: [
          {required: true, message: '折扣不能为空', trigger: 'blur'}
        ],
        expireTime: [
          {required: true, message: '过期时间不能为空', trigger: 'blur'}
        ],
        useStatus: [
          {required: true, message: '使用状态不能为空', trigger: 'blur'}
        ],
        salesmanId: [
          {required: true, message: '业务员ID不能为空', trigger: 'blur'}
        ],
        isNb: [
          {required: true, message: '是否内部不能为空', trigger: 'blur'}
        ],
        createTime: [
          {required: true, message: '创建时间不能为空', trigger: 'blur'}
        ]
      }
    }
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
            url: this.$http.adornUrl(`/shop/shopcustcoupon/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.custId = data.shopCustCoupon.custId
              this.dataForm.discount = data.shopCustCoupon.discount
              this.dataForm.expireTime = data.shopCustCoupon.expireTime * 1000
              this.dataForm.useStatus = data.shopCustCoupon.useStatus
              this.dataForm.salesmanId = data.shopCustCoupon.salesmanId
              this.dataForm.isNb = data.shopCustCoupon.isNb
              this.dataForm.createTime = data.shopCustCoupon.createTime
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
            url: this.$http.adornUrl(`/shop/shopcustcoupon/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'custId': this.dataForm.custId,
              'discount': this.dataForm.discount,
              'expireTime': this.dataForm.expireTime/1000,
              'useStatus': this.dataForm.useStatus,
              'salesmanId': this.dataForm.salesmanId,
              'isNb': this.dataForm.isNb,
              'createTime': this.dataForm.createTime
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
