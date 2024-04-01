<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="客户ID" prop="custId">
      <el-input v-model="dataForm.custId" placeholder="客户ID"></el-input>
    </el-form-item>
    <el-form-item label="升级之前等级" prop="beforLevel">
      <el-input v-model="dataForm.beforLevel" placeholder="升级之前等级"></el-input>
    </el-form-item>
    <el-form-item label="升级后等级" prop="afterLevel">
      <el-input v-model="dataForm.afterLevel" placeholder="升级后等级"></el-input>
    </el-form-item>
    <el-form-item label="升级时间" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder="升级时间"></el-input>
    </el-form-item>
    <el-form-item label="是否为内部客户" prop="isNb">
      <el-input v-model="dataForm.isNb" placeholder="是否为内部客户"></el-input>
    </el-form-item>
    <el-form-item label="业务员ID" prop="salesmanId">
      <el-input v-model="dataForm.salesmanId" placeholder="业务员ID"></el-input>
    </el-form-item>
    <el-form-item label="消耗金额" prop="useMoney">
      <el-input v-model="dataForm.useMoney" placeholder="消耗金额"></el-input>
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
          custId: '',
          beforLevel: '',
          afterLevel: '',
          createTime: '',
          isNb: '',
          salesmanId: '',
          useMoney: ''
        },
        dataRule: {
          custId: [
            { required: true, message: '客户ID不能为空', trigger: 'blur' }
          ],
          beforLevel: [
            { required: true, message: '升级之前等级不能为空', trigger: 'blur' }
          ],
          afterLevel: [
            { required: true, message: '升级后等级不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '升级时间不能为空', trigger: 'blur' }
          ],
          isNb: [
            { required: true, message: '是否为内部客户不能为空', trigger: 'blur' }
          ],
          salesmanId: [
            { required: true, message: '业务员ID不能为空', trigger: 'blur' }
          ],
          useMoney: [
            { required: true, message: '消耗金额不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/cust/levellog/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.custId = data.levelLog.custId
                this.dataForm.beforLevel = data.levelLog.beforLevel
                this.dataForm.afterLevel = data.levelLog.afterLevel
                this.dataForm.createTime = data.levelLog.createTime
                this.dataForm.isNb = data.levelLog.isNb
                this.dataForm.salesmanId = data.levelLog.salesmanId
                this.dataForm.useMoney = data.levelLog.useMoney
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
              url: this.$http.adornUrl(`/cust/levellog/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'custId': this.dataForm.custId,
                'beforLevel': this.dataForm.beforLevel,
                'afterLevel': this.dataForm.afterLevel,
                'createTime': this.dataForm.createTime,
                'isNb': this.dataForm.isNb,
                'salesmanId': this.dataForm.salesmanId,
                'useMoney': this.dataForm.useMoney
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
