<!--获取当前部门及以下人员（多选）-->
<template>
  <div>
    <div style="max-height: 300px;overflow-y: auto">
      <el-tree
        :data="deptPerson"
        show-checkbox
        default-expand-all
        node-key="id"
        :props="defaultProps"
        ref="tree"
        @check-change="change"
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
        selectedPerson:[] //选择的部门人员
      };
    },
    mounted() {
      this.getDeptPersonDataList()
    },
    methods:{
      // 获取数据列表
      getDeptPersonDataList () {
        this.$http({
          url: this.$http.adornUrl('/sys/sysdept/getDeptPersonTree'),
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
      change(){
        this.selectedPerson = this.$refs.tree.getCheckedNodes(true)
        this.$emit('change', this.selectedPerson)
      },
    }
  }
</script>

<style scoped>

</style>

