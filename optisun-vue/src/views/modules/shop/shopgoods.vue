<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <el-form :inline="true" size="small">
        <el-form-item>
          <el-input v-model="dataForm.name" clearable placeholder="请输入名称">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.category1" clearable placeholder="请输入分类">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.priceStart" clearable placeholder="开始价格">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-input v-model="dataForm.priceEnd" clearable placeholder="结束价格">
          </el-input>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-search"type="primary"@click="searchHandle()">查询</el-button>
        </el-form-item>
        <el-form-item>
          <el-button icon="el-icon-plus" v-if="isAuth('shop:shopgoods:save')"@click="addOrUpdateHandle()">新增</el-button>
          <el-button icon="el-icon-delete" v-if="isAuth('shop:shopgoods:delete')" type="danger" @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除 </el-button>
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
          width="80"
          label="编号">
        </el-table-column>
        <el-table-column
          prop="name"
          header-align="center"
          align="left"
          show-tooltip-when-overflow
          label="名称">
        </el-table-column>
        <el-table-column
          prop="img"
          header-align="center"
          align="center"
          label="图片"
          width="120">
          <template v-slot="scope">
            <el-image fit="contain" :src="scope.row.img" :preview-src-list="[scope.row.img]" style="width: 40px; height: 40px;"></el-image>
          </template>
        </el-table-column>
        <el-table-column
          prop="price"
          header-align="center"
          align="center"
          width="120"
          label="价格">
        </el-table-column>
        <el-table-column
          prop="category1"
          header-align="center"
          align="center"
          width="160"
          label="大分类">
        </el-table-column>
        <el-table-column
          prop="outLink"
          header-align="center"
          align="center"
          width="80"
          label="外部链接">
          <template v-slot="scope">
            <a target="_blank" :href="scope.row.outLink">查看</a>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          header-align="center"
          align="center"
          width="80"
          label="状态">
          <template v-slot="scope">
            <el-tag v-if="scope.row.status == 1" type="success" size="mini">上架</el-tag>
            <el-tag v-else type="danger" size="mini">下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-edit" v-if="isAuth('shop:shopgoods:update')" type="text" size="small"
                       @click="addOrUpdateHandle(scope.row.id)">修改
            </el-button>
            <el-button icon="el-icon-delete" v-if="isAuth('shop:shopgoods:delete')" type="text" size="small"
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
import AddOrUpdate from './shopgoods-add-or-update'

export default {
  data() {
    return {
      dataForm: {
        name: '',
        category1: '',
        priceStart: null,
        priceEnd: null,
        source: ''
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
    AddOrUpdate
  },
  activated() {
    this.getDataList()
  },
  methods: {
    searchHandle() {
      this.pageIndex = 1
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/shop/shopgoods/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'name': this.dataForm.name,
          'category1': this.dataForm.category1,
          'priceStart': this.dataForm.priceStart,
          'priceEnd': this.dataForm.priceEnd,
          'source': this.dataForm.source
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
          url: this.$http.adornUrl('/shop/shopgoods/delete'),
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
