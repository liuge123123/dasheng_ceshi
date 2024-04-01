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
          <el-button icon="el-icon-plus" v-if="isAuth('g:groom:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
          <el-button icon="el-icon-delete" v-if="isAuth('g:groom:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="dataList"
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
          prop="roomName"
          header-align="center"
          align="center"
          mini-width="200"
          label="房间名">
        </el-table-column>
          <el-table-column
          prop="roomDesc"
          header-align="center"
          align="center"
          mini-width="200"
          label="房间简介">
        </el-table-column>
          <el-table-column
          prop="image"
          header-align="center"
          align="center"
          width="70"
          label="封面图">
            <template slot-scope="scope">
              <el-image :src="scope.row.image" style="width: 50px;height: 50px;border-radius: 4px"></el-image>
            </template>
        </el-table-column>
          <el-table-column
          prop="priceBetween"
          header-align="center"
          align="center"
          width="220"
          label="价格区间">
            <template slot-scope="scope">
              <div class="disF">
                <div>{{scope.row.priceStart.toFixed(4)}}</div>
                <div style="margin: 0 5px 0 5px">--</div>
                <div>{{scope.row.priceEnd.toFixed(4)}}</div>
              </div>
            </template>
        </el-table-column>
          <el-table-column
          prop="comRate"
          header-align="center"
          align="center"
          width="100"
          label="佣金比例">
        </el-table-column>
          <el-table-column
          prop="gradeName"
          header-align="center"
          align="center"
          width="140"
          label="关联等级">
        </el-table-column>
          <el-table-column
          prop="isOpen"
          header-align="center"
          align="center"
          width="60"
          label="开启">
            <template slot-scope="scope">
              <div v-if="scope.row.isOpen == 1" class="blue">是</div>
              <div v-if="scope.row.isOpen == 0" class="gray">否</div>
            </template>
        </el-table-column>
          <el-table-column
          prop="createTime"
          width="180"
          header-align="center"
          align="center"
          label="创建时间">
            <template slot-scope="scope">
              <div>{{scope.row.createTime|timeStampDateTimeFormat}}</div>
            </template>
        </el-table-column>
          <el-table-column
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button v-if="isAuth('g:groom:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
            <el-button v-if="isAuth('g:groom:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
  import AddOrUpdate from './groom-add-or-update'
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
            { filedCode: 'key', filedName: '关键字', filedType: '1',placeholder:'房间名'}
          ]
        }
      }
    },
    filters:{
      timeStampDateTimeFormat
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
          url: this.$http.adornUrl('/g/groom/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            'id':this.dataForm.id
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
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/g/groom/delete'),
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
