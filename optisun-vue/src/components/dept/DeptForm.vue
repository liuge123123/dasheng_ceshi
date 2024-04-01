<template>
  <div>
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
      v-model="dataForm.deptName"
      v-popover:menuListPopover
      :readonly="true"
      placeholder="点击选择部门"
      class="menu-list__input"
    ></el-input>
  </div>
</template>

<script>

  export default {
    props: {
      deptId: {
        default: 0
      },
    },
    components: {
    },
    data() {
      return {
        dataForm:{
          deptId:'',
          deptName:''
        },
        dataList:[],
        defaultProps: {
          children: 'children',
          label: 'name'
        }
      }
    },
    created() {
      console.log("a")
      this.getDeptTree()
    },
    methods: {
      getDeptTree(){
        this.$http({
          url: this.$http.adornUrl('/sys/sysdept/tree'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataList = data.data
            if(this.deptId){
              this.dataForm.deptId  = this.deptId
              this.menuListTreeSetCurrentNode()
            }
          } else {
            this.dataList = []
          }
        })
      },
      //节点选择
      handleNodeClick(data) {
        this.dataForm.deptId = data.id
        this.dataForm.deptName = data.name;
        this.$emit("input",this.dataForm.deptId)
      },
      // 菜单树选中
      menuListTreeCurrentChangeHandle(data, node) {
        this.dataForm.deptId = data.id;
        this.dataForm.deptName = data.name;
        this.$emit("input", this.dataForm.deptId)
      },
      // 菜单树设置当前选中节点
      menuListTreeSetCurrentNode() {
        this.$refs.deptListTree.setCurrentKey(this.dataForm.deptId);
        this.dataForm.deptName = (this.$refs.deptListTree.getCurrentNode() ||
          {})["name"];
      }
    }
  }
</script>

<style scoped>

</style>
