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
          <el-button icon="el-icon-plus" v-if="isAuth('exercise:prize:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
          <el-button icon="el-icon-delete" v-if="isAuth('exercise:prize:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
          prop="id"
          header-align="center"
          width="120"
          label="ID"
          align="center">
        </el-table-column>
          <el-table-column
          prop="prizeType"
          header-align="center"
          align="center"
          label="奖品类型">
        </el-table-column>
          <el-table-column
          prop="prizeName"
          header-align="center"
          align="center"
          label="奖品名称">
        </el-table-column>
          <el-table-column
          prop="prizeValue"
          header-align="center"
          align="center"
          label="奖品价值">
        </el-table-column>
        <el-table-column
          prop="prizeNum"
          header-align="center"
          align="center"
          label="奖品数量">
        </el-table-column>
        <el-table-column
          prop="ratio"
          header-align="center"
          align="center"
          label="中奖几率">
        </el-table-column>
          <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          label="创建时间">
            <template slot-scope="scope">
              <div>{{scope.row.createTime|timeStampDateTimeFormat}}</div>
            </template>
        </el-table-column>
          <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button v-if="isAuth('g:ggoods:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
            <el-button v-if="isAuth('g:ggoods:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
  import AddOrUpdate from './prize-add-or-update'
  import {timeStampDateTimeFormat} from '@/utils/index.js'
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
            { filedCode: 'id', filedName: 'ID', filedType: '1'},
            { filedCode: 'key', filedName: '关键字', filedType: '1',placeholder:'奖品名称'}
          ]
        }
      }
    },
    components: {
      AddOrUpdate
    },
    filters:{
      timeStampDateTimeFormat
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
          url: this.$http.adornUrl('/exercise/prize/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            'id':this.dataForm.id
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
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/exercise/prize/delete'),
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
