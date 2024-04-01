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
<!--          <el-button icon="el-icon-plus" v-if="isAuth('t:torder:save')" type="primary" @click="addOrUpdateHandle()">新增</el-button>-->
          <el-button icon="el-icon-delete" v-if="isAuth('t:torder:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
            <el-button v-if="isAuth('t:torder:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">详情</el-button>
            <el-button  v-if="isAuth('t:torder:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
            <el-button v-if="scope.row.status != 2 && isAuth('t:torder:handOut')"   type="text" size="small" @click="manualSell(scope.row.id)">手动卖出</el-button>
          </template>
        </el-table-column>
        <el-table-column
          prop="id"
          header-align="center"
          width="120"
          label="订单id"
          align="center">
        </el-table-column>
        <el-table-column
          prop="orderNo"
          header-align="center"
          width="180"
          label="订单号"
          align="center">
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
          width="120"
          label="客户名称">
        </el-table-column>
        <el-table-column
          prop="goodsName"
          header-align="center"
          align="center"
          width="200"
          label="商品名称">
        </el-table-column>
          <el-table-column
          prop="money"
          header-align="center"
          align="center"
          width="150"
          label="付款">
        </el-table-column>
        <el-table-column
          prop="parameter1"
          header-align="center"
          align="center"
          width="120"
          label="佣金比例(%)">
        </el-table-column>
        <el-table-column
          prop="parameter2"
          header-align="center"
          align="center"
          width="80"
          label="佣金">
          <template slot-scope="scope">
            <div>{{scope.row.parameter2}}</div>
          </template>
        </el-table-column>
          <el-table-column
          prop="status"
          header-align="center"
          align="center"
          width="80"
          label="状态">
            <template slot-scope="scope">
              <div v-if="scope.row.status == 0" class="green">刚买入</div>
              <div v-if="scope.row.status == 1" class="yellow">出售中</div>
              <div v-if="scope.row.status == 2" class="red">已出售</div>
            </template>
        </el-table-column>
          <el-table-column
          prop="gradeName"
          header-align="center"
          align="center"
          width="120"
          label="关联等级">
            <template slot-scope="scope">
              <el-tag>{{scope.row.gradeName}}</el-tag>
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
        <el-table-column
          prop="sellTime"
          header-align="center"
          align="center"
          label="销售时间">
          <template slot-scope="scope">
            <div>{{scope.row.sellTime|timeStampDateTimeFormat}}</div>
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
  import AddOrUpdate from './torder-add-or-update'
  import {timeStampDateTimeFormat} from '@/utils/index.js'
  import SearchUserInput from "@/components/permission/SearchUserInput";
  import userAssembly from "@/components/user/user-assembly";
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
            { filedCode: 'id', filedName: '订单ID', filedType: '1'},
            { filedCode: 'custId', filedName: '客户ID', filedType: '1'},
            { filedCode: 'key', filedName: '订单号', filedType: '1',placeholder:'订单号'},
            { filedCode: 'money', filedName: '订单金额', filedType: '1',placeholder:'订单金额'}
          ]
        }
      }
    },
    components: {
      AddOrUpdate,
      SearchUserInput,
      userAssembly
    },
    activated () {
     // this.getDataList()
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
      //手动卖出
      manualSell(id){
        this.$confirm(`确定要手动卖出当前订单吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl(`/t/torder/manualSell`),
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
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/t/torder/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            'id':this.dataForm.id,
            'custId':this.dataForm.custId,
            'money': this.dataForm.money,
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
        let ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/t/torder/delete'),
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
      }
    }
  }
</script>


<style scoped lang="scss">

</style>
