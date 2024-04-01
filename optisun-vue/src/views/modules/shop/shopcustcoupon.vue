<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <el-form :inline="true" size="small">
        <el-form-item> <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="getDataList"></SearchUserInput></el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.custId" clearable placeholder="客户ID">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-select v-model="dataForm.useStatus" clearable placeholder="使用状态">
            <el-option value="" label="全部"></el-option>
            <el-option value="0" label="未使用"></el-option>
            <el-option value="1" label="已使用"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-search" type="primary"
                     @click="searchHandle()">查询
          </el-button>
          <el-button icon="el-icon-plus" v-if="isAuth('shop:shopcustcoupon:save')"
                     @click="addOrUpdateHandle()">新增
          </el-button>
          <el-button icon="el-icon-delete" v-if="isAuth('shop:shopcustcoupon:delete')" type="danger"
                     @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除
          </el-button>
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
          align="center"
          label="券ID">
        </el-table-column>
        <el-table-column
          prop="custId"
          header-align="center"
          align="center"
          label="客户ID">
        </el-table-column>
        <el-table-column
          prop="discount"
          header-align="center"
          align="center"
          label="折扣（折）">
        </el-table-column>
        <el-table-column
          prop="expireTime"
          header-align="center"
          align="center"
          label="过期时间">
          <template slot-scope="scope">
            <div>{{ scope.row.createTime|timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="useStatus"
          header-align="center"
          align="center"
          label="使用状态">
          <template v-slot="scope">
            <el-tag v-if="scope.row.useStatus == 1" type="danger" size="mini">已使用</el-tag>
            <el-tag v-else type="success" size="mini">未使用</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="salesmanId"
          header-align="center"
          align="center"
          label="业务员ID">
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
        <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-edit" v-if="isAuth('shop:shopcustcoupon:update')" type="text" size="small"
                       @click="addOrUpdateHandle(scope.row.id)">修改
            </el-button>
            <el-button icon="el-icon-delete" v-if="isAuth('shop:shopcustcoupon:delete')" type="text" size="small"
                       @click="deleteHandle(scope.row.id)">删除
            </el-button>
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
import AddOrUpdate from './shopcustcoupon-add-or-update'
import SearchUserInput from "@/components/permission/SearchUserInput";

export default {
  data() {
    return {
      dataForm: {
        custId: "",
        useStatus: ""
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
    SearchUserInput
  },
  activated() {
    this.getDataList()
  },
  methods: {
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
    searchHandle() {
      this.pageIndex = 1
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/shop/shopcustcoupon/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'custId': this.dataForm.custId,
          'useStatus': this.dataForm.useStatus,
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
          url: this.$http.adornUrl('/shop/shopcustcoupon/delete'),
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
    backHandle(options) {
      this.addOrUpdateVisible = false
      if (options.refresh) {
        this.getDataList()
      }
    }
  }
}
</script>
