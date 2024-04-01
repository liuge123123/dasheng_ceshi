<template>
  <div>
    <el-select :loading="loading" placeholder="请选择" v-model="valText" size="small" ref="selectblur">
      <el-option :value="valText"  :key="valText" class="permission-tree-panel">
        <el-tree ref="tree"
                 :data="dataTree"
                 :props="defaultProps"
                 node-key="id"
                 default-expand-all
                 :check-on-click-node="true"
                 @node-click="handleNodeClick"></el-tree>
      </el-option>
    </el-select>
  </div>
</template>

<script>
    export default {
        name: "SearchTeam",
        data(){
        return {
          valText:'',
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
        handleNodeClick(data) {
          this.$emit('input', data)
          this.valText = data.name
          this.$refs.selectblur.blur();
        },
        getData(){
          this.loading = true
          this.$http({
            url: this.$http.adornUrl('/sys/sysdatapermission/treeDept'),
            method: 'get',
            params: this.$http.adornParams()
          }).then(({data}) => {
            if (data && data.code === 0) {
              this.dataTree = data.list
            } else {
              this.dataTree = []
            }
            this.loading = false
          })
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
