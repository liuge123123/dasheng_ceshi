<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.custId ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="150px" size="small">
      <el-form-item label="是否关联内部员工" prop="salesmanId">
        <el-switch
          v-model="dataForm.isNb"
          :active-value="1"
          :inactive-value="0"
          active-color="#13ce66"
          inactive-color="#ff4949">
        </el-switch>
      </el-form-item>
      <el-form-item label="员工编号" prop="salesmanId" v-if="dataForm.isNb==1">
        <el-input v-model="dataForm.salesmanId" placeholder="员工编号"></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="custName">
        <el-input v-model="dataForm.custName" placeholder="会员名"></el-input>
      </el-form-item>
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="dataForm.nickName" placeholder="昵称"></el-input>
      </el-form-item>
      <el-form-item label="手机号" prop="mobile">
        <el-input v-model="dataForm.mobile" placeholder="手机号"></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="pwd">
        <el-input v-model="dataForm.pwd" placeholder="密码"></el-input>
      </el-form-item>
      <el-form-item label="会员等级" prop="level">
        <el-select v-model="dataForm.level" placeholder="请选择">
          <el-option
            v-for="item in gradeList"
            :key="item.gradeLevel"
            :label="item.gradeName"
            :value="item.gradeLevel">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="充值余额" prop="leftRechargeMoney">
        <el-input-number v-model="dataForm.leftRechargeMoney" placeholder="充值余额"></el-input-number>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="dataForm.status">
          <el-radio :label="1">启用</el-radio>
          <el-radio :label="0">停用</el-radio>
        </el-radio-group>
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
          custId: 0,
          custName: '',
          nickName: '',
          mobile: '',
          pwd: '',
          level: '',
          leftRechargeMoney: 0,
          status: 1,
          isNb: 1,
          salesmanId:0
        },
        dataRule: {
          custName: [
            { required: true, message: '会员名不能为空', trigger: 'blur' }
          ],
          nickName: [
            { required: true, message: '昵称不能为空', trigger: 'blur' }
          ],
          mobile: [
            { required: true, message: '手机号不能为空', trigger: 'blur' }
          ],
          pwd: [
            { required: true, message: '密码不能为空', trigger: 'blur' }
          ],
          level: [
            { required: true, message: '会员等级不能为空', trigger: 'blur' }
          ],
          leftRechargeMoney: [
            { required: true, message: '余额不能为空', trigger: 'blur' }
          ],
          salesmanId: [
            { required: true, message: '业务员不能为空', trigger: 'blur' }
          ]
        },
        gradeList:[]
      }
    },
    mounted() {
      this.getGradeList()
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.custId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.custId) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/cust/cust/info/${this.dataForm.custId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.custName = data.cust.custName
                this.dataForm.nickName = data.cust.nickName
                this.dataForm.mobile = data.cust.mobile
                this.dataForm.status = data.cust.status
                this.dataForm.level = data.cust.level
                this.dataForm.leftRechargeMoney = data.cust.leftRechargeMoney
                this.dataForm.salesmanId =  data.cust.salesmanId
                this.dataForm.salt = data.cust.salt,
                this.dataForm.isNb =  data.cust.isNb+''
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
              url: this.$http.adornUrl(`/cust/cust/${!this.dataForm.custId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'custId': this.dataForm.custId || undefined,
                'custName': this.dataForm.custName,
                'nickName': this.dataForm.nickName,
                'mobile': this.dataForm.mobile,
                'pwd': this.dataForm.pwd,
                'status': this.dataForm.status,
                'leftRechargeMoney': this.dataForm.leftRechargeMoney,
                'level': this.dataForm.level,
                'salesmanId': this.dataForm.salesmanId,
                'salt':this.dataForm.salt,
                'isNb':this.dataForm.isNb
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
      },
      // 获取等级
      getGradeList () {
        this.$http({
          url: this.$http.adornUrl('/t/tgoodsgrade/getListAll'),
          method: 'get',
          params: this.$http.adornParams({
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.gradeList = data.data
          } else {
            this.gradeList = []
          }
        })
      },
    }
  }
</script>
