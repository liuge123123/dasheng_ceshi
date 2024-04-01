<template>
    <div class="mod-config">
        <el-card v-if="!addOrUpdateVisible">
            <div class="cpt">查询条件</div>
            <search-form :options="searchFormOptions" @search="searchHandle"></search-form>
            <div class="cpt">
                结果一览
            </div>
            <el-form :inline="true" size="small">
                <el-form-item>
                    <el-button icon="el-icon-plus" v-if="isAuth('exercise:treasure:save')" type="primary"
                        @click="addOrUpdateHandle()">新增</el-button>
                    <el-button icon="el-icon-delete" v-if="isAuth('exercise:treasure:delete')" type="danger"
                        @click="deleteHandle()" :disabled="dataListSelections.length <= 0">批量删除</el-button>
                </el-form-item>
            </el-form>
            <el-table :data="dataList" border size="small" v-loading="dataListLoading"
                @selection-change="selectionChangeHandle" style="width: 100%;">
                <el-table-column type="selection" header-align="center" align="center" width="50">
                </el-table-column>

                <el-table-column prop="id" header-align="center" align="center" label="ID">
                </el-table-column>
                <el-table-column prop="uid" header-align="center" align="center" label="用户ID">
                </el-table-column>
                <el-table-column prop="type" header-align="center" align="center" label="类型">
                    <template slot-scope="scope">
                    <div v-if="scope.row.type==1">
                    <el-tag size="mini" type="success" effect="dark">固定金额</el-tag>
                    </div>
                    <div v-else>
                    <el-tag size="mini" type="danger" effect="dark">随机金额</el-tag>
                    </div>
                </template>
                </el-table-column>
                <el-table-column prop="amount" header-align="center" align="center" label="总金额">
                </el-table-column>
                <el-table-column prop="number" header-align="center" align="center" label="数量">
                </el-table-column>
                <el-table-column prop="remainder" header-align="center" align="center" label="剩余数量">
                </el-table-column>
                <el-table-column prop="code" header-align="center" align="center" label="红包码">
                </el-table-column>
                <el-table-column prop="status" header-align="center" align="center" label="状态">
                    <template slot-scope="scope">
                        <div v-if="scope.row.status == 1">
                            <el-tag size="mini" type="success" effect="dark">已抢完</el-tag>
                        </div>
                        <div v-else>
                            <el-tag size="mini" type="danger" effect="dark">未抢完</el-tag>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="createTime" header-align="center" align="center" label="创建时间">
                    <template slot-scope="scope">
                        <div>{{ scope.row.createTime | timeStampDateTimeFormat }}</div>
                    </template>
                </el-table-column>
                <el-table-column fixed="right" header-align="center" align="center" width="150" label="操作">
                    <template slot-scope="scope">
                        <el-button icon="el-icon-edit" v-if="isAuth('exercise:treasure:update')" type="text" size="small"
                            @click="addOrUpdateHandle(scope.row.id)">修改</el-button>
                        <el-button icon="el-icon-delete" v-if="isAuth('exercise:treasure:delete')" type="text" size="small"
                            @click="deleteHandle(scope.row.id)">删除</el-button>
                    </template>
                </el-table-column>
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
import AddOrUpdate from './treasure-add-or-update'
export default {
    data() {
        return {
            dataForm: {},
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalPage: 0,
            dataListLoading: false,
            dataListSelections: [],
            addOrUpdateVisible: false,
            searchFormOptions: {
                filedList: [
                    { filedCode: 'key', filedName: '关键字', filedType: '1' }
                ]
            }
        }
    },
    components: {
        AddOrUpdate
    },
    activated() {
        this.getDataList()
    },
    methods: {
        searchHandle(val) {
            this.dataForm = val
            this.pageIndex = 1
            this.getDataList()
        },
        // 获取数据列表
        getDataList() {
            this.dataListLoading = true
            this.$http({
                url: this.$http.adornUrl('/exercise/treasure/list'),
                method: 'get',
                params: this.$http.adornParams({
                    'page': this.pageIndex,
                    'limit': this.pageSize,
                    'key': this.dataForm.key
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
                    url: this.$http.adornUrl('/exercise/treasure/delete'),
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
        }
    }
}
</script>
