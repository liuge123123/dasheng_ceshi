<template>
  <div class="mod-config">
    <el-card  v-show="!user_addOrUpdateVisible">
      <div style="display: flex">
        <el-form :inline="true" size="small"  style="flex: 1">
          <el-form-item>
            <el-button
              icon="el-icon-plus"
              v-if="isAuth('sys:user:save')"
              type="primary"
              @click="OffendWordAddHandle"
              class="addButton">新增员工</el-button>
          </el-form-item>
        </el-form>
        <div>
          <div style="display: flex">
            <SearchUserInput @loaded="teamLoad" ref="teamSearch" @change="searchHanle"></SearchUserInput>
            <el-input
              placeholder="编号、用户名、姓名、电话号码"
              v-model="search"
              style="margin-left: 10px"
              class="selectBtn" size="small">
              <el-button
                slot="append"
                icon="el-icon-search"
                @click="searchHanle"
              ></el-button>
            </el-input>
          </div>
        </div>

      </div>
      <div class="disF">
<!--        <category-list style="width: 260px;" ref="categoryList"-->
<!--                       @nodeClick="categoryNodeClick"></category-list>-->
        <div style="padding-left: 0px; width: calc(100%); cursor: pointer">
          <user-list ref="userList" @update="updateHandle"></user-list>
        </div>
      </div>
    </el-card>
    <user-edit v-if="user_addOrUpdateVisible"
               @back="backHandle"
               ref="userEdit"></user-edit>
  </div>
</template>

<script>
import categoryList from "./user-group.vue"
import userList from "./user-list.vue"
import userEdit from './user-add-or-update'
import SearchUserInput from "../../../components/permission/SearchUserInput";
export default {
  data() {
    return {
      search:"",
      categoryId:0,
      dataForm: {},
      user_addOrUpdateVisible:false,
    };
  },
  components: {
    categoryList,
    userList,
    userEdit,
    SearchUserInput
  },
  activated() {

  },
  methods: {
    getCategorList(){
      this.$refs.categoryList.getCategoryList()
    },
    searchHanle(){
      let param = {"key":this.search,categoryId:this.categoryId,"team":this.$refs.teamSearch.getSeletedVal().toString()}
      this.$refs.userList.searchHandle(param)
    },
    categoryNodeClick(options){
      if(options.id){
        this.categoryId = options.id;
        this.searchHanle();
      }
    },
    backHandle(options) {
      this.user_addOrUpdateVisible = false;
      this.$nextTick(() => {
        if (options.refresh) {
          this.$refs.teamSearch.getData()
         // this.searchHanle();
        }
      })
    },
    OffendWordAddHandle(){
      this.user_addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.userEdit.init(0)
      })
    },
    updateHandle(data){
      this.user_addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.userEdit.init(data.id)
      })
    },
    shareChangeHandle(val){
      this.categoryId = 0
      this.searchHanle()
     // this.getCategorList()
    },
    deleteAllHandle(){
      this.$refs.userList.deleteHandle(null)
    },
    teamLoad(status, data){
      if(status){
        this.searchHanle()
      }else{
        this.$message({
          showClose: true,
          message: '数据加载失败，请联系管理员！',
          type: 'error'
        });
      }
    },
  },
};
</script>

<style scoped>

</style>
