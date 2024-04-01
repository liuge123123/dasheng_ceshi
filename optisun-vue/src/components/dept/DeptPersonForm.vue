<!--获取所有部门及所有人员-->
<template>
  <div>
    <div style="max-height: 300px;overflow-y: auto">
      <el-tree
        :data="deptPerson"
        default-expand-all
        node-key="id"
        :props="defaultProps"
        ref="tree"
        @node-click="nodeClickHandle"
      >
      </el-tree>
    </div>
  </div>
</template>

<script>
  export default {
    props: {
      isContainMy: {
        default: false //false部门人员不包含自己,
      },
    },
    data() {
      return {
        defaultProps: {
          children: 'children',
          label: 'name'
        },
        deptPerson:[], //部门人员
      };
    },
    mounted() {
      this.getDeptPersonDataList()
    },
    methods:{
      // 获取数据列表
      getDeptPersonDataList () {
        this.$http({
          url: this.$http.adornUrl('/sys/sysdept/getAllDeptPersonTree'),
          method: 'get',
          params: this.$http.adornParams(
            {
              'isContainMy':this.isContainMy
            }
          )
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.deptPerson = data.data
          } else {
            this.deptPerson = []
          }
        })
      },
      //节点选择
      nodeClickHandle(val){
        this.$emit("input",val)
      }
    }
  }
</script>

<style scoped>

</style>

