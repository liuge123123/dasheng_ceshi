<template>
  <div>
    <div>
      <el-tag @close="delTag(item)" closable class="tag" v-for="(item, index) in selectedPersonList" :key="index">{{item.name}}</el-tag>
      <el-button icon="el-icon-plus" size="mini" @click="openSelected">选择员工或部门</el-button>
    </div>
    <el-dialog title="选择员工或部门" :visible.sync="dialogVisible" width="600px">
      <div class="user-container">
        <div class="user-tree-panel">
          <el-input clearable v-model="filterText" placeholder="请输入名称">
            <i class="el-icon-search" slot="prefix"></i>
          </el-input>
          <div class="user-tree">
            <el-tree
              check-strictly
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
          <div class="user-selected-title">已选择员工或部门（{{selectedPerson.length}}）</div>
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
    name: "DeptPersonMutil2",
    props: {
      value: {
        type: Array
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
          let selectedArr = this.value
          let arr = []
          for (let i = 0; i < this.personList.length; i++){
            let item = this.personList[i]
            if(this.indexOf(selectedArr, item) != -1){
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
          let selectedArr = this.value
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
      indexOf(list, item){
        if(list && list.length > 0) {
          for (let i = 0; i < list.length; i++) {
            if (list[i].id == item.id && list[i].type == item.type) {
              return i
            }
          }
        }
        return -1
      },
      filterNode(value, data) {
        if (!value) {
          return true
        }
        return data.name.indexOf(value) !== -1
      },
      delTag(item){
        let arr = this.value
        let index = this.indexOf(arr, item)
        arr.splice(index,1)
        this.$emit('input', arr)
        this.$emit('change', arr)
      },
      openSelected(){
        this.dialogVisible = true
        let selectedArr = []
        if(this.value && this.value != ''){
          selectedArr = this.value
        }
        let checkIds = selectedArr.map(item => {
          return item.id
        })
        this.$nextTick(e => {
          this.$refs.tree.setCheckedKeys(checkIds)
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
          params: this.$http.adornParams({
            enableNoneDept: 1
          })
        }).then(({data}) => {
          if (data && data.code === 0) {
            this.deptPerson = data.data
            let parentId = "0"
            this.personList = this.getPersons([], this.deptPerson, parentId)
          } else {
            this.deptPerson = []
            this.personList = []
          }
        })
      },
      confirm(){
        let arr = this.$refs.tree.getCheckedNodes()
        arr = arr.map(item => {
          return {
            id: item.id,
            name: item.name,
            type: item.type
          }
        })
        this.dialogVisible = false
        this.$emit('input', arr)
        this.$emit('change', arr)
      },
      change(){
        this.selectedPerson = this.$refs.tree.getCheckedNodes()
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

