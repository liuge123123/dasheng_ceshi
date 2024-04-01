<template>
  <div  style=" box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);min-height: 80px">
    <div style="margin-top: 0;height: 40px;background:#eef1f6;border-radius:2px">
      <label style="color: #606266;padding-left: 20px;vertical-align: center;line-height: 40px">员工分组</label>
      <i class="el-icon-plus" style="float:right;padding-right: 10px;line-height: 40px"
         v-if="isAuth('sys:sysdept:save')" @click="addCategory()"></i>
    </div>
    <el-tree
      :data="classOptions"
      :props="menuListTreeProps"
      node-key="categoryId"
      ref="menuListTree"
      @node-click="handleNodeClick"
      :default-expand-all="true"
      :highlight-current="true"
      :expand-on-click-node="false"
      style="padding-bottom: 5px">
       <span class="custom-tree-node" slot-scope="{ node, data }" >
        <span >{{ data.name }}</span>
        <span class="actions" v-if="data.id != '0'">
           <i class="el-icon-edit" v-if="isAuth('sys:sysdept:update')" @click="() => Edit(data)"></i>
           <i class="el-icon-delete" v-if="isAuth('sys:sysdept:delete')" @click="() => remove(node, data)"></i>
        </span>
      </span>
    </el-tree>
    <el-dialog
      :title= "opType == 1 ? '新增' : '修改'"
      :visible.sync="centerDialogVisible"
      width="30%">
      <category-edit ref="CategoryEdit" :isHare="isHare"></category-edit>
      <span slot="footer" class="dialog-footer">
        <el-button @click="centerDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="okBtnEvent">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import CategoryEdit from "./user-group-add-or-update"
export default {
  data() {
    return {
      data:[],
      isHare:0,
      classOptions:[],
      centerDialogVisible:false,
      opType:1,
      menuListTreeProps: {
        label: 'groupName',
        children: 'children'
      },
    };
  },
  components: {
    CategoryEdit
  },
  mounted() {
    this.getCategoryList();
  },
  methods: {
    // 快捷回复中的分类信息
    getCategoryList() {
      this.$http({
        url: this.$http.adornUrl("/sys/sysdept/selectTree"),
        method: "get",
        params: this.$http.adornParams({
          flag:0
        }),
      }).then(({ data }) => {
        console.log(data)
        this.classOptions = data.data
      });
    },
    handleNodeClick(data) {
      let ids = ""
      let org_id = this.$store.state.user.userInfo.orgId.toString();
      if(data.parentId == org_id + "__0"){
        ids = "0";
      }else{
        ids = data.id;
        const children = data.children;
        if(children != null) {
          children.forEach(node => {
            ids += "," + node.id;
          })
        }
      }
      this.$emit('nodeClick', {id: ids})
    },

    /*新增分类*/
    addCategory(){
      this.centerDialogVisible = true;
      this.opType = 1
      this.$nextTick(v =>{
        this.$refs.CategoryEdit.init(0)
      })
    },

    /*保存*/
    okBtnEvent(){
      this.$refs.CategoryEdit.dataFormSubmit(result => {
        if(result){
          this.centerDialogVisible = false;
          this.getCategoryList();
        }
      });
    },

    /*编辑*/
    Edit(data){
      this.centerDialogVisible = true;
      this.opType = 0
      this.$nextTick(v =>{
        this.$refs.CategoryEdit.init(data.id);
      })
    },

    remove(node, data) {
      const children = node.childNodes;
      if (children != null){
        console.log(children)
        if (children.length > 0){
          this.$message({
            type: 'warning',
            message: '请先删除下级分类'
          })
          return
        }
      }
      let id = data.id;
      const h = this.$createElement;
      this.$msgbox({
        title: "提示",
        message: h("p", null, [h("span", null, "是否删除该分类？")]),
        showCancelButton: true,
        confirmButtonText: "确定",
        cancelButtonText: "取消",
      }).then((action) => {
        this.$http({
          url: this.$http.adornUrl("/sys/sysdept/delete"),
          method: "post",
          data: this.$http.adornData([id], false),
        }).then(({ data }) => {
          if (data && data.code === 0) {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getCategoryList(this.isHare);
                this.$refs.CategoryEdit.getCategoryList()
              },
            });
          } else {
            this.$message.error(data.msg);
          }
        });
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      });
    }
  },
};
</script>

<style scoped lang="scss">
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 5px;
  .actions{
    display: none;
  }
  &:hover {
    .actions{
      display: block;
    }
  }
}
</style>
