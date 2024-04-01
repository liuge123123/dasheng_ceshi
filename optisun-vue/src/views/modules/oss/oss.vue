<template>
  <div>
    <el-card class="mod-oss" v-if="!configVisible">
      <el-form :inline="true" :model="dataForm" size="small" class="disSb">
        <el-form-item>
          <el-button icon="el-icon-setting" type="primary" @click="configHandle()">云存储配置</el-button>
          <el-button icon="el-icon-upload" @click="uploadHandle()">上传文件</el-button>
          <el-button icon="el-icon-delete" style="border-color:rgb(245, 74, 69);color: rgb(245, 74, 69) " @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
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
          type="selection"
          header-align="center"
          align="center"
          width="50">
        </el-table-column>
        <el-table-column
          type="index"
          header-align="center"
          align="center"
          width="80"
          label="#">
          <template slot-scope="scope">
            {{ scope.$index + 1 + (pageIndex - 1) * pageSize }}
          </template>
        </el-table-column>
        <el-table-column
          prop="name"
          header-align="center"
          align="left"
          label="名称">
          <template slot-scope="scope">
            <preview :value="scope.row"></preview>
          </template>
        </el-table-column>
        <el-table-column
          prop="size"
          header-align="center"
          align="center"
          label="大小">
          <template slot-scope="scope">
            {{ scope.row.size | sizeTextFilter }}
          </template>
        </el-table-column>
        <el-table-column
          prop="size"
          header-align="center"
          align="center"
          label="时长">
          <template slot-scope="scope">
            {{ scope.row.size | sizeTextFilter }}
          </template>
        </el-table-column>
        <el-table-column
          prop="createDate"
          header-align="center"
          align="center"
          width="180"
          label="创建时间">
        </el-table-column>
        <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-delete" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
    <!-- 弹窗, 云存储配置 -->
    <config v-if="configVisible" ref="config" @back="backHandle"></config>
    <!-- 弹窗, 上传文件 -->
    <upload v-if="uploadVisible" ref="upload" @refreshDataList="getDataList"></upload>
  </div>
</template>

<script>
import Config from './oss-config'
import Upload from './oss-upload'
import Preview from './oss-preview'

export default {
  data() {
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
      configVisible: false,
      uploadVisible: false
    }
  },
  components: {
    Config,
    Upload,
    Preview
  },
  filters: {
    sizeTextFilter(val) {
      if (val < 1024) {
        return val + 'B'
      } else if (val < 1024 * 1024) {
        return (val / 1024).toFixed(2) + 'KB'
      } else if (val < 1024 * 1024 * 1024) {
        return (val / 1024 / 1024).toFixed(2) + 'MB'
      } else if (val < 1024 * 1024 * 1024 * 1024) {
        return (val / 1024 / 1024 / 1024).toFixed(2) + 'GB'
      } else {
        return (val / 1024 / 1024 / 1024 / 1024).toFixed(2) + 'TB'
      }
    }
  },
  activated() {
    this.searchHandle()
  },
  methods: {
    searchHandle(){
      this.pageIndex = 1
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/sys/oss/list'),
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
    // 云存储配置
    configHandle() {
      this.configVisible = true
      this.$nextTick(() => {
        this.$refs.config.init()
      })
    },
    // 上传文件
    uploadHandle() {
      this.uploadVisible = true
      this.$nextTick(() => {
        this.$refs.upload.init()
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
          url: this.$http.adornUrl('/sys/oss/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.$message({
              message: '操作成功',
              type: 'success',
              duration: 1500,
              onClose: () => {
                this.searchHandle()
              }
            })
          } else {
            this.$message.error(data.msg)
          }
        })
      }).catch(() => {
      })
    },
    backHandle(options) {
      this.configVisible = false
      if (options.refresh) {
        this.searchHandle()
      }
    },
  }
}
</script>
