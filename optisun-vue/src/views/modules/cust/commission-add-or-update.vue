<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="客户ID" prop="custId">
      <el-input v-model="dataForm.custId" placeholder="客户ID"></el-input>
    </el-form-item>
    <el-form-item label="银行名称" prop="bankname">
      <el-input v-model="dataForm.bankname" placeholder="银行名称"></el-input>
    </el-form-item>
    <el-form-item label="银行代码" prop="code">
      <el-input v-model="dataForm.code" placeholder="银行代码"></el-input>
    </el-form-item>
    <el-form-item label="持卡人" prop="name">
      <el-input v-model="dataForm.name" placeholder="持卡人"></el-input>
    </el-form-item>
    <el-form-item label="账号" prop="account">
      <el-input v-model="dataForm.account" placeholder="账号"></el-input>
    </el-form-item>
    <el-form-item label="IBAN" prop="iban">
      <el-input v-model="dataForm.iban" placeholder="IBAN"></el-input>
    </el-form-item>
    <el-form-item label="出生" prop="chusheng">
      <el-input v-model="dataForm.chusheng" placeholder="出生"></el-input>
    </el-form-item>
    <el-form-item label="提现金额" prop="money">
      <el-input v-model="dataForm.money" placeholder="提现金额"></el-input>
    </el-form-item>
    <el-form-item label="状态值:0=待审,1=已打款,2=驳回" prop="status">
      <el-input v-model="dataForm.status" placeholder="状态值:0=待审,1=已打款,2=驳回"></el-input>
    </el-form-item>
    <el-form-item label="" prop="cn">
      <el-input v-model="dataForm.cn" placeholder=""></el-input>
    </el-form-item>
    <el-form-item label="备注" prop="remark">
      <el-input v-model="dataForm.remark" placeholder="备注"></el-input>
    </el-form-item>
    <el-form-item label="到账金额" prop="dzMoney">
      <el-input v-model="dataForm.dzMoney" placeholder="到账金额"></el-input>
    </el-form-item>
    <el-form-item label="0赠1减" prop="cut">
      <el-input v-model="dataForm.cut" placeholder="0赠1减"></el-input>
    </el-form-item>
    <el-form-item label="创建时间" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
    </el-form-item>
    <el-form-item label="审核人" prop="checkBy">
      <el-input v-model="dataForm.checkBy" placeholder="审核人"></el-input>
    </el-form-item>
    <el-form-item label="审核人id" prop="checkTime">
      <el-input v-model="dataForm.checkTime" placeholder="审核人id"></el-input>
    </el-form-item>
    <el-form-item label="机构id" prop="orgId">
      <el-input v-model="dataForm.orgId" placeholder="机构id"></el-input>
    </el-form-item>
    <el-form-item label="删除标示(0正常1删除)" prop="delFlag">
      <el-input v-model="dataForm.delFlag" placeholder="删除标示(0正常1删除)"></el-input>
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
          bankname: '',
          code: '',
          name: '',
          account: '',
          iban: '',
          chusheng: '',
          money: '',
          status: '',
          cn: '',
          remark: '',
          dzMoney: '',
          cut: '',
          createTime: '',
          checkBy: '',
          checkTime: '',
          orgId: '',
          delFlag: ''
        },
        dataRule: {
          custId: [
            { required: true, message: '客户ID不能为空', trigger: 'blur' }
          ],
          bankname: [
            { required: true, message: '银行名称不能为空', trigger: 'blur' }
          ],
          code: [
            { required: true, message: '银行代码不能为空', trigger: 'blur' }
          ],
          name: [
            { required: true, message: '持卡人不能为空', trigger: 'blur' }
          ],
          account: [
            { required: true, message: '账号不能为空', trigger: 'blur' }
          ],
          iban: [
            { required: true, message: 'IBAN不能为空', trigger: 'blur' }
          ],
          chusheng: [
            { required: true, message: '出生不能为空', trigger: 'blur' }
          ],
          money: [
            { required: true, message: '提现金额不能为空', trigger: 'blur' }
          ],
          status: [
            { required: true, message: '状态值:0=待审,1=已打款,2=驳回不能为空', trigger: 'blur' }
          ],
          cn: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          remark: [
            { required: true, message: '备注不能为空', trigger: 'blur' }
          ],
          dzMoney: [
            { required: true, message: '到账金额不能为空', trigger: 'blur' }
          ],
          cut: [
            { required: true, message: '0赠1减不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '创建时间不能为空', trigger: 'blur' }
          ],
          checkBy: [
            { required: true, message: '审核人不能为空', trigger: 'blur' }
          ],
          checkTime: [
            { required: true, message: '审核人id不能为空', trigger: 'blur' }
          ],
          orgId: [
            { required: true, message: '机构id不能为空', trigger: 'blur' }
          ],
          delFlag: [
            { required: true, message: '删除标示(0正常1删除)不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/cust/commission/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.custId = data.commission.custId
                this.dataForm.bankname = data.commission.bankname
                this.dataForm.code = data.commission.code
                this.dataForm.name = data.commission.name
                this.dataForm.account = data.commission.account
                this.dataForm.iban = data.commission.iban
                this.dataForm.chusheng = data.commission.chusheng
                this.dataForm.money = data.commission.money
                this.dataForm.status = data.commission.status
                this.dataForm.cn = data.commission.cn
                this.dataForm.remark = data.commission.remark
                this.dataForm.dzMoney = data.commission.dzMoney
                this.dataForm.cut = data.commission.cut
                this.dataForm.createTime = data.commission.createTime
                this.dataForm.checkBy = data.commission.checkBy
                this.dataForm.checkTime = data.commission.checkTime
                this.dataForm.orgId = data.commission.orgId
                this.dataForm.delFlag = data.commission.delFlag
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
              url: this.$http.adornUrl(`/cust/commission/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'custId': this.dataForm.custId,
                'bankname': this.dataForm.bankname,
                'code': this.dataForm.code,
                'name': this.dataForm.name,
                'account': this.dataForm.account,
                'iban': this.dataForm.iban,
                'chusheng': this.dataForm.chusheng,
                'money': this.dataForm.money,
                'status': this.dataForm.status,
                'cn': this.dataForm.cn,
                'remark': this.dataForm.remark,
                'dzMoney': this.dataForm.dzMoney,
                'cut': this.dataForm.cut,
                'createTime': this.dataForm.createTime,
                'checkBy': this.dataForm.checkBy,
                'checkTime': this.dataForm.checkTime,
                'orgId': this.dataForm.orgId,
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
