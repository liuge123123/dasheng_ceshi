<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div class="cpt">查询条件</div>
      <search-form :options="searchFormOptions" @search="searchHandle"></search-form>
      <div class="cpt">
        结果一览
      </div>
      <el-form :inline="true" size="small">
        <el-form-item>
          <el-input v-model="dataForm.key" clearable placeholder="请输入关键字">
            <i slot="suffix" class="el-icon-search" @click="searchHandle"></i>
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-plus" v-if="isAuth('cust:taskcommission:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
          <el-button icon="el-icon-delete" v-if="isAuth('cust:taskcommission:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
          type="selection"
          header-align="center"
          align="center"
          width="50">
        </el-table-column>
        <el-table-column
                type="index"
                header-align="center"
                align="center">
          <template slot-scope="scope">
            {{pageSize * (pageIndex - 1) + scope.$index + 1}}
          </template>
        </el-table-column>
          <el-table-column
          prop="orderId"
          header-align="center"
          align="center"
          label="订单ID">
        </el-table-column>
          <el-table-column
          prop="orderTime"
          header-align="center"
          align="center"
          label="订单时间">
        </el-table-column>
          <el-table-column
          prop="orderCust"
          header-align="center"
          align="center"
          label="订单所属客户">
        </el-table-column>
          <el-table-column
          prop="firstCustId"
          header-align="center"
          align="center"
          label="一级客户ID">
        </el-table-column>
          <el-table-column
          prop="firstSalesmanId"
          header-align="center"
          align="center"
          label="一级客户所属业务员">
        </el-table-column>
          <el-table-column
          prop="firstIsNb"
          header-align="center"
          align="center"
          label="一级客户是否为内部">
        </el-table-column>
          <el-table-column
          prop="fisrtMoney"
          header-align="center"
          align="center"
          label="一级客户佣金">
        </el-table-column>
          <el-table-column
          prop="secondCustId"
          header-align="center"
          align="center"
          label="二级客户ID">
        </el-table-column>
          <el-table-column
          prop="secondSalesmanId"
          header-align="center"
          align="center"
          label="二级客户所属业务员">
        </el-table-column>
          <el-table-column
          prop="secondIsNb"
          header-align="center"
          align="center"
          label="二级客户是否为内部">
        </el-table-column>
          <el-table-column
          prop="secondMoney"
          header-align="center"
          align="center"
          label="二级客户佣金">
        </el-table-column>
          <el-table-column
          prop="thirdCustId"
          header-align="center"
          align="center"
          label="3级客户ID">
        </el-table-column>
          <el-table-column
          prop="thirdSalesmanId"
          header-align="center"
          align="center"
          label="3级客户所属业务员">
        </el-table-column>
          <el-table-column
          prop="thirdIsNb"
          header-align="center"
          align="center"
          label="3级客户是否为内部">
        </el-table-column>
          <el-table-column
          prop="thirdMoney"
          header-align="center"
          align="center"
          label="3级客户佣金">
        </el-table-column>
          <el-table-column
          prop="fourCustId"
          header-align="center"
          align="center"
          label="4级客户ID">
        </el-table-column>
          <el-table-column
          prop="fourSalesmanId"
          header-align="center"
          align="center"
          label="4级客户所属业务员">
        </el-table-column>
          <el-table-column
          prop="fourIsNb"
          header-align="center"
          align="center"
          label="4级客户是否为内部">
        </el-table-column>
          <el-table-column
          prop="fourMoney"
          header-align="center"
          align="center"
          label="4级客户佣金">
        </el-table-column>
          <el-table-column
          prop="fiveCustId"
          header-align="center"
          align="center"
          label="5级客户ID">
        </el-table-column>
          <el-table-column
          prop="fiveSalesmanId"
          header-align="center"
          align="center"
          label="5级客户所属业务员">
        </el-table-column>
          <el-table-column
          prop="fiveIsNb"
          header-align="center"
          align="center"
          label="5级客户是否为内部">
        </el-table-column>
          <el-table-column
          prop="fiveMoney"
          header-align="center"
          align="center"
          label="5级客户佣金">
        </el-table-column>
          <el-table-column
          prop="sixCustId"
          header-align="center"
          align="center"
          label="6级客户ID">
        </el-table-column>
          <el-table-column
          prop="sixSalesmanId"
          header-align="center"
          align="center"
          label="6级客户所属业务员">
        </el-table-column>
          <el-table-column
          prop="sixIsNb"
          header-align="center"
          align="center"
          label="6级客户是否为内部">
        </el-table-column>
          <el-table-column
          prop="sixMoney"
          header-align="center"
          align="center"
          label="6级客户佣金">
        </el-table-column>
          <el-table-column
          prop="sevenCustId"
          header-align="center"
          align="center"
          label="7级客户ID">
        </el-table-column>
          <el-table-column
          prop="sevenSalesmanId"
          header-align="center"
          align="center"
          label="7级客户所属业务员">
        </el-table-column>
          <el-table-column
          prop="sevenIsNb"
          header-align="center"
          align="center"
          label="7级客户是否为内部">
        </el-table-column>
          <el-table-column
          prop="sevenMoney"
          header-align="center"
          align="center"
          label="7级客户佣金">
        </el-table-column>
          <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-edit" v-if="isAuth('cust:taskcommission:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.orderId)">修改</el-button>
            <el-button icon="el-icon-delete" v-if="isAuth('cust:taskcommission:delete')" type="text" size="small" @click="deleteHandle(scope.row.orderId)">删除</el-button>
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
  import AddOrUpdate from './taskcommission-add-or-update'
  export default {
    data () {
      return {
        dataForm: {},
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        searchFormOptions: {
          filedList: [
            { filedCode: 'key', filedName: '关键字', filedType: '1'}
          ]
        }
      }
    },
    components: {
      AddOrUpdate
    },
    activated () {
      this.getDataList()
    },
    methods: {
      searchHandle(val){
        this.dataForm = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/cust/taskcommission/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key
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
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageIndex = val
        this.getDataList()
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      // 新增 / 修改
      addOrUpdateHandle (id) {
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id)
        })
      },
      // 删除
      deleteHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.orderId
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/cust/taskcommission/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
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
      backHandle(options){
        this.addOrUpdateVisible = false
        if(options.refresh){
          this.getDataList()
        }
      }
    }
  }
</script>
