<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" content="锁单"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
      <el-form-item label="会员ID" prop="custId">
        <el-input v-model="dataForm.custId" placeholder="会员ID"></el-input>
      </el-form-item>
      <el-form-item label="房间ID" prop="roomId">
        <el-select v-model="dataForm.roomId" placeholder="房间ID" @change="getGooldsList">
          <el-option v-for="item in roomList" :key="item.id" :value="item.id" :label="item.name + '-' + item.days + '天-' + item.rate + '%'"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="产品ID" prop="goodsId">
        <el-select v-model="dataForm.goodsId" placeholder="产品ID">
          <el-option v-for="item in goodsList" :key="item.id" :value="item.id" :label="item.name + '-' + item.price + 'CFA'"></el-option>
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
        custId: '',
        roomId: '',
        goodsId: '',
      },
      dataRule: {
        custId: [
          {required: true, message: '会员ID不能为空', trigger: 'blur'}
        ],
        roomId: [
          {required: true, message: '房间不能为空', trigger: 'blur'}
        ],
        goodsId: [
          {required: true, message: '产品ID不能为空', trigger: 'blur'}
        ]
      },
      roomList: [],
      goodsList: []
    }
  },
  created() {
    this.getRoomList()
  },
  methods: {
    goBack() {
      this.$emit('back', {})
    },
    getRoomList(){
      this.$http({
        url: this.$http.adornUrl(`/l/lroom/all`),
        method: 'get',
        params: this.$http.adornParams()
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.roomList = data.data
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    getGooldsList(){
      this.$http({
        url: this.$http.adornUrl(`/l/lgoods/all`),
        method: 'get',
        params: this.$http.adornParams({
          roomId: this.dataForm.roomId
        })
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.goodsList = data.data
        } else {
          this.$message.error(data.msg)
        }
      })
    },
    init(id) {
      this.dataForm.id = id || 0
      this.getRoomList()
      this.visible = true
      this.$nextTick(() => {
        this.$refs['dataForm'].resetFields()
      })
    },
    // 表单提交
    dataFormSubmit() {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/l/lorder/buyGive`),
            method: 'post',
            data: this.$http.adornData({
              'custId': this.dataForm.custId,
              'goodsId': this.dataForm.goodsId,
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
