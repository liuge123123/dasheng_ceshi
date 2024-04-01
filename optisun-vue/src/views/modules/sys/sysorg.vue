<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <el-form :inline="true" size="small" class="disSb">
        <el-form-item>
          <el-button v-if="isAuth('sys:sysorg:save')" icon="el-icon-plus" type="primary" @click="addOrUpdateHandle()">新增</el-button>
          <el-button v-if="isAuth('sys:sysorg:delete')" icon="el-icon-delete" style="border-color:rgb(245, 74, 69);color: rgb(245, 74, 69) " @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
        </el-form-item>
        <div>
          <el-form-item>
            <el-input
              placeholder="请输入关键字查询"
              v-model="dataForm.key"
              class="selectBtn" size="small">
              <i
                slot="suffix"
                class="el-icon-search"
                @click="searchHandle"
              ></i>
            </el-input>
          </el-form-item>
        </div>
      </el-form>
      <el-table
        :data="dataList"
        size="small"
        v-loading="dataListLoading"
        @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <el-table-column
          type="index"
          header-align="center"
          align="center"
          width="50">
        </el-table-column>
        <el-table-column
          prop="id"
          header-align="center"
          align="center"
          label="机构编号">
        </el-table-column>
        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="机构名称">
        </el-table-column>
        <el-table-column
          prop="logo"
          header-align="center"
          align="center"
          label="机构logo" width="150">
          <template slot-scope="scope">
            <div class="image-panel">
              <el-image style="width: 48px; height: 48px"
                        fit="cover"
                        :src="scope.row.logo"
                        :preview-src-list="[scope.row.logo]"></el-image>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          header-align="center"
          align="center"
          label="机构状态">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" size="small" type="danger">禁用</el-tag>
            <el-tag v-else size="small">正常</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button v-if="isAuth('sys:sysorg:update')" type="text" icon="el-icon-edit" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
            <el-button v-if="isAuth('sys:sysorg:delete')" type="text" icon="el-icon-delete" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
import AddOrUpdate from './sysorg-add-or-update'
export default {
  data () {
    return {
      dataForm: {
        key: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false
    }
  },
  components: {
    AddOrUpdate
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
        url: this.$http.adornUrl('/sys/sysorg/list'),
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
        return item.id
      })
      this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/sys/sysorg/delete'),
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
