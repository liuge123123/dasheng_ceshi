<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="会员ID" prop="custId">
      <el-input v-model="dataForm.custId" :readonly="dataForm.id ?true : false" placeholder="会员ID"></el-input>
    </el-form-item>
    <el-form-item label="产品级别" prop="goodsLevel">
        <el-select v-model="dataForm.goodsLevel">
          <el-option
            v-for="(t, i) of goodsLevelList"
            :key="i"
            :label="t.label"
            :value="t.value"
            ></el-option>
        </el-select>
    </el-form-item>
    <el-form-item label="产品订单次数" prop="goodsOrderCount">
      <el-input v-model="dataForm.goodsOrderCount" placeholder="产品订单次数"></el-input>
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
        goodsLevelList: [
            {label: 'A', value: 1},
            {label: 'B', value: 2},
            {label: 'C', value: 3},
            {label: 'D', value: 4},
            {label: 'E', value: 5}
        ],
        loading: false,
        dataForm: {
          id: 0,
          custId: '',
          goodsLevel: '',
          createTime: '',
          updateTime: '',
          goodsOrderCount: ''
        },
        dataRule: {
          custId: [
            { required: true, message: '会员ID不能为空', trigger: 'blur' }
          ],
          goodsLevel: [
            { required: true, message: '产品级别 A-E对应1-5不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '创建时间不能为空', trigger: 'blur' }
          ],
          updateTime: [
            { required: true, message: '更新时间不能为空', trigger: 'blur' }
          ],
          goodsOrderCount: [
            { required: true, message: '产品订单次数不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/l/ordercount/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.custId = data.orderCount.custId
                this.dataForm.goodsLevel = data.orderCount.goodsLevel
                this.dataForm.createTime = data.orderCount.createTime
                this.dataForm.updateTime = data.orderCount.updateTime
                this.dataForm.goodsOrderCount = data.orderCount.goodsOrderCount
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
              url: this.$http.adornUrl(`/l/ordercount/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'custId': this.dataForm.custId,
                'goodsLevel': this.dataForm.goodsLevel,
                'createTime': this.dataForm.createTime,
                'updateTime': this.dataForm.updateTime,
                'goodsOrderCount': this.dataForm.goodsOrderCount
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
