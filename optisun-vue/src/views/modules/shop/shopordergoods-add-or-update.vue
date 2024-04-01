<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="订单ID" prop="orderId">
      <el-input v-model="dataForm.orderId" placeholder="订单ID"></el-input>
    </el-form-item>
    <el-form-item label="产品ID" prop="goodsId">
      <el-input v-model="dataForm.goodsId" placeholder="产品ID"></el-input>
    </el-form-item>
    <el-form-item label="产品名称" prop="goodsName">
      <el-input v-model="dataForm.goodsName" placeholder="产品名称"></el-input>
    </el-form-item>
    <el-form-item label="产品数量" prop="goodsNum">
      <el-input v-model="dataForm.goodsNum" placeholder="产品数量"></el-input>
    </el-form-item>
    <el-form-item label="产品价格" prop="goodsPrice">
      <el-input v-model="dataForm.goodsPrice" placeholder="产品价格"></el-input>
    </el-form-item>
    <el-form-item label="产品图片" prop="goodsImg">
      <el-input v-model="dataForm.goodsImg" placeholder="产品图片"></el-input>
    </el-form-item>
    <el-form-item label="产品描述" prop="goodsDesc">
      <el-input v-model="dataForm.goodsDesc" placeholder="产品描述"></el-input>
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
    data () {
      return {
        loading: false,
        dataForm: {
          id: 0,
          orderId: '',
          goodsId: '',
          goodsName: '',
          goodsNum: '',
          goodsPrice: '',
          goodsImg: '',
          goodsDesc: ''
        },
        dataRule: {
          orderId: [
            { required: true, message: '订单ID不能为空', trigger: 'blur' }
          ],
          goodsId: [
            { required: true, message: '产品ID不能为空', trigger: 'blur' }
          ],
          goodsName: [
            { required: true, message: '产品名称不能为空', trigger: 'blur' }
          ],
          goodsNum: [
            { required: true, message: '产品数量不能为空', trigger: 'blur' }
          ],
          goodsPrice: [
            { required: true, message: '产品价格不能为空', trigger: 'blur' }
          ],
          goodsImg: [
            { required: true, message: '产品图片不能为空', trigger: 'blur' }
          ],
          goodsDesc: [
            { required: true, message: '产品描述不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/shop/shopordergoods/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.orderId = data.shopOrderGoods.orderId
                this.dataForm.goodsId = data.shopOrderGoods.goodsId
                this.dataForm.goodsName = data.shopOrderGoods.goodsName
                this.dataForm.goodsNum = data.shopOrderGoods.goodsNum
                this.dataForm.goodsPrice = data.shopOrderGoods.goodsPrice
                this.dataForm.goodsImg = data.shopOrderGoods.goodsImg
                this.dataForm.goodsDesc = data.shopOrderGoods.goodsDesc
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
              url: this.$http.adornUrl(`/shop/shopordergoods/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'orderId': this.dataForm.orderId,
                'goodsId': this.dataForm.goodsId,
                'goodsName': this.dataForm.goodsName,
                'goodsNum': this.dataForm.goodsNum,
                'goodsPrice': this.dataForm.goodsPrice,
                'goodsImg': this.dataForm.goodsImg,
                'goodsDesc': this.dataForm.goodsDesc
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
