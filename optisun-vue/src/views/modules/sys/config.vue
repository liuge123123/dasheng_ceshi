<template>
  <div>
    <el-card class="mod-config"  v-if="!addOrUpdateVisible">
      <el-form :inline="true" :model="dataForm" @keyup.enter.native="getDataList()" size="mini">
        <el-form-item>
          <el-input v-model="dataForm.paramKey" placeholder="参数名" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button @click="getDataList()">查询</el-button>
          <el-button type="primary" @click="addOrUpdateHandle()">新增</el-button>
          <el-button
            type="danger"
            @click="deleteHandle()"
            :disabled="dataListSelections.length <= 0"
          >批量删除</el-button>
        </el-form-item>
      </el-form>
      <el-table
        :data="searchList"
        border
        size="small"
        v-loading="dataListLoading"
        @selection-change="selectionChangeHandle"
        style="width: 100%;"
      >
        <el-table-column type="selection" header-align="center" align="center" width="50"></el-table-column>
        <el-table-column type="index" header-align="center" align="center" width="80"></el-table-column>
        <el-table-column prop="paramKey" header-align="center" align="center" label="参数名"></el-table-column>
        <el-table-column prop="paramValue" header-align="center" align="center" label="参数值"></el-table-column>
        <el-table-column prop="remark" header-align="center" align="center" label="备注"></el-table-column>
        <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
            <el-button type="text" size="small" @click="deleteHandle(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @back="backHandle"></add-or-update>
  </div>
</template>

<script>
import AddOrUpdate from "./config-add-or-update";
export default {
  data() {
    return {
      dataForm: {
        paramKey: ""
      },
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false
    };
  },
  computed: {
    searchList(){
      return this.dataList.filter(item => {
        return item.paramKey.toLowerCase().indexOf(this.dataForm.paramKey.toLowerCase()) != -1
      })
    }
  },
  components: {
    AddOrUpdate
  },
  activated() {
    this.getDataList();
  },
  methods: {
    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/sys/config/list"),
        method: "get",
        params: this.$http.adornParams({

        })
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.list;
        } else {
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });
    },
    // 多选
    selectionChangeHandle(val) {
      this.dataListSelections = val;
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map(item => {
            return item.id;
          });
      this.$confirm(
        `确定对[id=${ids.join(",")}]进行[${id ? "删除" : "批量删除"}]操作?`,
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      )
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/config/delete"),
            method: "post",
            data: this.$http.adornData(ids, false)
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.getDataList();
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        })
        .catch(() => {});
    },
    backHandle(options){
        this.addOrUpdateVisible = false
        if(options.refresh){
          this.getDataList()
        }
      },
  }
};
</script>
