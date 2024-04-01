<template>
  <div class="mod-config">
    <el-card v-if="!addOrUpdateVisible">
      <div style="display: flex;margin-bottom: 10px">
        <div>
          <el-button icon="el-icon-plus" v-if="isAuth('s:ssmsoriginal:save')" type="primary"
            @click="addOrUpdateHandle()">新增
          </el-button>
          <!--          <el-button icon="el-icon-delete" v-if="isAuth('s:ssmsoriginal:delete')" type="danger" @click="deleteHandle()"-->
          <!--                     :disabled="dataListSelections.length <= 0">批量删除-->
          <!--          </el-button>-->
          <el-button v-if="isAuth('s:ssmsoriginal:excel')" icon="el-icon-folder-opened" size="small" @click="excelHandle"
            plain>导出excel</el-button>
          <el-tag type="success">已上分金额:{{succesCount}}</el-tag>
          <el-tag type="danger">未上分金额:{{auditCount}}</el-tag>
        </div>
        <div style="flex: 1;display: flex;justify-content: flex-end;align-items: center; flex-wrap: wrap">
          <el-input v-model="dataForm.key" clearable style="width:200px;margin-left:5px;margin-right:5px" size="small"
            clearable placeholder="短信关键字">
          </el-input>
          <el-input v-model="dataForm.transId" clearable style="width:200px;margin-left:5px;margin-right:5px" size="small"
            clearable placeholder="交易ID">
          </el-input>
          <el-input v-model="dataForm.money" clearable style="width:150px;margin-left:5px;margin-right:5px" size="small"
            clearable placeholder="充值金额">
          </el-input>
          <el-select v-model="dataForm.parseStatus" clearable style="width:100px;margin-left:5px;margin-right:5px"
            size="small" clearable placeholder="解析状态">
            <el-option value="" label="全部"></el-option>
            <el-option value="0" label="未解析"></el-option>
            <el-option value="1" label="解析失败"></el-option>
            <el-option value="2" label="解析成功"></el-option>
          </el-select>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.receiveTime" clearable
            style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="接收时间">
          </el-date-picker>
          <el-select v-model="dataForm.isDone" clearable style="width:100px;margin-left:5px;margin-right:5px" size="small"
            clearable placeholder="上分状态">
            <el-option value="" label="全部"></el-option>
            <el-option value="0" label="未上分"></el-option>
            <el-option value="1" label="已上分"></el-option>
            <el-option value="2" label="重复信息"></el-option>
            <el-option value="3" label="已过期"></el-option>
          </el-select>
          <el-date-picker value-format="yyyy-MM-dd" v-model="dataForm.doneTime" clearable
            style="width:150px;margin-left:5px;margin-right:5px" size="small" clearable placeholder="上分时间">
          </el-date-picker>
          <el-select v-model="dataForm.wallet" clearable style="width:100px;margin-left:5px;margin-right:5px"
            size="small" clearable placeholder="钱包名称">
            <el-option value="" label="全部"></el-option>
            <el-option value="Airtel Money" label="Airtel Money"></el-option>
            <el-option value="Moov Money" label="Moov Money"></el-option>
            <el-option value="Zamani" label="Zamani"></el-option>
          </el-select>
          <el-button icon="el-icon-search" type="primary" size="small" @click="searchHandle()">查询</el-button>
        </div>
      </div>
      <el-table :data="dataList" border size="small" v-loading="dataListLoading" @selection-change="selectionChangeHandle"
        style="width: 100%;">
        <!--        <el-table-column-->
        <!--          type="selection"-->
        <!--          header-align="center"-->
        <!--          align="center"-->
        <!--          width="50">-->
        <!--        </el-table-column>-->
        <el-table-column type="index" header-align="center" align="center" label="#">
          <template slot-scope="scope">
            {{ pageSize * (pageIndex - 1) + scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="comeTo" header-align="center" align="left" label="来源" width="100">
        </el-table-column>
        <el-table-column prop="allContent" header-align="center" align="left" min-width="300" label="短信内容">
        </el-table-column>
        <el-table-column prop="receiveTime" header-align="center" align="center" label="接收时间" width="150">
          <template slot-scope="scope">
            <div>{{ scope.row.receiveTime / 1000 | timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="parseStatus" header-align="center" align="center" label="解析状态" width="100">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary" v-if="scope.row.parseStatus == 0">未解析</el-tag>
            <el-tag size="mini" type="danger" v-if="scope.row.parseStatus == 1">解析失败</el-tag>
            <el-tag size="mini" type="success" v-if="scope.row.parseStatus == 2">解析成功</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transId" header-align="center" align="center" label="交易ID" width="200">
        </el-table-column>
        <el-table-column prop="transDate" header-align="center" align="center" label="交易时间" width="150">
        </el-table-column>
        <el-table-column prop="money" header-align="center" align="center" width="120" label="交易金额">
        </el-table-column>
        <el-table-column prop="isDone" header-align="center" align="center" width="100" label="上分状态">
          <template slot-scope="scope">
            <el-tag size="mini" type="primary" v-if="scope.row.isDone == 0">未上分</el-tag>
            <el-tag size="mini" type="success" v-if="scope.row.isDone == 1">已上分</el-tag>
            <el-tag size="mini" type="warn" v-if="scope.row.isDone == 2">重复信息</el-tag>
            <el-tag size="mini" type="info" v-if="scope.row.isDone == 3">已过期</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="doneTime" header-align="center" align="center" width="150" label="上分时间">
          <template slot-scope="scope">
            <div>{{ scope.row.doneTime / 1000 | timeStampDateTimeFormat }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="wallet" header-align="center" align="center" width="120" show-tooltip-when-overflow
          label="所属钱包">
        </el-table-column>
        <el-table-column prop="remark" header-align="center" align="center" width="120" show-tooltip-when-overflow
          label="备注">
        </el-table-column>
        <!--        <el-table-column-->
        <!--          fixed="right"-->
        <!--          header-align="center"-->
        <!--          align="center"-->
        <!--          width="150"-->
        <!--          label="操作">-->
        <!--          <template slot-scope="scope">-->
        <!--            <el-button icon="el-icon-edit" v-if="isAuth('s:ssmsoriginal:update')" type="text" size="small"-->
        <!--                       @click="addOrUpdateHandle(scope.row.id)">修改-->
        <!--            </el-button>-->
        <!--            <el-button icon="el-icon-delete" v-if="isAuth('s:ssmsoriginal:delete')" type="text" size="small"-->
        <!--                       @click="deleteHandle(scope.row.id)">删除-->
        <!--            </el-button>-->
        <!--          </template>-->
        <!--        </el-table-column>-->
      </el-table>
      <el-pagination @size-change="sizeChangeHandle" @current-change="currentChangeHandle" :current-page="pageIndex"
        :page-sizes="[10, 20, 50, 100]" :page-size="pageSize" :total="totalPage"
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
    </el-card>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @back="backHandle"></add-or-update>
  </div>
</template>

<script>
import {timeStampDateTimeFormat,dateFormatStr} from '@/utils/index'
import AddOrUpdate from './ssmsoriginal-add-or-update'

export default {
  data() {
    return {
      dataForm: {
        key: '',
        parseStatus: '',
        receiveTime: '',
        transId: '',
        isDone: '',
        doneTime: '',
        money: '',
        wallet:''
      },
      dataList: [],
      pageIndex: 1,
      pageSize: 10,
      totalPage: 0,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      succesCount:0,//已上分金额
      auditCount:0//未上分金额
    }
  },
  components: {
    AddOrUpdate
  },
  activated() {
    this.getDataList()
    this.getDataCount()
  },
  methods: {
    searchHandle() {
      this.pageIndex = 1
      this.getDataList()
      this.getDataCount()
    },
    getDataCount() {
      this.$http({
        url: this.$http.adornUrl("/s/ssmsoriginal/count"),
        method: "get",
        params: this.$http.adornParams({
          'wallet': this.dataForm.wallet
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
        url: this.$http.adornUrl('/s/ssmsoriginal/list'),
        method: 'get',
        params: this.$http.adornParams({
          'page': this.pageIndex,
          'limit': this.pageSize,
          'key': this.dataForm.key,
          'parseStatus': this.dataForm.parseStatus,
          'receiveTime': this.dataForm.receiveTime,
          'transId': this.dataForm.transId,
          'transDate': this.dataForm.transDate,
          'isDone': this.dataForm.isDone,
          'doneTime': this.dataForm.doneTime,
          'money': this.dataForm.money,
          'wallet':this.dataForm.wallet,
        })
      }).then(({ data }) => {
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
          url: this.$http.adornUrl('/s/ssmsoriginal/delete'),
          method: 'post',
          data: this.$http.adornData(ids, false)
        }).then(({ data }) => {
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
    //导出
    excelHandle(){
        let that = this
        that.dataListLoading = true;
        this.$http({
          url: this.$http.adornUrl("/s/ssmsoriginal/downExcel"),
          method: "get",
          responseType: "blob", // 服务器返回的数据类型
          params: this.$http.adornParams({
            'page': this.pageIndex,
            'limit': this.pageSize,
            'key': this.dataForm.key,
            'parseStatus': this.dataForm.parseStatus,
            'receiveTime': this.dataForm.receiveTime,
            'transId': this.dataForm.transId,
            'isDone': this.dataForm.isDone,
            'doneTime': this.dataForm.doneTime,
            'money': this.dataForm.money
          })
        }).then(function(res) {
          that.dataListLoading=false;
          const content = res.data;
          const blob = new Blob([content], { type: "application/excel" }); // 构造一个blob对象来处理数据
          const fileName = dateFormatStr(new Date()) + "_sms.xls"; // 导出文件名
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
