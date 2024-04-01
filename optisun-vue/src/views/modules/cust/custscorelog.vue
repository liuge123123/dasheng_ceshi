<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <el-alert style="margin-bottom: 10px" type="warning" description="由于明细数据量巨大，现调整为必须输入用户才可以查询该用户的账户明细" />
      <div style="display: flex;margin-bottom: 10px;">
        <div>
        </div>
        <div  style="flex: 1;display: flex;justify-content: flex-end;align-items: center">
          <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="searchHandle()"></SearchUserInput>
          <el-input v-model="dataForm.key" clearable style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="用户ID">
          </el-input>
          <el-select v-model="dataForm.type" placeholder="请选择类型" style="width:150px;margin-left:5px;margin-right:5px" clearable>
            <el-option v-for="(item, index) in typeMap" :key="index" :value="index" :label="typeMap[index]"></el-option>
          </el-select>
          <el-button icon="el-icon-search" type="primary" size="small" @click="searchHandle()">查询</el-button>
        </div>
      </div>
      <el-table
        :data="dataList"
        size="small"
        v-loading="dataListLoading"
        @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <!-- <el-table-column
          type="index"
          label="#"
          header-align="center"
          align="center">
          <template slot-scope="scope">
            {{ pageSize * (pageIndex - 1) + scope.$index + 1 }}
          </template>
        </el-table-column> -->
        <el-table-column
          prop="id"
          header-align="center"
          align="center"
          label="ID">
        </el-table-column>
        <el-table-column
          prop="custId"
          header-align="center"
          align="center"
          label="用户ID">
        </el-table-column>
        <el-table-column
          prop="type"
          header-align="center"
          align="center"
          label="类型">
           <template slot-scope="scope">
                {{scope.row.type | typeFilter}}
           </template>
        </el-table-column>
       <!-- <el-table-column
         prop="beforeScore"
         header-align="center"
         align="center"
         label="变化前余额">
       </el-table-column>
       <el-table-column
         prop="afterScore"
         header-align="center"
         align="center"
         label="变化后余额">
        </el-table-column> -->
        <el-table-column
          prop="score"
          header-align="left"
          align="left"
          label="变化金额">
          <template slot-scope="scope">
            {{scope.row.direct == 1 ? '+' : ''}}{{scope.row.score}}
          </template>
        </el-table-column>
        <el-table-column
         prop="remark"
         header-align="center"
         align="center"
         label="备注">
        </el-table-column>
        <el-table-column
         prop="sourceId"
         header-align="center"
         align="center"
         label="来源ID">
        </el-table-column>
        <el-table-column
          prop="direct"
          header-align="center"
          align="center"
          label="方向">
           <template slot-scope="scope">
             <el-tag v-if="scope.row.direct == 1" type="success" effect="dark">增加</el-tag>
             <el-tag v-else type="danger" effect="dark">减少</el-tag>
           </template>
        </el-table-column>
        <el-table-column
          prop="salesmanId"
          header-align="center"
          align="center"
          label="业务员">
          <template slot-scope="scope">
            <userAssembly :value="scope.row.salesmanId"></userAssembly>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          label="创建时间">
          <template slot-scope="scope">
            <div>{{scope.row.createTime|timeStampDateTimeFormat}}</div>
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
  </div>
</template>

<script>
import {timeStampDateTimeFormat} from '@/utils/index.js'
import userAssembly from "../../../components/user/user-assembly";
import SearchUserInput from "@/components/permission/SearchUserInput";

const typeMap = {
  1: '系统赠送充值余额',
  2: '系统扣减充值余额',
  3:'签到赠送',
  4:'首充赠送',
  5:'VIP0过期扣减',
  6:'个人任务返佣',
  7:'团队佣金',
  8:'基金收益',
  9:'抽奖奖金',
  10:'充值',
  11:'提现',
  12:'基金分红',
  13:'基金本金',
  14:'商城购物',
  15:'充值奖励',
  16:'注册赠送体验金',
  17:'体验金购买基金',
  18:'宝箱领取',
  19:'系统扣减基金分红',
};

export default {
  data() {
    return {
      dataForm: {
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      searchFormOptions: {
        filedList: [
          { filedCode: 'key', filedName: '关键字', filedType: '1',placeholder:'客户ID'}
        ]
      },
      typeMap: typeMap
    }
  },
  components: {
    userAssembly,
    SearchUserInput
  },
  filters:{
    typeFilter (status) {
      return typeMap[status]
    }
  },
  activated() {
  },
  methods: {
    searchHandle() {
      this.pageIndex = 1
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      // if(!this.dataForm.key || this.dataForm.key == ''){
      //   this.$message.error('请输入客户ID，再点击查询')
      //   return;
      // }
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/cust/custscorelog/b_list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'key': this.dataForm.key,
          "team":this.$refs.teamSearch.getSeletedVal().toString(),
          "type": this.dataForm.type
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
          url: this.$http.adornUrl('/cust/scorerecord/delete'),
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
    },
  }
}
</script>
