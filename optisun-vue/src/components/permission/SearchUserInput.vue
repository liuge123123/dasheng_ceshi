<!--
用户数据权限选择框
事件：
1. 数据加载完成事件：loaded(status, data)，数据加载成功status = true, data是全部用户的id，数据加载失败status = false, data为错误信息
2. 选中值修改事件：change(data)，data为所有选中用户的id
3. 默认事件：input(data), data为所有选中用户的id
-->
<template>
  <div>
    <el-select :loading="loading" size="small" placeholder="请选择" v-model="valText">
      <el-option value="" class="permission-tree-panel">
          <el-tree ref="tree"
                   :data="dataTree"
                   :props="defaultProps"
                   node-key="id"
                   show-checkbox
                   default-expand-all
                   @check="handleCheckChange"></el-tree>
      </el-option>
    </el-select>
  </div>
</template>

<script>
  export default {
    name: "SearchInput",
    data(){
      return {
        valText: '全部',
        loading: false,
        dataTree: [],
        defaultProps: {
          children: 'children',
          label: 'name'
        }
      }
    },
    mounted(){
      this.getData()
    },
    methods: {
      getData(){
        this.loading = true
        this.$http({
          url: this.$http.adornUrl('/sys/sysdatapermission/userListTree'),
          method: 'get'
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.dataTree = [{
              id: '',
              name: '全部',
              children: data.list,
            }]
            this.$nextTick(item => {
              this.$refs.tree.setCheckedKeys([''])
              this.$emit('loaded', true, this.getSeletedVal())
            })
          }else{
            this.$nextTick(item => {
              this.$emit('loaded', false, data)
            })
          }
          this.loading = false
        })
      },
      isDeptExist(list, deptId){
        for(let i = 0; i < list.length; i++){
          let item = list[i]
          return true
        }
        return false
      },
      getSeletedVal(){
        // 返回值
        let checkedArr = this.$refs.tree.getCheckedNodes(false, false)
        let idArr = []
        checkedArr.forEach(item => {
          if(item.id!=''){
            idArr.push(item.id)
          }
          if((item.userType==1||item.userType==0)&& idArr.indexOf(0)<0){
             idArr.push(0)
          }
        })
        return idArr
      },
      getSeletedItems(){
        // 返回值
        let checkedArr = this.$refs.tree.getCheckedNodes(false, false)
        let idArr = []
        checkedArr.forEach(item => {
            if(item.id!=''){
              let option = {
                id:item.id,
                label:item.name
              }
              idArr.push(option)
            }
        })
        return idArr
      },

      handleCheckChange(data, indeterminate) {
        // 显示值
        let checkedAll = this.$refs.tree.getCheckedNodes(false, true)
        let nameArr = []
        for(let i = 0; i < checkedAll.length; i++){
          let item = checkedAll[i]
          if(item.id == ''){
            nameArr.push(item.name)
            break
          }
          else{
            nameArr.push(item.name)
          }
        }
        this.valText = nameArr.join(',')
        // 返回值
        let idArr = this.getSeletedVal()
        this.$emit('input', idArr)
        this.$emit('change', idArr)
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
