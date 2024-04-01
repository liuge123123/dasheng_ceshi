<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div style="display: flex;margin-bottom: 10px">
        <div>
          <el-button icon="el-icon-check"  v-if="isAuth('cust:commission:check')"   size="small"
                     @click="checkMore()" :disabled="dataListSelections.length <= 0">批量审核
          </el-button>
          <el-button icon="el-icon-check"  v-if="isAuth('cust:commission:check')"   size="small"
                     @click="checkAMore()" :disabled="dataListSelections.length <= 0">批量A卡审核
          </el-button>
          <el-button icon="el-icon-check"  v-if="isAuth('cust:commission:check')"   size="small"
                     @click="checkZMore()" :disabled="dataListSelections.length <= 0">批量Z卡审核
          </el-button>
          <el-button
            v-if="isAuth('cust:commission:excel')"
            icon="el-icon-folder-opened"
            size="small"
            @click="excelHandle('excel')"
            plain>导出excel
          </el-button>
          <el-button
            v-if="isAuth('cust:commission:excel')"
            icon="el-icon-document"
            size="small"
            @click="excelHandle('txt')"
            plain>导出文本文档
          </el-button>
          <el-tag type="success">已审核金额:{{succesCount}}</el-tag>
          <el-tag type="danger">待审核金额:{{auditCount}}</el-tag>
        </div>
        <div style="flex: 1;display: flex;justify-content: flex-end;align-items: center">
          <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="searchHandle"></SearchUserInput>
          <el-input v-model="dataForm.userId" clearable style="width:150px;margin-left:5px;margin-right:5px"
                    size="small" clearable placeholder="用户ID">
          </el-input>
          <!-- <el-input v-model="dataForm.bankName" clearable style="width:150px;margin-left:5px;margin-right:5px"
                    size="small" clearable placeholder="银行名称">
          </el-input> -->
          <el-input v-model="dataForm.mobile" clearable style="width:200px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="电话号码"></el-input>
          <el-select v-model="dataForm.status" clearable style="width:100px;margin-left:5px;margin-right:5px"
                     size="small" clearable placeholder="状态">
            <el-option value="-1" label="全部"></el-option>
            <el-option value="0" label="待审核"></el-option>
            <el-option value="1" label="已审核"></el-option>
            <el-option value="2" label="被驳回"></el-option>
          </el-select>
          <el-select v-model="dataForm.bankName" clearable style="width:100px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="银行卡类型">
            <el-option value="-1" label="全部"></el-option>
            <el-option value="Airtel Money" label="Airtel Money"></el-option>
            <el-option value="Moov Money" label="Moov Money"></el-option>
            <el-option value="Zamani" label="Zamani"></el-option>
          </el-select>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.createTime" clearable
                          style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable
                          placeholder="创建时间">
          </el-date-picker>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.updateTime" clearable
                          style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable
                          placeholder="更新时间">
          </el-date-picker>
          <el-button icon="el-icon-search" type="primary" size="small" @click="searchHandle()">查询</el-button>
        </div>
      </div>
      <el-table
        :data="dataList"
        :height="documentClientHeight - 220"
        size="small"
        v-loading="dataListLoading"
        @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <el-table-column
          type="selection"
          header-align="center"
          align="center"
          :selectable='selectable'
          width="50">
        </el-table-column>
        <!-- <el-table-column
          type="index"
          header-align="center"
          label="#"
          align="center">
          <template slot-scope="scope">
            {{ pageSize * (pageIndex - 1) + scope.$index + 1 }}
          </template>
        </el-table-column> -->
        <el-table-column
          header-align="center"
          align="center"
          width="150"
          label="操作">
          <template slot-scope="scope">
            <el-button icon="el-icon-check" v-if="scope.row.status==0 && isAuth('cust:commission:check')" type="text"
                       size="small"
                       @click="checkHandle(scope.row.id)">审核
            </el-button>
            <el-button icon="el-icon-close" v-if="scope.row.status==1 && isAuth('cust:commission:check')" type="text"
                       size="small"
                       @click="rejectHandle(scope.row.id)">驳回
            </el-button>
            <el-button icon="el-icon-view" type="text" size="small"
                       @click="nextHandle(scope.row.custId)">查看下级
            </el-button>
          </template>
        </el-table-column>
        <el-table-column
          prop="id"
          header-align="center"
          align="center"
          label="ID">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.id}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.id}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="custId"
          header-align="center"
          align="center"
          min-width="100px"
          label="用户编号">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.custId}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.custId}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="custName"
          header-align="center"
          align="center"
          min-width="100px"
          label="用户名">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.custName}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.custName}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="bankname"
          header-align="center"
          align="center"
          width="120px"
          label="银行名称">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.bankname}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.bankname}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="name"
          header-align="center"
          align="center"
          width="120px"
          label="持卡人">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.name}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.name}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="account"
          header-align="center"
          align="center"
          width="120px"
          label="账号">
          <template slot-scope="scope">
            <a href="javascript:" @click="initAccountList(scope.row.account)">{{ scope.row.account }}</a>
          </template>
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.account}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.account}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="money"
          header-align="center"
          align="center"
          width="100px"
          label="提现金额">
           <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.money}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.money}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="iban"
          header-align="center"
          align="center"
          width="100px"
          label="手续费">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.iban}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.iban}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="dzMoney"
          header-align="center"
          align="center"
          width="100px"
          label="到账金额">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.dzMoney}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.dzMoney}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="status"
          header-align="center"
          align="center"
          width="100px"
          label="状态值">
          <template slot-scope="scope">
            <div v-if="scope.row.status==0">
              <el-tag size="mini" effect="dark">待审核</el-tag>
            </div>
            <div v-else-if="scope.row.status==1">
              <el-tag size="mini" type="success" effect="dark">已打款</el-tag>
            </div>
            <div v-else-if="scope.row.status==2">
              <el-tag size="mini" type="info" effect="dark">已驳回</el-tag>
            </div>
            <div v-else-if="scope.row.status==3">
              <el-tag size="mini" type="danger" effect="dark">代付中</el-tag>
            </div>
            <div v-else-if="scope.row.status==4">
              <el-tag size="mini" type="danger" effect="dark">已发起代付</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="loginIp"
          header-align="center"
          align="center"
          width="120px"
          label="IP地址">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.loginIp}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.loginIp}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="remark"
          header-align="center"
          align="center"
          width="200px"
          label="备注">
        </el-table-column>

        <!--          <el-table-column-->
        <!--          prop="code"-->
        <!--          header-align="center"-->
        <!--          align="center"-->
        <!--          width="120px"-->
        <!--          label="银行代码">-->
        <!--        </el-table-column>-->
        <el-table-column
          prop="salesmanId"
          header-align="center"
          align="center"
          width="120px"
          label="业务员id">
        </el-table-column>
        <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          width="160px"
          label="创建时间">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{ scope.row.createTime|timeStampDateTimeFormat }}</el-tag>
                </div>
                <div v-else>
                  {{ scope.row.createTime|timeStampDateTimeFormat }}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="checkTime"
          header-align="center"
          align="center"
          width="160px"
          label="审核时间">
            <template slot-scope="scope">
                <div v-if="scope.row.bankname == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{ scope.row.checkTime|timeStampDateTimeFormat }}</el-tag>
                </div>
                <div v-else>
                  {{ scope.row.checkTime|timeStampDateTimeFormat }}
                </div>
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
    <!--审核-->
    <check-assembly ref="checkAssembly" @checkSubmit="checkSubmit"></check-assembly>
    <!--审核-->
    <auto-assembly ref="autoAssembly" @checkSubmit="autoSubmit"></auto-assembly>
    <!-- 驳回 -->
    <reject-assembly ref="rejectAssembly" @checkSubmit="rejectSubmit"></reject-assembly>
    <!--点击查看下级-->
    <cust-grade-show ref="custGrad"></cust-grade-show>
    <!-- 相同账号 -->
    <cust-tree ref="custTree"></cust-tree>
  </div>
</template>

<script>
import {timeStampDateTimeFormat, dateFormatStr} from '@/utils/index'
import AddOrUpdate from './commission-add-or-update'
import checkAssembly from "./common/check-assembly";
import AutoAssembly from "./common/auto-assembly";
import RejectAssembly from "./common/reject-assembly";
import SearchUserInput from "@/components/permission/SearchUserInput";
import CustGradeShow from "./cust-grade-show";
import CustTree from "./common/cust-tree";

export default {
  data() {
    return {
      dataForm: {
        userId: '',
        bankName: '',
        mobile:'',
        status: '',
        createTime: null,
        updateTime: null
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
          {filedCode: 'key', filedName: '关键字', filedType: '1'}
        ]
      },
      sum: {
        total: 0,
        success: 0
      },
      succesCount:0,
      auditCount:0
    }
  },
  components: {
    AddOrUpdate,
    checkAssembly,
    SearchUserInput,
    RejectAssembly,
    CustGradeShow,
    CustTree,
    AutoAssembly
  },
  activated() {},
  computed: {
    documentClientHeight() {
      return this.$store.state.common.documentClientHeight
    },
  },
  methods: {
    initAccountList(account) {
      this.$refs.custTree.init(account)
    },
    //点击查看下级
    nextHandle(custId) {
      this.$nextTick(() => {
        this.$refs.custGrad.init(custId)
      })
    },
    searchHandle() {
      this.pageIndex = 1
      this.getDataList()
      this.$http({
        url: this.$http.adornUrl("/cust/commission/count"),
        method: "get",
        params: this.$http.adornParams({
          'key': this.dataForm.key,
          'userId': this.dataForm.userId.trim(),
          'bankName': this.dataForm.bankName.trim(),
          'mobile': this.dataForm.mobile.trim(),
          'status': this.dataForm.status,
          'createTime': this.dataForm.createTime,
          'updateTime': this.dataForm.updateTime,
          "team": this.$refs.teamSearch.getSeletedVal().toString()
        }),
      }).then(({data}) => {
       
        if (data && data.code === 0) {
          let res = data.data;
          console.log(res)
          this.succesCount = res.successCount
          this.auditCount = res.auditCount
        }
      })
    },
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/cust/commission/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'key': this.dataForm.key,
          'userId': this.dataForm.userId.trim(),
          'bankName': this.dataForm.bankName.trim(),
          'mobile': this.dataForm.mobile.trim(),
          'status': this.dataForm.status,
          'createTime': this.dataForm.createTime,
          'updateTime': this.dataForm.updateTime,
          "team": this.$refs.teamSearch.getSeletedVal().toString()
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
    selectable(row, index){
      if(row.status==0){
        return true;
      }
      else{
        return false;
      }
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
    checkMore(){
      this.$nextTick(() => {
        this.$refs.checkAssembly.initMore()
      })
    },
    checkAMore(){
      //判断选择数据全部事A卡数据
      let isTrue = false;
      this.dataListSelections.forEach(item => {
        if(item.bankname!="Airtel Money"){
          isTrue = true;
        }
      });
      if(isTrue){
        this.$message.error("请选择正确数据")
        return
      }
      this.$nextTick(() => {
        this.$refs.autoAssembly.initMore("Airtel Money")
      })
    },
    checkZMore(){
      //判断选择数据全部事A卡数据
      let isTrue = false;
      this.dataListSelections.forEach(item => {
        if(item.bankname!="Zamani"){
          isTrue = true;
        }
      });
      if(isTrue){
        this.$message.error("请选择正确数据")
        return
      }
      this.$nextTick(() => {
        this.$refs.autoAssembly.initMore("Zamani")
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
          url: this.$http.adornUrl('/cust/commission/delete'),
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
    //审核
    checkHandle(id) {
      this.$nextTick(() => {
        this.$refs.checkAssembly.init(id)
      })
    },
    //审核提交
    checkSubmit(val) {
      let ids = val.id ? [val.id] : this.dataListSelections.map(item => {
        return item.id
      })
      let total=ids.length;
      let success=0;
      let err=0;
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      for(let i=0;i<ids.length;i++){
        let params = {
          id: ids[i],
          status: val.checkStatus,
          remark: val.checkStatus==1?'':val.rejectReason
        }
        this.$http({
          url: this.$http.adornUrl(`/cust/commission/check`),
          method: 'post',
          data: this.$http.adornData(params)
        }).then(({data}) => {
          if (data && data.code === 0) {
            success++;
          } else {
            err++;
          }
          if(success+err==total){
            loading.close();
            this.$message({
              message: '操作完成，总条数：'+total+'，成功数：'+success+'，失败数：'+err,
              type: err == 0 ? 'success' : 'danger',
              duration: err == 0 ? 1500 : 3000,
              onClose: () => {
                this.getDataList()
              }
            })
          }
        }).catch(err => {
          loading.close();
        })
      }
    },
    //审核提交
    autoSubmit(val) {
      let ids = val.id ? [val.id] : this.dataListSelections.map(item => {
        return item.id
      })
      let total=ids.length;
      let success=0;
      let err=0;
      const loading = this.$loading({
        lock: true,
        text: 'Loading',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      });
      for(let i=0;i<ids.length;i++){
        let params = {
          id: ids[i],
          status: 3,
          channelId:val.channelId
        }
        this.$http({
          url: this.$http.adornUrl(`/cust/commission/autocheck`),
          method: 'post',
          data: this.$http.adornData(params)
        }).then(({data}) => {
          if (data && data.code === 0) {
            success++;
          } else {
            err++;
          }
          if(success+err==total){
            loading.close();
            this.$message({
              message: '操作完成，总条数：'+total+'，成功数：'+success+'，失败数：'+err,
              type: err == 0 ? 'success' : 'danger',
              duration: err == 0 ? 1500 : 3000,
              onClose: () => {
                this.getDataList()
              }
            })
          }
        }).catch(err => {
          loading.close();
        })
      }
    },
    rejectHandle(id) {
      this.$nextTick(() => {
        this.$refs.rejectAssembly.init(id)
      })
    },
    rejectSubmit(val) {
      let params = {
        id: val.id,
        status: 2,
        remark: val.rejectReason
      }
      this.$http({
        url: this.$http.adornUrl(`/cust/commission/check`),
        method: 'post',
        data: this.$http.adornData(params)
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
    },
    //合计
    getSummaries(param) {
      // const { columns, data } = param;
      // console.log(data)
      // for(let i=0;i<data.length;i++){
      //   this.sum.total=this.sum.total+Number(data[i].money)
      // }
      // console.log(this.sum.total)
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
    excelHandle(exportType) {
      let that = this
      that.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/cust/commission/downExcel"),
        method: "get",
        responseType: "blob", // 服务器返回的数据类型
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'key': this.dataForm.key,
          'userId': this.dataForm.userId.trim(),
          'bankName': this.dataForm.bankName.trim(),
          'mobile': this.dataForm.mobile.trim(),
          'status': this.dataForm.status,
          'createTime': this.dataForm.createTime,
          'updateTime': this.dataForm.updateTime,
          "team": this.$refs.teamSearch.getSeletedVal().toString(),
          "exportType": exportType
        })
      }).then(function (res) {
        that.dataListLoading = false;
        const content = res.data;
        let blob = null
        let fileName = null
        if(exportType == 'excel'){
          blob = new Blob([content], {type: "application/excel"}); // 构造一个blob对象来处理数据
          fileName = dateFormatStr(new Date()) + "_commission.xls"; // 导出文件名
        }else{
          blob = new Blob([content], {type: "application/txt"}); // 构造一个blob对象来处理数据
          fileName = dateFormatStr(new Date()) + "_commission.txt"; // 导出文件名
        }
        // 对于<a>标签，只有 Firefox 和 Chrome（内核） 支持 download 属性
        // IE10以上支持blob但是依然不支持download
        if ("download" in document.createElement("a")) {
          // 支持a标签download的浏览器
          const link = document.createElement("a"); // 创建a标签
          link.download = fileName; // a标签添加属性
          link.style.display = "none";
          link.href = URL.createObjectURL(blob);
          document.body.appendChild(link);
          link.click(); // 执行下载
          URL.revokeObjectURL(link.href); // 释放url
          document.body.removeChild(link); // 释放标签
        } else {
          // 其他浏览器
          navigator.msSaveBlob(blob, fileName);
        }
      });
    }
  }
}
</script>
