<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" :content="!dataForm.id ? '新增' : '修改'"></el-page-header>
    <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="订单时间" prop="orderTime">
      <el-input v-model="dataForm.orderTime" placeholder="订单时间"></el-input>
    </el-form-item>
    <el-form-item label="订单所属客户" prop="orderCust">
      <el-input v-model="dataForm.orderCust" placeholder="订单所属客户"></el-input>
    </el-form-item>
    <el-form-item label="一级客户ID" prop="firstCustId">
      <el-input v-model="dataForm.firstCustId" placeholder="一级客户ID"></el-input>
    </el-form-item>
    <el-form-item label="一级客户所属业务员" prop="firstSalesmanId">
      <el-input v-model="dataForm.firstSalesmanId" placeholder="一级客户所属业务员"></el-input>
    </el-form-item>
    <el-form-item label="一级客户是否为内部" prop="firstIsNb">
      <el-input v-model="dataForm.firstIsNb" placeholder="一级客户是否为内部"></el-input>
    </el-form-item>
    <el-form-item label="一级客户佣金" prop="fisrtMoney">
      <el-input v-model="dataForm.fisrtMoney" placeholder="一级客户佣金"></el-input>
    </el-form-item>
    <el-form-item label="二级客户ID" prop="secondCustId">
      <el-input v-model="dataForm.secondCustId" placeholder="二级客户ID"></el-input>
    </el-form-item>
    <el-form-item label="二级客户所属业务员" prop="secondSalesmanId">
      <el-input v-model="dataForm.secondSalesmanId" placeholder="二级客户所属业务员"></el-input>
    </el-form-item>
    <el-form-item label="二级客户是否为内部" prop="secondIsNb">
      <el-input v-model="dataForm.secondIsNb" placeholder="二级客户是否为内部"></el-input>
    </el-form-item>
    <el-form-item label="二级客户佣金" prop="secondMoney">
      <el-input v-model="dataForm.secondMoney" placeholder="二级客户佣金"></el-input>
    </el-form-item>
    <el-form-item label="3级客户ID" prop="thirdCustId">
      <el-input v-model="dataForm.thirdCustId" placeholder="3级客户ID"></el-input>
    </el-form-item>
    <el-form-item label="3级客户所属业务员" prop="thirdSalesmanId">
      <el-input v-model="dataForm.thirdSalesmanId" placeholder="3级客户所属业务员"></el-input>
    </el-form-item>
    <el-form-item label="3级客户是否为内部" prop="thirdIsNb">
      <el-input v-model="dataForm.thirdIsNb" placeholder="3级客户是否为内部"></el-input>
    </el-form-item>
    <el-form-item label="3级客户佣金" prop="thirdMoney">
      <el-input v-model="dataForm.thirdMoney" placeholder="3级客户佣金"></el-input>
    </el-form-item>
    <el-form-item label="4级客户ID" prop="fourCustId">
      <el-input v-model="dataForm.fourCustId" placeholder="4级客户ID"></el-input>
    </el-form-item>
    <el-form-item label="4级客户所属业务员" prop="fourSalesmanId">
      <el-input v-model="dataForm.fourSalesmanId" placeholder="4级客户所属业务员"></el-input>
    </el-form-item>
    <el-form-item label="4级客户是否为内部" prop="fourIsNb">
      <el-input v-model="dataForm.fourIsNb" placeholder="4级客户是否为内部"></el-input>
    </el-form-item>
    <el-form-item label="4级客户佣金" prop="fourMoney">
      <el-input v-model="dataForm.fourMoney" placeholder="4级客户佣金"></el-input>
    </el-form-item>
    <el-form-item label="5级客户ID" prop="fiveCustId">
      <el-input v-model="dataForm.fiveCustId" placeholder="5级客户ID"></el-input>
    </el-form-item>
    <el-form-item label="5级客户所属业务员" prop="fiveSalesmanId">
      <el-input v-model="dataForm.fiveSalesmanId" placeholder="5级客户所属业务员"></el-input>
    </el-form-item>
    <el-form-item label="5级客户是否为内部" prop="fiveIsNb">
      <el-input v-model="dataForm.fiveIsNb" placeholder="5级客户是否为内部"></el-input>
    </el-form-item>
    <el-form-item label="5级客户佣金" prop="fiveMoney">
      <el-input v-model="dataForm.fiveMoney" placeholder="5级客户佣金"></el-input>
    </el-form-item>
    <el-form-item label="6级客户ID" prop="sixCustId">
      <el-input v-model="dataForm.sixCustId" placeholder="6级客户ID"></el-input>
    </el-form-item>
    <el-form-item label="6级客户所属业务员" prop="sixSalesmanId">
      <el-input v-model="dataForm.sixSalesmanId" placeholder="6级客户所属业务员"></el-input>
    </el-form-item>
    <el-form-item label="6级客户是否为内部" prop="sixIsNb">
      <el-input v-model="dataForm.sixIsNb" placeholder="6级客户是否为内部"></el-input>
    </el-form-item>
    <el-form-item label="6级客户佣金" prop="sixMoney">
      <el-input v-model="dataForm.sixMoney" placeholder="6级客户佣金"></el-input>
    </el-form-item>
    <el-form-item label="7级客户ID" prop="sevenCustId">
      <el-input v-model="dataForm.sevenCustId" placeholder="7级客户ID"></el-input>
    </el-form-item>
    <el-form-item label="7级客户所属业务员" prop="sevenSalesmanId">
      <el-input v-model="dataForm.sevenSalesmanId" placeholder="7级客户所属业务员"></el-input>
    </el-form-item>
    <el-form-item label="7级客户是否为内部" prop="sevenIsNb">
      <el-input v-model="dataForm.sevenIsNb" placeholder="7级客户是否为内部"></el-input>
    </el-form-item>
    <el-form-item label="7级客户佣金" prop="sevenMoney">
      <el-input v-model="dataForm.sevenMoney" placeholder="7级客户佣金"></el-input>
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
          orderId: 0,
          orderTime: '',
          orderCust: '',
          firstCustId: '',
          firstSalesmanId: '',
          firstIsNb: '',
          fisrtMoney: '',
          secondCustId: '',
          secondSalesmanId: '',
          secondIsNb: '',
          secondMoney: '',
          thirdCustId: '',
          thirdSalesmanId: '',
          thirdIsNb: '',
          thirdMoney: '',
          fourCustId: '',
          fourSalesmanId: '',
          fourIsNb: '',
          fourMoney: '',
          fiveCustId: '',
          fiveSalesmanId: '',
          fiveIsNb: '',
          fiveMoney: '',
          sixCustId: '',
          sixSalesmanId: '',
          sixIsNb: '',
          sixMoney: '',
          sevenCustId: '',
          sevenSalesmanId: '',
          sevenIsNb: '',
          sevenMoney: ''
        },
        dataRule: {
          orderTime: [
            { required: true, message: '订单时间不能为空', trigger: 'blur' }
          ],
          orderCust: [
            { required: true, message: '订单所属客户不能为空', trigger: 'blur' }
          ],
          firstCustId: [
            { required: true, message: '一级客户ID不能为空', trigger: 'blur' }
          ],
          firstSalesmanId: [
            { required: true, message: '一级客户所属业务员不能为空', trigger: 'blur' }
          ],
          firstIsNb: [
            { required: true, message: '一级客户是否为内部不能为空', trigger: 'blur' }
          ],
          fisrtMoney: [
            { required: true, message: '一级客户佣金不能为空', trigger: 'blur' }
          ],
          secondCustId: [
            { required: true, message: '二级客户ID不能为空', trigger: 'blur' }
          ],
          secondSalesmanId: [
            { required: true, message: '二级客户所属业务员不能为空', trigger: 'blur' }
          ],
          secondIsNb: [
            { required: true, message: '二级客户是否为内部不能为空', trigger: 'blur' }
          ],
          secondMoney: [
            { required: true, message: '二级客户佣金不能为空', trigger: 'blur' }
          ],
          thirdCustId: [
            { required: true, message: '3级客户ID不能为空', trigger: 'blur' }
          ],
          thirdSalesmanId: [
            { required: true, message: '3级客户所属业务员不能为空', trigger: 'blur' }
          ],
          thirdIsNb: [
            { required: true, message: '3级客户是否为内部不能为空', trigger: 'blur' }
          ],
          thirdMoney: [
            { required: true, message: '3级客户佣金不能为空', trigger: 'blur' }
          ],
          fourCustId: [
            { required: true, message: '4级客户ID不能为空', trigger: 'blur' }
          ],
          fourSalesmanId: [
            { required: true, message: '4级客户所属业务员不能为空', trigger: 'blur' }
          ],
          fourIsNb: [
            { required: true, message: '4级客户是否为内部不能为空', trigger: 'blur' }
          ],
          fourMoney: [
            { required: true, message: '4级客户佣金不能为空', trigger: 'blur' }
          ],
          fiveCustId: [
            { required: true, message: '5级客户ID不能为空', trigger: 'blur' }
          ],
          fiveSalesmanId: [
            { required: true, message: '5级客户所属业务员不能为空', trigger: 'blur' }
          ],
          fiveIsNb: [
            { required: true, message: '5级客户是否为内部不能为空', trigger: 'blur' }
          ],
          fiveMoney: [
            { required: true, message: '5级客户佣金不能为空', trigger: 'blur' }
          ],
          sixCustId: [
            { required: true, message: '6级客户ID不能为空', trigger: 'blur' }
          ],
          sixSalesmanId: [
            { required: true, message: '6级客户所属业务员不能为空', trigger: 'blur' }
          ],
          sixIsNb: [
            { required: true, message: '6级客户是否为内部不能为空', trigger: 'blur' }
          ],
          sixMoney: [
            { required: true, message: '6级客户佣金不能为空', trigger: 'blur' }
          ],
          sevenCustId: [
            { required: true, message: '7级客户ID不能为空', trigger: 'blur' }
          ],
          sevenSalesmanId: [
            { required: true, message: '7级客户所属业务员不能为空', trigger: 'blur' }
          ],
          sevenIsNb: [
            { required: true, message: '7级客户是否为内部不能为空', trigger: 'blur' }
          ],
          sevenMoney: [
            { required: true, message: '7级客户佣金不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      goBack(){
        this.$emit('back', {})
      },
      init (id) {
        this.dataForm.orderId = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.orderId) {
            this.loading = true
            this.$http({
              url: this.$http.adornUrl(`/cust/taskcommission/info/${this.dataForm.orderId}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              this.loading = false
              if (data && data.code === 0) {
                this.dataForm.orderTime = data.taskCommission.orderTime
                this.dataForm.orderCust = data.taskCommission.orderCust
                this.dataForm.firstCustId = data.taskCommission.firstCustId
                this.dataForm.firstSalesmanId = data.taskCommission.firstSalesmanId
                this.dataForm.firstIsNb = data.taskCommission.firstIsNb
                this.dataForm.fisrtMoney = data.taskCommission.fisrtMoney
                this.dataForm.secondCustId = data.taskCommission.secondCustId
                this.dataForm.secondSalesmanId = data.taskCommission.secondSalesmanId
                this.dataForm.secondIsNb = data.taskCommission.secondIsNb
                this.dataForm.secondMoney = data.taskCommission.secondMoney
                this.dataForm.thirdCustId = data.taskCommission.thirdCustId
                this.dataForm.thirdSalesmanId = data.taskCommission.thirdSalesmanId
                this.dataForm.thirdIsNb = data.taskCommission.thirdIsNb
                this.dataForm.thirdMoney = data.taskCommission.thirdMoney
                this.dataForm.fourCustId = data.taskCommission.fourCustId
                this.dataForm.fourSalesmanId = data.taskCommission.fourSalesmanId
                this.dataForm.fourIsNb = data.taskCommission.fourIsNb
                this.dataForm.fourMoney = data.taskCommission.fourMoney
                this.dataForm.fiveCustId = data.taskCommission.fiveCustId
                this.dataForm.fiveSalesmanId = data.taskCommission.fiveSalesmanId
                this.dataForm.fiveIsNb = data.taskCommission.fiveIsNb
                this.dataForm.fiveMoney = data.taskCommission.fiveMoney
                this.dataForm.sixCustId = data.taskCommission.sixCustId
                this.dataForm.sixSalesmanId = data.taskCommission.sixSalesmanId
                this.dataForm.sixIsNb = data.taskCommission.sixIsNb
                this.dataForm.sixMoney = data.taskCommission.sixMoney
                this.dataForm.sevenCustId = data.taskCommission.sevenCustId
                this.dataForm.sevenSalesmanId = data.taskCommission.sevenSalesmanId
                this.dataForm.sevenIsNb = data.taskCommission.sevenIsNb
                this.dataForm.sevenMoney = data.taskCommission.sevenMoney
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
              url: this.$http.adornUrl(`/cust/taskcommission/${!this.dataForm.orderId ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'orderId': this.dataForm.orderId || undefined,
                'orderTime': this.dataForm.orderTime,
                'orderCust': this.dataForm.orderCust,
                'firstCustId': this.dataForm.firstCustId,
                'firstSalesmanId': this.dataForm.firstSalesmanId,
                'firstIsNb': this.dataForm.firstIsNb,
                'fisrtMoney': this.dataForm.fisrtMoney,
                'secondCustId': this.dataForm.secondCustId,
                'secondSalesmanId': this.dataForm.secondSalesmanId,
                'secondIsNb': this.dataForm.secondIsNb,
                'secondMoney': this.dataForm.secondMoney,
                'thirdCustId': this.dataForm.thirdCustId,
                'thirdSalesmanId': this.dataForm.thirdSalesmanId,
                'thirdIsNb': this.dataForm.thirdIsNb,
                'thirdMoney': this.dataForm.thirdMoney,
                'fourCustId': this.dataForm.fourCustId,
                'fourSalesmanId': this.dataForm.fourSalesmanId,
                'fourIsNb': this.dataForm.fourIsNb,
                'fourMoney': this.dataForm.fourMoney,
                'fiveCustId': this.dataForm.fiveCustId,
                'fiveSalesmanId': this.dataForm.fiveSalesmanId,
                'fiveIsNb': this.dataForm.fiveIsNb,
                'fiveMoney': this.dataForm.fiveMoney,
                'sixCustId': this.dataForm.sixCustId,
                'sixSalesmanId': this.dataForm.sixSalesmanId,
                'sixIsNb': this.dataForm.sixIsNb,
                'sixMoney': this.dataForm.sixMoney,
                'sevenCustId': this.dataForm.sevenCustId,
                'sevenSalesmanId': this.dataForm.sevenSalesmanId,
                'sevenIsNb': this.dataForm.sevenIsNb,
                'sevenMoney': this.dataForm.sevenMoney
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
