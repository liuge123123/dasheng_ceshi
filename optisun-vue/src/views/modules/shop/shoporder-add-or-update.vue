<template>
  <el-card v-loading="loading">
    <el-page-header class="cph" @back="goBack" content="订单详情"></el-page-header>

    <div class="cpf">
      <el-descriptions title="订单详情" :column="3" border>
        <el-descriptions-item label="订单编号">{{ dataForm.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="客户ID">{{ dataForm.custId }}</el-descriptions-item>
        <el-descriptions-item label="产品数量">{{ dataForm.orderNum }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">{{ dataForm.orderMoney }}</el-descriptions-item>
        <el-descriptions-item label="订单折扣">
          {{ dataForm.orderDiscount }}折
          <span v-if="dataForm.couponId == 0">VIP折扣</span>
          <span v-else>折扣券ID：{{dataForm.couponId}}</span>
        </el-descriptions-item>
        <el-descriptions-item label="实付金额">{{ dataForm.orderPayMoney }}</el-descriptions-item>

        <el-descriptions-item label="订单状态">
          <el-tag size="mini" type="success" v-if="dataForm.orderStatus == 1">已支付</el-tag>
          <el-tag size="mini" type="danger" v-if="dataForm.orderStatus == 2">已退单</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="订单时间">{{ dataForm.orderTime | timeStampDateTimeFormat }}</el-descriptions-item>
        <el-descriptions-item label="退单时间">{{ dataForm.quitTime | timeStampDateTimeFormat }}</el-descriptions-item>
        <el-descriptions-item label="退单员工">{{ dataForm.quitUser }}</el-descriptions-item>
      </el-descriptions>
      <br><br>

      <div class="buyer-panel" v-loading="editForm.editBuyerLoading">
        <el-descriptions title="收货信息" :column="2" border>
          <el-descriptions-item label="购买者姓名">
            <el-input v-if="editForm.editBuyer" v-model="editForm.buyerName"></el-input>
            <span v-else>{{ dataForm.buyerName }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="购买者电话">
            <el-input v-if="editForm.editBuyer" v-model="editForm.buyderPhone"></el-input>
            <span v-else>{{ dataForm.buyderPhone }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="购买者地址" :span="2">
            <el-input v-if="editForm.editBuyer" v-model="editForm.buyerAddress"></el-input>
            <span v-else>{{ dataForm.buyerAddress }}</span>
          </el-descriptions-item>
        </el-descriptions>
        <div class="buyer-edit">
          <el-button size="mini" v-if="!editForm.editBuyer" type="primary" @click="edit">修改收货信息</el-button>
          <el-button size="mini" v-if="editForm.editBuyer" @click="cancel">取消</el-button>
          <el-button size="mini" v-if="editForm.editBuyer" type="primary" @click="confirm">确定</el-button>
        </div>
      </div>

      <br><br>
      <div class="el-descriptions__title" style="margin-bottom: 20px;">订单产品</div>
      <el-table :data="dataForm.goodsList">
        <el-table-column prop="goodsImg" label="图片">
          <template v-slot="scope"><el-image :src="scope.row.goodsImg" :preview-src-list="[scope.row.goodsImg]" fit="contain" style="width: 80px; height: 80px; border: 1px solid #EEEEEE"></el-image></template>
        </el-table-column>
        <el-table-column prop="goodsName" label="名称"></el-table-column>
        <el-table-column prop="goodsPrice" label="价格" align="center"></el-table-column>
        <el-table-column prop="goodsNum" label="数量" align="right"></el-table-column>
      </el-table>
    </div>



  </el-card>
</template>

<script>
export default {
  data() {
    return {
      loading: false,
      dataForm: {
        id: 0,
        custId: '',
        orderNo: '',
        orderNum: '',
        orderMoney: '',
        orderDiscount: '',
        orderPayMoney: '',
        orderTime: '',
        buyerName: '',
        buyderPhone: '',
        buyerAddress: '',
        couponId: '',
        salesmanId: '',
        isNb: '',
        orderStatus: '',
        quitTime: '',
        quitUser: '',
        goodsList: []
      },
      editForm: {
        editBuyer: false,
        editBuyerLoading: false,
        buyerName: '',
        buyderPhone: '',
        buyerAddress: '',
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
        if (this.dataForm.id) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/shop/shoporder/info/${this.dataForm.id}`),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.dataForm.custId = data.shopOrder.custId
              this.dataForm.orderNo = data.shopOrder.orderNo
              this.dataForm.orderNum = data.shopOrder.orderNum
              this.dataForm.orderMoney = data.shopOrder.orderMoney
              this.dataForm.orderDiscount = data.shopOrder.orderDiscount
              this.dataForm.orderPayMoney = data.shopOrder.orderPayMoney
              this.dataForm.orderTime = data.shopOrder.orderTime
              this.dataForm.buyerName = data.shopOrder.buyerName
              this.dataForm.buyderPhone = data.shopOrder.buyderPhone
              this.dataForm.buyerAddress = data.shopOrder.buyerAddress
              this.dataForm.couponId = data.shopOrder.couponId
              this.dataForm.salesmanId = data.shopOrder.salesmanId
              this.dataForm.isNb = data.shopOrder.isNb
              this.dataForm.orderStatus = data.shopOrder.orderStatus
              this.dataForm.quitTime = data.shopOrder.quitTime
              this.dataForm.quitUser = data.shopOrder.quitUser
              this.dataForm.goodsList = data.shopOrder.goodsList
            } else {
              this.$message.error(data.msg)
            }
          })
        }
      })
    },
    edit(){
      this.editForm.buyerName = this.dataForm.buyerName
      this.editForm.buyderPhone = this.dataForm.buyderPhone
      this.editForm.buyerAddress = this.dataForm.buyerAddress
      this.editForm.editBuyer = true
    },
    cancel(){
      this.editForm.editBuyer = false
    },
    confirm(){
      this.editForm.editBuyerLoading = true
      this.$http({
        url: this.$http.adornUrl(`/shop/shoporder/updateBuyer`),
        method: 'post',
        data: this.$http.adornData({
          id: this.dataForm.id,
          buyerName: this.editForm.buyerName,
          buyderPhone: this.editForm.buyderPhone,
          buyerAddress: this.editForm.buyerAddress
        })
      }).then(({data}) => {
        this.editForm.editBuyerLoading = false
        if (data && data.code === 0) {
          this.dataForm.buyerName = this.editForm.buyerName
          this.dataForm.buyderPhone = this.editForm.buyderPhone
          this.dataForm.buyerAddress = this.editForm.buyerAddress
          this.editForm.editBuyer = false
          this.$message.success('操作成功')
        } else {
          this.$message.error(data.msg)
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.buyer-panel{
  position: relative;
  .buyer-edit{
    position: absolute;
    top: 0;
    right: 0;
  }
}
</style>
