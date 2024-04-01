<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="商品名称" prop="productName">
      <el-input v-model="dataForm.productName" placeholder="商品名称"></el-input>
    </el-form-item>
    <el-form-item label="产品图片" prop="imgUrl">
      <el-input v-model="dataForm.imgUrl" placeholder="产品图片"></el-input>
    </el-form-item>
    <el-form-item label="产品价格" prop="price">
      <el-input v-model="dataForm.price" placeholder="产品价格"></el-input>
    </el-form-item>
    <el-form-item label="商品描述" prop="detail">
      <el-input v-model="dataForm.detail" placeholder="商品描述"></el-input>
    </el-form-item>
    <el-form-item label="店面名称" prop="shopName">
      <el-input v-model="dataForm.shopName" placeholder="店面名称"></el-input>
    </el-form-item>
    <el-form-item label="链接地址" prop="productUrl">
      <el-input v-model="dataForm.productUrl" placeholder="链接地址"></el-input>
    </el-form-item>
    <el-form-item label="建立时间" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder="建立时间"></el-input>
    </el-form-item>
    <el-form-item label="建立人" prop="createBy">
      <el-input v-model="dataForm.createBy" placeholder="建立人"></el-input>
    </el-form-item>
    <el-form-item label="修改时间" prop="updateTime">
      <el-input v-model="dataForm.updateTime" placeholder="修改时间"></el-input>
    </el-form-item>
    <el-form-item label="修改人" prop="updateBy">
      <el-input v-model="dataForm.updateBy" placeholder="修改人"></el-input>
    </el-form-item>
    <el-form-item label="删除标识(0-正常；1-已删除)" prop="delFlag">
      <el-input v-model="dataForm.delFlag" placeholder="删除标识(0-正常；1-已删除)"></el-input>
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
          productId: 0,
          productName: '',
          imgUrl: '',
          price: '',
          detail: '',
          shopName: '',
          productUrl: '',
          createTime: '',
          createBy: '',
          updateTime: '',
          updateBy: '',
          delFlag: ''
        },
        dataRule: {
          productName: [
            { required: true, message: '商品名称不能为空', trigger: 'blur' }
          ],
          imgUrl: [
            { required: true, message: '产品图片不能为空', trigger: 'blur' }
          ],
          price: [
            { required: true, message: '产品价格不能为空', trigger: 'blur' }
          ],
          detail: [
            { required: true, message: '商品描述不能为空', trigger: 'blur' }
          ],
          shopName: [
            { required: true, message: '店面名称不能为空', trigger: 'blur' }
          ],
          productUrl: [
            { required: true, message: '链接地址不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '建立时间不能为空', trigger: 'blur' }
          ],
          createBy: [
            { required: true, message: '建立人不能为空', trigger: 'blur' }
          ],
          updateTime: [
            { required: true, message: '修改时间不能为空', trigger: 'blur' }
          ],
          updateBy: [
            { required: true, message: '修改人不能为空', trigger: 'blur' }
          ],
          delFlag: [
            { required: true, message: '删除标识(0-正常；1-已删除)不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.productId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.productId) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/basic/product/info/${this.dataForm.productId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.productName = data.product.productName
                this.dataForm.imgUrl = data.product.imgUrl
                this.dataForm.price = data.product.price
                this.dataForm.detail = data.product.detail
                this.dataForm.shopName = data.product.shopName
                this.dataForm.productUrl = data.product.productUrl
                this.dataForm.createTime = data.product.createTime
                this.dataForm.createBy = data.product.createBy
                this.dataForm.updateTime = data.product.updateTime
                this.dataForm.updateBy = data.product.updateBy
                this.dataForm.delFlag = data.product.delFlag
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
              url: this.$http.adornUrl(`/basic/product/${!this.dataForm.productId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'productId': this.dataForm.productId || undefined,
                'productName': this.dataForm.productName,
                'imgUrl': this.dataForm.imgUrl,
                'price': this.dataForm.price,
                'detail': this.dataForm.detail,
                'shopName': this.dataForm.shopName,
                'productUrl': this.dataForm.productUrl,
                'createTime': this.dataForm.createTime,
                'createBy': this.dataForm.createBy,
                'updateTime': this.dataForm.updateTime,
                'updateBy': this.dataForm.updateBy,
                'delFlag': this.dataForm.delFlag
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
