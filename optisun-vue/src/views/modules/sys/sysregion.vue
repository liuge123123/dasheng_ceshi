<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <el-form
        :inline="true"
        :model="dataForm"
        size="small"
        class="disSb">
        <el-form-item>
          <el-button v-if="isAuth('sys:sysregion:save')"  icon="el-icon-plus"  size="small" type="primary" @click="addOrUpdateHandle()">新增区域</el-button>
        </el-form-item>
        <div>
          <el-form-item>
            <el-input
              placeholder="请输入关键字"
              v-model="dataForm.keyword"
              class="selectBtn" size="small">
              <el-button
                slot="append"
                icon="el-icon-search"
                @click="searchHandle"
              ></el-button>
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
          prop="id"
          header-align="center"
          align="center"
          label="ID">
        </el-table-column>
        <el-table-column
          prop="pid"
          header-align="center"
          align="center"
          label="父id">
        </el-table-column>
        <el-table-column
          prop="shortname"
          header-align="center"
          align="center"
          label="简称">
        </el-table-column>
        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="名称">
        </el-table-column>
        <el-table-column
          prop="mergerName"
          header-align="center"
          align="center"
          label="全称">
        </el-table-column>
        <el-table-column
          prop="level"
          header-align="center"
          align="center"
          label="层级">
        </el-table-column>
        <el-table-column
          prop="pinyin"
          header-align="center"
          align="center"
          label="拼音">
        </el-table-column>
        <el-table-column
          prop="code"
          header-align="center"
          align="center"
          label="长途区号">
        </el-table-column>
        <el-table-column
          prop="zipCode"
          header-align="center"
          align="center"
          label="邮编">
        </el-table-column>
        <el-table-column
          prop="first"
          header-align="center"
          align="center"
          label="首字母">
        </el-table-column>
        <el-table-column
          prop="lng"
          header-align="center"
          align="center"
          label="经度">
        </el-table-column>
        <el-table-column
          prop="lat"
          header-align="center"
          align="center"
          label="纬度">
        </el-table-column>
        <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button v-if="isAuth('sys:sysregion:update')" type="text" size="small"
                       @click="addOrUpdateHandle(scope.row.id)">修改
            </el-button>
            <el-button v-if="isAuth('sys:sysregion:delete')" type="text" size="small"
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
  import AddOrUpdate from './sysregion-add-or-update'

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
      searchHandle(val) {
        this.dataForm = val
        this.pageIndex = 1
        this.getDataList()
      },
      // 获取数据列表
      getDataList() {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/sys/sysregion/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.keyword
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
            url: this.$http.adornUrl('/sys/sysregion/delete'),
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
