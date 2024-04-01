<template>
  <div class="mod-config">
    <el-row :gutter="15">
      <el-col :span="10">
        <el-card>
          <div slot="header" style="position: relative">
            <span>数据字典</span>
            <div style="position: absolute; right: 0; top: 0">
<!--              <el-button icon="el-icon-plus" style="padding: 0" size="small" v-if="isAuth('sys:dictionary:save')"-->
<!--                         type="text" @click="addOrUpdateHandle(0, '-1')">新增字典-->
<!--              </el-button>-->
            </div>
          </div>
          <el-table
            :data="dataList"
            :height="tableHeight"
            highlight-current-row
            size="small"
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandle"
            @current-change="getSubDataList"
            style="width: 100%;">
            <el-table-column
              type="index"
              header-align="center"
              align="center"
              label="#"
              width="50">
            </el-table-column>
            <table-tree-column
              prop="value"
              treeKey="code"
              parentKey="parentCode"
              header-align="center"
              align="left"
              label="名称">
            </table-tree-column>
            <el-table-column
              prop="code"
              header-align="center"
              align="center"
              label="编码">
            </el-table-column>
            <el-table-column
              prop="sort"
              header-align="center"
              align="center"
              label="排序">
            </el-table-column>
            <el-table-column
              prop="comments"
              header-align="center"
              align="center"
              label="备注">
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="14">
        <el-card>
          <div slot="header" style="position: relative">
            <span>数据字典选项值</span>
            <div style="position: absolute; right: 0; top: 0">
              <el-button icon="el-icon-plus" style="padding: 0" size="small" v-if="isAuth('sys:dictionary:save')"
                         type="text" @click="addOrUpdateHandle(0, currentDict.code)">新增选项值
              </el-button>
            </div>
          </div>
          <el-table
            :data="subDataList"
            :height="tableHeight"
            size="small"
            v-loading="subDataListLoading"
            @selection-change="selectionChangeHandle"
            style="width: 100%;">
            <el-table-column
              type="index"
              header-align="center"
              align="center"
              label="#"
              width="50">
            </el-table-column>
            <table-tree-column
              prop="value"
              treeKey="code"
              parentKey="parentCode"
              header-align="center"
              align="left"
              label="名称">
            </table-tree-column>
            <el-table-column
              prop="code"
              header-align="center"
              align="center"
              width="120"
              label="编码">
            </el-table-column>
            <el-table-column
              prop="sort"
              header-align="center"
              align="center"
              label="排序">
            </el-table-column>
            <el-table-column
              prop="isEdit"
              header-align="center"
              align="center"
              label="是否可删除">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.isEdit ==0" type="danger">否</el-tag>
                <el-tag v-else>是</el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="comments"
              header-align="center"
              align="center"
              label="备注">
            </el-table-column>
            <el-table-column
              header-align="center"
              align="center"
              width="150"
              label="操作">
              <template slot-scope="scope">
                <el-button icon="el-icon-edit" type="text" size="small" @click="addOrUpdateHandle(scope.row.id, scope.row.parentCode)">修改</el-button>
                <el-button icon="el-icon-delete" type="text" size="small" @click="deleteHandle(scope.row.id, 2)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible" ref="addOrUpdate" @back="backHandle"
                   @refreshDataList="getDataList"></add-or-update>
  </div>
</template>

<script>
  import AddOrUpdate from './dictionary-add-or-update'
  import TableTreeColumn from '@/components/table-tree-column'
  import {treeDataTranslate} from '@/utils'

  export default {
    props: {
      dicType:{default:10}
    },
    data() {
      return {
        dataList: [],
        dataListLoading: false,
        dataListSelections: [],
        addOrUpdateVisible: false,
        subDataList: [],
        subDataListLoading: false,
        subDataListSelections: [],
        currentDict: null
      }
    },
    computed: {
      tableHeight: {
        get() {
          return this.$store.state.common.documentClientHeight - 165
        }
      }
    },
    components: {
      AddOrUpdate,
      TableTreeColumn,
      treeDataTranslate
    },
    activated() {
      this.getDataList()
    },
    methods: {
      // 获取数据列表
      getDataList() {
        this.dataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/sys/dictionary/firstList'),
          method: 'get',
          params: {
            'dicType':this.dicType
          }
        }).then(({data}) => {
          console.log(data)
          if (data && data.code === 0) {
            this.dataList = data.list
          } else {
            this.dataList = []
          }
          this.dataListLoading = false
        })
      },
      // 获取数据列表
      getSubDataList (item) {
        this.currentDict = item
        this.subDataListLoading = true
        this.$http({
          url: this.$http.adornUrl('/sys/dictionary/codeList'),
          method: 'get',
          params: {
            code: item.code
          }
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.subDataList = data.list
          } else {
            this.subDataList = []
          }
          this.subDataListLoading = false
        })
      },
      // 多选
      selectionChangeHandle(val) {
        this.dataListSelections = val
      },
      // 新增 / 修改
      addOrUpdateHandle(id, parentCode) {
        this.addOrUpdateVisible = true
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id, parentCode)
        })
      },
      // 删除
      deleteHandle(id, type) {
        var ids = id ? [id] : this.dataListSelections.map(item => {
          return item.id
        })
        this.$confirm(`确定对[id=${ids.join(',')}]进行[${id ? '删除' : '批量删除'}]操作?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: this.$http.adornUrl('/sys/dictionary/delete'),
            method: 'post',
            data: this.$http.adornData(ids, false)
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  if (type == 1) {
                    this.getDataList()
                  }else {
                    this.getSubDataList(this.currentDict)
                  }
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
        if(options.refresh) {
          if (options.type == 1) {
            this.getDataList()
          } else {
            this.getSubDataList(this.currentDict)
          }
        }
      }
    }
  }
</script>
