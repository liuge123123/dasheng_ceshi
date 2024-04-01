<template>
  <div>
    <div>
      <el-tag @close="delTag(item)" closable class="tag" v-for="(item, index) in selectedPersonList" :key="index">{{item.name}}</el-tag>
      <el-button icon="el-icon-plus" size="mini" @click="openSelected">选择员工</el-button>
    </div>
    <el-dialog title="选择员工" :visible.sync="dialogVisible" width="600px">
      <div class="user-container">
        <div class="user-tree-panel">
          <el-input clearable v-model="filterText" placeholder="请输入员工名称">
            <i class="el-icon-search" slot="prefix"></i>
          </el-input>
          <div class="user-tree">
            <el-tree
              :filter-node-method="filterNode"
              @check-change="change"
              :data="deptPerson"
              show-checkbox
              :expand-on-click-node="false"
              check-on-click-node
              default-expand-all
              node-key="id"
              :props="defaultProps"
              ref="tree">
            </el-tree>
          </div>
        </div>
        <div class="user-selected">
          <div class="user-selected-title">已选择成员（{{selectedPerson.length}}）</div>
          <div class="user-selected-list">
            <div class="item" v-for="(item, index) in selectedPerson">
              <div class="name">{{item.name}}</div>
              <div class="actions">
                <i class="el-icon-close" @click="delPerson(item)"></i>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer">
        <el-button size="small" type="primary" @click="confirm">确定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
  export default {
    name: "DeptPersonMutil",
    props: {
      value: {
        type: String
      }
    },
    data() {
      return {
        dialogVisible: false,
        defaultProps: {
          children: 'children',
          label: 'name'
        },
        deptPerson:[], //部门人员
        personList: [],
        selectedPerson: [],
        filterText: ''
      };
    },
    computed: {
      selectedPersonList(){
        if(this.value && this.value.length != ''){
          let selectedArr = this.value.split(',')
          let arr = []
          for (let i = 0; i < this.personList.length; i++){
            let item = this.personList[i]
            if(item.type == 1 && selectedArr.indexOf(item.id) != -1){
              arr.push(item)
            }
          }
          return arr
        }
        return  []
      },
      userInfo() {
        return this.$store.state.user.userInfo;
      },
      selectedCount(){
        if(this.value && this.value != ''){
          let selectedArr = this.value.split(',')
          return selectedArr.length
        }
        return 0;
      }
    },
    created() {
      this.getDeptPersonDataList()
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    },
    methods:{
      filterNode(value, data) {
        if (!value) {
          return true
        }
        return data.name.indexOf(value) !== -1
      },
      delTag(item){
        let id = item.id
        let arr = this.value.split(',')
        let index = arr.indexOf(id)
        arr.splice(index,1)
        this.$emit('input', arr.join(','))
        this.$emit('change', arr.join(','))
      },
      openSelected(){
        this.dialogVisible = true
        let selectedArr = []
        if(this.value && this.value != ''){
          selectedArr = this.value.split(',')
        }
        this.$nextTick(e => {
          this.$refs.tree.setCheckedKeys(selectedArr)
        })
      },
      getPersons(resList, tree, parentId){
        for (let i = 0; i < tree.length; i++){
          let item = tree[i]
          if(item.parentId == parentId){
            resList.push(item)
            if(item.children && item.children.length > 0){
              this.getPersons(resList, item.children, item.id)
            }
          }
        }
        return resList
      },
      // 获取数据列表
      getDeptPersonDataList () {
        this.$http({
          url: this.$http.adornUrl('/sys/sysdept/getDeptPersonTree'),
          method: 'get',
          params: this.$http.adornParams()
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.deptPerson = data.data
            let parentId = this.userInfo.orgId + "__0"
            this.personList = this.getPersons([], this.deptPerson, parentId)
          } else {
            this.deptPerson = []
            this.personList = []
          }
        })
      },
      confirm(){
        let arr = this.$refs.tree.getCheckedKeys(true)
        this.dialogVisible = false
        this.$emit('input', arr.join(','))
        this.$emit('change', arr.join(','))
      },
      change(){
        this.selectedPerson = this.$refs.tree.getCheckedNodes(true)
      },
      delPerson(item){
        this.$refs.tree.setChecked(item.id, false, false)
      }
    }
  }
</script>

<style lang="scss" scoped>
  .tag{
    margin-right: 5px;
  }
  .user-container{
    display: flex;
    .user-tree-panel{
      width: 290px;
      border: 1px solid #eeeeee;
      margin-right: 20px;
      border-radius: 4px;
      padding: 10px;
      .user-tree{
        margin-top: 10px;
        height: 400px;
        overflow-y: auto;
      }
    }
    .user-selected{
      width: 290px;
      border: 1px solid #eeeeee;
      border-radius: 4px;
      .user-selected-title{
        line-height: 32px;
        /*border-bottom: 1px solid #eeeeee;*/
        padding: 10px;
        color: #333333;
        font-weight: bold;
      }
      .user-selected-list{
        height: 400px;
        overflow-y: auto;
        .item{
          display: flex;
          line-height: 32px;
          padding: 0 10px;
          &:hover{
            background: rgb(236, 245, 255);
            .actions{
              color: #F56C6C;
            }
          }
          .name{
            flex: 1;
          }
          .actions{
            cursor: pointer;
          }
        }
      }
    }
  }
</style>

