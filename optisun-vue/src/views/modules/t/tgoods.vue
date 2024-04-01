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
          <el-button icon="el-icon-plus" v-if="isAuth('t:tgoods:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>
          <el-button icon="el-icon-delete" v-if="isAuth('t:tgoods:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="dataList"
        size="small"
        v-loading="dataListLoading"
        @selection-change="selectionChangeHandle">
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
          prop="goodsName"
          header-align="center"
          align="center"
          width="200"
          label="商品名称">
        </el-table-column>
        <el-table-column
          prop="price"
          header-align="center"
          align="center"
          width="150"
          label="价格">
        </el-table-column>
          <el-table-column
          prop="estimateprice"
          header-align="center"
          align="center"
          width="120"
          label="预期回报金额">
        </el-table-column>
          <el-table-column
          prop="periodId"
          header-align="center"
          align="center"
          width="100"
          label="周期">
        </el-table-column>
          <el-table-column
          prop="avatar"
          header-align="center"
          align="center"
          width="60"
          label="图片">
            <template slot-scope="scope">
              <el-image :src="scope.row.avatar" style="width: 50px;height: 50px;border-radius: 4px"></el-image>
            </template>
        </el-table-column>
        <el-table-column
          prop="stockNum"
          header-align="center"
          align="center"
          width="100"
          label="库存数量">
        </el-table-column>
<!--          <el-table-column-->
<!--          prop="isVip"-->
<!--          header-align="center"-->
<!--          align="center"-->
<!--          label="VIP">-->
<!--            <template slot-scope="scope">-->
<!--              <el-tag v-if="scope.row.isVip == 1">是</el-tag>-->
<!--              <el-tag type="info" v-else>否</el-tag>-->
<!--            </template>-->
<!--        </el-table-column>-->
          <el-table-column
          prop="createTtime"
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
            <el-button v-if="isAuth('t:tgoods:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
            <el-button v-if="isAuth('t:tgoods:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
  import AddOrUpdate from './tgoods-add-or-update'
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
            { filedCode: 'key', filedName: '关键字', filedType: '1',placeholder:'商品名称'}
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
    filters:{
      timeStampDateTimeFormat
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
          url: this.$http.adornUrl('/t/tgoods/list'),
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
            url: this.$http.adornUrl('/t/tgoods/delete'),
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
