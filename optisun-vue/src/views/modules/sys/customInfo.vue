<template>
  <div>
    <el-card v-if="!addOrUpdateVisible">
      <div style="display: flex;">
        <div style="flex: 1;">
          <el-button icon="el-icon-plus" size="small" type="primary" @click="addOrUpdateHandle()">新增字段</el-button>
        </div>
        <el-form :inline="true" @keyup.enter.native="getDataList()" size="small">
          <el-form-item>
            <el-select v-model="modelId" placeholder="请选择" clearable>
              <el-option label="客户" value="0"></el-option>
              <el-option label="订单" value="1"></el-option>
              <el-option label="产品" value="2"></el-option>
              <el-option label="工单" value="3"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button @click="getDataList()" icon="el-icon-search">查询</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table
        :data="dataList"
        style="width: 100%"
        size="small"
        v-loading="dataListLoading"
        :default-sort="{prop: 'filedName', order: 'descending'}">
        <el-table-column align="center" label="序号" width="100" type="index" :index="indexMethod">
        </el-table-column>
        <el-table-column
          prop="filedCode"
          label="字段编码"
          width="120"
          header-align="center"
          align="center"
          sortable>
        </el-table-column>
        <el-table-column
          prop="filedName"
          label="字段名称"
          min-width="180"
          header-align="center"
          align="center"
          sortable>
        </el-table-column>
        <el-table-column
          prop="modelId"
          label="字段场景"
          min-width="200"
          header-align="center"
          align="center">
          <template slot-scope="scope">
            <mutil-tag :data="scope.row.modelId" :labels="filedModels"></mutil-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="filedType"
          label="字段类型"
          header-align="center"
          align="center"
          width="180">
          <template slot-scope="scope">
            <el-tag type="success">{{scope.row.filedType | statusFilter}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="sysFiled"
          label="系统字段"
          width="150"
          header-align="center"
          align="center">
          <template slot-scope="scope">
            <span :style="{color: scope.row.sysFiled == 1 ? '#67C23A' : '#E6A23C'}">{{scope.row.sysFiled ==  1 ? '系统字段' : '自定义字段'}}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="userName"
          label="建立人"
          width="180">
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="建立时间"
          width="160"
          header-align="center"
          align="center"
          sortable>
        </el-table-column>
        <el-table-column
          label="操作"
          width="120"
          header-align="center"
          align="center">
          <template slot-scope="scope">
            <el-button v-if="isAuth('sys:scustomfield:update')" type="text" size="small"
                       @click="addOrUpdateHandle(scope.row.filedId)">编辑
            </el-button>
            <el-button v-if="isAuth('sys:scustomfield:delete')" type="text" size="small"
                       @click="deleteHandle(scope.row.filedId)">删除
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
        :total="totalPage" :size="small"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
    </el-card>
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @back="backHandle"></add-or-update>
  </div>
</template>
<script>
  import AddOrUpdate from './custominfo-add-or-update'
  import MutilTag from "@/components/mutil-tag/MutilTag";
  export default {
    data() {
      return {
        modelId: '',
        small: '',
        addOrUpdateVisible: false,
        dataList: [],
        dataListLoading: false,
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        filedModels: {
          '0': '客户',
          '1': '订单',
          '2': '产品',
           '3': '工单'
        }
      }
    },
    components: {
      AddOrUpdate,
      MutilTag
    },
    created() {
      this.getDataList()
    },
    filters: {
      statusFilter(status) {
        const statusMap = {
          '1': "文本",
          '2': "数字",
          '3': "日期",
          '4': "文本域",
          '5': "富文本",
          '6': "单选",
          '7': "多选",
          '8': "图片",
          '9': "文件",
          '10': '下拉框',
          '11': '关联工单分类',
          // '12': '关联座席组用户',
          '13': '关联字典',
          // '14': '关联用户',
          // '15': '关联客户标签',
          // '16': '关联咨询分类',
          '17':'区域',
          '18':'选择客户'
        };
        return statusMap[status];
      }
    },
    methods: {
      indexMethod(index) {

        return (index + this.pageSize * (this.pageIndex - 1))
      },

      // 获取数据列表
      getDataList() {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/servicesys/scustomfield/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'modelId': this.modelId
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
      addOrUpdateHandle(id) {
        console.log(id);
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id)
        })
      },
      backHandle(options) {
        this.addOrUpdateVisible = false
        if (options.refresh) {
          this.getDataList()
        }
      },
      // 删除
      deleteHandle(id) {
        this.$confirm('确定进行删除操作?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/servicesys/scustomfield/update'),
            method: 'post',
            data: this.$http.adornData({
              'filedId': id,
              'updateBy': this.$store.state.user.id,
              'delFlag': 1
            })
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
      }
    }
  }
</script>
<style scoped>
  .site-content__wrapper {
    background: rgb(255, 255, 255);
  }
  .tips {
    width: 60%;
    margin: 0 auto;
  }
  .u-title {
    display: inline-block;
    vertical-align: middle;
    border: 0;
    padding-top: 0;
    padding-bottom: 10px;
    font-size: 14px;
    color: #37474f;
  }

  .u-title .summary {
    font-size: 14px;
    line-height: 1.5;
    color: #666;
  }

  .u-ipt {
    width: 340px;
    padding: 10px;
    height: 40px;
    line-height: 20px;
    box-sizing: border-box;
    border-radius: 2px;
    border: 1px solid #e6eaeb;
    color: #37474f;
  }

  .add-button {
    position: absolute;
    right: 180px;
    top: 58px;
  }
</style>
