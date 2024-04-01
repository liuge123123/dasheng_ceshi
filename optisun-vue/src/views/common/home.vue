<template>
  <div class="home-container">
    <div class="left">
      <!--汇总统计-->
      <div v-loading="loading1">
        <div class="header">
          <div>
            <span>汇总统计</span>
            <i class="el-icon-refresh" @click="getTotalData"></i>
          </div>
          <div style="display: flex">
            <el-date-picker
              size="small"
              v-model="searchForm.dataRang"
              type="datetimerange"
              range-separator="至"
              start-placeholder="开始日期"
              end-placeholder="结束日期"
              @change="getTotalData"
              value-format="yyyy-MM-dd HH:mm:ss"
            >
            </el-date-picker>
            <SearchUserInput @loaded="teamLoadTotal" ref="teamTotalSearch" style="width: 200px;margin-left: 5px"
                             @change="getTotalData"></SearchUserInput>
          </div>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">总充值金额</div>
              <div class="value" v-loading="totalLoading.totalRechargeMoney">{{ totalMdoel.totalRechargeMoney }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">总提现金额</div>
              <div class="value" v-loading="totalLoading.totalGiveMoney">{{ totalMdoel.totalGiveMoney }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">总存取差</div>
              <div class="value" v-loading="totalLoading.totalDiffMoney">{{ totalMdoel.totalDiffMoney }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">总赠送金额</div>
              <div class="value" v-loading="totalLoading.totalFreeMoney">{{ totalMdoel.totalFreeMoney }}</div>
            </div>
          </div>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">总注册人数</div>
              <div class="value" v-loading="totalLoading.totalRegisterNum">{{ totalMdoel.totalRegisterNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">总充值人数</div>
              <div class="value" v-loading="totalLoading.totalRechargeNum">{{ totalMdoel.totalRechargeNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">总提现人数</div>
              <div class="value" v-loading="totalLoading.totalCommissionNum">{{ totalMdoel.totalCommissionNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">总充值笔数</div>
              <div class="value" v-loading="totalLoading.totalRechargeOrderNum">{{ totalMdoel.totalRechargeOrderNum }}</div>
            </div>
          </div>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">待审核提现金额</div>
              <div class="value" v-loading="totalLoading.totalAuditWithdraw">{{ totalMdoel.totalAuditWithdraw }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="total"></icon-svg>
            </div>
            <div class="data">
              <div class="text">当前波比(取款/充值)</div>
              <div class="value" v-loading="totalLoading.currentBB">{{ totalMdoel.currentBB }}%</div>
            </div>
          </div>
          <div class="item">
            
          </div>
          <div class="item">
            
          </div>
        </div>
      </div>
      <!--今日汇总统计-->
      <div v-loading="loading2">
        <div class="header">
          <div>
            <span>今日汇总统计</span>
            <i class="el-icon-refresh" @click="getTodayData"></i>
          </div>
          <SearchUserInput @loaded="teamLoadToday" ref="teamDaySearch" style="width: 200px;"
                           @change="getTodayData"></SearchUserInput>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日新增注册人数</div>
              <div class="value" v-loading="todayLoading.todayRegisterNum">{{ todayModel.todayRegisterNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日登录人数</div>
              <div class="value" v-loading="todayLoading.todayLoginNum">{{ todayModel.todayLoginNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日赠送金额</div>
              <div class="value" v-loading="todayLoading.todayFreeMoney">{{ todayModel.todayFreeMoney }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日充值金额</div>
              <div class="value" v-loading="todayLoading.todayRechargeMoney">{{ todayModel.todayRechargeMoney }}</div>
            </div>
          </div>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日充值人数</div>
              <div class="value" v-loading="todayLoading.todayRechargeNum">{{ todayModel.todayRechargeNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日充值笔数</div>
              <div class="value" v-loading="todayLoading.todayRechargeOrderNum">{{ todayModel.todayRechargeOrderNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日首充人数</div>
              <div class="value" v-loading="todayLoading.todayFirstRechargeNum">{{ todayModel.todayFirstRechargeNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日首充金额</div>
              <div class="value" v-loading="todayLoading.todayFirstRechargeMoney">{{ todayModel.todayFirstRechargeMoney }}</div>
            </div>
          </div>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日任务做单人数</div>
              <div class="value"  v-loading="todayLoading.todayOrderNum">{{ todayModel.todayOrderNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <!-- <div class="data">
              <div class="text">今日任务做单佣金</div>
              <div class="value"  v-loading="todayLoading.todayTaskMoney">{{ todayModel.todayTaskMoney }}</div>
            </div> -->
            <div class="data">
              <div class="text">今日领取收益人数</div>
              <div class="value"  v-loading="todayLoading.todayProfitNum">{{ todayModel.todayProfitNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日提现金额</div>
              <div class="value"  v-loading="todayLoading.todayGiveMoney">{{ todayModel.todayGiveMoney }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="today"></icon-svg>
            </div>
            <div class="data">
              <div class="text">今日提现人数</div>
              <div class="value"  v-loading="todayLoading.todayCommissionNum">{{ todayModel.todayCommissionNum }}</div>
            </div>
          </div>
        </div>
      </div>
      <!--昨日汇总统计-->
      <div v-loading="loading3">
        <div class="header">
          <div>
            <span>昨日汇总统计</span>
            <i class="el-icon-refresh" @click="getYesterdayData"></i>
          </div>
          <SearchUserInput @loaded="teamLoadYesterday" ref="teamYesterDaySearch" style="width: 200px;"
                           @change="getYesterdayData"></SearchUserInput>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <div class="data">
              <div class="text">昨日新增注册人数</div>
              <div class="value" v-loading="yesterdayLoading.yesterdayRegisterNum">{{ yesterdayModel.yesterdayRegisterNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <div class="data">
              <div class="text">昨日充值金额</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayRechargeMoney">{{ yesterdayModel.yesterdayRechargeMoney }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <div class="data">
              <div class="text">昨日充值人数</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayRechagreNum">{{ yesterdayModel.yesterdayRechagreNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <div class="data">
              <div class="text">昨日充值笔数</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayRechargeOrderNum">{{ yesterdayModel.yesterdayRechargeOrderNum }}</div>
            </div>
          </div>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <div class="data">
              <div class="text">昨日任务做单人数</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayOrderNum">{{ yesterdayModel.yesterdayOrderNum }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <!-- <div class="data">
              <div class="text">昨日任务做单佣金</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayTaskMoney">{{ yesterdayModel.yesterdayTaskMoney }}</div>
            </div> -->
            <div class="data">
              <div class="text">昨日领取收益人数</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayProfitNum">{{ yesterdayModel.yesterdayProfitNum }}</div>
            </div>
            
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <div class="data">
              <div class="text">昨日提现金额</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayGiveMoney">{{ yesterdayModel.yesterdayGiveMoney }}</div>
            </div>
          </div>
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <div class="data">
              <div class="text">昨日提现人数</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayCommissionNum">{{ yesterdayModel.yesterdayCommissionNum }}</div>
            </div>
          </div>
        </div>
        <div class="row1">
          <div class="item">
            <div>
              <icon-svg class="icon" name="history"></icon-svg>
            </div>
            <div class="data">
              <div class="text">昨日首充人数</div>
              <div class="value"  v-loading="yesterdayLoading.yesterdayRechargeFisrtCustNum">{{ yesterdayModel.yesterdayRechargeFisrtCustNum }}</div>
            </div>
          </div>
          <div class="item"></div>
          <div class="item"></div>
          <div class="item"></div>
        </div>
      </div>
      <!--图表-->
      <div class="header">
        <div>
          <span>注册用户数统计</span>
          <i class="el-icon-refresh" @click="getRegisterList"></i>
        </div>
        <SearchUserInput @loaded="teamLoadRegister" ref="teamRegisterSearch" style="width: 200px;"
                         @change="getRegisterList"></SearchUserInput>
      </div>
      <div class="row2">
        <el-card class="card">
          <div class="header">注册用户数统计</div>
          <line-chart :chartData="custRegister"></line-chart>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import LineChart from "./dashboard/LineChart";
import SearchUserInput from "@/components/permission/SearchUserInput";

export default {
  name: "home",
  components: {
    LineChart,
    SearchUserInput
  },
  data() {
    return {
      searchForm: {
        dataRang: []
      },
      custRegister: {
        name: "注册用户数统计",
        xData: [],
        valData: []
      },
      loading1: false,
      loading2: false,
      loading3: false,
      totalMdoel: {
        totalRechargeMoney: 0,
        totalGiveMoney: 0,
        totalFreeMoney: 0,
        totalDiffMoney: 0,
        totalRechargeNum: 0,
        totalCommissionNum: 0,
        totalRegisterNum: 0,
        totalRechargeOrderNum: 0,
        totalAuditWithdraw:0,
        currentBB:0
      },
      totalLoading: {
        totalRechargeMoney: false,
        totalGiveMoney: false,
        totalFreeMoney: false,
        totalDiffMoney: false,
        totalRechargeNum: false,
        totalCommissionNum: false,
        totalRegisterNum: false,
        totalRechargeOrderNum: false,
        totalAuditWithdraw:false,
        currentBB:false
      },
      todayModel: {
        todayRegisterNum: 0,
        todayLoginNum: 0,
        todayFreeMoney: 0,
        todayRechargeMoney: 0,
        todayRechargeNum: 0,
        todayFirstRechargeNum: 0,
        todayFirstRechargeMoney: 0,
        todayOrderNum: 0,
        todayTaskMoney: 0,
        todayProfitNum:0,
        todayGiveMoney: 0,
        todayCommissionNum: 0,
        todayRechargeOrderNum: 0
      },
      todayLoading: {
        todayRegisterNum: false,
        todayLoginNum: false,
        todayFreeMoney: false,
        todayRechargeMoney: false,
        todayRechargeNum: false,
        todayFirstRechargeNum: false,
        todayFirstRechargeMoney: false,
        todayOrderNum: false,
        todayTaskMoney: false,
        todayProfitNum:false,
        todayGiveMoney: false,
        todayCommissionNum: false,
        todayRechargeOrderNum: false
      },
      yesterdayModel: {
        yesterdayRegisterNum: 0,
        yesterdayRechargeMoney: 0,
        yesterdayRechagreNum: 0,
        yesterdayOrderNum: 0,
        yesterdayTaskMoney: 0,
        yesterdayProfitNum:0,
        yesterdayGiveMoney: 0,
        yesterdayCommissionNum: 0,
        yesterdayRechargeOrderNum: 0,
        yesterdayRechargeFisrtCustNum: 0
      },
      yesterdayLoading: {
        yesterdayRegisterNum: false,
        yesterdayRechargeMoney: false,
        yesterdayRechagreNum: false,
        yesterdayOrderNum: false,
        yesterdayTaskMoney: false,
        yesterdayProfitNum:false,
        yesterdayGiveMoney: false,
        yesterdayCommissionNum: false,
        yesterdayRechargeOrderNum: false,
        yesterdayRechargeFisrtCustNum: false
      }
    }
  },
  mounted() {
  },
  created() {
  },

  methods: {
    getTotalData(){
      for (const key in this.totalMdoel) {
        this.getTotalDataHandle(key)
      }
    },
    //汇总统计
    getTotalDataHandle(key) {
      this.totalLoading[key] = true
      let startTime = null
      let endTime = null
      if (this.searchForm.dataRang.length > 0) {
        startTime = this.searchForm.dataRang[0]
        endTime = this.searchForm.dataRang[1]
      }
      this.$http({
        url: this.$http.adornUrl("/sys/home/getHomeTotalInfo"),
        method: "get",
        params: this.$http.adornParams({
          "team": this.$refs.teamTotalSearch.getSeletedVal().toString(),
          "startTime": startTime,
          "endTime": endTime,
          "key": key
        }),
      }).then(({data}) => {
        this.totalLoading[key] = false
        if (data && data.code === 0) {
          let res = data.data;
          this.totalMdoel[key] = res[key]
          // this.totalMdoel = {
          //   totalRechargeMoney: res.totalRechargeMoney,
          //   totalGiveMoney: res.totalGiveMoney,
          //   totalFreeMoney: res.totalFreeMoney,
          //   totalDiffMoney: res.totalDiffMoney,
          //   totalRechargeNum: res.totalRechargeNum,
          //   totalCommissionNum: res.totalCommissionNum,
          //   totalRegisterNum: res.totalRegisterNum,
          //   totalRechargeOrderNum: res.totalRechargeOrderNum
          // }
        }
      })
    },
    getTodayData(){
      for (const key in this.todayModel) {
        this.getTodayDataHandle(key)
      }
    },
    //今日汇总统计
    getTodayDataHandle(key) {
      this.todayLoading[key] = true
      this.$http({
        url: this.$http.adornUrl("/sys/home/getHomeToday"),
        method: "get",
        params: this.$http.adornParams({
          "team": this.$refs.teamDaySearch.getSeletedVal().toString(),
          "key": key
        }),
      }).then(({data}) => {
        this.todayLoading[key] = false
        if (data && data.code === 0) {
          let res = data.data;
          this.todayModel[key] = res[key]
          // this.todayModel = {
          //   todayRegisterNum: res.todayRegisterNum,
          //   todayLoginNum: res.todayLoginNum,
          //   todayFreeMoney: res.todayFreeMoney,
          //   todayRechargeMoney: res.todayRechargeMoney,
          //   todayRechargeNum: res.todayRechargeNum,
          //   todayFirstRechargeNum: res.todayFirstRechargeNum,
          //   todayFirstRechargeMoney: res.todayFirstRechargeMoney,
          //   todayOrderNum: res.todayOrderNum,
          //   todayTaskMoney: res.todayTaskMoney,
          //   todayGiveMoney: res.todayGiveMoney,
          //   todayCommissionNum: res.todayCommissionNum,
          //   todayRechargeOrderNum: res.todayRechargeOrderNum
          // }
        }
      })
    },
    getYesterdayData(){
      for (const key in this.yesterdayModel) {
        this.getYesterdayDataHandle(key)
      }
    },
    //获取昨日统计
    getYesterdayDataHandle(key) {
      this.yesterdayLoading[key] = true
      this.$http({
        url: this.$http.adornUrl("/sys/home/getHomeYesterday"),
        method: "get",
        params: this.$http.adornParams({
          "team": this.$refs.teamYesterDaySearch.getSeletedVal().toString(),
          "key": key
        }),
      }).then(({data}) => {
        this.yesterdayLoading[key] = false
        if (data && data.code === 0) {
          let res = data.data;
          this.yesterdayModel[key] = res[key]
          // this.yesterdayModel = {
          //   yesterdayRegisterNum: res.yesterdayRegisterNum,
          //   yesterdayRechargeMoney: res.yesterdayRechargeMoney,
          //   yesterdayRechagreNum: res.yesterdayRechagreNum,
          //   yesterdayOrderNum: res.yesterdayOrderNum,
          //   yesterdayTaskMoney: res.yesterdayTaskMoney,
          //   yesterdayGiveMoney: res.yesterdayGiveMoney,
          //   yesterdayCommissionNum: res.yesterdayCommissionNum,
          //   yesterdayRechargeOrderNum: res.yesterdayRechargeOrderNum,
          //   yesterdayRechargeFisrtCustNum: res.yesterdayRechargeFisrtCustNum
          // }
        }
      })
    },
    getRegisterList() {
      this.$http({
        url: this.$http.adornUrl("/sys/home/getRegisterList"),
        method: "get",
        params: this.$http.adornParams({
          "team": this.$refs.teamRegisterSearch.getSeletedVal().toString()
        }),
      }).then(({data}) => {
        console.log(data)
        if (data && data.code === 0) {
          let res = data.data;
          res.forEach((item) => {
            this.custRegister.xData.push(item.date);
            this.custRegister.valData.push(item.num);
          });
        }
      });
    },
    //团队 总
    teamLoadTotal(status, data) {
      if (status) {
        this.getTotalData();
      } else {
        this.$message({
          showClose: true,
          message: '数据加载失败，请联系管理员！',
          type: 'error'
        });
      }
    },
    // 团队 今日
    teamLoadToday(status, data) {
      if (status) {
        this.getTodayData();
      } else {
        this.$message({
          showClose: true,
          message: '数据加载失败，请联系管理员！',
          type: 'error'
        });
      }
    },
    // 团队 昨日
    teamLoadYesterday(status, data) {
      if (status) {
        this.getYesterdayData();
      } else {
        this.$message({
          showClose: true,
          message: '数据加载失败，请联系管理员！',
          type: 'error'
        });
      }
    },
    // 团队 注册统计
    teamLoadRegister(status, data) {
      if (status) {
        this.getRegisterList();
      } else {
        this.$message({
          showClose: true,
          message: '数据加载失败，请联系管理员！',
          type: 'error'
        });
      }
    }
  },
};
</script>

<style lang="scss" scoped>
.home-container {
  display: flex;
  padding: 20px;

  .left {
    flex: 1;
    margin-right: 15px;

    .header {
      display: flex;
      justify-content: space-between;
      margin-left: 5px;
      font-size: 18px;
      color: #909399;
      padding-top: 10px;
    }

    .row1 {
      display: flex;

      .item {
        display: flex;
        align-items: center;
        background: #fff;
        padding-left: 20px;
        padding-top: 20px;
        border-bottom: 1px solid #EEEEEE;
        //padding: 20px;
        //box-sizing: border-box;
        flex: 1;
        margin-top: 10px;
        margin-right: 10px;
        // border-radius: 4px;
        color: #909399;
        height: 110px;
        position: relative;
        overflow: hidden;

        &:last-child {
          margin-right: 0;
        }

        .data {
          display: flex;
          flex-direction: column;
          justify-content: center;

          .text {
            font-size: 12px;
            margin: 5px 0 5px;
          }

          .value {
            font-size: 40px;
            color: #333333;;
          }
        }

        .icon {
          width: 45px;
          height: 45px;
          margin-right: 10px;
        }
      }
    }

    .row2 {
      margin-top: 10px;
    }
  }
}
</style>

