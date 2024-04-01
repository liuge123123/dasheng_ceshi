<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <el-form :model="dataForm" :inline="true" size="small">
        <el-form-item> <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="getDataList"></SearchUserInput></el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.orderNo" clearable placeholder="订单编号">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.custId" clearable placeholder="客户ID">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.couponId" clearable placeholder="优惠券ID">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-date-picker v-model="dataForm.orderTime" value-format="yyyy-MM-dd" clearable placeholder="下单时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-select v-model="dataForm.orderStatus" clearable placeholder="订单状态">
            <el-option value="1" label="已支付"></el-option>
            <el-option value="2" label="已退单"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-search" type="primary"
                     @click="searchHandle">查询
          </el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="dataList"
        border
        size="small"
        v-loading="dataListLoading"
        @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <el-table-column
          prop="orderNo"
          header-align="center"
          align="center"
          min-width="180"
          label="订单编号">
        </el-table-column>
        <el-table-column
          prop="custId"
          header-align="center"
          align="center"
          label="客户ID">
        </el-table-column>
        <el-table-column
          prop="orderNum"
          header-align="center"
          align="center"
          label="产品数量">
        </el-table-column>
        <el-table-column
          prop="orderMoney"
          header-align="center"
          align="center"
          label="订单金额">
        </el-table-column>
        <el-table-column
          prop="orderDiscount"
          header-align="center"
          align="center"
          label="订单折扣">
          <template slot-scope="scope">
            <div>{{ scope.row.orderDiscount }}折 | {{scope.row.couponId == 0 ? 'VIP' : '折扣券ID: ' + scope.row.couponId}}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="orderPayMoney"
          header-align="center"
          align="center"
          label="实付金额">
        </el-table-column>
        <el-table-column
          prop="orderTime"
          header-align="center"
          align="center"
          min-width="160"
          label="订单时间">
          <template slot-scope="scope">
            <div>{{ scope.row.orderTime|timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="orderStatus"
          header-align="center"
          align="center"
          label="订单状态">
          <template slot-scope="scope">
            <div v-if="scope.row.orderStatus == 2">
              <el-tooltip>
                <div slot="content">
                  <div>操作员工ID：{{scope.row.quitUser}} </div>
                  <div>退单时间：{{scope.row.quitTime | timeStampDateTimeFormat}}</div>
                </div>
                <el-tag size="mini" type="danger">已退单 <i class="el-icon-info"></i></el-tag>
              </el-tooltip>
            </div>
            <div v-else>
              <el-tag size="mini" type="success">已支付</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="salesmanId"
          header-align="center"
          align="center"
          label="业务员ID">
        </el-table-column>
        <el-table-column
          header-align="center"
          align="right"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-switch-button" v-if="isAuth('shop:shoporder:quit') && scope.row.orderStatus == 1" type="text" size="small"
                       @click="quitHandle(scope.row)">退单
            </el-button>
            <el-button icon="el-icon-view" v-if="isAuth('shop:shoporder:info')" type="text" size="small"
                       @click="addOrUpdateHandle(scope.row.id)">详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="pageIndex"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
    </el-card>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @back="backHandle"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from './shoporder-add-or-update'
import SearchUserInput from "@/components/permission/SearchUserInput";
export default {
  data() {
    return {
      dataForm: {
        orderNo: '',
        custId: '',
        couponId: '',
        orderTime: '',
        orderStatus: null
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false
    }
  },
  components: {
    AddOrUpdate,
    SearchUserInput
  },
  activated() {

  },
  methods: {
    //团队
    teamLoad(status, data){
      if(status){
        this.getDataList()
      }else{
        this.$message({
          showClose: true,
          message: '数据加载失败，请联系管理员！',
          type: 'error'
        });
      }
    },
    searchHandle() {
      this.pageIndex = 1
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/shop/shoporder/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'orderNo': this.dataForm.orderNo,
          'custId': this.dataForm.custId,
          'couponId': this.dataForm.couponId,
          'orderTime': this.dataForm.orderTime,
          'orderStatus': this.dataForm.orderStatus,
          'team':this.$refs.teamSearch.getSeletedVal().toString()
        })
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.page.list
          this.totalPage = data.page.totalCount
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      })
    },
    // 每页数
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageIndex = 1
      this.getDataList()
    },
    // 当前页
    currentChangeHandle(val) {
      this.pageIndex = val
      this.getDataList()
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id)
      })
    },
    // 退单
    quitHandle(item) {
      this.$confirm(`确定对${item.orderNo}进行退单操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/shop/shoporder/quit'),
          method: 'post',
          data: this.$http.adornData(item.id, false)
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.getDataList()
              }
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      })
    },
    backHandle(options) {
      this.addOrUpdateVisible = false
      if (options.refresh) {
        this.getDataList()
      }
    }
  }
}
</script>
