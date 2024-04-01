<template>
  <el-dialog :visible.sync="addOrUpdateVisible" class="mod-config"
             :title="'使用提现账号' + dataForm.account + '会员清单，共计' + this.dataList.length + '人'">
    <!--      <div style="display: flex;margin-bottom: 10px">-->
    <!--        <div style="flex: 1;">-->
    <!--        </div>-->
    <!--        <div>-->
    <!--          <el-input v-model="dataForm.custId" style="margin-left:5px;margin-right:5px;width:120px" size="small" clearable placeholder="用户编号">-->
    <!--          </el-input>-->
    <!--          <el-input v-model="dataForm.mobile" style="margin-left:5px;margin-right:5px;width:150px" size="small" clearable placeholder="手机号/用户名">-->
    <!--          </el-input>-->
    <!--          <el-input v-model="dataForm.loginIp" style="margin-left:5px;margin-right:5px;width:150px" size="small" clearable placeholder="登录IP">-->
    <!--          </el-input>-->
    <!--          <el-input v-model="dataForm.joinIp" style="margin-left:5px;margin-right:5px;width:150px" size="small" clearable placeholder="注册IP">-->
    <!--          </el-input>-->
    <!--          <el-select v-model="dataForm.levelId" style="margin-left:5px;margin-right:5px;width:100px" size="small" clearable placeholder="会员等级">-->
    <!--            <el-option value="" label="全部"></el-option>-->
    <!--            <el-option value="4" label="普通用户"></el-option>-->
    <!--            <el-option value="5" label="VIP1"></el-option>-->
    <!--            <el-option value="6" label="VIP2"></el-option>-->
    <!--            <el-option value="7" label="VIP3"></el-option>-->
    <!--            <el-option value="11" label="VIP4"></el-option>-->
    <!--          </el-select>-->
    <!--          <el-button icon="el-icon-search" type="primary" size="small" @click="getDataList()">查询</el-button>-->
    <!--        </div>-->
    <!--      </div>-->
    <el-table
      size="small"
      :data="dataList"
      :height="documentClientHeight - 350"
      v-loading="dataListLoading"
      style="width: 100%;">
      <el-table-column
        type="index"
        header-align="center"
        label="#"
        align="center">
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
        width="100px"
        label="用户名">
      </el-table-column>
      <el-table-column
        prop="mobile"
        header-align="center"
        align="center"
        width="120px"
        label="手机号">
      </el-table-column>
      <el-table-column prop="status" header-align="center" label="账号状态"
                       align="center"
                       width="160px">
        <template slot-scope="scope">
          {{ scope.row.status == 1 ? '正常' : '封禁' }}
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
        label="会员等级">
        <template slot-scope="scope">
          <el-tag size="mini" type="success" effect="dark">{{ scope.row.gradeName }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        prop="leftCommissionMoney"
        header-align="center"
        align="center"
        width="100px"
        label="余额">
      </el-table-column>
      <el-table-column
        prop="complete"
        header-align="center"
        align="center"
        label="是否充值">
        <template slot-scope="scope">
          <div v-if="scope.row.complete==0">
            <el-tag size="mini" type="danger" effect="dark">未完成-0</el-tag>
          </div>
          <div v-else-if="scope.row.complete==1">
            <el-tag size="mini" type="success" effect="dark">已完成-1</el-tag>
          </div>
          <div v-else-if="scope.row.complete==2">
            <el-tag size="mini" type="success" effect="dark">已完成-2</el-tag>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        prop="loginTime"
        header-align="center"
        align="center"
        width="160px"
        label="登录时间">
        <template slot-scope="scope">
          <div>{{ scope.row.loginTime | timeStampDateTimeFormat }}</div>
        </template>
      </el-table-column>
      <el-table-column
        prop="loginIp"
        header-align="center"
        align="center"
        width="160px"
        label="登录IP">
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
        label="注册时间">
        <template slot-scope="scope">
          <div>{{ scope.row.joinTime | timeStampDateTimeFormat }}</div>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script>
import {timeStampDateTimeFormat} from '@/utils/index'

export default {
  data() {
    return {
      dataForm: {
        currentCustId: 0,
        level: 1,
        custId: '',
        mobile: '',
        loginIp: '',
        joinIp: '',
        levelId: '',
        account: ''
      },
      dataList: [],
      dataListLoading: false,
      addOrUpdateVisible: false
    }
  },
  components: {},
  activated() {
  },
  computed: {
    documentClientHeight() {
      return this.$store.state.common.documentClientHeight
    },
  },
  methods: {
    init(account) {
      this.dataList = []
      this.dataForm.account = account
      this.addOrUpdateVisible = true
      this.getDataList()
    },
    searchHandle(val) {
      this.dataForm = val
      this.getDataList()
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/cust/commission/getCustListByAccount'),
        method: 'get',
        params: this.$http.adornParams({
          'account': this.dataForm.account
        })
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.data
        } else {
          this.dataList = []
        }
        this.dataListLoading = false
      })
    }
  }
}
</script>
<style lang="scss" scoped>

</style>
