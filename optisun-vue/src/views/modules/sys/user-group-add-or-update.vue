<template>
  <el-form class="cpf" :model="dataForm" :rules="dataRule" ref="dataForm" label-width="120px" size="small">
    <el-form-item label="组名称" prop="name">
      <el-input v-model="dataForm.name" placeholder="组名称" maxlength="50"></el-input>
    </el-form-item>
    <el-form-item label="上级分组" prop="parentId">
      <el-popover ref="menuListPopover" placement="bottom-start" trigger="click">
        <el-tree :data="dataList" :props="defaultProps"
                 ref="deptListTree"
                 @current-change="menuListTreeCurrentChangeHandle"
                 :default-expand-all="true"
                 :highlight-current="true"
                 :expand-on-click-node="false"
                 node-key="id" @node-click="handleNodeClick"></el-tree>
      </el-popover>
      <el-input
        v-model="dataForm.parentName"
        v-popover:menuListPopover
        :readonly="true"
        placeholder="点击选择上级分组"
        class="menu-list__input"
      ></el-input>
    </el-form-item>
    <el-form-item label="排序" prop="sort">
      <el-input-number v-model="dataForm.sort" size="small" controls-position="right" :min="1" :max="10000"
                       style="width: 100%"></el-input-number>
    </el-form-item>
  </el-form>
</template>

<script>
import { treeDataTranslate } from '@/utils'
export default {
  data () {
    return {
      loading: false,
      dataForm: {
        deptId: 0,
        name: '',
        parentName:'',
        parentId: '',
        sort: ''
      },
      dataRule: {
        name: [
          { required: true, message: '部门名称不能为空', trigger: 'blur' }
        ],
        parentId: [
          { required: true, message: '上级部门不能为空', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '排序不能为空', trigger: 'blur' }
        ]
      },
      dataList:[],
      defaultProps: {
        children: 'children',
        label: 'name'
      }
    }
  },
  created() {

  },
  methods: {
    goBack(){
      this.$emit('back', {})
    },
    init (id) {
      this.dataForm.deptId = id || 0
      this.$nextTick(() => {
        this.$refs["dataForm"].resetFields();
        this.getDepList();
      });
    },

    getDepList(){
      this.$http({
        url: this.$http.adornUrl('/sys/sysdept/selectTree'),
        method: 'get',
        params: this.$http.adornParams()
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.data
        } else {
          this.dataList = []
        }
        this.getModel();
      })
    },

    getModel(){
      if (!this.dataForm.deptId) {
        // 新增
        this.menuListTreeSetCurrentNode();
      }
      else  {
        this.loading = true
        this.$http({
          url: this.$http.adornUrl(`/sys/sysdept/info/${this.dataForm.deptId}`),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          this.loading = false
          if (data && data.code === 0) {
            this.dataForm.name = data.sysDept.name
            this.dataForm.parentId = data.sysDept.parentId
            this.dataForm.delFlag = data.sysDept.delFlag
            this.dataForm.createTime = data.sysDept.createTime
            this.dataForm.createUserId = data.sysDept.createUserId
            this.dataForm.orgId = data.sysDept.orgId
            this.dataForm.sort = data.sysDept.sort
            this.menuListTreeSetCurrentNode();
          }else{
            this.$message.error(data.msg)
          }
        })
      }
    },

    // 表单提交
    dataFormSubmit (callback) {
      this.$refs['dataForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          this.$http({
            url: this.$http.adornUrl(`/sys/sysdept/${!this.dataForm.deptId ? 'save' : 'update'}`),
            method: 'post',
            data: this.$http.adornData({
              'deptId': this.dataForm.deptId || undefined,
              'name': this.dataForm.name,
              'parentId': this.dataForm.parentId,
              'delFlag': this.dataForm.delFlag,
              'createTime': this.dataForm.createTime,
              'createUserId': this.dataForm.createUserId,
              'orgId': this.dataForm.orgId,
              'sort': this.dataForm.sort
            })
          }).then(({data}) => {
            this.loading = false
            if (data && data.code === 0) {
              this.$message({
                message: '操作成功',
                type: 'success',
                duration: 1500,
                onClose: () => {
                  this.visible = false
                  callback(true)
                }
              })
            } else {
              callback(false)
              this.$message.error(data.msg)
            }
          })
        }else{
          callback(false)
        }
      })
    },
    // 获取数据列表
    getDataList () {
      this.$http({
        url: this.$http.adornUrl('/sys/sysdept/selectTree'),
        method: 'get',
        params: this.$http.adornParams()
      }).then(({data}) => {
        if (data && data.code === 0) {
          this.dataList = data.data
        } else {
          this.dataList = []
        }
      })
    },
    //节点选择
    handleNodeClick(data) {
      this.dataForm.parentId = data.id
    },
    // 菜单树选中
    menuListTreeCurrentChangeHandle(data, node) {
      this.dataForm.parentId = data.id;
      this.dataForm.parentName = data.name;
    },
    // 菜单树设置当前选中节点
    menuListTreeSetCurrentNode() {
      this.$refs.deptListTree.setCurrentKey(this.dataForm.parentId);
      this.dataForm.parentName = (this.$refs.deptListTree.getCurrentNode() ||
        {})["name"];
    },
  }
};
</script>
