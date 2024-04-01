<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="名称" prop="name">
        <el-input v-model="dataForm.name" placeholder="名称"></el-input>
      </el-form-item>
      <el-form-item label="图片" prop="img">
        <img-upload v-model="dataForm.img"></img-upload>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number controls-position="right" v-model="dataForm.price" placeholder="价格"></el-input-number>
      </el-form-item>
      <el-form-item label="大分类" prop="category1">
        <el-input v-model="dataForm.category1" placeholder="大分类"></el-input>
      </el-form-item>
      <el-form-item label="小分类" prop="category2">
        <el-input v-model="dataForm.category2" placeholder="小分类"></el-input>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="dataForm.status" placeholder="状态">
          <el-option :value="0" label="下架"></el-option>
          <el-option :value="1" label="上架"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="外部链接" prop="outLink">
        <el-input v-model="dataForm.outLink" placeholder="外部链接"></el-input>
      </el-form-item>
      <el-form-item label="排序" prop="sortNum">
        <el-input v-model="dataForm.sortNum" placeholder="排序，越大越前"></el-input>
      </el-form-item>
      <el-form-item label="类型" prop="type">
        <el-select v-model="dataForm.type" placeholder="类型">
          <el-option :value="1" label="现售"></el-option>
          <el-option :value="2" label="预售"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="描述" prop="description">
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
import FileUpload from "../../../components/upload/file-upload";

export default {
  components: {FileUpload},
  data() {
    return {
      loading: false,
      dataForm: {
        id: 0,
        name: '',
        img: '',
        price: '',
        category1: '',
        category2: '',
        specs: '',
        description: '',
        status: '',
        outLink: '',
        sortNum: '',
        type: ''
      },
      dataRule: {
        name: [
          {required: true, message: '名称不能为空', trigger: 'blur'}
        ],
        img: [
          {required: true, message: '图片不能为空', trigger: 'blur'}
        ],
        price: [
          {required: true, message: '价格不能为空', trigger: 'blur'}
        ],
        category1: [
          {required: true, message: '大分类不能为空', trigger: 'blur'}
        ],
        category2: [
          {required: true, message: '小分类不能为空', trigger: 'blur'}
        ],
        specs: [
          {required: true, message: '规则不能为空', trigger: 'blur'}
        ],
        description: [
          {required: true, message: '描述不能为空', trigger: 'blur'}
        ],
        status: [
          {required: true, message: '状态不能为空', trigger: 'blur'}
        ],
        outLink: [
          {required: true, message: '外部链接不能为空', trigger: 'blur'}
        ],
        sortNum: [
          {required: true, message: '排序，越大越前不能为空', trigger: 'blur'}
        ],
        type: [
          {required: true, message: '类型，1：现售，2：预售不能为空', trigger: 'blur'}
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
            url: this.$http.adornUrl(`/shop/shopgoods/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.name = data.shopGoods.name
              this.dataForm.img = data.shopGoods.img
              this.dataForm.price = data.shopGoods.price
              this.dataForm.category1 = data.shopGoods.category1
              this.dataForm.category2 = data.shopGoods.category2
              this.dataForm.specs = data.shopGoods.specs
              this.dataForm.description = data.shopGoods.description
              this.dataForm.status = data.shopGoods.status
              this.dataForm.outLink = data.shopGoods.outLink
              this.dataForm.sortNum = data.shopGoods.sortNum
              this.dataForm.type = data.shopGoods.type
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
            url: this.$http.adornUrl(`/shop/shopgoods/${!this.dataForm.id ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'id': this.dataForm.id || undefined,
              'name': this.dataForm.name,
              'img': this.dataForm.img,
              'price': this.dataForm.price,
              'category1': this.dataForm.category1,
              'category2': this.dataForm.category2,
              'specs': this.dataForm.specs,
              'description': this.dataForm.description,
              'status': this.dataForm.status,
              'outLink': this.dataForm.outLink,
              'sortNum': this.dataForm.sortNum,
              'type': this.dataForm.type
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
