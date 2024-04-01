<template>
  <div>
    <el-select :loading="loading" placeholder="请选择" v-model="valText" size="small">
      <el-option value="" class="permission-tree-panel">
        <el-tree ref="tree"
                 :data="dataTree"
                 :props="defaultProps"
                 node-key="id"
                 default-expand-all
                 show-checkbox
                 @check="handleCheckChange"></el-tree>
      </el-option>
    </el-select>
  </div>
</template>

<script>
    export default {
        name: "SearchTeam",
        data(){
        return {
          valText:'全部',
          loading: false,
          dataTree: [],
          defaultProps: {
            children: 'children',
            label: 'name'
          },
          dataRule: {
            valText: [
              { required: true, message: '请选择部门', trigger: 'change' }
            ]
          }
        }
      },
      mounted() {
          this.getData()
      },
      methods:{
        getData(){
          this.loading = true
          this.$http({
            url: this.$http.adornUrl('/sys/sysdatapermission/treeDept'),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dataTree = [{
                id: '',
                name: '全部',
                type: 2,
                children: data.list,
              }]
              this.$nextTick(item => {
                this.$refs.tree.setCheckedKeys([''])
                this.$emit('loaded', true, this.getSeletedVal())
              })
            } else {
              this.$nextTick(item => {
                this.$emit('loaded', false, data)
              })
            }
            this.loading = false
          })
        },
        handleCheckChange(data, indeterminate) {
          // 显示值
          let checkedAll = this.$refs.tree.getCheckedNodes(false, false)
          let nameArr = []
          for(let i = 0; i < checkedAll.length; i++){
            let item = checkedAll[i]
            if(item.id == ''){
              nameArr.push(item.name)
              break
            }else if(item.type == 2){
              if(!this.isDeptExist(checkedAll, item.parentId)){
                nameArr.push(item.name)
              }
            }else if(item.type == 1){
              if(!this.isDeptExist(checkedAll, item.deptId)){
                nameArr.push(item.name)
              }
            }
          }
          this.valText = nameArr.join(',')

          // 返回值
          let idArr = this.getSeletedVal()
          this.$emit('input', idArr)
          this.$emit('change', idArr)
        },
        isDeptExist(list, deptId){
          for(let i = 0; i < list.length; i++){
            let item = list[i]
            if(item.type == 2 && item.id == deptId){
              return true
            }
          }
          return false
        },
        getSeletedVal(){
          // 返回值
          let checkedArr = this.$refs.tree.getCheckedNodes(true, false)
          let idArr = []
          checkedArr.forEach(item => {
            if(item.type == 2) {
              idArr.push(item.id)
            }
          })
          return idArr
        },
      }
    }
</script>

<style scoped lang="scss">
  .permission-tree-panel{
    height: auto;
    padding: 0;
    background: #ffffff;
    font-weight: normal;
  }
</style>
