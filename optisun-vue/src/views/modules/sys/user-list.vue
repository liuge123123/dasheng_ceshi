<template>
  <div class="mod-config">
    <el-table
      :data="dataList"
      size="small"
      :height="documentClientHeight - 210"
      v-loading="dataListLoading"
      @selection-change="selectionChangeHandle"
      style="width:100%;">
      <el-table-column
        type="index"
        header-align="center"
        align="center"
        width="50"
        label="#">
      </el-table-column>
<!--      <el-table-column-->
<!--        prop="jobNumber"-->
<!--        header-align="center"-->
<!--        align="center"-->
<!--        label="员工工号"-->
<!--        width="80">-->
<!--      </el-table-column>-->
      <el-table-column
        prop="userId"
        header-align="center"
        align="center"
        label="员工编号">
      </el-table-column>
      <el-table-column
        prop="username"
        header-align="center"
        align="center"
        label="员工账号">
      </el-table-column>
      <el-table-column
        prop="name"
        header-align="center"
        align="center"
        label="姓名">
      </el-table-column>
      <el-table-column
        prop="mobile"
        header-align="center"
        align="center"
        label="电话号码">
      </el-table-column>
      <el-table-column prop="type" header-align="center" align="center" label="账号类型">
           <template slot-scope="scope">
             <el-tag v-if="scope.row.type==0">超级管理员</el-tag>
             <el-tag type="success" v-else-if="scope.row.type==1">平台账号</el-tag>
             <el-tag type="info" v-else-if="scope.row.type==2">总代理账号</el-tag>
             <el-tag type="warning" v-else-if="scope.row.type==3">分代理账号</el-tag>
             <el-tag type="danger" v-else-if="scope.row.type==4">普通员工账号</el-tag>
           </template>
      </el-table-column>
      <el-table-column
        prop="relationId"
        header-align="center"
        align="center"
        label="上级ID">
      </el-table-column>
      <el-table-column
        prop="status"
        header-align="center"
        align="center"
        label="状态"
        width="150">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" size="small" type="danger">禁用</el-tag>
          <el-tag v-else size="small">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="createTime"
        header-align="center"
        align="center"
        width="160px"
        label="创建时间">
        <template slot-scope="scope">
          <div>{{scope.row.createTime | timeStampDateTimeFormat}}</div>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        header-align="center"
        align="center"
        width="190"
        label="操作">
        <template slot-scope="scope">
          <el-button v-if="isAuth('sys:user:update')" type="text" size="small" @click="addOrUpdateHandle(scope.row.userId)">修改</el-button>
          <el-button v-if="isAuth('sys:user:delete')" type="text" size="small" @click="deleteHandle(scope.row.userId)">删除</el-button>
          <el-button v-if="isAuth('sys:user:reset')" type="text" size="small" @click="resertHandle(scope.row.userId)">初始化密码</el-button>
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

  </div>
</template>

<script>
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
        addOrUpdateVisible: false
      }
    },
    components: {
    },
    activated () {
    },
    computed: {
      documentClientHeight(){
        return this.$store.state.common.documentClientHeight
      },
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
          url: this.$http.adornUrl('/sys/user/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            'deptId':this.dataForm.categoryId,
            'team':this.dataForm.team
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
        // this.addOrUpdateVisible = true
        // this.$nextTick(() => {
        //   this.$refs.addOrUpdate.init(id)
        // })
        this.$emit('update', {id: id})
      },
      // 删除
      deleteHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.userId
        })
        this.$confirm(`确定对选择项进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/sys/user/delete'),
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
      //初始化密码
      resertHandle(id){
        this.$http({
          url: this.$http.adornUrl("/sys/user/resetPassword"),
          method: "post",
          data: this.$http.adornData(id, false)
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "操作成功，密码成功初始化为123456",
              type: "success",
              duration: 1500,
              onClose: () => {
              },
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      },
    }
  }
</script>
