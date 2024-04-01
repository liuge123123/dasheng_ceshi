<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div style="display: flex;margin-bottom: 10px">
        <div style="flex: 1;">
          <el-button icon="el-icon-plus" v-if="isAuth('content:article:save')" type="primary" size="small"
                     @click="addOrUpdateHandle()">新增
          </el-button>
        </div>
        <div>
          <el-input v-model="dataForm.key" style="width:200px" size="small" clearable placeholder="标题">
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
          header-align="center"
          align="center">
          <template slot-scope="scope">
            {{ pageSize * (pageIndex - 1) + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column
          prop="title"
          header-align="center"
          align="center"
          label="标题">
        </el-table-column>
        <el-table-column
          prop="status"
          header-align="center"
          align="center"
          label="状态">
          <template slot-scope="scope">
            <el-tag  v-if="scope.row.status==1" type="success" effect="dark">已上架</el-tag>
            <el-tag v-else type="info" effect="dark">已下架</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="sort"
          header-align="center"
          align="center"
          label="排序">
        </el-table-column>
        <el-table-column
          prop="positionCode"
          header-align="center"
          align="center"
          label="位置编码">
        </el-table-column>
        <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          label="建立时间">
          <template slot-scope="scope">
            <div>{{scope.row.createTime | timeStampDateTimeFormat}}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="updatetime"
          header-align="center"
          align="center"
          label="修改时间">
          <template slot-scope="scope">
            <div>{{scope.row.updateTime | timeStampDateTimeFormat}}</div>
          </template>
        </el-table-column>
        <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-edit" v-if="isAuth('content:article:update')" type="text" size="small"
                       @click="addOrUpdateHandle(scope.row.articleId)">修改
            </el-button>
            <el-button icon="el-icon-delete" v-if="isAuth('content:article:delete')" type="text" size="small"
                       @click="deleteHandle(scope.row.articleId)">删除
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
import AddOrUpdate from './article-add-or-update'
import {timeStampDateTimeFormat} from '@/utils/index'
export default {
  data() {
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
          {filedCode: 'key', filedName: '关键字', filedType: '1'}
        ]
      }
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
        url: this.$http.adornUrl('/content/article/list'),
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
        return item.articleId
      })
      this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/content/article/delete'),
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
