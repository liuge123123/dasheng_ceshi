<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="等级" prop="gradeId">
      <el-select v-model="dataForm.gradeId" placeholder="请选择">
        <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.gradeName"
          :value="item.id">
        </el-option>
      </el-select>
    </el-form-item>

    <el-form-item label="商品名称" prop="goodsName">
      <el-input v-model="dataForm.goodsName" placeholder="商品名称" type="text"
                maxlength="125"
                show-word-limit
      ></el-input>
    </el-form-item>
    <el-form-item label="价格" prop="price">
      <el-input-number v-model="dataForm.price" style="width: 30%" :step="0.1" ></el-input-number>
    </el-form-item>
    <el-form-item label="预期回报金额" prop="estimateprice">
      <el-input-number v-model="dataForm.estimateprice" style="width: 30%" :step="0.1" ></el-input-number>
    </el-form-item>
    <el-form-item label="周期" prop="periodId">
      <el-input v-model="dataForm.periodId" placeholder="周期"></el-input>
    </el-form-item>
    <el-form-item label="图片" prop="avatar">
      <img-up-load v-model="dataForm.avatar"></img-up-load>
    </el-form-item>
      <el-form-item label="库存数量" prop="stockNum">
        <el-input-number v-model="dataForm.stockNum" style="width: 30%"  :precision="0" :min="0" ></el-input-number>
      </el-form-item>

<!--    <el-form-item label="图片组" prop="images">-->
<!--      <img-list-up-load v-model="dataForm.images"></img-list-up-load>-->
<!--    </el-form-item>-->
<!--    <el-form-item label="状态" prop="status">-->
<!--      <el-radio v-model="dataForm.status" label="0">当前订单</el-radio>-->
<!--      <el-radio v-model="dataForm.status" label="1">出售商品</el-radio>-->
<!--      <el-radio v-model="dataForm.status" label="2">定居</el-radio>-->
<!--    </el-form-item>-->
<!--    <el-form-item label="完成" prop="completeNum" >-->
<!--      <div>{{dataForm.completeNum}}</div>-->
<!--    </el-form-item>-->
<!--    <el-form-item label="剩余" prop="lessNum">-->
<!--      <div>{{dataForm.lessNum}}</div>-->
<!--    </el-form-item>-->
<!--    <el-form-item label="是否超级商品" prop="isVip">-->
<!--      <el-checkbox v-model="dataForm.isVip" true-label="1" false-label="0">是否超级商品</el-checkbox>-->
<!--    </el-form-item>-->
    <el-form-item label="排序" prop="weigh">
      <el-input-number v-model="dataForm.weigh" :step="1" style="width: 30%"></el-input-number>
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
  import imgListUpLoad from '@/components/upload/img-list-upload-assembly'
  export default {
    components:{
      imgUpLoad,
      imgListUpLoad
    },
    data () {
      return {
        loading: false,
        dataForm: {
          id: 0,
          gradeId: '',
          goodsName: '',
          price: '',
          estimateprice: '',
          periodId: '',
          avatar: '',
          images: '',
          status: '',
          completeNum: '',
          lessNum: '',
          isVip: '',
          weigh: '',
          stockNum:0
        },
        options:[],
        dataRule: {
          gradeId: [
            { required: true, message: '请选择等级', trigger: 'blur' }
          ],
          goodsName: [
            { required: true, message: '商品名称不能为空', trigger: 'blur' }
          ],
          price: [
            { required: true, message: '价格不能为空', trigger: 'blur' }
          ],
          estimateprice: [
            { required: true, message: '预期回报不能为空', trigger: 'blur' }
          ],
          periodId: [
            { required: true, message: '周期不能为空', trigger: 'blur' }
          ],
          avatar: [
            { required: true, message: '图片不能为空', trigger: 'blur' }
          ],
          images: [
            { required: true, message: '图片组不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '请选择状态', trigger: 'blur' }
          ],
          completeNum: [
            { required: true, message: '完成不能为空', trigger: 'blur' }
          ],
          lessNum: [
            { required: true, message: '剩余不能为空', trigger: 'blur' }
          ],
          isVip: [
            { required: true, message: '是否超级商品不能为空', trigger: 'blur' }
          ],
          weigh: [
            { required: true, message: '权重不能为空', trigger: 'blur' }
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
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/t/tgoods/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.gradeId = data.tGoods.gradeId
                this.dataForm.goodsName = data.tGoods.goodsName
                this.dataForm.price = data.tGoods.price
                this.dataForm.estimateprice = data.tGoods.estimateprice
                this.dataForm.periodId = data.tGoods.periodId
                this.dataForm.avatar = data.tGoods.avatar
                this.dataForm.images = data.tGoods.images
                this.dataForm.status = data.tGoods.status
                this.dataForm.completeNum = data.tGoods.completeNum
                this.dataForm.lessNum = data.tGoods.lessNum
                this.dataForm.isVip = data.tGoods.isVip
                this.dataForm.weigh = data.tGoods.weigh
                this.dataForm.stockNum = data.tGoods.stockNum
              }else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },

      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/t/tgoodsgrade/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': 1,
            'limit': 10,
            'key': ''
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.options = data.page.list
            console.log(this.options)
          } else {
            this.options = []
          }
          this.dataListLoading = false
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/t/tgoods/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'gradeId': this.dataForm.gradeId,
                'goodsName': this.dataForm.goodsName,
                'price': this.dataForm.price,
                'estimateprice': this.dataForm.estimateprice,
                'periodId': this.dataForm.periodId,
                'avatar': this.dataForm.avatar,
                'images': this.dataForm.images,
                'status': this.dataForm.status,
                'isVip': this.dataForm.isVip,
                'weigh': this.dataForm.weigh,
                'stockNum':this.dataForm.stockNum
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
