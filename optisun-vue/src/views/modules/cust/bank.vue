<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div style="display: flex;margin-bottom: 10px">
        <div style="flex: 1;">
        </div>
          <div  style="flex: 1;display: flex;justify-content: flex-end;align-items: center">
            <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="searchHandle()"></SearchUserInput>
            <el-input v-model="dataForm.key" style="width:200px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="用户编号、用户名、持卡人、账号">
            </el-input>
          <el-button icon="el-icon-search" type="primary" size="small" @click="searchHandle()">查询</el-button>
        </div>
      </div>
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
                type="index"
                label="#"
                header-align="center"
                align="center">
          <template slot-scope="scope">
            {{pageSize * (pageIndex - 1) + scope.$index + 1}}
          </template>
        </el-table-column>
          <el-table-column
          prop="custId"
          header-align="center"
          align="center"
          label="用户编号">
        </el-table-column>
        <el-table-column
          prop="custName"
          header-align="center"
          align="center"
          label="用户名">
        </el-table-column>
          <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="持卡人">
        </el-table-column>
          <el-table-column
          prop="account"
          header-align="center"
          align="center"
          label="账号">
        </el-table-column>
        <el-table-column
          prop="cateName"
          header-align="center"
          align="center"
          label="银行种类名称">
        </el-table-column>
        <el-table-column
          prop="image"
          header-align="center"
          align="center"
          label="银行种类图标">
            <template slot-scope="scope">
                  <div v-if="scope.row.image!='' && scope.row.image!=null"><el-image :src="scope.row.image" style="width: 50px; height: 50px"></el-image></div>
            </template>
        </el-table-column>
        <el-table-column
          prop="salesmanId"
          header-align="center"
          align="center"
          width="120px"
          label="业务员id">
        </el-table-column>
          <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          label="创建时间">
            <template slot-scope="scope">
              <div>{{scope.row.createTime | timeStampDateTimeFormat}}</div>
            </template>
        </el-table-column>
          <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-delete" v-if="isAuth('cust:bank:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
  import {timeStampDateTimeFormat} from '@/utils/index'
  import AddOrUpdate from './bank-add-or-update'
  import SearchUserInput from "@/components/permission/SearchUserInput";
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
      AddOrUpdate,
      SearchUserInput
    },
    activated () {
      this.getDataList()
    },
    methods: {
      searchHandle(){
        this.pageIndex = 1
        this.getDataList()
      },
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/cust/bank/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            "team":this.$refs.teamSearch.getSeletedVal().toString()
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
            url: this.$http.adornUrl('/cust/bank/delete'),
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
      //团队
      teamLoad(status, data){
        if(status){
          this.searchHandle()
        }else{
          this.$message({
            showClose: true,
            message: '数据加载失败，请联系管理员！',
            type: 'error'
          });
        }
      }
    }
  }
</script>
