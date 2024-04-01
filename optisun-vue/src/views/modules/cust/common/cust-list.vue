<template>
  <div class="mod-config">
    <div v-if="!addOrUpdateVisible">
      <div style="display: flex;margin-bottom: 10px">
        <div style="flex: 1;">
        </div>
        <div>
          <el-input v-model="dataForm.custId" style="margin-left:5px;margin-right:5px;width:120px" size="small" clearable placeholder="用户编号">
          </el-input>
          <el-input v-model="dataForm.mobile" style="margin-left:5px;margin-right:5px;width:150px" size="small" clearable placeholder="手机号/用户名">
          </el-input>
          <el-input v-model="dataForm.loginIp" style="margin-left:5px;margin-right:5px;width:150px" size="small" clearable placeholder="登录IP">
          </el-input>
          <el-input v-model="dataForm.joinIp" style="margin-left:5px;margin-right:5px;width:150px" size="small" clearable placeholder="注册IP">
          </el-input>
          <el-select v-model="dataForm.levelId" style="margin-left:5px;margin-right:5px;width:100px" size="small" clearable placeholder="会员等级">
            <el-option value="" label="全部"></el-option>
            <el-option value="0" label="游客"></el-option>
            <el-option value="1" label="入门级"></el-option>
            <el-option value="2" label="初学者"></el-option>
            <el-option value="3" label="进阶级"></el-option>
            <el-option value="4" label="大亨"></el-option>
            <el-option value="5" label="伯爵"></el-option>
          </el-select>
          <el-button icon="el-icon-search" type="primary" size="small" @click="getDataList()">查询</el-button>
        </div>
      </div>
      <el-table
        size="small"
        :data="dataList"
        :height="documentClientHeight - 300"
        v-loading="dataListLoading"
        @sort-change="sortChangeHandle"
        @selection-change="selectionChangeHandle"
        style="width: 100%;">

        <el-table-column
          header-align="center"
          align="center"
          width="180"
          label="操作">
          <template slot-scope="scope">
            <el-tooltip class="item" effect="dark" :content="scope.row.status == 0 ? '解封' : '封禁'" placement="top">
              <el-button icon="el-icon-lock" type="text" v-if="isAuth('cust:cust:statusChange')" size="small"
                         @click="custDisable(scope.row)"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column
          prop="custId"
          header-align="center"
          align="center"
          width="100px"
          label="用户编号">
        </el-table-column>
        <el-table-column
          prop="custName"
          header-align="center"
          align="center"
          width="130px"
          label="用户名">
        </el-table-column>
        <el-table-column prop="status" header-align="center" label="账号状态"
                         align="center"
                         width="160px">
          <template slot-scope="scope">
            {{ scope.row.status == 1 ? '正常' : '封禁' }}
          </template>
        </el-table-column>
        <el-table-column
          prop="mobile"
          header-align="center"
          align="center"
          width="120px"
          label="手机号">
        </el-table-column>
        <el-table-column
          prop="isNb"
          header-align="center"
          align="center"
          width="120px"
          label="内外账号">
          <template slot-scope="scope">
            <el-tag size="mini" type="danger" v-if="scope.row.isNb==0" effect="dark">真实玩家-0</el-tag>
            <el-tag size="mini" type="success" v-else effect="dark">业务员-1</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="salesmanId"
          header-align="center"
          align="center"
          width="150px"
          label="业务员">
          <template slot-scope="scope">
            <userAssembly :value="scope.row.salesmanId"></userAssembly>
          </template>
        </el-table-column>
        <el-table-column
          prop="parentId"
          header-align="center"
          align="center"
          width="100px"
          label="上级Id">
        </el-table-column>
        <el-table-column
          prop="level"
          header-align="center"
          align="center"
          label="会员等级"
          width="100px"
          sortable='custom'>
          <template slot-scope="scope">
            <el-tag size="mini" type="success" effect="dark">{{ scope.row.gradeName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="registerMoney"
          header-align="center"
          align="center"
          width="130px"
          sortable='custom'
          label="体验金">
        </el-table-column>
        <el-table-column
          prop="totalCommissionMoney"
          header-align="center"
          align="center"
          width="100px"
          sortable='custom'
          label="累计佣金">
        </el-table-column>
        <el-table-column
          prop="leftCommissionMoney"
          header-align="center"
          align="center"
          width="120px"
          sortable='custom'
          label="可提现佣金">
        </el-table-column>
        <el-table-column
          prop="totalRechargeMoney"
          header-align="center"
          align="center"
          width="130px"
          sortable='custom'
          label="累计充值金额">
        </el-table-column>
        <el-table-column
          prop="taskCommissionMoney"
          header-align="center"
          align="center"
          width="100px"
          sortable='custom'
          label="任务佣金">
        </el-table-column>
        <el-table-column
          prop="teamCommissionMoney"
          header-align="center"
          align="center"
          width="100px"
          sortable='custom'
          label="团队佣金">
        </el-table-column>
        <el-table-column
          prop="complete"
          header-align="center"
          align="center"
          label="完成首充">
          <template slot-scope="scope">
            <div v-if="scope.row.totalRechargeMoney > 0">
              <el-tag size="mini" type="success" effect="dark">已完成-1</el-tag>
            </div>
            <div v-else>
              <el-tag size="mini" type="danger" effect="dark">未完成-0</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="joinCftNum"
          header-align="center"
          align="center"
          label="参与理财">
          <template slot-scope="scope">
            <el-tag size="mini" type="success" v-if="scope.row.joinCftNum > 0">参与{{scope.row.joinCftNum}}次</el-tag>
            <el-tag size="mini" type="danger" v-if="scope.row.joinCftNum == 0">未参与</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="personCftMoney"
          header-align="center"
          align="center"
          label="理财收益">
        </el-table-column>
        <el-table-column
          prop="loginTime"
          header-align="center"
          align="center"
          width="160px"
          sortable='custom'
          label="最后登录时间">
          <template slot-scope="scope">
            <div>{{ scope.row.loginTime | timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="loginIp"
          header-align="center"
          align="center"
          width="160px"
          label="最后登录IP">
        </el-table-column>
        <el-table-column
          prop="joinIp"
          header-align="center"
          align="center"
          width="160px"
          label="注册IP">
        </el-table-column>
        <el-table-column
          prop="joinTime"
          header-align="center"
          align="center"
          width="160px"
          sortable="custom"
          label="注册时间">
          <template slot-scope="scope">
            <div>{{ scope.row.joinTime | timeStampDateTimeFormat }}</div>
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

    <!-- 封禁或解封用户 -->
    <cust-disable ref="custDisable" @back="backHandle"></cust-disable>
  </div>
</template>

<script>
  import {timeStampDateTimeFormat} from '@/utils/index'
  import CustDisable from "./cust-disable";
  export default {
    data () {
      return {
        dataForm: {
          currentCustId:0,
          level:1,
          custId: '',
          mobile: '',
          loginIp: '',
          joinIp: '',
          levelId: ''
        },
        dataList: [],
        pageIndex: 1,
        pageSize: 10,
        totalPage: 0,
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        sortField: 'joinTime',
        sortOrder: 'desc'
      }
    },
    components: {
      CustDisable
    },
    activated () {
    },
    computed: {
      documentClientHeight(){
        return this.$store.state.common.documentClientHeight
      },
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
          url: this.$http.adornUrl('/cust/cust/childList'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            'currentCustId':this.dataForm.currentCustId,
            'level':this.dataForm.level,
            'custId': this.dataForm.custId.trim(),
            'mobile': this.dataForm.mobile.trim(),
            'loginIp': this.dataForm.loginIp.trim(),
            'joinIp': this.dataForm.joinIp.trim(),
            'levelId': this.dataForm.levelId,
            'sortField': this.sortField,
            'sortOrder': this.sortOrder
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
      sortChangeHandle(val){
        if (val.prop) {
          this.sortField = val.prop
          if (val.order == 'ascending') {
            this.sortOrder = 'asc'
          } else if (val.order == 'descending') {
            this.sortOrder = 'desc'
          } else{
            this.sortField = 'joinTime'
            this.sortOrder = 'desc'
          }
          this.searchHandle()
        }
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
          return item.custId
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/cust/cust/delete'),
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
      resertHandle(custId){
        this.$confirm(`确定要进行初始化密码操作吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl("/cust/cust/resetPassword"),
            method: "post",
            data: this.$http.adornData({
              'custId': custId
            })
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
          })
        })
      },
      //点击查看下级
      nextHandle(custId){
        this.$nextTick(() => {
          this.$refs.custGrad.init(custId)
        })
      },
      //查看下级数据
      getNextDataList(currentId,level){
        this.dataForm.currentCustId = currentId
        this.dataForm.level=level
        this.getDataList()
      },
      //状态变化
      statusChange(val,custId){
        this.$http({
          url: this.$http.adornUrl("/cust/cust/statusChage"),
          method: "post",
          data: this.$http.adornData({
            'custId': custId,
            'status':val
          })
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1000,
              onClose: () => {
                this.getDataList()
              },
            });
          } else {
            this.$message.error(data.msg);
          }
        })
      },
      custDisable(cust) {
        this.$refs.custDisable.init(cust.custId, cust.status)
      }
    }
  }
</script>
<style lang="scss" scoped>

</style>
