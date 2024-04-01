<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="客户ID" prop="custId">
      <el-input v-model="dataForm.custId" placeholder="客户ID"></el-input>
    </el-form-item>
    <el-form-item label="银行名称" prop="bankName">
      <el-input v-model="dataForm.bankName" placeholder="银行名称"></el-input>
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
    <el-form-item label="参数1" prop="parameter1">
      <el-input v-model="dataForm.parameter1" placeholder="参数1"></el-input>
    </el-form-item>
    <el-form-item label="图片" prop="image">
      <el-input v-model="dataForm.image" placeholder="图片"></el-input>
    </el-form-item>
    <el-form-item label="状态值:0=待审,1=通过,2=驳回" prop="type">
      <el-input v-model="dataForm.type" placeholder="状态值:0=待审,1=通过,2=驳回"></el-input>
    </el-form-item>
    <el-form-item label="创建时间" prop="createTime">
      <el-input v-model="dataForm.createTime" placeholder="创建时间"></el-input>
    </el-form-item>
    <el-form-item label="更新时间" prop="updateTime">
      <el-input v-model="dataForm.updateTime" placeholder="更新时间"></el-input>
    </el-form-item>
    <el-form-item label="银行种类" prop="cateId">
      <el-input v-model="dataForm.cateId" placeholder="银行种类"></el-input>
    </el-form-item>
    <el-form-item label="银行种类名称" prop="cateName">
      <el-input v-model="dataForm.cateName" placeholder="银行种类名称"></el-input>
    </el-form-item>
    <el-form-item label="机构id" prop="orgId">
      <el-input v-model="dataForm.orgId" placeholder="机构id"></el-input>
    </el-form-item>
    <el-form-item label="删除(0正常 1删除)" prop="delFlag">
      <el-input v-model="dataForm.delFlag" placeholder="删除(0正常 1删除)"></el-input>
    </el-form-item>
    <el-form-item label="创建人id" prop="createBy">
      <el-input v-model="dataForm.createBy" placeholder="创建人id"></el-input>
    </el-form-item>
    <el-form-item label="修改人id" prop="updateBy">
      <el-input v-model="dataForm.updateBy" placeholder="修改人id"></el-input>
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
          bankName: '',
          code: '',
          name: '',
          account: '',
          iban: '',
          chusheng: '',
          parameter1: '',
          image: '',
          type: '',
          createTime: '',
          updateTime: '',
          cateId: '',
          cateName: '',
          orgId: '',
          delFlag: '',
          createBy: '',
          updateBy: ''
        },
        dataRule: {
          custId: [
            { required: true, message: '客户ID不能为空', trigger: 'blur' }
          ],
          bankName: [
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
          parameter1: [
            { required: true, message: '参数1不能为空', trigger: 'blur' }
          ],
          image: [
            { required: true, message: '图片不能为空', trigger: 'blur' }
          ],
          type: [
            { required: true, message: '状态值:0=待审,1=通过,2=驳回不能为空', trigger: 'blur' }
          ],
          createTime: [
            { required: true, message: '创建时间不能为空', trigger: 'blur' }
          ],
          updateTime: [
            { required: true, message: '更新时间不能为空', trigger: 'blur' }
          ],
          cateId: [
            { required: true, message: '银行种类不能为空', trigger: 'blur' }
          ],
          cateName: [
            { required: true, message: '银行种类名称不能为空', trigger: 'blur' }
          ],
          orgId: [
            { required: true, message: '机构id不能为空', trigger: 'blur' }
          ],
          delFlag: [
            { required: true, message: '删除(0正常 1删除)不能为空', trigger: 'blur' }
          ],
          createBy: [
            { required: true, message: '创建人id不能为空', trigger: 'blur' }
          ],
          updateBy: [
            { required: true, message: '修改人id不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/cust/bank/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.custId = data.bank.custId
                this.dataForm.bankName = data.bank.bankName
                this.dataForm.code = data.bank.code
                this.dataForm.name = data.bank.name
                this.dataForm.account = data.bank.account
                this.dataForm.iban = data.bank.iban
                this.dataForm.chusheng = data.bank.chusheng
                this.dataForm.parameter1 = data.bank.parameter1
                this.dataForm.image = data.bank.image
                this.dataForm.type = data.bank.type
                this.dataForm.createTime = data.bank.createTime
                this.dataForm.updateTime = data.bank.updateTime
                this.dataForm.cateId = data.bank.cateId
                this.dataForm.cateName = data.bank.cateName
                this.dataForm.orgId = data.bank.orgId
                this.dataForm.delFlag = data.bank.delFlag
                this.dataForm.createBy = data.bank.createBy
                this.dataForm.updateBy = data.bank.updateBy
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
              url: this.$http.adornUrl(`/cust/bank/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'custId': this.dataForm.custId,
                'bankName': this.dataForm.bankName,
                'code': this.dataForm.code,
                'name': this.dataForm.name,
                'account': this.dataForm.account,
                'iban': this.dataForm.iban,
                'chusheng': this.dataForm.chusheng,
                'parameter1': this.dataForm.parameter1,
                'image': this.dataForm.image,
                'type': this.dataForm.type,
                'createTime': this.dataForm.createTime,
                'updateTime': this.dataForm.updateTime,
                'cateId': this.dataForm.cateId,
                'cateName': this.dataForm.cateName,
                'orgId': this.dataForm.orgId,
                'delFlag': this.dataForm.delFlag,
                'createBy': this.dataForm.createBy,
                'updateBy': this.dataForm.updateBy
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
