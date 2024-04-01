<template>
  <el-dialog :visible.sync="visible" title="上级" size="small" width="70%" class="container">
    <el-table :data="dataList" style="width: 100%">
      <el-table-column
          type="index"
          header-align="center"
          label="级别" width="180"
          align="center">
          <template slot-scope="scope">
            {{ scope.$index + 1 }} 级别
          </template>
        </el-table-column>
      <el-table-column prop="custId" label="用户编号" sortable width="180">
      </el-table-column>
      <el-table-column prop="custName" label="姓名" sortable width="180">
      </el-table-column>
      <el-table-column prop="isNb" label="内外账号">
        <template slot-scope="scope">
          <el-tag size="mini" type="danger" v-if="scope.row.isNb == 0" effect="dark">真实玩家-0</el-tag>
          <el-tag size="mini" type="success" v-else effect="dark">业务员-1</el-tag>
        </template>
      </el-table-column>
    </el-table>
  </el-dialog>
</template>

<script>
export default {
  name: "cust-pregrade-show",
  data() {
    return {
      visible: false,
      activeName: 'first',
      custId: '',
      dataList: [{
        custId: 1,
        custName: '王小虎',
        isNb: 1
      }]
    }
  },
  mounted() {
  },
  methods: {
    init(val) {
      this.visible = true
      this.custId = val
      this.getDataList()
    },
    getDataList() {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl(`/cust/cust/parentList/${this.custId}`),
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.dataList = data.list
          this.totalPage = 1
        } else {
          this.dataList = []
          this.totalPage = 0
        }
        this.dataListLoading = false
      })
    },
  }
}
</script>

<style scoped></style>
