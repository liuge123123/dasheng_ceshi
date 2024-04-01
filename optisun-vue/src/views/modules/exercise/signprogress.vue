<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div class="cpt">查询条件</div>
      <div style="display: flex;justify-content: flex-start;">
        <el-form label-width="80px" ref="searchForm" size="small">
          <el-form-item> <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="getDataList"></SearchUserInput></el-form-item>
        </el-form>
        <search-form :options="searchFormOptions" @search="searchHandle"></search-form>
      </div>
      <div class="cpt">
        结果一览
      </div>
      <el-form :inline="true" size="small">
        <el-form-item>
          <el-button icon="el-icon-delete" v-if="isAuth('exercise:signprogress:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
          prop="custId"
          header-align="center"
          align="center"
          label="客户ID">
        </el-table-column>
          <el-table-column
          prop="custName"
          header-align="center"
          align="center"
          label="客户名称">
        </el-table-column>
          <el-table-column
          prop="productId"
          header-align="center"
          align="center"
          label="签到产品ID">
        </el-table-column>
          <el-table-column
          prop="productName"
          header-align="center"
          align="center"
          label="签到产品名称">
        </el-table-column>
        <el-table-column
          prop="productImg"
          header-align="center"
          align="center"
          label="签到产品图片">
          <template slot-scope="scope">
            <el-image :src="scope.row.productImg" style="width: 50px;height: 50px;border-radius: 4px"></el-image>
          </template>
        </el-table-column>
          <el-table-column
          prop="productPrice"
          header-align="center"
          align="center"
          label="每次消耗积分">
        </el-table-column>
          <el-table-column
          prop="signNum"
          header-align="center"
          align="center"
          label="已兑换次数">
        </el-table-column>
          <el-table-column
          prop="signProgress"
          header-align="center"
          align="center"
          label="已兑换进度">
        </el-table-column>
          <el-table-column
          prop="lastTime"
          header-align="center"
          align="center"
          width="160px"
          label="最后一次兑换时间">
            <template slot-scope="scope">
              <div>{{ scope.row.lastTime|timeStampDateTimeFormat }}</div>
            </template>
        </el-table-column>
        <el-table-column
          prop="nextComplete"
          header-align="center"
          align="center"
          label="下次是否兑换完成">
          <template slot-scope="scope">
            <el-switch
              @change="switchChange(scope.row)"
              v-model="scope.row.nextComplete"
              active-color="#13ce66"
              inactive-color="#ff4949"
              :active-value="1"
              :inactive-value="0"
              :disabled="scope.row.signProgress=='1000000'">
            </el-switch>
          </template>
        </el-table-column>
<!--          <el-table-column-->
<!--          fixed="right"-->
<!--          header-align="center"-->
<!--          align="center"-->
<!--          width="150"-->
<!--          label="操作">-->
<!--          <template slot-scope="scope">-->
<!--            <el-button icon="el-icon-delete" v-if="isAuth('exercise:signprogress:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>-->
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
   import SearchUserInput from "@/components/permission/SearchUserInput";
  import AddOrUpdate from './signprogress-add-or-update'
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
            { filedCode: 'id', filedName: 'ID', filedType: '1',placeholder:'客户ID'},
            { filedCode: 'key', filedName: '关键字', filedType: '1',placeholder:'客户名称、产品名称'}
          ]
        }
      }
    },
    components: {
      AddOrUpdate,
      SearchUserInput
    },
    activated () {

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
          url: this.$http.adornUrl('/exercise/signprogress/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            'id':this.dataForm.id,
            "team": this.$refs.teamSearch.getSeletedVal().toString()
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
            url: this.$http.adornUrl('/exercise/signprogress/delete'),
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
      },
      switchChange(val){
        this.$http({
          url: this.$http.adornUrl(`/exercise/signprogress/nextComplateChange`),
          method: 'post',
          data: this.$http.adornData({
            'id': val.id,
            'nextComplete': val.nextComplete,
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
              this.getDataList()
          }
          else{
            this.$message.error(data.msg)
          }
        })
      },
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
    }
  }
</script>
