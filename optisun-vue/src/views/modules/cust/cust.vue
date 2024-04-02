<template>
  <div>
    <el-card v-if="!addOrUpdateVisible">
      <div style="display: flex;margin-bottom: 10px;align-items:center">
        <el-button icon="el-icon-plus" v-if="isAuth('cust:cust:save')" type="primary" size="small"
                   @click="addOrUpdateHandle()">新增
        </el-button>
        <!--        <el-button size="small" @click="excelHander">导出</el-button>-->
        <div style="flex: 1;display: flex;justify-content: flex-end;align-items: center">
          <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="searchHandle()"></SearchUserInput>
          <el-input v-model="dataForm.custId" style="margin-left:5px;margin-right:5px;width:120px" size="small"
                    clearable placeholder="用户编号">
          </el-input>
          <el-input v-model="dataForm.mobile" style="margin-left:5px;margin-right:5px;width:150px" size="small"
                    clearable placeholder="手机号/用户名">
          </el-input>
          <el-input v-model="dataForm.loginIp" style="margin-left:5px;margin-right:5px;width:150px" size="small"
                    clearable placeholder="登录IP">
          </el-input>
          <el-input v-model="dataForm.joinIp" style="margin-left:5px;margin-right:5px;width:150px" size="small"
                    clearable placeholder="注册IP">
          </el-input>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.joinTime" clearable  style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="注册日期">
          </el-date-picker>
          <!-- <el-select v-model="dataForm.levelId" style="margin-left:5px;margin-right:5px;width:100px" size="small"
                     clearable placeholder="会员等级">
            <el-option value="" label="全部"></el-option>
            <el-option value="0" label="游客"></el-option>
            <el-option value="1" label="入门级"></el-option>
            <el-option value="2" label="初学者"></el-option>
            <el-option value="3" label="进阶级"></el-option>
            <el-option value="4" label="大亨"></el-option>
            <el-option value="5" label="伯爵"></el-option>
          
          </el-select> -->
          <el-select v-model="dataForm.totalRechargeMoney" clearable style="width:100px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="类型">
            <el-option value="-1" label="全部"></el-option>
            <el-option value="1" label="已充值"></el-option>
            <el-option value="2" label="未充值"></el-option>
            <el-option value="3" label="已备注"></el-option>
            <el-option value="4" label="已参与"></el-option>
            <el-option value="5" label="未参与"></el-option>
          </el-select>

          <el-button icon="el-icon-search" type="primary" size="small" @click="searchHandle()">查询</el-button>
        </div>
      </div>
      <el-table
        id="out-table"
        :data="dataList"
        :height="documentClientHeight - 220"
        v-loading="dataListLoading"
        @sort-change="sortChangeHandle"
        @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <el-table-column
          type="selection"
          header-align="center"
          align="center"
          width="50">
        </el-table-column>
        <el-table-column
          header-align="center"
          align="center"
          width="240"
          label="操作">
          <template slot-scope="scope">
            <el-button type="text" @click="openCustFrontHandle(scope.row)">一键登录</el-button>
            <el-dropdown placement="bottom-start">
              <i class="el-icon-more" style="margin-left: 20px; color: #3370FF; cursor: pointer;"></i>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-if="isAuth('cust:cust:initPs')" @click.native="resertHandle(scope.row.custId)">初始登陆密码</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:initMoneyPs')" @click.native="resertMoneyHandle(scope.row.custId)">初始提现密码</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:seeNext')" divided @click.native="nextHandle(scope.row.custId)">查看下级</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:seePre')"  @click.native="preHandle(scope.row.custId)">查看上级</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:recharge')" @click.native="rechargeHandle(scope.row)">扣减/赠送金额</el-dropdown-item>
                <!-- <el-dropdown-item v-if="isAuth('cust:cust:registerMoney')" @click.native="registerMoneyHandle(scope.row)">扣减/赠送体验金</el-dropdown-item> -->
                <!-- <el-dropdown-item v-if="isAuth('cust:cust:luckChange')" @click.native="custLuck(scope.row)">赠送抽奖次数</el-dropdown-item> -->
                <el-dropdown-item v-if="isAuth('cust:cust:statusChange')" divided @click.native="custDisable(scope.row)">{{scope.row.status == 0 ? '解封' : '封禁'}}</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:mobileChange')" @click.native="custMobile(scope.row)">修改手机号</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:remarkChange')" @click.native="custRemark(scope.row)">修改备注</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:vip')" @click.native="custVip(scope.row)">修改会员等级</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:orderbuy')" @click.native="custTaskLimit(scope.row)">{{scope.row.taskLimit == 1 ? '禁止理财' : '允许理财'}}</el-dropdown-item>
                <el-dropdown-item @click.native="custWidthdrawLimit(scope.row)">{{scope.row.withdrawLimit == 1 ? '允许提现' : '禁止提现'}}</el-dropdown-item>
                <el-dropdown-item @click.native="custPrivilege(scope.row)">{{scope.row.isPrivilege == 1 ? '降权' : '提权'}}</el-dropdown-item>
                <el-dropdown-item v-if="isAuth('cust:cust:delete')" @click.native="deleteHandle()">删除</el-dropdown-item>

                <el-dropdown-item v-if="isAuth('cust:cust:remarkChange')" @click.native="custRemark(scope.row)">备注</el-dropdown-item>

                <el-dropdown-item v-if="isAuth('l:lorder:buy')" @click.native="custOrder(scope.row)">锁单</el-dropdown-item>
                
              
              </el-dropdown-menu>
            </el-dropdown>
          </template>
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
          prop="custId"
          header-align="center"
          align="center"
          width="100px"
          label="用户编号">
              <template slot-scope="scope">
                <div v-if="scope.row.status == 1">
                  {{scope.row.custId}}
                </div>
                <div v-else>
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.custId}}</el-tag>
                </div>
              </template>
        </el-table-column>
        <el-table-column
          prop="custName"
          header-align="center"
          align="center"
          width="130px"
          label="用户名">
            <template slot-scope="scope">
              <div v-if="scope.row.status == 1">
                {{scope.row.custName}}
              </div>
              <div v-else>
                <el-tag size="mini" type="danger" effect="dark">{{scope.row.custName}}</el-tag>
              </div>
            </template>
        </el-table-column>
        <el-table-column prop="status" header-align="center" label="账号状态"
                         align="center"
                         width="160px">
          

          <template slot-scope="scope">
            <div v-if="scope.row.status == 1">
              <el-tag size="mini" type="success" effect="dark">正常</el-tag>
            </div>
            <div v-else>
              <el-tag size="mini" type="danger" effect="dark">封禁</el-tag>
            </div>
          </template>
        </el-table-column>

        

        <!-- <el-table-column
          prop="num"
          header-align="center"
          align="center"
          width="100px"
          sortable='custom'
          label="一级团数">
        </el-table-column> -->
      
        
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
          sortable='custom'
          label="上级Id">
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
        <!-- <el-table-column
          prop="leftRechargeMoney"
          header-align="center"
          align="center"
          width="100px"
          sortable='custom'
          label="充值余额">
        </el-table-column> -->
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
          width="130px"
          label="理财认购总金额">
        </el-table-column>
        <el-table-column
          prop="fundCommissionMoney"
          header-align="center"
          align="center"
          label="理财分红">
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

        <el-table-column prop="status" header-align="center" label="提现"
                         align="center"
                         width="160px">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.withdrawLimit == 0" size="mini" type="success">允许提现</el-tag>
            <el-tag v-if="scope.row.withdrawLimit == 1" size="mini" type="danger">禁止提现</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="taskLimit" header-align="center" label="理财"
                         align="center"
                         width="160px">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.taskLimit == 0" size="mini" type="danger">禁止理财</el-tag>
            <el-tag v-if="scope.row.taskLimit == 1" size="mini" type="success">允许理财</el-tag>
          </template>
        </el-table-column>
        <el-table-column header-align="center" label="特权"
                         align="center"
                         width="160px">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isPrivilege == 0" size="mini" type="danger">一般账号</el-tag>
            <el-tag v-if="scope.row.isPrivilege == 1" size="mini" type="success">特权账号</el-tag>
          </template>
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
          prop="mobile"
          header-align="center"
          align="center"
          width="120px"
          label="手机号">
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
        <!-- <el-table-column
          prop="luckLeftNum"
          header-align="center"
          align="center"
          width="130px"
          sortable='custom'
          label="抽奖次数">
        </el-table-column> -->
        <el-table-column
          prop="remark"
          header-align="center"
          align="center"
          width="130px"
          sortable='custom'
          label="备注">
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
    <!--点击查看下级-->
    <cust-grade-show ref="custGrad"></cust-grade-show>
    <!--点击查看上级-->
    <cust-pregrade-show ref="custPreGrad"></cust-pregrade-show>
    <!--系统赠送、扣减-->
    <cust-scorelog-add ref="custScorelog" @back="backHandle"></cust-scorelog-add>
    <!--调整上下级-->
    <cust-change-up ref="custChangeUp"></cust-change-up>
    <!-- 封禁或解封用户 -->
    <cust-disable ref="custDisable" @back="backHandle"></cust-disable>
    <!-- 修改手机号 -->
    <cust-mobile ref="custMobileRef" @back="backHandle"></cust-mobile>
    <!-- 修改备注 -->
    <cust-remark ref="custRemarkRef" @back="backHandle"></cust-remark>
    <!-- 修改提现费率 -->
    <withdraw-rate ref="withdrawRateRef" @back="backHandle"></withdraw-rate>
    <!-- 抽奖次数设置 -->
    <cust-luck ref="custLuckRef" @back="backHandle"></cust-luck>

    <cust-vip ref="custVipRef" @back="backHandle"></cust-vip>

    <cust-order ref="custOrderRef" @back="backHandle"></cust-order>
    <!-- 系统赠送、扣减体验金 -->
    <register-money-add ref="registerMoney1" @back="backHandle"></register-money-add>
  </div>
</template>

<script>
import AddOrUpdate from './cust-add-or-update'
import custGradeShow from "./cust-grade-show";
import custPregradeShow from "./cust-pregrade-show";
import {timeStampDateTimeFormat} from '@/utils/index'
import SearchUserInput from "@/components/permission/SearchUserInput";
import FileSaver from 'file-saver'
import * as XLSX from 'xlsx/xlsx.mjs'
import custScorelogAdd from "./cust-scorelog-add";
import registerMoneyAdd from "./register-money-add";
import custChangeUp from "./common/cust-change-up";
import userAssembly from "../../../components/user/user-assembly";
import userChild from "../../../components/user/user-child";
import CustDisable from "./common/cust-disable";
import CustMobile from "./common/cust-mobile";
import CustRemark from "./common/cust-remark";
import WithdrawRate from "./common/cust-withdraw-rate";
import CustLuck from "./common/cust-luck";
import CustVip from "./common/cust-vip";
import CustOrder from './common/lorder-add-or-update';

export default {
  data() {
    return {
      dataForm: {
        custId: '',
        mobile: '',
        loginIp: '',
        joinIp: '',
        joinTime: '',
        levelId: '',
        totalRechargeMoney: ''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      addOrderVisible:  false,
      sortField: 'joinTime',
      sortOrder: 'desc'
    }
  },
  components: {
    AddOrUpdate,
    custGradeShow,
    custPregradeShow,
    SearchUserInput,
    custScorelogAdd,
    custChangeUp,
    userAssembly,
    userChild,
    CustDisable,
    CustMobile,
    CustRemark,
    WithdrawRate,
    CustLuck,
    CustVip,
    registerMoneyAdd,
    CustOrder
  },
  activated() {
  },
  computed: {
    documentClientHeight() {
      return this.$store.state.common.documentClientHeight
    },
  },
  methods: {
    custPrivilege(cust){
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/custPrivilegeChage"),
        method: "post",
        data: this.$http.adornData({
          'custId': cust.custId,
          'isPrivilege': cust.isPrivilege == 1 ? 0 : 1,
        })
      }).then(({ data }) => {
        this.loading = false
        if (data && data.code === 0) {
          this.visible = false
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
    custWidthdrawLimit(cust){
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/limitStatusChage"),
        method: "post",
        data: this.$http.adornData({
          'custId': cust.custId,
          'status': cust.withdrawLimit == 1 ? 0 : 1,
        })
      }).then(({ data }) => {
        this.loading = false
        if (data && data.code === 0) {
          this.visible = false
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
    custTaskLimit(cust){
      this.loading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/custTaskLimit"),
        method: "post",
        data: this.$http.adornData({
          'custId': cust.custId,
          'taskLimit': cust.taskLimit == 1 ? 0 : 1,
        })
      }).then(({ data }) => {
        this.loading = false
        if (data && data.code === 0) {
          this.visible = false
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
    },
    custMobile(cust){
      this.$refs.custMobileRef.init(cust.custId, cust.mobile)
    },
    custRemark(cust){
      this.$refs.custRemarkRef.init(cust.custId, cust.remark)
    },
    custWithdrawRate(cust){
      this.$refs.withdrawRateRef.init(cust.custId, cust.withdrawRate)
    },
    custVip(cust){
      this.$refs.custVipRef.init(cust.custId, cust.level)
    },
    custLuck(cust){
      this.$refs.custLuckRef.init(cust.custId)
    },
    searchHandle() {
      this.pageIndex = 1
      this.getDataList()
    },
    custOrder(cust) {
      this.$refs.custOrderRef.init(cust.custId)
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/cust/cust/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'key': this.dataForm.key,
          'custId': this.dataForm.custId.trim(),
          'mobile': this.dataForm.mobile.trim(),
          'loginIp': this.dataForm.loginIp.trim(),
          'joinIp': this.dataForm.joinIp.trim(),
          'joinTime': this.dataForm.joinTime.trim(),
          'levelId': this.dataForm.levelId,
          'totalRechargeMoney': this.dataForm.totalRechargeMoney,
          "team": this.$refs.teamSearch.getSeletedVal().toString(),
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
    sortChangeHandle(val) {
      if (val.prop) {
        this.sortField = val.prop
        if (val.order == 'ascending') {
          this.sortOrder = 'asc'
        } else if (val.order == 'descending') {
          this.sortOrder = 'desc'
        } else {
          this.sortField = 'joinTime'
          this.sortOrder = 'desc'
        }
        this.searchHandle()
      }
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
    backHandle(options) {
      this.addOrUpdateVisible = false
      if (options.refresh) {
        this.getDataList()
      }
    },
    //初始化密码
    resertHandle(custId) {
      this.$confirm(`确定要进行登陆密码初始化操作吗?`, '提示', {
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
        }).then(({data}) => {
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
    //初始化提现密码
    resertMoneyHandle(custId) {
      this.$confirm(`确定要进行提现密码初始化操作吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl("/cust/cust/resetMoneyPassword"),
          method: "post",
          data: this.$http.adornData({
            'custId': custId
          })
        }).then(({data}) => {
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
    nextHandle(custId) {
      this.$nextTick(() => {
        this.$refs.custGrad.init(custId)
      })
    },
    //点击查看上级
    preHandle(custId) {
      this.$nextTick(() => {
        this.$refs.custPreGrad.init(custId)
      })
    },
    //团队
    teamLoad(status, data) {
      if (status) {
        this.searchHandle()
      } else {
        this.$message({
          showClose: true,
          message: '数据加载失败，请联系管理员！',
          type: 'error'
        });
      }
    },
    //导出
    excelHander() {
      /* generate workbook object from table */
      let fix = document.querySelector('.el-table__fixed');
      let wb;
      if (fix) {
        wb = XLSX.utils.table_to_book(document.querySelector("#out-table").removeChild(fix));
        document.querySelector("#out-table").appendChild(fix);
      } else {
        wb = XLSX.utils.table_to_book(document.querySelector("#out-table"));
      }
      /* get binary string as output */
      let wbout = XLSX.write(wb, {
        bookType: 'xlsx',
        bookSST: true,
        type: 'array'
      });

      try {
        FileSaver.saveAs(new Blob([wbout], {
          type: 'application/octet-stream'
        }), 'file.xlsx');
      } catch (e) {
        if (typeof console !== 'undefined') console.log(e, wbout)
      }
      return wbout;
    },
    //状态变化
    statusChange(val, custId) {
      this.$http({
        url: this.$http.adornUrl("/cust/cust/statusChage"),
        method: "post",
        data: this.$http.adornData({
          'custId': custId,
          'status': val
        })
      }).then(({data}) => {
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
    //系统赠送与扣减
    rechargeHandle(row) {
      this.$nextTick(() => {
        this.$refs.custScorelog.init(row)
      })
    },
    //系统赠送与扣减体验金
    registerMoneyHandle(row) {
      this.$nextTick(() => {
        this.$refs.registerMoney1.init(row)
      })
    },
    //调整上级
    changeUpHandle(row) {
      this.$nextTick(() => {
        this.$refs.custChangeUp.init(row)
      })
    },
    // 一键客户前端
    openCustFrontHandle(row){
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl("/cust/cust/getAuthCode"),
        method: "get",
        params: this.$http.adornParams({
          'custId': row.custId,
        })
      }).then(({data}) => {
        this.dataListLoading = false
        if (data && data.code === 0) {
          let url = `${window.SITE_CONFIG['frontUrl']}/#/?code=${data.authCode}`
          window.open(url, '_blank')
        } else {
          this.$message.error(data.msg);
        }
      }).catch(e => {
        this.dataListLoading = false
      })
    }
  }
}
</script>
<style lang="scss" scoped>

</style>
