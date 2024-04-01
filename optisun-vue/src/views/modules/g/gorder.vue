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
<!--          <el-button icon="el-icon-plus" v-if="isAuth('g:gorder:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>-->
          <el-button icon="el-icon-delete" v-if="isAuth('g:gorder:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
<!--            <el-button  v-if="isAuth('g:gorder:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">详情</el-button>-->
            <el-button v-if="isAuth('g:gorder:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
<!--            <el-button v-if="scope.row.status == 1 && isAuth('g:gorder:handOut')"   type="text" size="small" @click="manualSell(scope.row.id)">手动卖出</el-button>-->
          </template>
        </el-table-column>
        <el-table-column
          prop="id"
          header-align="center"
          width="120"
          label="ID"
          align="center">
        </el-table-column>
          <el-table-column
          prop="orderNo"
          header-align="center"
          align="center"
          width="170"
          label="订单号">
        </el-table-column>
        <el-table-column
          prop="custId"
          header-align="center"
          align="center"
          width="120"
          label="客户ID">
        </el-table-column>
          <el-table-column
          prop="custName"
          header-align="center"
          align="center"
          min-width="160"
          label="客户名称">
        </el-table-column>
          <el-table-column
          prop="goodsName"
          header-align="center"
          align="center"
          min-width="200"
          label="商品">
        </el-table-column>
        <el-table-column
          prop="level"
          header-align="center"
          align="center"
          min-width="200"
          label="等级">
          <template slot-scope="scope">
            <el-tag size="mini" type="success" effect="dark" >{{ scope.row.level|levelFilter }}</el-tag>
          </template>
        </el-table-column>
       <el-table-column
          prop="commission"
          header-align="center"
          align="center"
          width="120"
          label="佣金">
        </el-table-column>
          <el-table-column
          prop="status"
          header-align="center"
          align="center"
          width="100"
          label="状态">
            <template slot-scope="scope">
              <div v-if="scope.row.status == 1" class="yellow">待处理</div>
              <div v-if="scope.row.status == 2" class="blue">已完成</div>
            </template>
        </el-table-column>
        <el-table-column
          prop="salesmanId"
          header-align="center"
          align="center"
          width="150px"
          label="业务员">
          <template slot-scope="scope">
            <userAssembly :value="scope.row.salesmanId"></userAssembly>
          </template>
        </el-table-column>
          <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          width="180"
          label="创建时间">
            <template slot-scope="scope">
              <div>{{scope.row.createTime|timeStampDateTimeFormat}}</div>
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
  import AddOrUpdate from './gorder-add-or-update'
  import {timeStampDateTimeFormat} from '@/utils/index.js'
  import SearchUserInput from "@/components/permission/SearchUserInput";
  import userAssembly from "../../../components/user/user-assembly";
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
            { filedCode: 'custId', filedName: '客户ID', filedType: '1'},
            { filedCode: 'key', filedName: '订单号', filedType: '1',placeholder:'订单号'},
            { filedCode: 'commission', filedName: '佣金金额', filedType: '1',placeholder:'佣金金额'}
          ]
        }
      }
    },
    components: {
      AddOrUpdate,
      SearchUserInput,
      userAssembly
    },
    filters:{
      timeStampDateTimeFormat,
      levelFilter (status) {
        //类型：1:系统赠送；2系统扣减;3签到赠送；4首充赠送
        const statusMap = {
          1: 'vip0',
          2: 'vip1',
          3:'vip2',
          4:'vip3',
          5:'vip4',
          6:'vip5',
          7:'vip6',
          8:'vip7'
        }
        return statusMap[status]
      }
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
          url: this.$http.adornUrl('/g/gorder/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            'id':this.dataForm.id,
            'custId':this.dataForm.custId,
            'commission': this.dataForm.commission,
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
            url: this.$http.adornUrl('/g/gorder/delete'),
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
          this.getDataList()
        }else{
          this.$message({
            showClose: true,
            message: '数据加载失败，请联系管理员！',
            type: 'error'
          });
        }
      },
      //手动卖出
      manualSell(id){
        this.$confirm(`确定要手动卖出当前订单吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl(`/g/gorder/manualSell`),
            method: 'post',
            params: this.$http.adornParams({
              'id':id
            })
          }).then(({data}) => {
            this.loading = false
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
    }
  }
</script>
