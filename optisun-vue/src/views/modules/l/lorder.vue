<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <el-form :inline="true" size="small">
        <el-form-item>
          <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="getDataList"></SearchUserInput>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.custId" clearable placeholder="客户ID">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.goodsName" clearable placeholder="产品名称">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.goodsCycle" clearable placeholder="产品周期">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="dataForm.orderStatus" clearable placeholder="订单状态">
            <el-option value="" label="全部"></el-option>
            <el-option value="1" label="收益中"></el-option>
            <el-option value="2" label="已完成"></el-option>
            <el-option value="4" label="退单中"></el-option>
            <el-option value="5" label="退单完成"></el-option>
            <el-option value="6" label="暂停收益"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.createTime" clearable placeholder="下单时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.expireTime" clearable placeholder="到期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-search" @click="searchHandle">查询</el-button>
        </el-form-item>

        <el-form-item>
          <el-button icon="el-icon-lock" v-if="isAuth('l:lorder:buy')" type="primary" @click="addOrUpdateHandle()">锁单
          </el-button>
          <!--          <el-button icon="el-icon-delete" v-if="isAuth('l:lorder:delete')" type="danger" @click="deleteHandle()"-->
          <!--                     :disabled="dataListSelections.length <= 0">批量删除-->
          <!--          </el-button>-->
        </el-form-item>
      </el-form>
      <el-table :data="dataList" border size="small" v-loading="dataListLoading" @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <!--        <el-table-column-->
        <!--          type="selection"-->
        <!--          header-align="center"-->
        <!--          align="center"-->
        <!--          width="50">-->
        <!--        </el-table-column>-->
        <!-- <el-table-column
          fixed="left"
          type="index"
          label="#"
          header-align="center"
          align="center">
          <template slot-scope="scope">
            {{ pageSize * (pageIndex - 1) + scope.$index + 1 }}
          </template>
        </el-table-column> -->
        <el-table-column fixed="left" header-align="center" align="center" width="150" label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-switch-button" v-if="scope.row.orderStatus == 1 && isAuth('l:lorder:quit')"
              type="text" size="small" @click="quitHandle(scope.row)">退单
            </el-button>
            <el-button icon="el-icon-switch-button" v-if="scope.row.orderStatus == 1 && isAuth('l:lorder:pasue')"
              type="text" size="small" @click="pasueHandle(scope.row)">暂停收益
            </el-button>
            <el-button icon="el-icon-switch-button" v-if="scope.row.orderStatus == 6 && isAuth('l:lorder:open')"
              type="text" size="small" @click="openHandle(scope.row)">开启收益
            </el-button>
          </template>
        </el-table-column>
        <el-table-column fixed="left" prop="id" header-align="center" align="center" label="ID">
        </el-table-column>
        <el-table-column fixed="left" prop="custId" header-align="center" align="center" width="120" label="客户ID">
        </el-table-column>
        <el-table-column fixed="left" prop="custName" header-align="center" align="center" width="120" label="客户名称">
        </el-table-column>
        <el-table-column fixed="left" prop="salesmanId" header-align="center" align="center" width="120" label="业务员">
          <template slot-scope="scope">
            <userAssembly :value="scope.row.salesmanId"></userAssembly>
          </template>
        </el-table-column>
        <el-table-column prop="goodsName" header-align="center" align="center" width="150" label="产品名称">
        </el-table-column>
        <el-table-column prop="goodsPrice" header-align="center" align="center" width="120" label="价格 CFA">
        </el-table-column>
        <el-table-column prop="goodsRate" header-align="center" align="center" width="120" label="日收益 CFA">
          <template slot-scope="scope">
            {{ scope.row.goodsIncomeDay}}
          </template>
        </el-table-column>
        <el-table-column prop="goodsCycle" header-align="center" align="center" width="120" label="产品周期 天">
        </el-table-column>
        <el-table-column prop="goodsPrice" header-align="center" align="center" width="120" label="预期总收益 CFA">
          <template slot-scope="scope">
            {{ scope.row.goodsIncomeDay * scope.row.goodsCycle}}
          </template>
        </el-table-column>
        <el-table-column prop="receiveTime" header-align="center" align="center" width="150" label="领取时间">
          <template slot-scope="scope">
            <div>{{ scope.row.receiveTime | timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="receiveProfit" header-align="center" align="center" width="120" label="已领取收益 CFA">
        </el-table-column>
        <el-table-column prop="goodsIsDay" header-align="center" align="center" width="120" label="是否日反">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary" v-if="scope.row.goodsIsDay == 0">否</el-tag>
            <el-tag size="mini" type="success" v-if="scope.row.goodsIsDay == 1">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="goodsIsGive" header-align="center" align="center" width="120" label="是否赠送">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary" v-if="scope.row.goodsIsGive == 0">否</el-tag>
            <el-tag size="mini" type="success" v-if="scope.row.goodsIsGive == 1">是</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="goodsIsCapitalReturn" header-align="center" align="center" width="120" label="是否到期反本">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary" v-if="scope.row.goodsIsCapitalReturn == 0">否</el-tag>
            <el-tag size="mini" type="success" v-if="scope.row.goodsIsCapitalReturn == 1">是</el-tag>
          </template>
        </el-table-column>
        <!-- <el-table-column
          prop="totalProfit"
          header-align="center"
          align="center"
          width="120"
          label="已产生收益 CFA">
        </el-table-column> -->
        <el-table-column prop="orderStatus" header-align="center" align="center" width="120" label="订单状态">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary" v-if="scope.row.orderStatus == 1">收益中</el-tag>
            <el-tag size="mini" type="success" v-if="scope.row.orderStatus == 2">已完成</el-tag>
            <el-tag size="mini" type="warning" v-if="scope.row.orderStatus == 3">结算中</el-tag>
            <el-tag size="mini" type="warning" v-if="scope.row.orderStatus == 4">退单中</el-tag>
            <el-tag size="mini" type="danger" v-if="scope.row.orderStatus == 5">退单完成</el-tag>
            <el-tag size="mini" type="danger" v-if="scope.row.orderStatus == 6">暂停收益</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" header-align="center" align="center" width="150" label="下单时间">
          <template slot-scope="scope">
            <div>{{ scope.row.createTime | timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" header-align="center" align="center" width="150" label="到期时间">
          <template slot-scope="scope">
            <div>{{ scope.row.expireTime | timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="doneTime" header-align="center" align="center" width="150" label="结算时间">
          <template slot-scope="scope">
            <div>{{ scope.row.doneTime | timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>

        <!-- <el-table-column
          prop="cancelTime"
          header-align="center"
          align="center"
          width="150"
          label="退单时间">
          <template slot-scope="scope">
            <div>{{scope.row.cancelTime|timeStampDateTimeFormat}}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="lockTime"
          header-align="center"
          align="center"
          width="150"
          label="退单解冻时间">
          <template slot-scope="scope">
            <div>{{scope.row.lockTime|timeStampDateTimeFormat}}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="cancelRemark"
          header-align="center"
          align="center"
          show-overflow-tooltip
          width="200"
          label="退单说明">
        </el-table-column> -->
      </el-table>
      <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex"
        :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
    </el-card>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @back="backHandle"></add-or-update>

    <lorder-quit ref="lorderQuitRef" @back="backHandle"></lorder-quit>
  </div>
</template>

<script>
import AddOrUpdate from './lorder-add-or-update'
import SearchUserInput from "@/components/permission/SearchUserInput";
import userAssembly from "@/components/user/user-assembly";
import LorderQuit from "./lorder-quit";
export default {
  data() {
    return {
      dataForm: {
        custId: '',
        goodsName: '',
        goodsCycle: '',
        orderStatus: '',
        expireTime: '',
        createTime: '',
        receiveTime: '',
        receiveProfit: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
    }
  },
  components: {
    AddOrUpdate,
    SearchUserInput,
    userAssembly,
    LorderQuit
  },
  activated() {
    // this.getDataList()
  },
  methods: {
    //团队
    teamLoad(status, data) {
      if (status) {
        this.getDataList()
      } else {
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
        url: this.$http.adornUrl('/l/lorder/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'custId': this.dataForm.custId,
          'goodsName': this.dataForm.goodsName,
          'goodsCycle': this.dataForm.goodsCycle,
          'orderStatus': this.dataForm.orderStatus,
          'createTime': this.dataForm.createTime,
          'expireTime': this.dataForm.expireTime,
          "team": this.$refs.teamSearch.getSeletedVal().toString()
        })
      }).then(({ data }) => {
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
    // 删除
    deleteHandle(id) {
      var ids = id ? [id] : this.dataListSelections.map(item => {
        return item.id
      })
      this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/l/lorder/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({ data }) => {
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
    },
    quitHandle(order) {
      this.$refs.lorderQuitRef.init(order.id)
    },
    pasueHandle(order) {
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/l/lorder/pasue"),
        method: "post",
        data: this.$http.adornData(order.id, false)
      }).then(({ data }) => {
        this.loading = false
        if (data && data.code === 0) {
          this.visible = false
          this.$message({
            message: "操作成功",
            type: "success",
            duration: 1000,
            onClose: () => {
              this.getDataList()
            },
          });
        } else {
          this.$message.error(data.msg);
        }
      })
    },
    openHandle(order) {
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/l/lorder/open"),
        method: "post",
        data: this.$http.adornData(order.id, false)
      }).then(({ data }) => {
        this.loading = false
        if (data && data.code === 0) {
          this.visible = false
          this.$message({
            message: "操作成功",
            type: "success",
            duration: 1000,
            onClose: () => {
              this.getDataList()
            },
          });
        } else {
          this.$message.error(data.msg);
        }
      })
    }
  }
}
</script>
