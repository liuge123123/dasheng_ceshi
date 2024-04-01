<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div class="cpt">查询条件</div>
      <search-form :options="searchFormOptions" @search="searchHandle"></search-form>
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
          prop="id"
          header-align="center"
          width="120"
          label="ID"
          align="center">
        </el-table-column>
        <el-table-column
          prop="userName"
          header-align="center"
          align="center"
          label="用户名称">
          <template slot-scope="scope">
            {{scope.row.userId}} - {{scope.row.userName}}
          </template>
        </el-table-column>
        <el-table-column
          prop="drawTime"
          header-align="center"
          align="center"
          label="抽奖时间">
        </el-table-column>
<!--        <el-table-column-->
<!--          prop="isHit"-->
<!--          header-align="center"-->
<!--          align="center"-->
<!--          label="是否中奖">-->
<!--        </el-table-column>-->
        <el-table-column
          prop="hitPrize"
          header-align="center"
          align="center"
          label="奖品名称">
        </el-table-column>
        <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          label="创建时间">
          <template slot-scope="scope">
            <div>{{ scope.row.createTime|timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <!--          <el-table-column-->
        <!--          fixed="right"-->
        <!--          header-align="center"-->
        <!--          align="center"-->
        <!--          width="150"-->
        <!--          label="操作">-->
        <!--          <template slot-scope="scope">-->
        <!--            <el-button v-if="isAuth('exercise:record:detail')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">详情</el-button>-->
        <!--          </template>-->
        <!--        </el-table-column>-->
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
import AddOrUpdate from './record-add-or-update'
import {timeStampDateTimeFormat} from '@/utils/index.js'

export default {
  data() {
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
          {filedCode: 'userId', filedName: '客户ID', filedType: '1'},
          {filedCode: 'hitPrize', filedName: '奖品名称', filedType: '1', placeholder: '中奖奖品名称'}
        ]
      }
    }
  },
  components: {
    AddOrUpdate
  },
  filters: {
    timeStampDateTimeFormat
  },
  activated() {
    this.getDataList()
  },
  methods: {
    searchHandle(val) {
      this.dataForm = val
      this.pageIndex = 1
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/exercise/record/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'userId': this.dataForm.userId,
          'hitPrize': this.dataForm.hitPrize
        })
      }).then(({data}) => {
        if (data && data.code === 0) {
          console.log(data)
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
    backHandle(options) {
      this.addOrUpdateVisible = false
      if (options.refresh) {
        this.getDataList()
      }
    }
  }
}
</script>
