<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="商品名称" prop="goodsName">
      <el-input v-model="dataForm.goodsName" placeholder="商品名称"></el-input>
    </el-form-item>
    <el-form-item label="图片" prop="image">
      <img-up-load v-model="dataForm.image"></img-up-load>
    </el-form-item>
<!--    <el-form-item label="价格" prop="price">-->
<!--      <el-input-number style="width: 30%" v-model="dataForm.price" :step="1" :max="100000000000"></el-input-number>-->
<!--    </el-form-item>-->
      <el-form-item label="关联会员等级" prop="level">
        <el-select v-model="dataForm.level" style="width: 30%" placeholder="请选择">
          <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.gradeName"
            :value="item.id">
          </el-option>
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
import imgUpLoad from '@/components/upload/img-upload'

  export default {
    components:{
      imgUpLoad,
    },
    data () {
      return {
        loading: false,
        dataForm: {
          id: 0,
          goodsName: '',
          image: '',
          price: '',
          level: 1
        },
        options:[],
        dataRule: {
          goodsName: [
            { required: true, message: '产品名不能为空', trigger: 'blur' }
          ],
          image: [
            { required: true, message: '图片不能为空', trigger: 'blur' }
          ],
          // price: [
          //   { required: true, message: '价格不能为空', trigger: 'blur' }
          // ],
          level: [
            { required: true, message: '请选择关联会员等级', trigger: 'blur' }
          ]
        }
      }
    },
    mounted() {
      this.getDataList()
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/t/tgoodsgrade/getListAll'),
          method: 'get',
          params: this.$http.adornParams({
            'page': 1,
            'limit': 100,
            'key': ''
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.options = data.data
          } else {
            this.options = []
          }
          this.dataListLoading = false
        })
      },
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/g/ggoods/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.goodsName = data.gGoods.goodsName
                this.dataForm.image = data.gGoods.image
                this.dataForm.price = data.gGoods.price
                this.dataForm.level = data.gGoods.level
                this.dataForm.createBy = data.gGoods.createBy
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
              url: this.$http.adornUrl(`/g/ggoods/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'goodsName': this.dataForm.goodsName,
                'image': this.dataForm.image,
                'price': this.dataForm.price,
                'level': this.dataForm.level,
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
