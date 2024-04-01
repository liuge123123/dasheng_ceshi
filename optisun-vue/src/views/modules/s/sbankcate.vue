<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div style="display: flex;margin-bottom: 10px">
        <div style="flex: 1;">
          <el-button icon="el-icon-plus" v-if="isAuth('s:sbankcate:save')" size="small" type="primary" @click="addOrUpdateHandle()">新增</el-button>
        </div>
        <div>
          <el-input v-model="dataForm.key" style="width:200px" size="small" clearable placeholder="请输入关键字">
          </el-input>
          <el-button icon="el-icon-search" type="primary" size="small" @click="getDataList()">查询</el-button>
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
<!--        <el-table-column-->
<!--                type="index"-->
<!--                header-align="center"-->
<!--                label="#"-->
<!--                align="center">-->
<!--          <template slot-scope="scope">-->
<!--            {{pageSize * (pageIndex - 1) + scope.$index + 1}}-->
<!--          </template>-->
<!--        </el-table-column>-->
        <el-table-column
          prop="id"
          header-align="center"
          align="center"
          label="ID">
        </el-table-column>
        <el-table-column
          prop="logo"
          header-align="center"
          align="center"
          label="银行种类图标">
          <template slot-scope="scope">
            <div v-if="scope.row.logo!=''"><el-image :src="scope.row.logo" style="width: 50px; height: 50px"></el-image></div>
          </template>
        </el-table-column>
        <el-table-column
          prop="showName"
          header-align="center"
          align="center"
          label="前端显示名称">
        </el-table-column>
       <el-table-column
          prop="name"
          header-align="center"
          align="center"
          label="卡类别名称">
        </el-table-column>
          <el-table-column
          prop="letter"
          header-align="center"
          align="center"
          label="字母标示">
        </el-table-column>
        <el-table-column
          prop="fee"
          header-align="center"
          align="center"
          label="汇率">
        </el-table-column>
        <el-table-column
          prop="rechargeOpen"
          header-align="center"
          align="center"
          label="开启充值">
          <template slot-scope="scope">
            <div>{{scope.row.rechargeOpen == 1 ? '是' : '否'}}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="cashoutOpen"
          header-align="center"
          align="center"
          label="开启提现">
          <template slot-scope="scope">
            <div>{{scope.row.cashoutOpen == 1 ? '是' : '否'}}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="sort"
          header-align="center"
          align="center"
          label="排序">
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
          prop="updateTime"
          header-align="center"
          align="center"
          label="更新时间">
          <template slot-scope="scope">
            <div>{{scope.row.updateTime | timeStampDateTimeFormat}}</div>
          </template>
        </el-table-column>
          <el-table-column
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-edit" v-if="isAuth('s:sbankcate:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
            <el-button icon="el-icon-delete" v-if="isAuth('s:sbankcate:delete')" type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
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
  import AddOrUpdate from './sbankcate-add-or-update'
  import {timeStampDateTimeFormat} from '@/utils/index'
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
      AddOrUpdate
    },
    activated () {
      this.getDataList()
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
          url: this.$http.adornUrl('/s/sbankcate/list'),
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
            url: this.$http.adornUrl('/s/sbankcate/delete'),
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
