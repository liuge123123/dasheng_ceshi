<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div style="display: flex;margin-bottom: 10px">
        <div>
          <el-button icon="el-icon-check"  v-if="isAuth('cust:recharge:check')"   size="small"
                     @click="checkMore()" :disabled="dataListSelections.length <= 0">批量审核
          </el-button>

          <el-button
            v-if="isAuth('cust:recharge:excel')"
            icon="el-icon-folder-opened"
            size="small"
            @click="excelHandle"
            plain>导出excel</el-button>
            <el-tag type="success">已充值金额:{{succesCount}}</el-tag>
        </div>
        <div  style="flex: 1;display: flex;justify-content: flex-end;align-items: center">
          <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="searchHandle()"></SearchUserInput>
          <el-input v-model="dataForm.bankName" clearable style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="银行名称">
          </el-input>
          <el-select v-model="dataForm.status" clearable style="width:100px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="状态">
            <el-option value="-1" label="全部"></el-option>
            <el-option value="0" label="待审核"></el-option>
            <el-option value="1" label="已审核"></el-option>
            <el-option value="2" label="被驳回"></el-option>
          </el-select>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.createTime" clearable  style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="创建时间">
          </el-date-picker>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.updateTime" clearable style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="更新时间">
          </el-date-picker>
          <el-input v-model="dataForm.key" clearable style="width:200px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="用户ID、交易ID"></el-input>
          <el-input v-model="dataForm.mobile" clearable style="width:200px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="电话号码"></el-input>
          <el-select v-model="dataForm.moneyTypeName" clearable style="width:100px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="银行卡类型">
            <el-option value="-1" label="全部"></el-option>
            <el-option value="Airtel" label="Airtel"></el-option>
            <el-option value="Moov" label="Moov"></el-option>
            <el-option value="Zamani" label="Zamani"></el-option>
          </el-select>
          <el-button icon="el-icon-search" type="primary" size="small" @click="searchHandle()">查询</el-button>
        </div>
      </div>
      <el-table
        :data="dataList"
        size="small"
        v-loading="dataListLoading"
        :height="documentClientHeight - 220"
        @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <el-table-column
          type="selection"
          header-align="center"
          align="center"
          :selectable='selectable'
          width="50">
        </el-table-column>
        <el-table-column
          header-align="center"
          align="center"
          width="180"
          label="操作">

          <template slot-scope="scope">
            <el-button icon="el-icon-check" v-if="scope.row.status==0 && isAuth('cust:recharge:check')"  type="text" size="small"
                       @click="checkHandle(scope.row.rechargeId)">审核
            </el-button>
            <el-button icon="el-icon-close" v-if="scope.row.status==1 && isAuth('cust:recharge:check')"  type="text" size="small"
                       @click="rejectHandle(scope.row.rechargeId)">驳回
            </el-button>
            <el-button icon="el-icon-edit" v-if="isAuth('cust:recharge:update') && scope.row.status==0" type="text" size="small"
                       @click="addOrUpdateHandle(scope.row)">修改
            </el-button>
          </template>
        </el-table-column>
        <!-- <el-table-column
                type="index"
                header-align="center"
                label="#"
                align="center">
          <template slot-scope="scope">
            {{pageSize * (pageIndex - 1) + scope.$index + 1}}
          </template>
        </el-table-column> -->
        <el-table-column
          prop="rechargeId"
          header-align="center"
          align="center"
         
          label="ID">
        </el-table-column>
        <el-table-column
          prop="status"
          header-align="center"
          align="center"
          width="120px"
          label="状态值">
            <template slot-scope="scope">
              <div v-if="scope.row.status==0">
                <el-tag  effect="dark">待审核</el-tag>
              </div>
              <div v-else-if="scope.row.status==1">
                <el-tag type="success" effect="dark">已付款</el-tag>
              </div>
              <div v-else-if="scope.row.status==2">
                <el-tag type="info" effect="dark">已驳回</el-tag>
              </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="orderCode"
          header-align="center"
          align="center"
          width="180px"
          label="订单号">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.orderCode}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.orderCode}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="custId"
          header-align="center"
          align="center"
          width="120px"
          label="用户编号">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
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
          width="120px"
          label="用户名">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.custName}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.custName}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="isFirst"
          header-align="center"
          align="center"
          width="100px"
          label="是否首充">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.isFirst==1" type="success" effect="dark">是</el-tag>
            <el-tag type="info" v-else effect="dark">否</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="moneyFront"
          header-align="center"
          align="center"
          width="150px"
          label="换算前充值金额">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.moneyFront}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.moneyFront}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="amount"
          header-align="center"
          align="center"
          width="120px"
          label="充值金额">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.amount}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.amount}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="type"
          header-align="center"
          align="center"
          width="120px"
          label="充值类型">
          <template slot-scope="scope">
            <div v-if="scope.row.type==1">
              <el-tag type="success" effect="dark">充值-web端</el-tag>
            </div>
            <div v-else-if="scope.row.type==2">
              <el-tag type="danger" effect="dark">赠送</el-tag>
            </div>
            <div v-else-if="scope.row.type==3">
              <el-tag type="info" effect="dark">签到</el-tag>
            </div>
            <div v-else-if="scope.row.type==4">
              <el-tag type="success" effect="dark">充值-后台</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="moneytypename"
          header-align="center"
          align="center"
          width="120px"
          label="充值名称">

            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.moneytypename}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.moneytypename}}
                </div>
            </template>

        </el-table-column>
        <el-table-column
          prop="transid"
          header-align="center"
          align="center"
          width="300px"
          label="交易id">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.transid}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.transid}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="salesmanId"
          header-align="center"
          align="center"
          width="120px"
          label="业务员id">
        </el-table-column>
        <el-table-column
          prop="fee"
          header-align="center"
          align="center"
          width="120px"
          label="对美元费率">
        </el-table-column>
        
        <el-table-column prop="platformBankName"
           header-align="center"
           align="center"
           width="120px"
           label="钱包类型">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.platformBankName}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.platformBankName}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="checkRemark"
          header-align="center"
          align="center"
          width="200px"
          label="备注">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark">{{scope.row.checkRemark}}</el-tag>
                </div>
                <div v-else>
                  {{scope.row.checkRemark}}
                </div>
            </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          header-align="center"
          align="center"
          width="160px"
          label="建立时间">
            <template slot-scope="scope">
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark"><div>{{scope.row.createTime | timeStampDateTimeFormat}}</div></el-tag>
                </div>
                <div v-else>
                  <div>{{scope.row.createTime | timeStampDateTimeFormat}}</div>
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
                <div v-if="scope.row.moneytypename == 'Zamani'">
                  <el-tag size="mini" type="danger" effect="dark"><div>{{scope.row.checkTime | timeStampDateTimeFormat}}</div></el-tag>
                </div>
                <div v-else>
                  <div>{{scope.row.checkTime | timeStampDateTimeFormat}}</div>
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
    <!--审核-->
    <check-assembly ref="checkAssembly" @checkSubmit="checkSubmit"></check-assembly>
    <!--修改充值订单-->
     <recharge-update ref="rechargeUpdate" @back="backHandle"></recharge-update>
    <!-- 驳回 -->
    <reject-assembly ref="rejectAssembly" @checkSubmit="rejectSubmit"></reject-assembly>
  </div>
</template>

<script>
  import {timeStampDateTimeFormat,dateFormatStr} from '@/utils/index'
  import checkAssembly from "./common/check-assembly";
  import SearchUserInput from "@/components/permission/SearchUserInput";
  import rechargeUpdate from "./recharge-update";
  import RejectAssembly from "./common/reject-assembly";
  export default {
    data () {
      return {
        dataForm: {
          key:'',
          bankName: '',
          status: '',
          moneyTypeName: '',
          createTime: null,
          updateTime: null,
          mobile:''
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
            { filedCode: 'key', filedName: '关键字', filedType: '1'}
          ]
        },
        succesCount:0
      }
    },
    components: {
      checkAssembly,
      SearchUserInput,
      rechargeUpdate,
      RejectAssembly
    },
    activated () {
    },
    computed: {
      documentClientHeight(){
        return this.$store.state.common.documentClientHeight
      },
    },
    methods: {
      checkMore(){
        this.$nextTick(() => {
          this.$refs.checkAssembly.initMore()
        })
      },
      selectable(row, index){
        if(row.status==0){
          return true;
        }
        else{
          return false;
        }
      },
      searchHandle(){
        this.pageIndex = 1
        this.getDataList()
        this.$http({
        url: this.$http.adornUrl("/cust/recharge/count"),
        method: "get",
        params: this.$http.adornParams({
          'bankName': this.dataForm.bankName.trim(),
          'status': this.dataForm.status,
          'moneyTypeName': this.dataForm.moneyTypeName.trim(),
          'createTime': this.dataForm.createTime,
          'updateTime': this.dataForm.updateTime,
          "team":this.$refs.teamSearch.getSeletedVal().toString(),
          'key':this.dataForm.key,
          'mobile':this.dataForm.mobile
        }),
      }).then(({data}) => {
       
        if (data && data.code === 0) {
          let res = data.data;
          this.succesCount = res.successCount
        }
      })
      },
      // 获取数据列表
      getDataList () {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/cust/recharge/list'),
          method: 'get',
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'bankName': this.dataForm.bankName.trim(),
            'status': this.dataForm.status,
            'moneyTypeName': this.dataForm.moneyTypeName.trim(),
            'createTime': this.dataForm.createTime,
            'updateTime': this.dataForm.updateTime,
            "team":this.$refs.teamSearch.getSeletedVal().toString(),
            'key':this.dataForm.key,
            'mobile':this.dataForm.mobile
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
      addOrUpdateHandle (row) {
        this.$nextTick(() => {
          this.$refs.rechargeUpdate.init(row)
        })
      },
      // 删除
      deleteHandle (id) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.rechargeId
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/cust/recharge/delete'),
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
        if(options.refresh){
          this.getDataList()
        }
      },
      //审核
      checkHandle(id){
        this.$nextTick(() => {
          this.$refs.checkAssembly.init(id)
        })
      },
      //审核提交
      checkSubmit(val){
        let ids = val.id ? [val.id] : this.dataListSelections.map(item => {
          return item.rechargeId
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
            rechargeId: ids[i],
            status: val.checkStatus,
            remark: val.rejectReason
          }
          this.$http({
            url: this.$http.adornUrl(`/cust/recharge/check`),
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
      // 驳回
      rejectHandle(id){
        this.$nextTick(() => {
          this.$refs.rejectAssembly.init(id)
        })
      },
      // 驳回提交
      rejectSubmit(val){
        let params = {
          rechargeId: val.id,
          status: 2,
          remark: val.rejectReason
        }
        this.$http({
          url: this.$http.adornUrl(`/cust/recharge/reject`),
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
      //团队
      teamLoad(status, data){
        if(status){
          this.searchHandle()
        }else{
          this.$message({
            showClose: true,
            message: '数据加载失败，请联系管理员！',
            type: 'error'
          });
        }
      },
      //导出
      excelHandle(){
        let that = this
        that.dataListLoading = true;
        this.$http({
          url: this.$http.adornUrl("//cust/recharge//downExcel"),
          method: "get",
          responseType: "blob", // 服务器返回的数据类型
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'bankName': this.dataForm.bankName.trim(),
            'status': this.dataForm.status,
            'moneyTypeName': this.dataForm.moneyTypeName,
            'createTime': this.dataForm.createTime,
            'updateTime': this.dataForm.updateTime,
            "team":this.$refs.teamSearch.getSeletedVal().toString(),
            'key':this.dataForm.key,
            'mobile':this.dataForm.mobile
          })
        }).then(function(res) {
          that.dataListLoading=false;
          const content = res.data;
          const blob = new Blob([content], { type: "application/excel" }); // 构造一个blob对象来处理数据
          const fileName = dateFormatStr(new Date()) + "_recharge.xls"; // 导出文件名
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
